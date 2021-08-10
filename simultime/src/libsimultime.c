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

static pthread_rwlock_t monotonic_conds_lock;


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
static pthread_mutex_t init_mtx;
static pthread_cond_t  cond;
static bool interrupted  = false;
static int exit_code = 0;
static bool stop_sent = false;
static struct timespec last_step_inc;
static struct timespec total_simulation_time;
static struct system_time_s starttime = {{0, -1}, {0, -1}, {0, -1}, {0, -1}};

__thread struct timespec time_left_in_step; // per thread structure with time left for this thread in the current step
__thread bool thread_initialized = false;

static void st_cleanup (void) __attribute__ ((destructor));
static void st_init (void) __attribute__ ((constructor));

#ifndef DEBUG
#define DEBUG 1
#endif

#define debug_print(fmt, ...) \
            do { if (DEBUG) fprintf(stdout, fmt, __VA_ARGS__); } while (0)

void notify_sleep(const struct timespec * time_sleeping)
{
  char msg[100] = {'\0'};
  int msg_size = sprintf(msg, "SLEEPING %ld %ld\n", time_sleeping->tv_sec, time_sleeping->tv_nsec);
  debug_print("st:thread %lx, pid %d send %s", pthread_self(), getpid(), msg);
  write(serial_port, msg, msg_size);
}

void notify_wait(const struct timespec * timetowait)
{
  char msg[100] = {'\0'};
  int msg_size = sprintf(msg, "WAITING %ld %ld\n", timetowait->tv_sec, timetowait->tv_nsec);
  debug_print("st:thread %lx, pid %d send %s", pthread_self(), getpid(), msg);
  write(serial_port, msg, msg_size);
}

void notify_start()
{
  char msg[7] = {'\0'};
  int msg_size = sprintf(msg, "START\n");
  debug_print("st:thread %lx, pid %d send %s", pthread_self(), getpid(), msg);
  write(serial_port, msg, msg_size);
}

void notify_stop()
{
  char msg[7] = {'\0'};
  int msg_size = sprintf(msg, "STOP\n");
  debug_print("st:thread %lx, pid %d send %s", pthread_self(), getpid(), msg);
  write(serial_port, msg, msg_size);
  stop_sent = true;
}

void increment_current_time(long sec, long nsec){
  pthread_mutex_lock(&mtx);
  last_step_inc.tv_sec = sec;
  last_step_inc.tv_nsec = nsec;
  struct timespec total_simulation_time_tmp = total_simulation_time;
  timespec_add(&total_simulation_time_tmp, &last_step_inc, &total_simulation_time);
  pthread_cond_broadcast(&cond);
  debug_print("st:Increment current time for thread %lx\n", pthread_self() );
  pthread_mutex_unlock(&mtx);
}

void get_simulation_time(struct timespec* simulation_time) {
  pthread_mutex_lock(&mtx);
  *simulation_time = total_simulation_time;
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
If cumulated waiting time for the calling thread is higher than the current step, then suspends the execution
       of the calling thread until either at least the simulated time specified in *req has elapsed (by the simulator),
       or the delivery of a signal that triggers the invocation of a handler in the calling thread that terminates the process.

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
  if (!thread_initialized){
    pthread_mutex_lock(&mtx); 
    time_left_in_step = last_step_inc;
    thread_initialized = true;
    pthread_mutex_unlock(&mtx);
  }

  struct timespec local_rem;
  if (rem ==NULL) 
  {
    rem = &local_rem;
  }
  if (as_nanoseconds(req) <= as_nanoseconds(&time_left_in_step)) {
    // time requested is less that step time -> so we may return without waiting next step
    notify_sleep(req);
    struct timespec left;
    timespec_sub(&time_left_in_step, req, &left);
    time_left_in_step = left;
    timespec_clear(rem);
    return 0;
  } 
  else 
  {
    // we doesn't have enough time in curent step, so we will have to wait for next step
    *rem = *req;    
    while (timespec_cmp(rem, &timespec_zero) > 0)
    {
      // wait for step (semaphore/signal)
      pthread_mutex_lock(&mtx);
      notify_wait(rem);
      pthread_cond_wait(&cond,&mtx);    
      struct timespec step_inc = last_step_inc;
      time_left_in_step = last_step_inc;
      pthread_mutex_unlock(&mtx);
      if (interrupted) {
        return -1;
      }
      // a step furter...
      debug_print("st:simulsleep a step further %ld sec %ld nsec by thread %lx\n", step_inc.tv_sec, step_inc.tv_nsec, pthread_self() );
      struct timespec result;
      timespec_sub(rem, &step_inc, &result);
      *rem = result;
    }
    timespec_clear(rem); // prevent negative sec or nsec
    debug_print("st:simulsleep resumed for thread %lx\n",  pthread_self() );
    return 0;
  }
}







void* do_listen(void* data) {
  int fd = *(int*)data;  
  char buf [100];
  
  while (! interrupted) 
  {
    int n = read (fd, buf, sizeof(buf) );
    buf[n] = 0;
    /* recept either 
    * 'STEP 123456789 987654321' --> do step of nb sec and nanosec
    * 'CANCELED'  --> simulation was canceled by user
    * 'STOPPED' --> application or simulation stopped
    * 'ERROR error message' --> simulation was ended with error message
    */ 
    debug_print("st:thread %lx, pid %d received \"%s\"\n", pthread_self(), getpid(), buf );    
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
        kill(getpid(), SIGTERM);
        return &exit_code;
      } else if ( strncmp(buf, "CANCELED", strlen("CANCELED")) == 0)
      {
        interrupt_simulation();
        fprintf( stderr, "Simulation stopped by user\n");
        exit_code = 3;
        kill(getpid(), SIGTERM);
        return &exit_code;
      } else if ( strncmp(buf, "STOPPED", strlen("STOPPED")) == 0)
      {
        interrupt_simulation();
        exit_code = 0;
        if (! stop_sent){
          fprintf( stderr, "Simulation stopped\n");
          kill(getpid(), SIGTERM);
        } else {
          fprintf( stderr, "Application stopped\n");
        }
        return &exit_code;
      }
    } 
  }

  return &exit_code;
}

/* Get system time from system for all clocks */
static void system_time_from_system (struct system_time_s * systime)
{
  (*real_clock_gettime)(CLOCK_REALTIME, &systime->real);
  (*real_clock_gettime)(CLOCK_MONOTONIC, &systime->mon);
  (*real_clock_gettime)(CLOCK_MONOTONIC_RAW, &systime->mon_raw);
  (*real_clock_gettime)(CLOCK_BOOTTIME, &systime->boot);
}



/*
 *      =======================================================================
 *      Initialization                                                 === INIT
 *      =======================================================================
 */

static void st_init(void)
{

  if (initialized){
    return;
  }
  pthread_mutex_lock(&init_mtx);
    if (initialized){
      pthread_mutex_unlock(&init_mtx);
      return;
    }
  /* Look up all real_* functions. NULL will mark missing ones. */
  real_stat =               dlsym(RTLD_NEXT, "__xstat"); // TODO: faked
  real_fstat =              dlsym(RTLD_NEXT, "__fxstat"); // TODO: faked
  real_fstatat =            dlsym(RTLD_NEXT, "__fxstatat"); // TODO: faked
  real_lstat =              dlsym(RTLD_NEXT, "__lxstat"); // TODO: faked
  real_stat64 =             dlsym(RTLD_NEXT,"__xstat64"); // TODO: faked
  real_fstat64 =            dlsym(RTLD_NEXT, "__fxstat64"); // TODO: faked
  real_fstatat64 =          dlsym(RTLD_NEXT, "__fxstatat64"); // TODO: faked
  real_lstat64 =            dlsym(RTLD_NEXT, "__lxstat64"); // TODO: faked
  real_time =               dlsym(RTLD_NEXT, "time"); 
  real_ftime =              dlsym(RTLD_NEXT, "ftime");
  real_gettimeofday =       dlsym(RTLD_NEXT, "gettimeofday"); 
  real_nanosleep =          dlsym(RTLD_NEXT, "nanosleep");
  real_clock_nanosleep =    dlsym(RTLD_NEXT, "clock_nanosleep");
  real_usleep =             dlsym(RTLD_NEXT, "usleep"); 
  real_sleep =              dlsym(RTLD_NEXT, "sleep");
  real_alarm =              dlsym(RTLD_NEXT, "alarm"); // TODO: faked
  real_poll =               dlsym(RTLD_NEXT, "poll"); // TODO: faked
  real_ppoll =              dlsym(RTLD_NEXT, "ppoll"); // TODO: faked
  real_epoll_wait =         dlsym(RTLD_NEXT, "epoll_wait"); // TODO: faked
  real_epoll_pwait =        dlsym(RTLD_NEXT, "epoll_pwait"); // TODO: faked
  real_select =             dlsym(RTLD_NEXT, "select"); // TODO: faked
  real_pselect =            dlsym(RTLD_NEXT, "pselect"); // TODO: faked
  real_sem_timedwait =      dlsym(RTLD_NEXT, "sem_timedwait"); // TODO: faked
  real_pthread_cond_timedwait_225 = dlvsym(RTLD_NEXT, "pthread_cond_timedwait", "GLIBC_2.2.5"); // TODO: faked
  real_pthread_cond_timedwait_232 = dlvsym(RTLD_NEXT, "pthread_cond_timedwait", "GLIBC_2.3.2"); // TODO: faked
  real_pthread_cond_init_232 = dlvsym(RTLD_NEXT, "pthread_cond_init", "GLIBC_2.3.2"); // TODO: faked
  real_pthread_cond_destroy_232 = dlvsym(RTLD_NEXT, "pthread_cond_destroy", "GLIBC_2.3.2"); // TODO: faked
  if (NULL == real_pthread_cond_timedwait_232)
  {
    real_pthread_cond_timedwait_232 =  dlsym(RTLD_NEXT, "pthread_cond_timedwait"); // TODO: faked
  }
  if (NULL == real_pthread_cond_init_232)
  {
    real_pthread_cond_init_232 =  dlsym(RTLD_NEXT, "pthread_cond_init"); // TODO: faked
  }
  if (NULL == real_pthread_cond_destroy_232)
  {
    real_pthread_cond_destroy_232 =  dlsym(RTLD_NEXT, "pthread_cond_destroy"); // TODO: faked
  }

  if (pthread_rwlock_init(&monotonic_conds_lock,NULL) != 0) {
    fprintf(stderr,"monotonic_conds_lock init failed\n");
    exit(-1);
  }
  real_clock_gettime  =     dlsym(RTLD_NEXT, "__clock_gettime"); 
  if (NULL == real_clock_gettime)
  {
    real_clock_gettime  =   dlsym(RTLD_NEXT, "clock_gettime"); 
  }
  real_timer_settime_22 =   dlvsym(RTLD_NEXT, "timer_settime","GLIBC_2.2"); // TODO: faked
  real_timer_settime_233 =  dlvsym(RTLD_NEXT, "timer_settime","GLIBC_2.3.3"); // TODO: faked
  if (NULL == real_timer_settime_233)
  {
    real_timer_settime_233 =  dlsym(RTLD_NEXT, "timer_settime"); // TODO: faked
  }
  real_timer_gettime_22 =   dlvsym(RTLD_NEXT, "timer_gettime","GLIBC_2.2"); // TODO: faked
  real_timer_gettime_233 =  dlvsym(RTLD_NEXT, "timer_gettime","GLIBC_2.3.3"); // TODO: faked
  if (NULL == real_timer_gettime_233)
  {
    real_timer_gettime_233 =  dlsym(RTLD_NEXT, "timer_gettime"); // TODO: faked
  }

  // Open File descriptor on USB
  char* usb_serial_file =  getenv("SIMULTIME_USB");
  if (usb_serial_file == NULL)
  {
    usb_serial_file = "/dev/ttyUSB0";

  }
  debug_print("st:opening %s in thread %ld\n", usb_serial_file,  listening_thread );
  serial_port = open(usb_serial_file, O_RDWR | O_NOCTTY );
  // Check for errors
  if (serial_port < 0) {
      fprintf( stderr,"Error %i from open: %s\n", errno, strerror(errno));
      exit(1);
  }

  struct termios tty;

  if(tcgetattr(serial_port, &tty) != 0) {
    fprintf( stderr,"Error %i from tcgetattr: %s\n", errno, strerror(errno));
    exit(1);
  }
  
  tty.c_cflag &= ~PARENB; // Clear parity bit, disabling parity (most common)
  tty.c_cflag &= ~CSTOPB; // Clear stop field, only one stop bit used in communication (most common)
  tty.c_cflag &= ~CSIZE; // Clear all bits that set the data size 
  tty.c_cflag |= CS8; // 8 bits per byte (most common)
  tty.c_cflag |= CRTSCTS; // enable RTS/CTS hardware flow control (most common)
  tty.c_cflag |= CREAD | CLOCAL; // Turn on READ & ignore ctrl lines (CLOCAL = 1)

  tty.c_lflag |= ICANON;
  tty.c_lflag &= ~ECHO; // Disable echo
  tty.c_lflag &= ~ECHOE; // Disable erasure
  tty.c_lflag &= ~ECHONL; // Disable new-line echo
  tty.c_lflag &= ~ISIG; // Disable interpretation of INTR, QUIT and SUSP

  tty.c_iflag &= ~(IXON | IXOFF | IXANY); // Turn off s/w flow ctrl
  tty.c_iflag &= ~(IGNBRK|BRKINT|PARMRK|ISTRIP); // Disable any special handling of received bytes
  tty.c_iflag |= IGNPAR | IGNCR;

  tty.c_oflag =0;

  tty.c_cc[VTIME] = 0;
  tty.c_cc[VMIN] = 0;

  // Set in/out baud rate to be 9600
  cfsetispeed(&tty, B9600);
  cfsetospeed(&tty, B9600);

  // Save tty settings, also checking for error
  if (tcsetattr(serial_port, TCSANOW, &tty) != 0) {
      fprintf( stderr,"Error %i from tcsetattr: %s\n", errno, strerror(errno));
      exit(1);
  }

  // Start thread
  int rc = pthread_create(&listening_thread, NULL, do_listen, (void*)&serial_port);
  if (rc) {
      fprintf(stderr,"Fail to create listening thread (%d)\n", rc);
      exit(2);
  }
  system_time_from_system(&starttime);
  debug_print("st: Simulation started for process %d at:\n", getpid());
  debug_print("st: CLOCK_REALTIME  %ld sec %ld nsec\n", starttime.real.tv_sec, starttime.real.tv_nsec);
  debug_print("st: CLOCK_MONOTONIC  %ld sec %ld nsec\n", starttime.mon.tv_sec, starttime.mon.tv_nsec);
  debug_print("st: CLOCK_MONOTONIC_RAW  %ld sec %ld nsec\n", starttime.mon_raw.tv_sec, starttime.mon_raw.tv_nsec);
  debug_print("st: CLOCK_BOOTTIME  %ld sec %ld nsec\n", starttime.boot.tv_sec, starttime.boot.tv_nsec);
  // wait for first step (semaphore/signal)
  pthread_mutex_lock(&mtx);
  notify_start();
  pthread_cond_wait(&cond,&mtx);   
 
  time_left_in_step = last_step_inc;
  pthread_mutex_unlock(&mtx);
  
  initialized = 1;
  pthread_mutex_unlock(&init_mtx);
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

/*
 *      =======================================================================
 *      Fake                                                                ===
 *      =======================================================================
 */

/*
 * Faked clock_gettime()
 */
int clock_gettime(clockid_t clk_id, struct timespec *tp)
{
  struct timespec simulation_time;
  get_simulation_time(&simulation_time);
  switch (clk_id)
  {
  case CLOCK_MONOTONIC:
    timespec_add(&starttime.mon, &simulation_time, tp);
    return 0;
  case CLOCK_MONOTONIC_RAW:
    timespec_add(&starttime.mon_raw, &simulation_time, tp);
    return 0;
  case CLOCK_BOOTTIME:
    timespec_add(&starttime.boot, &simulation_time, tp);
    return 0;
  case CLOCK_REALTIME:
    timespec_add(&starttime.real, &simulation_time, tp);
    return 0;
  default: /*don't touch*/
    return (*real_clock_gettime)(clk_id, tp);  
  }
}

int __clock_gettime(clockid_t clk_id, struct timespec *tp)
{
  return clock_gettime( clk_id, tp);
}


/*
 * Faked gettimeofday()
 * man page says: The use of the timezone structure is obsolete; the tz argument
                  should normally be specified as NULL.
 */

int gettimeofday(struct timeval *tv, void* tz)
{
  struct timespec timespec_result;
  int err = clock_gettime(CLOCK_REALTIME, &timespec_result);
  if (err != 0)
  {
    return ((time_t) -1);
  }
  if (tv != NULL)
  {
    tv->tv_sec = timespec_result.tv_sec;
    tv->tv_usec = timespec_result.tv_nsec * 1000;
  }
  if (tz != NULL) {
    fprintf(stderr, "specify timezone in gettimeofday is deprecated and not taken into account");
  }
  return 0;
}

/*
 * Faked time()
 */

time_t time(time_t * result)
{
  struct timespec timespec_result;
  int err = clock_gettime(CLOCK_REALTIME, &timespec_result);
  if (err != 0)
  {
    return ((time_t) -1);
  }
  if (result != NULL)
  {
    *result = timespec_result.tv_sec;
  }
  return timespec_result.tv_sec;
}

/*
 * Faked ftime()
 * NOTE: This function is deprecated, and will be removed in a
       future version of the GNU C library.  Use clock_gettime(2)
       instead.
 * So only minimal field will be filled
 */
int ftime(struct timeb *tp)
{
  struct timespec timespec_result;
  int err = clock_gettime(CLOCK_REALTIME, &timespec_result);
  if (err != 0)
  {
    return ((time_t) -1);
  }
  if (tp != NULL)
  {
    tp->time = timespec_result.tv_sec;
    tp->millitm = timespec_result.tv_nsec * 1000 * 1000;
  }
  return 0;
}

/*
 * Faked nanosleep()
 */
int nanosleep(const struct timespec *req, struct timespec *rem)
{
  st_init();
  return simulsleep(req, rem);
}

/*
 * Faked clock_nanosleep()
 */
int clock_nanosleep(clockid_t clock_id, int flags, const struct timespec *req, struct timespec *rem)
{
  st_init();

  int result;
  if (req != NULL)
  {
    if (flags & TIMER_ABSTIME) /* sleep until absolute time */
    {
      struct timespec timeadj;
      struct timespec simulation_time;
      struct timespec abs_simulation_time;
      get_simulation_time(&simulation_time);

      if (clock_id == CLOCK_REALTIME)
      {
        timespec_add(&starttime.real, &simulation_time, &abs_simulation_time);
        timespec_sub(req, &abs_simulation_time, &timeadj);
        result = simulsleep(&timeadj, rem);
      }
      else if (clock_id == CLOCK_MONOTONIC)
      {
        timespec_add(&starttime.mon, &simulation_time, &abs_simulation_time);
        timespec_sub(req, &abs_simulation_time, &timeadj);
        result = simulsleep(&timeadj, rem);
      }
      else /* presumably only CLOCK_PROCESS_CPUTIME_ID, leave untouched */
      {
        result = (*real_clock_nanosleep)(clock_id, flags, req, rem);
      }
    }
    else /* sleep for a relative time interval */
    {
      if ((clock_id == CLOCK_REALTIME) || (clock_id == CLOCK_MONOTONIC)) /* don't touch CLOCK_PROCESS_CPUTIME_ID */
      {
        result = simulsleep(req, rem);
      }
      else
      {
        result = (*real_clock_nanosleep)(clock_id, flags, req, rem);
      }
    }
  }
  else
  {
    return -1;
  }
  /* return the result to the caller */
  return result;
}

/*
 * Faked usleep()
 */
int usleep(useconds_t usec)
{
  st_init();

  int result;
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
  st_init();

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