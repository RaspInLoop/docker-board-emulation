package org.raspinloop.emulator.proxyserver.qemu;

import java.util.List;

import org.raspinloop.orchestrator.api.EmulatorParam;

public interface QemuCommand {
	
	void setParameter(EmulatorParam param);

	Class<? extends EmulatorParam> getParamType();

	List<String> toCommandList();

	short getSimulTimeSocketPort();

	short getOutboundZmqPort();

	short getInboundZmqPort();

	short getSshPort();
}
