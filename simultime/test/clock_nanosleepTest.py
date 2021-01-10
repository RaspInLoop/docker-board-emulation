from avocado import Test
from avocado.utils import process
import time
import avocado
from timeServerSubProcesses import TimeServerSubProcesses

class ClockNanoSleepTest(Test):

    @avocado.fail_on(process.CmdError)
    def test_absolute_long_sleep(self):
        """ 
        test a clock_nanosleep call that will wait more than a step but less than the total simulation duration
        
        We expect clock_nanosleep returning without error after sending 5 WAITING messages upon reception of each "STEP 0 200"  
        """
        step_sec = 0
        step_ns = 200
        simulation_duration = 1
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            process.run("test/clock_nanosleep", verbose=True, allow_output_check="both", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            logs = timeserver.getLog()
            waiting = list(filter(lambda msg:'WAITING' in msg, logs))
            step = list(filter(lambda msg:'STEP' in msg, logs))
            self.log.debug('{} waiting message send'.format(len(waiting)))
            self.log.debug('{} step message received'.format(len(step)))
            self.assertEqual(5, len(waiting)) 
            self.assertEqual(6, len(step)) # 5 steps + the 1st one (fort the start)


    @avocado.fail_on(process.CmdError)
    def test_absolute_short_sleep(self):
        """ 
        test a clock_nanosleep call that will wait less than a step 
        
        We expect clock_nanosleep returning without error after sending 1 SLEEP messages upon reception of a "STEP 0 200000"    
        """
        step_sec = 0
        step_ns = 200000
        simulation_duration = 1
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            # clock_nanosleep will wait 1000 ns in TIMER_ABSTIME
            process.run("test/clock_nanosleep", verbose=True, allow_output_check="both", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            logs = timeserver.getLog()
            waiting = list(filter(lambda msg:'SLEEPING 0 1000' in msg, logs))
            step = list(filter(lambda msg:'STEP' in msg, logs))
            self.log.debug('{} waiting message send'.format(len(waiting)))
            self.log.debug('{} step message received'.format(len(step)))            
            self.assertEqual(1, len(waiting))
            self.assertEqual(1, len(step)) # the one received just after the START

    @avocado.fail_on(process.CmdError)
    def test_relative_long_sleep(self):
        """ 
        test a clock_nanosleep call that will wait more than a step but less than the total simulation duration
        
        We expect clock_nanosleep returning without error after sending 5 WAITING messages upon reception of each "STEP 0 500000000"  
        """
        step_sec = 0
        step_ns = 500000000
        simulation_duration = 3
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
             # clock_nanosleep will wait 2.5 s in RELAVIVE
            process.run("test/clock_nanosleep_rel", verbose=True, allow_output_check="both", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            logs = timeserver.getLog()
            waiting = list(filter(lambda msg:'WAITING' in msg, logs))
            step = list(filter(lambda msg:'STEP' in msg, logs))
            self.log.debug('{} waiting message send'.format(len(waiting)))
            self.log.debug('{} step message received'.format(len(step)))
            self.assertEqual(5, len(waiting)) 
            self.assertEqual(6, len(step)) # 5 steps + the 1st one (fort the start)


    @avocado.fail_on(process.CmdError)
    def test_relative_short_sleep(self):
        """ 
        test a clock_nanosleep call that will wait less than a step 
        
        We expect clock_nanosleep returning without error after sending 1 SLEEP messages upon reception of a "STEP 0 200000"    
        """
        step_sec = 5
        step_ns = 0
        simulation_duration = 10
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            # clock_nanosleep will wait 2.5 s in RELAVIVE
            process.run("test/clock_nanosleep_rel", verbose=True, allow_output_check="both", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            logs = timeserver.getLog()
            waiting = list(filter(lambda msg:'SLEEPING 0 1000' in msg, logs))
            step = list(filter(lambda msg:'STEP' in msg, logs))
            self.log.debug('{} waiting message send'.format(len(waiting)))
            self.log.debug('{} step message received'.format(len(step)))
            # Absolute time is elapsed With the STEP 0 200000 received between START ans the first wait 
            self.assertEqual(0, len(waiting))
            self.assertEqual(1, len(step)) # the one received just after the START

    def test_relative_sleep_interrupted(self):
        """ 
        test a clock_nanosleep call that will wait more than the total simulation duration
        
        We expect clock_nanosleep returning with error after sending WAITING messages upon reception of 3 "STEP 1 0" 
        return code must indicate that the process was interrupted. 
        """
        step_sec = 1
        step_ns = 250000000
        simulation_duration = 1
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            result = process.run("test/clock_nanosleep_rel", verbose=True, allow_output_check="both", ignore_status=True, shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})
            self.log.debug("result: {}".format(result))
            self.assertNotEqual(result.exit_status, 0, "exit_code cannot be 0 when interrupted")