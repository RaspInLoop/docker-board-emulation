package org.raspinloop.emulator.proxyserver.fmi;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.raspinloop.emulator.proxyserver.fmi.CoSimulation.Iface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@Component
@WithStateMachine
public class Server {

	private TSimpleServer simpleServer;
	private List<FmiStatusAware> statusListener;
	private ExecutorService executorService;

	public Server(@Autowired Iface fmiAdapter, @Autowired List<FmiStatusAware> statusListener,
			@Value("${fmi.server.port:9090}") int port,
			@Autowired @Qualifier("singleThreaded") ExecutorService executorService) throws TTransportException {
		super();
		this.statusListener = statusListener;
		this.executorService = executorService;
		CoSimulation.Processor<Iface> processor = new CoSimulation.Processor<Iface>(fmiAdapter);
		TServerTransport serverTransport;
		serverTransport = new TServerSocket(port);
		simpleServer = new TSimpleServer(new TSimpleServer.Args(serverTransport).processor(processor));
	}

	@OnStateEntry(target = "WAITING")
	public void start() {
		executorService.submit(() -> simpleServer.serve());
		statusListener.forEach(FmiStatusAware::onSimulationWaiting);
	}

	@OnStateEntry(target = "STOPPED")
	public void stop() {
		if (simpleServer.isServing()) {
			simpleServer.stop();
		}
		statusListener.forEach(FmiStatusAware::onSimulationStopped);
	}
}
