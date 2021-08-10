#include <time.h>
#include <stdio.h>

int main()
{
  struct timespec elapsed = {2, 500000000L} ; /* 2s and 500 000 000 ns */

  return clock_nanosleep(CLOCK_MONOTONIC, 0, &elapsed, NULL);
}