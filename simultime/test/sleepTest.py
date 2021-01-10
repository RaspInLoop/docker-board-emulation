
from avocado import Test
from avocado.utils import process
import time
import socket
import avocado
from timeServerSubProcesses import TimeServerSubProcesses

class SleepTest(Test):

    @avocado.fail_on(process.CmdError)
    def test_long_leep(self):
        """ 
        test a sleep call that will wait more than a step but less than the total simulation duration
        
        We expect sleep returning without error after sending 5 WAITING messages upon reception of each "STEP 1 0"  
        """
        step_sec = 1
        step_ns = 0
        simulation_duration = 10
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            process.run("/bin/sleep 5", verbose=True, allow_output_check="none", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            logs = timeserver.getLog()
            waiting = list(filter(lambda msg:'WAITING' in msg, logs))
            step = list(filter(lambda msg:'STEP' in msg, logs))
            self.log.debug('{} waiting message send'.format(len(waiting)))
            self.log.debug('{} step message received'.format(len(step)))
            self.assertEqual(5, len(waiting))
            self.assertEqual(5+1, len(step)) # 5 steps + the 1st one (fort the start)

    @avocado.fail_on(process.CmdError)
    def test_long_sleep_ns(self):
        """ 
        test a sleep call that will wait more than a step but less than the total simulation duration
        
        We expect sleep returning without error after sending 500 WAITING messages upon reception of each "STEP 0 10000000"  
        """
        step_sec = 0
        step_ns = 10000000 #10 000 000 ns => 10 ms
        simulation_duration = 10
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            process.run("/bin/sleep 5", verbose=True, allow_output_check="both", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            logs = timeserver.getLog()
            waiting = list(filter(lambda msg:'WAITING' in msg, logs))
            step = list(filter(lambda msg:'STEP' in msg, logs))
            self.log.debug('{} waiting message send'.format(len(waiting)))
            self.log.debug('{} step message received'.format(len(step)))
            self.assertEqual(500, len(waiting))
            self.assertEqual(500+1, len(step)) # 500 steps + the 1st one (fort the start)

    @avocado.fail_on(process.CmdError)
    def test_short_sleep(self):
        """ 
        test a sleep call that will wait less than a step 
        
        We expect sleep returning without error after sending 1 SLEEP messages upon reception of a "STEP 10 0"  
        """
        step_sec = 10
        step_ns = 0
        simulation_duration = 100
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            process.run("/bin/sleep 5", verbose=True, allow_output_check="both", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            logs = timeserver.getLog()
            waiting = list(filter(lambda msg:'SLEEPING 5 0' in msg, logs))
            step = list(filter(lambda msg:'STEP' in msg, logs))
            self.log.debug('{} waiting message send'.format(len(waiting)))
            self.log.debug('{} step message received'.format(len(step)))
            self.assertEqual(1, len(waiting))
            self.assertEqual(1, len(step)) # only the 1st one (fort the start)

    def test_sleep_interrupted(self):
        """ 
        test a sleep call that will wait more than the total simulation duration
        
        We expect sleep returning with error after sending WAITING messages upon reception of 3 "STEP 1 0" 
        return code must indicate that the process was interrupted. 
        """
        step_sec = 1
        step_ns = 0
        simulation_duration = 3
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:

            result = process.run("/bin/sleep 5", verbose=True, allow_output_check="both", ignore_status=True, shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            self.log.debug("result: {}".format(result))
            self.assertNotEqual(result.exit_status, 0, "exit_code cannot be 0 when interrupted")

            logs = timeserver.getLog()
            print("timeservers logs: {}".format(logs) )


