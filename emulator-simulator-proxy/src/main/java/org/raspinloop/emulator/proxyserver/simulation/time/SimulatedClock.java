package org.raspinloop.emulator.proxyserver.simulation.time;

import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.START;
import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.STOP;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.raspinloop.emulator.proxyserver.simulation.SimulationEvents;
import org.raspinloop.emulator.proxyserver.simulation.SimulationStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
public class SimulatedClock {

	private SimulatedTimeAdapter incomingMessageAdapter;
	Sinks.Many<SimulatedTimeMessage> stepsSink = Sinks.many().multicast().onBackpressureBuffer();
	Instant instant = Instant.ofEpochMilli(0);
	Duration timeLeftIncurrentStep = Duration.ZERO;
	private Map<Class<? extends SimulatedTimeMessage>, Consumer<SimulatedTimeMessage>> handlers = new HashMap<>();
	private StateMachine<SimulationStates, SimulationEvents> stateMachine;
	Object waitLock = new Object();
	

	public SimulatedClock(@Autowired SimulatedTimeAdapter incomingMessageAdapter,
			@Autowired StateMachine<SimulationStates, SimulationEvents> stateMachine) {
		this.incomingMessageAdapter = incomingMessageAdapter;
		this.stateMachine = stateMachine;
		handlers.put(StartMessage.class, this::doStart);
		handlers.put(StopMessage.class, this::doStop);
		handlers.put(WaitingMessage.class, this::doWait);
		handlers.put(SleepingMessage.class, this::doSleep);
	}

	public void startListening() {

		incomingMessageAdapter.publishEmulatorTimeNotifications().publishOn(Schedulers.boundedElastic())
				.doOnError((error) -> log.error("Error processing simulatedtime change message in the {}", this, error))
				.repeat().subscribe(this::handleEmulatorNotification);
	}

	private void handleEmulatorNotification(SimulatedTimeMessage simulatedtimemessage) {
		handlers.getOrDefault(simulatedtimemessage.getClass(), message -> {
		}).accept(simulatedtimemessage);
	}

	private void doStart(SimulatedTimeMessage simulatedtimemessage) {
		instant = Instant.ofEpochMilli(0);
		stateMachine.sendEvent(START);
	}

	private void doStop(SimulatedTimeMessage simulatedtimemessage) {
		stateMachine.sendEvent(STOP);
	}

	private void doWait(SimulatedTimeMessage simulatedtimemessage) {
		WaitingMessage waitingMessage = (WaitingMessage) simulatedtimemessage;
		log.debug("Waiting for {}s {}ns", waitingMessage.getSeconds(), waitingMessage.getNanos());
		synchronized (waitLock) {
			waitLock.notifyAll();
		}
	}

	private void doSleep(SimulatedTimeMessage simulatedtimemessage) {
		SleepingMessage sleepingMessage = (SleepingMessage) simulatedtimemessage;
		synchronized (this) {
			Duration sleepindDuration = Duration.ofSeconds(sleepingMessage.getSeconds(), sleepingMessage.getNanos());
			timeLeftIncurrentStep = timeLeftIncurrentStep.minus(sleepindDuration);
			instant = instant.plus(sleepindDuration);
		}
	}

	public void doStep(double communicationStepSize) {
		// update internal clock
		int stepSeconds = (int) communicationStepSize;
		int stepNanos = (int) ((communicationStepSize -  (int) communicationStepSize)* 1_000_000_000);
		synchronized (this) {
			instant = instant.plus(timeLeftIncurrentStep);
			timeLeftIncurrentStep = Duration.ofSeconds(stepSeconds, stepNanos);
		}
		// notify libsimultime
		stepsSink.emitNext(new StepMessage(stepSeconds, stepNanos), FAIL_FAST);

	}

	public void notifyStopped() {
		stepsSink.emitNext(new StoppedMessage(), FAIL_FAST);
	}

	public void waitForBlocking() throws InterruptedException {
		synchronized (waitLock) {
			waitLock.wait();
		}
	}

	public Flux<SimulatedTimeMessage> publishSimulatedTimeChanges() {
		return stepsSink.asFlux();
	}

	public synchronized Instant instant() {
		return instant.plusMillis(0);
	}
}
