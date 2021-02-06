package org.raspinloop.emulator.proxyserver.messaging;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import org.raspinloop.emulator.hardwareemulation.IoChange;
import org.raspinloop.emulator.hardwareemulation.IoTimedChange;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Primary
public class GpiostateOutboundAdapter extends AbstractMessageHandler {

	
	private SimulatedClock clock;
	private Sinks.Many<IoTimedChange> emulatorChangesSink;

	public GpiostateOutboundAdapter(@Autowired SimulatedClock clock) {
		super();
		this.clock = clock;
		emulatorChangesSink = Sinks.many().multicast().onBackpressureBuffer();			
	}

	public Flux<IoTimedChange> publishIoChanges(){
		return emulatorChangesSink.asFlux();
	}

	@Override
	protected void handleMessageInternal(Message<?> message) {
		IoChange change = IoChange.class.cast(message.getPayload());
		emulatorChangesSink.emitNext(IoTimedChange.timeTag(change, clock.instant()), FAIL_FAST);
	}
}
