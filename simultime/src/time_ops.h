/*
 * Time operation inline functions based on sys/time.h
 */

#ifndef TIME_OPS_H
#define TIME_OPS_H
#include <time.h>

static struct timespec timespec_zero = {0 ,0};

inline uint64_t as_nanoseconds(const struct timespec* ts) __attribute__((always_inline));
inline void to_timespec(uint64_t nanos, struct timespec *result) __attribute__((always_inline));
inline bool timespec_is_set (const struct timespec *t) __attribute__((always_inline));
inline void timespec_clear (struct timespec *t) __attribute__((always_inline));
inline void timespec_add (const struct timespec* a, const struct timespec* b, struct timespec *result) __attribute__((always_inline));
inline void timespec_sub (const struct timespec *a, const struct timespec *b, struct timespec *result) __attribute__((always_inline));
inline int timespec_cmp (const struct timespec *a, const struct timespec *b)  __attribute__((always_inline));

inline uint64_t as_nanoseconds(const struct timespec* ts) 
{
    return ts->tv_sec * (uint64_t)1000000000L + ts->tv_nsec;
}

inline void to_timespec(uint64_t nanos, struct timespec *result) 
{
    result->tv_sec = nanos / 1000000000L;
    result->tv_nsec =  nanos % 1000000000L;
}

inline bool timespec_is_set (const struct timespec *t) 
{
   return t->tv_nsec || t->tv_sec;
}

inline void timespec_clear (struct timespec *t)
{
   t->tv_nsec = 0;
   t->tv_sec = 0;
}

inline void timespec_add (const struct timespec* a, const struct timespec* b, struct timespec *result)
{
   result->tv_sec = a->tv_sec + b->tv_sec;
   result->tv_nsec = a->tv_nsec + b->tv_nsec;
   if (result->tv_nsec > 1000000000L) {
      result->tv_sec += result->tv_nsec / 1000000000L;
      result->tv_nsec = result->tv_nsec % 1000000000L;
   } 
}

inline void timespec_sub (const struct timespec *a, const struct timespec *b, struct timespec *result)
{   
   result->tv_sec = a->tv_sec - b->tv_sec;
   if (a->tv_nsec < b->tv_nsec) {
      result->tv_sec -=1;
       result->tv_nsec = (a->tv_nsec + 1000000000L) - b->tv_nsec;
   } else {
      result->tv_nsec = a->tv_nsec - b->tv_nsec;
   }
}

inline int timespec_cmp (const struct timespec *a, const struct timespec *b)
{   
   struct timespec diff;
   timespec_sub(a, b, &diff);
   if (!  timespec_is_set(&diff)) {
      return 0;
   } else if (diff.tv_sec >= 0) {
      return 1;
   } else {
      return -1;
   }
}

#endif
