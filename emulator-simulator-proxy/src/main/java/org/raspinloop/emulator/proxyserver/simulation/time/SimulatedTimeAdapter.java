package org.raspinloop.emulator.proxyserver.simulation.time;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
public class SimulatedTimeAdapter {
	
	Sinks.Many<SimulatedTimeMessage> messageSink = Sinks.many().multicast().onBackpressureBuffer();

	
	public void accept(SimulatedTimeMessage message) {
		log.debug("received SimulatedTimeMessage: {}", message);
		messageSink.emitNext(message, FAIL_FAST);
	}

	public Flux<? extends SimulatedTimeMessage> publishEmulatorTimeNotifications() {
		return messageSink.asFlux();
	}

}
