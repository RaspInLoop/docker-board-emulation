package org.raspinloop.emulator.proxyserver.messaging;

import org.raspinloop.emulator.hardwareemulation.GpioProvider;
import org.raspinloop.emulator.hardwareemulation.IoChange;
import org.raspinloop.emulator.proxyserver.simulation.Board;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.messaging.Message;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class GpiostateMessageProducer extends MessageProducerSupport {

	private GpioProvider gpio;

	public GpiostateMessageProducer(Board board) {
		this.gpio = board.getGpioProvider();
	}
	
	@Override
	protected void doStart() {

		Flux<? extends Message<?>> dataFlux = gpio.publishIoChanges()		
						.map(this::toMessage)
						.publishOn(Schedulers.boundedElastic())					
						.doOnError((error) ->
								logger.error(error, () -> "Error processing i/o change message in the " + this))						
						.repeat(this::isActive);
		subscribeToPublisher(dataFlux);
	}
	
	private Message<?> toMessage(IoChange change){
		return getMessageBuilderFactory().withPayload(change).build();		
	}
	
}
