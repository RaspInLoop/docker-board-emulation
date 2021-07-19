package org.raspinloop.emulator.proxyserver.messaging;

import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.support.management.ManageableSmartLifecycle;

import lombok.Getter;
import lombok.Setter;

public class SmartLifeCycleTcpSendingMessageHandler extends TcpSendingMessageHandler
		implements ManageableSmartLifecycle {

	@Getter
	@Setter
	private boolean autoStartup;
}
