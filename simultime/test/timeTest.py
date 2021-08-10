from avocado import Test
from avocado.utils import process
import time
import avocado
from timeServerSubProcesses import TimeServerSubProcesses

class TimeTest(Test):

    @avocado.fail_on(process.CmdError)
    def test_time(self):
        """ 
        test two time calls at 5 second interval
        
        We expect our test program time returning without error after receiving 5 lot of STEP 1 0
        """
        step_sec = 1
        step_ns = 0
        simulation_duration = 10
        with TimeServerSubProcesses( step_sec, step_ns, simulation_duration) as timeserver:
            process.run("test/time", verbose=True, allow_output_check="both", shell=False, env={'LD_PRELOAD':"./libsimultime.so.1" , 'SIMULTIME_USB':"/tmp/virtualcom0"})

