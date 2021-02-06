package org.raspinloop.emulator.simulator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.transport.TTransportException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.raspinloop.emulator.proxyserver.fmi.CoSimulation.Iface;
import org.raspinloop.emulator.proxyserver.fmi.FmiAdapter;
import org.raspinloop.emulator.proxyserver.fmi.FmiStatusAware;
import org.raspinloop.emulator.proxyserver.fmi.Server;
import org.raspinloop.emulator.proxyserver.messaging.GpiostateOutboundAdapter;
import org.raspinloop.emulator.proxyserver.simulation.Board;
import org.raspinloop.emulator.proxyserver.simulation.SimulationEvents;
import org.raspinloop.emulator.proxyserver.simulation.SimulationStates;
import org.raspinloop.emulator.proxyserver.simulation.StateMachineConfig;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
class FmiServerIntegrationTest {

	@Autowired Server server;
	
	@MockBean
	FmiStatusAware mockStatusAware;
	
	@Autowired StateMachine<SimulationStates, SimulationEvents> stateMachine;
	
	@Test
	void serverStartStop() throws InterruptedException{
		Mockito.verify(mockStatusAware, Mockito.never()).onSimulationWaiting(); //
		stateMachine.sendEvent(SimulationEvents.START); // this should send on reception of (first?) libsimultime message
		Mockito.verify(mockStatusAware, Mockito.atLeastOnce()).onSimulationWaiting(); 
		assertTrue(serverListening("127.0.0.1", 9090));
		
		stateMachine.sendEvent(SimulationEvents.STOP);
		stateMachine.sendEvent(SimulationEvents.FMU_TERMINATE_RESULT); // this should send upon reception of FMU commands 
		Mockito.verify(mockStatusAware, Mockito.atLeastOnce()).onSimulationStopped(); 	
		int nbRetries = 10;
		while (nbRetries ++ > 0) {
			if (!serverListening("127.0.0.1", 9090)) {
				break;
			}			
		}
		assertTrue(nbRetries>0, "Waiting too long to close server");
	}
	
	
	
	public static boolean serverListening(String host, int port)
	{
	    Socket s = null;
	    try
	    {
	        s = new Socket(host, port);
	        return true;
	    }
	    catch (Exception e)
	    {
	        return false;
	    }
	    finally
	    {
	        if(s != null)
	            try {s.close();}
	            catch(Exception e){}
	    }
	}
	
    
    @Configuration
    @EnableAsync
    @Import(StateMachineConfig.class)
    static class MyTestConfiguration {
    	    	  
		@Bean
		SimulatedTimeAdapter simulatedTimeAdapter() {
			return new SimulatedTimeAdapter();
		}
		
    	@Bean
    	SimulatedClock clock(@Autowired SimulatedTimeAdapter simulatedTimeAdapter,
				 @Autowired StateMachine<SimulationStates, SimulationEvents> stateMachine) {
    		return new SimulatedClock(simulatedTimeAdapter, stateMachine);
    	}
    	
    	@Bean
    	GpiostateOutboundAdapter gpioOutboundAdapter(SimulatedClock clock) {
    		return new GpiostateOutboundAdapter(clock);
    	}
    	
    	@Bean
    	Board board(GpiostateOutboundAdapter gpioOutboundAdapter) {
    		return new Board(gpioOutboundAdapter);
    	}       
    	
    	@Bean
    	Iface fmiAdapter(@Value("${fmi.server.version:3.0}") String version,
    			@Value("${fim.server.platformtype:raspberry}") String typesPlatform,
    			@Autowired Board board,
    			@Autowired StateMachine<SimulationStates, SimulationEvents> stateMachine, 
    			@Autowired SimulatedClock clock) {
    		return new FmiAdapter(version, typesPlatform, board, stateMachine, clock);
    	}
    	
    	@Bean
    	Server server(@Autowired Iface fmiAdapter,
				  @Autowired List<FmiStatusAware> statusListener,
				  @Value("${fmi.server.port:9090}") int port,
				  @Autowired ExecutorService singleThreadedExecutor) throws TTransportException {
    		return new Server(fmiAdapter, statusListener, port, singleThreadedExecutor);
    	}
    	
        @Bean("singleThreaded")
        public ExecutorService singleThreadedExecutor() {
            return Executors.newSingleThreadExecutor();
        }
    }
    
    
}
