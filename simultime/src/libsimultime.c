/*
 *  This file is part of libsimultime, version 0.0.1
 * 
 *  It is a derivative work of libfaketile (https://github.com/wolfcw/libfaketime)
 * 
 *  libsimultime is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License v2 as published by the
 *  Free Software Foundation.
 *
 *  libsimultime is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 *  more details.
 *
 *  You should have received a copy of the GNU General Public License v2 along
 *  with the libsimultime; if not, write to the Free Software Foundation,
 *  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/*
*
*
*/

#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <unistd.h>
#include <fcntl.h>
#include <poll.h>
#include <sys/epoll.h>
#include <time.h>
#include <math.h>
#include <errno.h>
#include <string.h>
#include <semaphore.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <limits.h>
#include <pthread.h>
#include <signal.h>
#include <sys/timeb.h>
#include <dlfcn.h>
#include <fcntl.h> // Contains file controls like O_RDWR
#include <termios.h> // Contains POSIX terminal control definitions
#include <unistd.h> // write(), read(), close()
#include "uthash.h"
#include "time_ops.h"
#include "faketime_common.h"

#define BUFFERLEN   256

extern char *__progname;
#include <endian.h>


/* pointers to real (not faked) functions */
static int          (*real_stat)            (int, const char *, struct stat *);
static int          (*real_fstat)           (int, int, struct stat *);
static int          (*real_fstatat)         (int, int, const char *, struct stat *, int);
static int          (*real_lstat)           (int, const char *, struct stat *);
static int          (*real_stat64)          (int, const char *, struct stat64 *);
static int          (*real_fstat64)         (int, int , struct stat64 *);
static int          (*real_fstatat64)       (int, int , const char *, struct stat64 *, int);
static int          (*real_lstat64)         (int, const char *, struct stat64 *);
static time_t       (*real_time)            (time_t *);
static int          (*real_ftime)           (struct timeb *);
static int          (*real_gettimeofday)    (struct timeval *, void *);
static int          (*real_clock_gettime)   (clockid_t clk_id, struct timespec *tp);

static int          (*real_pthread_cond_timedwait_225)  (pthread_cond_t *, pthread_mutex_t*, struct timespec *);
static int          (*real_pthread_cond_timedwait_232)  (pthread_cond_t *, pthread_mutex_t*, struct timespec *);
static int          (*real_pthread_cond_init_232) (pthread_cond_t *restrict, const pthread_condattr_t *restrict);
static int          (*real_pthread_cond_destroy_232) (pthread_cond_t *);
//static pthread_rwlock_t monotonic_conds_lock;


static int          (*real_timer_settime_22)   (int timerid, int flags, const struct itimerspec *new_value,
                                                struct itimerspec * old_value);
static int          (*real_timer_settime_233)  (timer_t timerid, int flags,
                                                const struct itimerspec *new_value,
                                                struct itimerspec * old_value);
static int          (*real_timer_gettime_22)   (int timerid,
                                                struct itimerspec *curr_value);
static int          (*real_timer_gettime_233)  (timer_t timerid,
                                                struct itimerspec *curr_value);


static int          (*real_nanosleep)       (const struct timespec *req, struct timespec *rem);

static int          (*real_clock_nanosleep) (clockid_t clock_id, int flags, const struct timespec *req, struct timespec *rem);

static int          (*real_usleep)          (useconds_t usec);
static unsigned int (*real_sleep)           (unsigned int seconds);
static unsigned int (*real_alarm)           (unsigned int seconds);
static int          (*real_poll)            (struct pollfd *, nfds_t, int);
static int          (*real_ppoll)           (struct pollfd *, nfds_t, const struct timespec *, const sigset_t *);

static int          (*real_epoll_wait)      (int epfd, struct epoll_event *events, int maxevents, int timeout);
static int          (*real_epoll_pwait)     (int epfd, struct epoll_event *events, int maxevents, int timeout, const sigset_t *sigmask);

static int          (*real_select)          (int nfds, fd_set *restrict readfds,
                                             fd_set *restrict writefds,
                                             fd_set *restrict errorfds,
                                             struct timeval *restrict timeout);

static int          (*real_pselect)         (int nfds, fd_set *restrict readfds,
                                             fd_set *restrict writefds,
                                             fd_set *restrict errorfds,
                                             const struct timespec *timeout,
                                             const sigset_t *sigmask);

static int          (*real_sem_timedwait)   (sem_t*, const struct timespec*);

static int initialized = 0;
static int serial_port;
static pthread_t  listening_thread; 
static pthread_mutex_t mtx;
static pthread_cond_t  cond;
static bool interrupted  = false;
static int exit_code = 0;
struct timespec last_step_inc;

static void st_cleanup (void) __attribute__ ((destructor));
static void st_init (void) __attribute__ ((constructor));

#ifndef DEBUG
#define DEBUG 1
#endif

#define debug_print(fmt, ...) \
            do { if (DEBUG) fprintf(stderr, fmt, __VA_ARGS__); } while (0)

void notify_wait(const struct timespec * timetowait)
{
  char msg[100] = {'\0'};
  int msg_size = sprintf(msg, "WAITING %ld %ld\n", timetowait->tv_sec, timetowait->tv_nsec);
  debug_print("st:WAITING %ld %ld for thread %lx\n", timetowait->tv_sec, timetowait->tv_nsec, pthread_self() );
  write(serial_port, msg, sizeof(msg_size));
}

void notify_start()
{
  char msg[7] = {'\0'};
  int msg_size = sprintf(msg, "START\n");
  debug_print("st:START by thread %lx\n", pthread_self() );
  write(serial_port, msg, sizeof(msg_size));
}

void notify_stop()
{
  char msg[7] = {'\0'};
  int msg_size = sprintf(msg, "STOP\n");
  debug_print("st:STOP by thread %lx\n", pthread_self() );
  write(serial_port, msg, sizeof(msg_size));
}

void increment_current_time(long sec, long nsec){
  pthread_mutex_lock(&mtx);
  last_step_inc.tv_sec = sec;
  last_step_inc.tv_nsec = nsec;
  pthread_cond_broadcast(&cond);
  debug_print("st:Increment current time for thread %lx\n", pthread_self() );
  pthread_mutex_unlock(&mtx);
}

void interrupt_simulation() {
  pthread_mutex_lock(&mtx);
  interrupted = true;
  pthread_cond_broadcast(&cond);
  debug_print("st:Simulation interrupted for thread %lx\n", pthread_self() );
  pthread_mutex_unlock(&mtx);  
}
/*
simulsleep suspends the execution of the calling thread until either
       at least the simulated time specified in *req has elapsed (by the simulator), or the delivery of a
       signal that triggers the invocation of a handler in the calling
       thread or that terminates the process.

On successfully sleeping for the requested interval, nanosleep()
       returns 0.  If the call is interrupted by a signal handler or
       encounters an error, then it returns -1, with errno set to indicate
       the error.
*/
int simulsleep(const struct timespec *req, struct timespec *rem)
{
  if (interrupted) {
      return -1;
  }

  struct timespec local_rem;
  if (rem ==NULL) 
  {
    rem = &local_rem;
  }
   *rem = *req;
  notify_wait(req);
  while (rem->tv_sec > 0 || rem->tv_nsec > 0)
  {
    // wait for step (semaphore/signal)
    pthread_mutex_lock(&mtx);
    pthread_cond_wait(&cond,&mtx);    
    struct timespec step_inc = last_step_inc;
    pthread_mutex_unlock(&mtx);
    if (interrupted) {
      return -1;
    }
    // a step furter...
    debug_print("st:simulsleep a step further %ld sec %ld nsec by thread %lx\n", step_inc.tv_sec, step_inc.tv_nsec, pthread_self() );
    struct timespec result;
    timespecsub(rem, &step_inc, &result);
    *rem = result;
  }
  debug_print("st:simulsleep resumed for thread %lx\n",  pthread_self() );
  return 0;
}



/*
 * Faked nanosleep()
 */
int nanosleep(const struct timespec *req, struct timespec *rem)
{
  int result;  

  if (!initialized)
  {
    st_init();
  }
  
  result = simulsleep(req, rem);

  /* return the result to the caller */
  return result;
}

/*
 * Faked clock_nanosleep()
 */
// int clock_nanosleep(clockid_t clock_id, int flags, const struct timespec *req, struct timespec *rem)
// {
//   int result;
//   struct timespec real_req;

//   if (!initialized)
//   {
//     st_init();
//   }
//   if (real_clock_nanosleep == NULL)
//   {
//     return -1;
//   }
//   if (req != NULL)
//   {
//     if (flags & TIMER_ABSTIME) /* sleep until absolute time */
//     {
//       struct timespec tdiff, timeadj;
//       timespecsub(req, &user_faked_time_timespec, &timeadj);
//       if (user_rate_set)
//       {
//         timespecmul(&timeadj, 1.0/user_rate, &tdiff);
//       }
//       else
//       {
//         tdiff = timeadj;
//       }
//       if (clock_id == CLOCK_REALTIME)
//       {
//         timespecadd(&ftpl_starttime.real, &tdiff, &real_req);
//       }
//       else if (clock_id == CLOCK_MONOTONIC)
//       {
//         timespecadd(&ftpl_starttime.mon, &tdiff, &real_req);
//       }
//       else /* presumably only CLOCK_PROCESS_CPUTIME_ID, leave untouched */
//       {
//        real_req = *req;
//       }
//     }
//     else /* sleep for a relative time interval */
//     {
//       if (user_rate_set && !dont_fake && ((clock_id == CLOCK_REALTIME) || (clock_id == CLOCK_MONOTONIC))) /* don't touch CLOCK_PROCESS_CPUTIME_ID */
//       {
//         timespecmul(req, 1.0 / user_rate, &real_req);
//       }
//       else
//       {
//         real_req = *req;
//       }
//     }
//   }
//   else
//   {
//     return -1;
//   }

//   DONT_FAKE_TIME(result = (*real_clock_nanosleep)(clock_id, flags, &real_req, rem));
//   if (result == -1)
//   {
//     return result;
//   }
//   /* fake returned parts */
//   if ((rem != NULL) && ((rem->tv_sec != 0) || (rem->tv_nsec != 0)))
//   {
//     if (user_rate_set && !dont_fake)
//     {
//       timespecmul(rem, user_rate, rem);
//     }
//   }
//   /* return the result to the caller */
//   return result;
// }

/*
 * Faked usleep()
 */
int usleep(useconds_t usec)
{
  int result;

  if (!initialized)
  {
    st_init();
  }
  struct timespec nano_req, rem;

  nano_req.tv_sec = usec / 1000000;
  nano_req.tv_nsec = (usec % 1000000) * 1000;
  result = simulsleep(&nano_req, &rem);
 
  return result;
}

/*
 * Faked sleep()
 */
unsigned int sleep(unsigned int seconds)
{
  if (!initialized)
  {
    st_init();
  }
  int result;
  struct timespec real_req = {seconds, 0}, rem; 
  result = simulsleep(&real_req, &rem);
  if (result == -1)
  {
    return 0;
  }
  /* return the result to the caller */
  return rem.tv_sec;
}



void* do_listen(void* data) {
  int fd = *(int*)data;  
  char buf [100];
  
  while (! interrupted) 
  {
    int n = read (fd, buf, sizeof(buf) );
    /* recept either 
    * 'STEP 123456789 987654321' --> do step of nb sec and nanosec
    * 'CANCELED'  --> simulation was canceled by user
    * 'ENDED'  --> Simulation ended
    * 'STOPPED' --> application stopped before end of simulation
    * 'ERROR error message' --> simulation was ended with error message
    */ 
    debug_print("st:do_listen \" %s \" received thread %lx\n", buf, pthread_self() );
    if (n >= 5) //No message are smaller that 5
    {
      unsigned long sec,nsec;
      char error_message [100];
      if (sscanf(buf, "STEP %lu %lu",&sec, &nsec) == 2)
      {
        increment_current_time(sec, nsec);
      } else if (sscanf(buf, "ERROR %s", error_message) == 1)
      {
        interrupt_simulation();
        fprintf( stderr, "Simulation stopped due to error: %s\n", error_message);
        exit_code = 2;
      } else if (strncmp(buf, "ENDED", strlen("ENDED")) == 0)
      {
        interrupt_simulation();
        fprintf( stderr, "Simulation ended\n");
        exit_code = 1;
      } else if ( strncmp(buf, "CANCELED", strlen("CANCELED")) == 0)
      {
        interrupt_simulation();
        fprintf( stderr, "Simulation stopped by user\n");
        exit_code = 3;
      } else if ( strncmp(buf, "STOPPED", strlen("STOPPED")) == 0)
      {
        interrupt_simulation();
        exit_code = 0;
        fprintf( stderr, "Application stopped\n");
      }
    } 
  }
  if (exit_code > 0)
  {
    kill(getpid(), SIGTERM);
  }
  return &exit_code;
}

/*
 *      =======================================================================
 *      Initialization                                                 === INIT
 *      =======================================================================
 */

static void st_init(void)
{

  /* Look up all real_* functions. NULL will mark missing ones. */
  real_stat =               dlsym(RTLD_NEXT, "__xstat");
  real_fstat =              dlsym(RTLD_NEXT, "__fxstat");
  real_fstatat =            dlsym(RTLD_NEXT, "__fxstatat");
  real_lstat =              dlsym(RTLD_NEXT, "__lxstat");
  real_stat64 =             dlsym(RTLD_NEXT,"__xstat64");
  real_fstat64 =            dlsym(RTLD_NEXT, "__fxstat64");
  real_fstatat64 =          dlsym(RTLD_NEXT, "__fxstatat64");
  real_lstat64 =            dlsym(RTLD_NEXT, "__lxstat64");
  real_time =               dlsym(RTLD_NEXT, "time");
  real_ftime =              dlsym(RTLD_NEXT, "ftime");
  real_gettimeofday =       dlsym(RTLD_NEXT, "gettimeofday");
  real_nanosleep =          dlsym(RTLD_NEXT, "nanosleep");
  real_clock_nanosleep =    dlsym(RTLD_NEXT, "clock_nanosleep");
  real_usleep =             dlsym(RTLD_NEXT, "usleep");
  real_sleep =              dlsym(RTLD_NEXT, "sleep");
  real_alarm =              dlsym(RTLD_NEXT, "alarm");
  real_poll =               dlsym(RTLD_NEXT, "poll");
  real_ppoll =              dlsym(RTLD_NEXT, "ppoll");
  real_epoll_wait =         dlsym(RTLD_NEXT, "epoll_wait");
  real_epoll_pwait =        dlsym(RTLD_NEXT, "epoll_pwait");
  real_select =             dlsym(RTLD_NEXT, "select");
  real_pselect =            dlsym(RTLD_NEXT, "pselect");
  real_sem_timedwait =      dlsym(RTLD_NEXT, "sem_timedwait");

  real_pthread_cond_timedwait_225 = dlvsym(RTLD_NEXT, "pthread_cond_timedwait", "GLIBC_2.2.5");

  real_pthread_cond_timedwait_232 = dlvsym(RTLD_NEXT, "pthread_cond_timedwait", "GLIBC_2.3.2");
  real_pthread_cond_init_232 = dlvsym(RTLD_NEXT, "pthread_cond_init", "GLIBC_2.3.2");
  real_pthread_cond_destroy_232 = dlvsym(RTLD_NEXT, "pthread_cond_destroy", "GLIBC_2.3.2");

  if (NULL == real_pthread_cond_timedwait_232)
  {
    real_pthread_cond_timedwait_232 =  dlsym(RTLD_NEXT, "pthread_cond_timedwait");
  }
  if (NULL == real_pthread_cond_init_232)
  {
    real_pthread_cond_init_232 =  dlsym(RTLD_NEXT, "pthread_cond_init");
  }
  if (NULL == real_pthread_cond_destroy_232)
  {
    real_pthread_cond_destroy_232 =  dlsym(RTLD_NEXT, "pthread_cond_destroy");
  }

  // if (pthread_rwlock_init(&monotonic_conds_lock,NULL) != 0) {
  //   fprintf(stderr,"monotonic_conds_lock init failed\n");
  //   exit(-1);
  // }
  real_clock_gettime  =     dlsym(RTLD_NEXT, "__clock_gettime");
  if (NULL == real_clock_gettime)
  {
    real_clock_gettime  =   dlsym(RTLD_NEXT, "clock_gettime");
  }
  real_timer_settime_22 =   dlvsym(RTLD_NEXT, "timer_settime","GLIBC_2.2");
  real_timer_settime_233 =  dlvsym(RTLD_NEXT, "timer_settime","GLIBC_2.3.3");
  if (NULL == real_timer_settime_233)
  {
    real_timer_settime_233 =  dlsym(RTLD_NEXT, "timer_settime");
  }
  real_timer_gettime_22 =   dlvsym(RTLD_NEXT, "timer_gettime","GLIBC_2.2");
  real_timer_gettime_233 =  dlvsym(RTLD_NEXT, "timer_gettime","GLIBC_2.3.3");
  if (NULL == real_timer_gettime_233)
  {
    real_timer_gettime_233 =  dlsym(RTLD_NEXT, "timer_gettime");
  }

  initialized = 1;

  // Open File descriptor on USB
  char* usb_serial_file =  getenv("SIMULTIME_USB");
  if (usb_serial_file == NULL)
  {
    usb_serial_file = "/dev/ttyUSB0";

  }
  debug_print("st:opening %s in thread %ld\n", usb_serial_file,  listening_thread );
  serial_port = open(usb_serial_file, O_RDWR);
  // Check for errors
  if (serial_port < 0) {
      printf("Error %i from open: %s\n", errno, strerror(errno));
  }

  // Start thread
  int rc = pthread_create(&listening_thread, NULL, do_listen, (void*)&serial_port);
  if (rc) {
      fprintf(stderr,"Fail to create listening thread (%d)\n", rc);        
  }
  notify_start();
}

static void st_cleanup (void)
{
  // Stop thread  
  debug_print("st:stoping thread %ld\n",  listening_thread );
  notify_stop(); // will echo a STOPPED message to the listening thread
  int* exit_code;
  pthread_join(listening_thread, (void **)&exit_code);
  debug_print("st:thread stopped witw code %d, %ld\n", *exit_code, listening_thread );

  debug_print("st:closing USB for thread %lx\n",  pthread_self() );
  // close USB FileDescriptor
  close(serial_port);
}