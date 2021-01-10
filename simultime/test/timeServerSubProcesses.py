import avocado.utils.network.ports
from avocado.utils import process
from timeServer import TimeServer
import os
import tempfile
import time


class TimeServerSubProcesses():

    def __init__(self, step_s, step_ns, duration):
        self.step_s = step_s
        self.step_ns = step_ns
        self.duration = duration

    def __enter__(self): 
        self.start()
        return self

    def __exit__(self, *args): 
        self.stop()

    def start(self):
        self.server_port = avocado.utils.network.ports.find_free_port()
        self.tempdir = tempfile.TemporaryDirectory()
        self.log = os.path.join(self.tempdir.name, 'timeserver.log')
        self.timeserver = process.SubProcess("python3 -u test/timeServer.py {} {} {} {} > {}".format(self.step_s, self.step_ns, self.duration, self.server_port, self.log ), shell=True)
        self.timeserver.start()
        self.wait_for_started()
        print("timeserver started")
        self.socat = process.SubProcess("socat pty,link=/tmp/virtualcom0,echo=0,crnl tcp:127.0.0.1:{}".format(self.server_port), verbose=False, allow_output_check="none", shell=True)
        self.socat.start()
        print("socat started")

    def get_port(self):
        return self.server_port

    def stop(self):
        self.socat.kill()
        print("socat closed")
        self.timeserver.wait(0.1)
        print("timeserver closed")
        self.tempdir.cleanup()

    def getLog(self): 
        logfile = open(self.log,"r")
        logs = list()
        line = logfile.readline()
        while line:
            logs.append(line)
            line = logfile.readline()
        return logs

    def wait_for_started(self):
        while not os.path.exists(self.log):
            time.sleep(0.5)
        logfile = open(self.log,"r")
        line = logfile.readline()
        while not line or (line and 'Serving on' not in line):
            line = logfile.readline()
            time.sleep(0.1)