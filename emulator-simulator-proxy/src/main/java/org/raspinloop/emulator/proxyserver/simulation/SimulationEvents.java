package org.raspinloop.emulator.proxyserver.simulation;

public enum SimulationEvents {
	FMU_INSTANCIATE,
	FMU_SET_VALUE,
	FMU_DO_STEP,
	FMU_GET_VALUE,
	STOP,
	FMU_TERMINATE_RESULT,
	START
}
