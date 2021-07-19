package org.raspinloop.emulator.proxyserver.messaging;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.raspinloop.emulator.proxyserver.OrchestratorNotifier;
import org.raspinloop.emulator.proxyserver.qemu.QemuProcess;
import org.raspinloop.emulator.proxyserver.simulation.Board;
import org.raspinloop.orchestrator.api.EmulatorStartMessage;
import org.raspinloop.orchestrator.api.Started;
import org.raspinloop.orchestrator.api.Starting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisReceiver {

	@Autowired
	private ConfigurableApplicationContext context;

	@Value("${raspinloop.qemu.start.timeout:30}")
	int startTimeout;

	@Autowired
	QemuProcess qemu;
	
	@Autowired
	OrchestratorNotifier orchestratorNotifier;
	
	@Autowired
	Board board;

	    public void receiveMessage(EmulatorStartMessage message) {
	        log.info("REDIS: EmulatorStartMessage Received.");
	        log.trace("message: {}", message);	
	        try {
				String emulatorId = InetAddress.getLocalHost().getHostName();		
				board.setHardwareProperties(message.getHardwareProperties());
				orchestratorNotifier.onStarted();
					} catch (RestClientException e) {
				log.error("Cannot notify Orchestrator: {}", e.getMessage());
				log.error("Discarding Message {}", message);
			} catch (UnknownHostException e) {
				log.error("Unable to get hostname");
			}						
	    }}