package org.raspinloop.emulator.proxyserver;

import org.raspinloop.emulator.proxyserver.qemu.QemuStartInfo;
import org.raspinloop.emulator.proxyserver.qemu.QemuStatusAware;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimulatedTimeListenerStarter implements QemuStatusAware {

	@Autowired TcpReceivingChannelAdapter tcpReceivingChannelAdapter;
	@Autowired SimulatedClock simulatedClock;
	
	@Autowired
	@Lazy
	MessageChannel controlBus;
	
	@Override
	public void onLaunched(QemuStartInfo info) {
		Message<String> operation = MessageBuilder.withPayload("@outcomingSimulationTimeFlow.start()").build();
		controlBus.send(operation);
		tcpReceivingChannelAdapter.start();	
		simulatedClock.startListening();
	}

	@Override
	public void onStopping() {
		tcpReceivingChannelAdapter.stop();
	}
	
}
