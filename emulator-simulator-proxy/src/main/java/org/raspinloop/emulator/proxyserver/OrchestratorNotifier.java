package org.raspinloop.emulator.proxyserver;

import org.raspinloop.emulator.proxyserver.fmi.FmiStatusAware;
import org.raspinloop.emulator.proxyserver.qemu.QemuStartInfo;
import org.raspinloop.emulator.proxyserver.qemu.QemuStatusAware;
import org.raspinloop.orchestrator.api.Launched;
import org.raspinloop.orchestrator.api.ReadyForSimulation;
import org.raspinloop.orchestrator.api.SimulationStopped;
import org.raspinloop.orchestrator.api.Started;
import org.raspinloop.orchestrator.api.Stopped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;

@Component
public class OrchestratorNotifier implements QemuStatusAware, FmiStatusAware {

	@Autowired
	RestTemplate orchestratorRestTemplate;

	@Getter
	@Setter
	private String emulatorId;
	
	@Override
	public void onLaunched(QemuStartInfo info) {
		orchestratorRestTemplate.postForObject("/emulator/{emulatorId}/status", new Launched(info.getStartDuration()), Void.class,
				getEmulatorId());		
	}

	@Override
	public void onStopping() {
		orchestratorRestTemplate.patchForObject("/emulator/{emulatorId}/status", new Stopped(),
				Void.class, getEmulatorId());
	}
		
	public void onStarted() {
		orchestratorRestTemplate.patchForObject("/emulator/{emulatorId}/status", new Started(),
				Void.class, getEmulatorId());
	}

	@Override
	public void onSimulationWaiting() {
		orchestratorRestTemplate.patchForObject("/emulator/{emulatorId}/status", new ReadyForSimulation(),
				Void.class, getEmulatorId());
	}
	
	@Override
	public void onSimulationStopped() {
		orchestratorRestTemplate.patchForObject("/emulator/{emulatorId}/status", new SimulationStopped(),
				Void.class, getEmulatorId());
	}	
}
