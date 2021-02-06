package org.raspinloop.emulator.proxyserver;

import org.raspinloop.emulator.proxyserver.fmi.FmiStatusAware;
import org.raspinloop.emulator.proxyserver.qemu.QemuStartInfo;
import org.raspinloop.emulator.proxyserver.qemu.QemuStatusAware;
import org.raspinloop.orchestrator.api.ReadyForSimulation;
import org.raspinloop.orchestrator.api.SimulationStopped;
import org.raspinloop.orchestrator.api.Started;
import org.raspinloop.orchestrator.api.Stopped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrchestratorNotifier implements QemuStatusAware, FmiStatusAware {

	@Autowired
	RestTemplate orchestratorRestTemplate;

	private String emulatorId;
	
	@Override
	public void onStarted(QemuStartInfo info) {
		orchestratorRestTemplate.patchForObject("/emulator/{emulatorId}/status", new Started(info.getStartDuration()), Void.class,
				getEmulatorId());		
	}

	@Override
	public void onStopped() {
		orchestratorRestTemplate.patchForObject("/emulator/{emulatorId}/status", new Stopped(),
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

	public String getEmulatorId() {
		return emulatorId;
	}

	public void setEmulatorId(String emulatorId) {
		this.emulatorId = emulatorId;
	}


	
}
