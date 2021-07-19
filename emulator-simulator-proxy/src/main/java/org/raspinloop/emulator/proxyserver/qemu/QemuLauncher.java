package org.raspinloop.emulator.proxyserver.qemu;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeoutException;

import org.raspinloop.emulator.proxyserver.OrchestratorNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QemuLauncher implements ApplicationRunner {

	@Value("${raspinloop.qemu.start.timeout:30}")
	int startTimeout;

	@Autowired
	private ConfigurableApplicationContext context;
	
	@Autowired
	QemuProcess qemu;
	
	@Autowired
	RestTemplate orchestratorRestTemplate;
	
	@Autowired
	OrchestratorNotifier orchestratorNotifier;
	
	@Autowired
	QemuCommandFactory qemuCommandFactory;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {				
			String emulatorId = InetAddress.getLocalHost().getHostName();
			orchestratorNotifier.setEmulatorId(emulatorId);

			String param = args.getOptionValues("param").get(0);
			ObjectMapper mapper = new ObjectMapper();
			EmulatorParam parameters = mapper.readValue(param, EmulatorParam.class);
			QemuCommand command = qemuCommandFactory.buildFrom(parameters);
			qemu.setCommand(command);			
			qemu.copyImageLocally();
			qemu.startAndWaitInBackground(startTimeout);
		} catch (RestClientException e) {
			log.error("Cannot notify Orchestrator: {}", e.getMessage());
		}			
		catch ( IOException | TimeoutException e) {
			log.error("Application stop! Cannot start QEMU: {}", e.getMessage());
			System.exit(SpringApplication.exit(context));
		}
	}

}
