#include <time.h>
#include <unistd.h>
#include <stdio.h>

int main()
{
  time_t start = time(NULL);
  sleep(5);
  time_t end;
  time(&end);
  fprintf(stdout, "start time %ld", start);
  fprintf(stdout, "end time %ld", end);
  return  !(end - start == 5);
}