package org.raspinloop.emulator.simulator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withMarginOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.raspinloop.emulator.proxyserver.simulation.SimulationEvents;
import org.raspinloop.emulator.proxyserver.simulation.SimulationStates;
import org.raspinloop.emulator.proxyserver.simulation.StateMachineConfig;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeAdapter;
import org.raspinloop.emulator.proxyserver.simulation.time.SleepingMessage;
import org.raspinloop.emulator.proxyserver.simulation.time.StartMessage;
import org.raspinloop.emulator.proxyserver.simulation.time.WaitingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.annotation.DirtiesContext;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ClockIntegrationTest {

	@Autowired
	SimulatedTimeAdapter simulatedTimeAdapter;

	@Autowired
	SimulatedClock clock;

	@Test
	@DirtiesContext
	void clockStartTest() throws InterruptedException {
		clock.startListening();
		simulatedTimeAdapter.accept(new StartMessage());
		Thread.sleep(1);
		assertThat(clock.instant()).isCloseTo(Instant.EPOCH, Assertions.within(1, ChronoUnit.NANOS));
	}

	@Test
	@DirtiesContext
	void clockWaitForBlockingTest() throws InterruptedException, ExecutionException, TimeoutException {
		clock.startListening();
		simulatedTimeAdapter.accept(new StartMessage());
		ExecutorService service = Executors.newFixedThreadPool(1);
		Future<Boolean> result = service.submit(() -> {
			try {
				clock.waitForBlocking();
				log.info("unblocked");
				return true;
			} catch (Exception e) {
				log.info("Exception while blocking", e);
				return false;
			}
		});
		assertFalse(result.isDone()); // still blocked
		simulatedTimeAdapter.accept(new WaitingMessage(1,0));
		assertTrue(result.get(1, TimeUnit.SECONDS)); // No more blocked
	}
	
	@Test
	@DirtiesContext
	void clockInstantDoStepTest() throws InterruptedException {
		clock.startListening();
		simulatedTimeAdapter.accept(new StartMessage());
		Thread.sleep(1);
		Instant baseinstant = clock.instant();
		clock.doStep(0.2); // 0.2 second step
		// during this first step, no time has flow... we need to wait the next step to see the 0.2 increment 
		assertThat(Duration.between(baseinstant, clock.instant())).isCloseTo(Duration.ZERO, withMarginOf(Duration.ofNanos(1)));
		clock.doStep(1);
		// during this second step, we see only the 0.2 increment form the first step 
		assertThat(Duration.between(baseinstant, clock.instant())).isCloseTo(Duration.ofMillis(200), withMarginOf(Duration.ofNanos(1)));
		clock.doStep(1_000);
		// and so on...
		assertThat(Duration.between(baseinstant, clock.instant())).isCloseTo(Duration.ofMillis(1_200), withMarginOf(Duration.ofNanos(1)));
		clock.doStep(0.2); // 0.2 second step
		assertThat(Duration.between(baseinstant, clock.instant())).isCloseTo(Duration.ofMillis(1_001_200), withMarginOf(Duration.ofNanos(1)));
	}

	@Test
	@DirtiesContext
	void clockInstantSleepingTest() throws InterruptedException {
		clock.startListening();
		simulatedTimeAdapter.accept(new StartMessage());
		Thread.sleep(10);
		Instant baseinstant = clock.instant();
		clock.doStep(10); // 10 second step
		simulatedTimeAdapter.accept(new SleepingMessage(1, 0));
		
		simulatedTimeAdapter.accept(new SleepingMessage(0, 200_000_000));
		
		simulatedTimeAdapter.accept(new SleepingMessage(1, 200_000_000));
		Thread.sleep(1);
		assertThat(Duration.between(baseinstant, clock.instant())).isCloseTo(Duration.ofMillis(2_400), withMarginOf(Duration.ofNanos(1)));

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

	}
}
