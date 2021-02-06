package org.raspinloop.emulator.proxyserver;

import org.raspinloop.emulator.proxyserver.qemu.QemuStartInfo;
import org.raspinloop.emulator.proxyserver.qemu.QemuStatusAware;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.stereotype.Component;

@Component
public class SimulatedTimeListenerStarter implements QemuStatusAware {

	@Autowired TcpReceivingChannelAdapter tcpReceivingChannelAdapter;
	@Autowired SimulatedClock simulatedClock;
	
	@Override
	public void onStarted(QemuStartInfo info) {
		tcpReceivingChannelAdapter.start();	
		simulatedClock.startListening();
	}

	@Override
	public void onStopped() {
		tcpReceivingChannelAdapter.stop();
	}
	
}
