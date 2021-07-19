package org.raspinloop.emulator.proxyserver.qemu;

import java.util.List;

public interface QemuCommand {
	
	void setParameter(EmulatorParam param);

	Class<? extends EmulatorParam> getParamType();
	
	List<String> toCommandList();
	
	String getImage();

	short getSimulTimeSocketPort();

	short getOutboundZmqPort();

	short getInboundZmqPort();

	short getSshPort();
}
