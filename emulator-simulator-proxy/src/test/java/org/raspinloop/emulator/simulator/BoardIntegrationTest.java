package org.raspinloop.emulator.simulator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulationException;
import org.raspinloop.emulator.hardwareemulation.IoChange;
import org.raspinloop.emulator.hardwareemulation.IoChange.Change;
import org.raspinloop.emulator.proxyserver.messaging.GpiostateOutboundAdapter;
import org.raspinloop.emulator.proxyserver.simulation.Board;
import org.raspinloop.emulator.proxyserver.simulation.SimulationEvents;
import org.raspinloop.emulator.proxyserver.simulation.SimulationStates;
import org.raspinloop.emulator.proxyserver.simulation.StateMachineConfig;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeAdapter;
import org.raspinloop.orchestrator.api.EmulatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.DefaultMessageBuilderFactory;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.test.StepVerifier;

@SpringBootTest
@DirtiesContext
class BoardIntegrationTest {

    @Autowired
    private Board board;

    @Autowired
    GpiostateOutboundAdapter gpioOutboundAdapter;
    
	private EmulatorParam emulatorParam;

	@BeforeEach
	void setup() throws JsonProcessingException, IOException {
		emulatorParam = new EmulatorParam(){};
		File jsonConfigFileName = new File(getClass().getClassLoader().getResource("defaultDummyBoard.json").getFile());
		ObjectMapper mapper = new ObjectMapper();
		emulatorParam.setHardwareproperties(mapper.readTree(jsonConfigFileName));
	}
	
    @Test
    void boardInputTest() throws JsonProcessingException, HardwareEmulationException, InterruptedException {
    	board.setParameter(emulatorParam);
    	board.configure();
    	board.start();
    	// emulate gpio change: IO0 input is at address 0
    	clearBit(0);
    	    	
    	List<Boolean> inputs = board.getHardwareEmulation().getBoolean(List.of(1)); // ref for IO0 input = 1
    	Boolean input = inputs.stream().findFirst().orElseThrow();
    	assertFalse(input, "IO0 Must be clear at startup");
    	
    	setBit(0);// emulate gpio change
    	gpioOutboundAdapter.handleMessage(getMessageBuilderFactory().withPayload(new IoChange(Change.SET, (byte)0)).build());
    	inputs = board.getHardwareEmulation().getBoolean(List.of(1)); 
    	input = inputs.stream().findFirst().orElseThrow();
    	assertTrue(input, "IO0 Must be set");
    	board.stop();
    }

	@Test
    void boardOutputTest() throws JsonProcessingException, HardwareEmulationException, InterruptedException {
    	board.setParameter(emulatorParam);
    	board.configure();
    	board.start();  	
    	Map<Integer, Boolean> refValue = new HashMap<>();
    	refValue.put(9, false); // ref for IO2 output = 9
    	board.getHardwareEmulation().setBoolean(refValue); 
    	refValue.put(9, true);
    	board.getHardwareEmulation().setBoolean(refValue); 
    	refValue.put(9, false);
    	board.getHardwareEmulation().setBoolean(refValue); 
    	board.stop();
    	
    	StepVerifier.create(board.getGpioProvider().publishIoChanges())
    	.expectNext(IoChange.clear((byte)2))
    	.expectNext(IoChange.set((byte)2))
    	.expectNext(IoChange.clear((byte)2))
    	.expectComplete()
    	.verify(Duration.ofSeconds(1));
    }

    private void setBit(int i) {
    	gpioOutboundAdapter.handleMessage(getMessageBuilderFactory().withPayload(new IoChange(Change.SET, (byte)i)).build());
	}

	private void clearBit(int i) {
		gpioOutboundAdapter.handleMessage(getMessageBuilderFactory().withPayload(new IoChange(Change.CLEAR, (byte)i)).build());
	}
	
	private MessageBuilderFactory messageBuilderFactory;
	
    private MessageBuilderFactory getMessageBuilderFactory() {
		if (this.messageBuilderFactory == null) {
			this.messageBuilderFactory = new DefaultMessageBuilderFactory();
		}
		return this.messageBuilderFactory;
	}
    
    
    @Configuration
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
       
    }
    
}