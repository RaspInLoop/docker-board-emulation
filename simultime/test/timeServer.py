import asyncio
import sys

class TimeServer():

    def __init__(self, queue,  step_s, step_ns, duration, port):
        self.queue = queue
        self.step_s = step_s
        self.step_ns = step_ns
        self.duration = duration
        self.port = port


    async def wait_for_message(self, reader):
        data = await reader.readline()
        message = data.decode()
        self.queue.put("message received %r" %(message))
        return message

    async def write_step(self, writer):
        message = "STEP %d %d\n" % self.get_step_data()
        writer.write(message.encode())
        self.queue.put("message sent: %r" %(message))
        await writer.drain()

    async def handle_message(self, reader, writer):
        addr = writer.get_extra_info('peername')
        self.queue.put('New client socket: {}'.format(addr))
        waitting_time_s = self.duration
        waitting_time_ns = 0
        message = ""
        while ("STOP" not in message and waitting_time_s >= 0 and waitting_time_ns >= 0) :
            message  = await self.wait_for_message(reader)
            if ("START" in message):
                waitting_time_s = self.duration
                waitting_time_ns = 0
            if (("WAITING" in message) or ("START" in message)):
                waitting_time_s -= self.step_s
                waitting_time_ns -= self.step_ns
                if (waitting_time_ns < 0):
                    waitting_time_s -= 1
                    waitting_time_ns += 1000000000
                await self.write_step(writer)        
            if ( not message):
                message="STOP"
        self.queue.put("elapsed time {}s {}ns".format(waitting_time_s,waitting_time_ns))
        writer.write("STOPPED\n".encode())
        self.queue.put("message sent: STOPPED")
        await writer.drain()
        self.queue.put("Close the client socket")
        writer.close()

    def get_step_data(self):
        return ( self.step_s, self.step_ns)


    def start(self):
        self.queue.put("Starting")
        loop = asyncio.get_event_loop()
        coro = asyncio.start_server(self.handle_message, '127.0.0.1', self.port, loop=loop)
        server = loop.run_until_complete(coro)
        # Serve requests until Ctrl+C is pressed
        self.queue.put('Serving on {}'.format(server.sockets[0].getsockname()))
        try:
            loop.run_forever()
        except KeyboardInterrupt:
            pass

        # Close the server
        server.close()
        loop.run_until_complete(server.wait_closed())
        loop.close()

class LoggerQueue():
    def put(self, message):
        print(message)


if __name__ == "__main__":
    queue = LoggerQueue()
    server = TimeServer(queue, int(sys.argv[1]), int(sys.argv[2]), int(sys.argv[3]), int(sys.argv[4]))
    server.start()
    