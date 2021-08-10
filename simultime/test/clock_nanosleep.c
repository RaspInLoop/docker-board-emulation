#include <time.h>
#include <stdio.h>

int main()
{
  struct timespec deadline;
  clock_gettime(CLOCK_MONOTONIC, &deadline);

  // Add the time you want to sleep
  deadline.tv_nsec += 1000;

  // Normalize the time to account for the second boundary
  if(deadline.tv_nsec >= 1000000000) {
      deadline.tv_nsec -= 1000000000;
      deadline.tv_sec++;
  }
  return clock_nanosleep(CLOCK_MONOTONIC, TIMER_ABSTIME, &deadline, NULL);
}