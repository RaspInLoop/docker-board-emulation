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
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@Component
@WithStateMachine
public class Server {

	private TSimpleServer simpleServer;
	private List<FmiStatusAware> statusListener;
	private ExecutorService executorService;
	private Iface fmiAdapter;
	private int port;

	public Server(@Autowired Iface fmiAdapter, @Autowired List<FmiStatusAware> statusListener,
			@Value("${fmi.server.port:9090}") int port,
			@Autowired @Qualifier("singleThreaded") ExecutorService executorService) {
		super();
		this.fmiAdapter = fmiAdapter;
		this.statusListener = statusListener;
		this.port = port;
		this.executorService = executorService;
		
		
	}

	@OnStateEntry(target = "WAITING")
	public void start() throws TTransportException {
		CoSimulation.Processor<Iface> processor = new CoSimulation.Processor<Iface>(fmiAdapter);
		TServerTransport serverTransport;
		serverTransport = new TServerSocket(port);
		simpleServer = new TSimpleServer(new TSimpleServer.Args(serverTransport).processor(processor));
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
