package org.raspinloop.emulator.proxyserver;

import java.util.List;

import org.raspinloop.emulator.proxyserver.qemu.QemuProcess;
import org.raspinloop.emulator.proxyserver.simulation.Board;
import org.raspinloop.orchestrator.api.EmulatorParam;
import org.raspinloop.orchestrator.api.Starting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Launcher implements ApplicationRunner {

	@Autowired
	private ConfigurableApplicationContext context;

	@Value("${raspinloop.qemu.start.timeout:30}")
	int startTimeout;

	@Autowired
	QemuProcess qemu;
	
	@Autowired
	RestTemplate orchestratorRestTemplate;
	
	@Autowired
	OrchestratorNotifier orchestratorNotifier;
	
	@Autowired
	Board board;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			List<String> ids = args.getOptionValues("id");
			if (ids == null || ids.size() <= 0) {
				log.error("id argument must be provided");
				System.exit(SpringApplication.exit(context));
			}
			Runtime.getRuntime().addShutdownHook(qemu.getClosingThread()); 
			// Ask for qemu setup info
			String emulatorId = ids.get(0);
			EmulatorParam param = orchestratorRestTemplate.postForObject("/emulator/{emulatorId}/status", new Starting(),
					EmulatorParam.class, emulatorId);
			orchestratorNotifier.setEmulatorId(emulatorId);
			board.setParameter(param);
			qemu.startAndWait(param,  startTimeout);
		} catch (Exception e) {
			log.error("Application stop: {}", e.getMessage());
			System.exit(SpringApplication.exit(context));
		}
	}
}
