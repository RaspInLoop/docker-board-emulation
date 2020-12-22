# simultime

simultime intercepts various system calls that programs use to wait and sleep to fake the flow of time. 
Using a simulator to compute external I/O changes implies to use its time, the clock "tick" are no longer provided by the OS/hardware but by the simulator (via emulator-simulator-proxy)

## usage
LD_PRELOAD=/usr/local/lib/libsimultime.so.1 SIMULTIME_USB=/dev/ttyUSB0 your_application

### with gdb
$ gdb
(gdb) set environment LD_PRELOAD /usr/local/lib/libsimultime.so.1
(gdb) set environment SIMULTIME_USB /dev/ttyUSB0
(gdb) run

