# simultime

simultime intercepts various system calls that programs use to wait and sleep to fake the flow of time. 
Using a simulator to compute external I/O changes implies to use its time, the clock "tick" are no longer provided by the OS/hardware but by the simulator (via emulator-simulator-proxy)

## usage
LD_PRELOAD=/usr/local/lib/libsimultime.so.1 SIMULTIME_USB=/dev/ttyUSB0 your_application

## Debugging

### using test/timeServer.py
test/timeServer.py is a server listening on a tcp port. il uses the defined time protocol
Launch it wit `python  test/timeServer.py step_s step_ns duration port` where:
* step_s is the number of second of the step interval
* step_ns is the number of nanosecond of the step interval
* duration is the duration of the simulation
* port is the listening port.

In addition, you have to create a tty connected to this server with `socat pty,link=/tmp/virtualcom0,raw tcp:127.0.0.1:{port}`

Then you may start your application with LD_PRELOAD and SIMULTIME_USB env variable.

### with gdb
$ gdb
(gdb) set environment LD_PRELOAD /usr/local/lib/simultime/libsimultime.so.1
(gdb) set environment SIMULTIME_USB /tmp/virtualcom0
(gdb) run

##