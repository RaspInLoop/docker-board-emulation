package org.raspinloop.emulator.proxyserver.messaging;

import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeAdapter;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeMessage;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.messaging.Message;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SimulatedTimeMessageProducer extends MessageProducerSupport {


	private SimulatedClock simulatedClock;

	public SimulatedTimeMessageProducer(SimulatedClock simulatedClock) {
		this.simulatedClock = simulatedClock;
	}
	
	@Override
	protected void doStart() {

		Flux<? extends Message<?>> dataFlux = simulatedClock.publishSimulatedTimeChanges()		
						.map(this::toMessage)
						.publishOn(Schedulers.boundedElastic())					
						.doOnError((error) ->
								logger.error(error, () -> "Error processing simulatedtime change message in the " + this))						
						.repeat(this::isActive);
		subscribeToPublisher(dataFlux);
	}
	
	private Message<?> toMessage(SimulatedTimeMessage change){
		return getMessageBuilderFactory().withPayload(change).build();		
	}
	
}
