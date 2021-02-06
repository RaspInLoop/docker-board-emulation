package org.raspinloop.emulator.proxyserver.messaging;

import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeAdapter;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Primary
public class SimulatedTimeOutboundAdapter extends AbstractMessageHandler {

	public SimulatedTimeOutboundAdapter( @Autowired SimulatedTimeAdapter simulatedTimeAdapter) {
		super();
		this.simulatedTimeAdapter = simulatedTimeAdapter;
	}
	private SimulatedTimeAdapter simulatedTimeAdapter;
	
	@Override
	protected void handleMessageInternal(Message<?> message) {
		SimulatedTimeMessage change = SimulatedTimeMessage.class.cast(message.getPayload());
		simulatedTimeAdapter.accept(change);		
	}
}
