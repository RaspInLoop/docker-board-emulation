package org.raspinloop.emulator.proxyserver.fmi;

public interface FmiStatusAware {
	void onSimulationWaiting();
	void onSimulationStopped();
}
