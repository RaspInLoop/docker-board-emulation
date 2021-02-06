package org.raspinloop.emulator.proxyserver.qemu;

import java.util.Arrays;
import java.util.List;

import org.raspinloop.orchestrator.api.EmulatorParam;
import org.raspinloop.orchestrator.api.Raspberrypi3bOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class QemuRaspberry3bCommand implements QemuCommand {

	@Getter
	private Raspberrypi3bOptions parameter; // parameters configurable from orchestrator
	
	@Override
	public void setParameter(EmulatorParam param) {
		if (param instanceof Raspberrypi3bOptions) {
		parameter = (Raspberrypi3bOptions)param;		
		}
		else {
			throw new IllegalArgumentException(param.getClass() + "cannot be set to " + this.getClass());
		}
	}
	
	@Value("${raspinloop.qemu.path:qemu-system-aarch64}")
	private String qemuPath;
	
	@Getter
	@Value("${raspinloop.qemu.ssh.port:5555}")
	private short sshPort; // internal (used only between proxy and qemu, no need to be configurable from orchestrator
	
	@Getter
	@Value("${raspinloop.qemu.simultime.port:5554}")
	private short simulTimeSocketPort; // internal (used only between proxy and qemu, no need to be configurable from orchestrator
	
	@Getter
	@Value("${raspinloop.qemu.inboundzmq.port:5556}")
	private short inboundZmqPort; // internal (used only between proxy and qemu, no need to be configurable from orchestrator
	
	@Getter
	@Value("${raspinloop.qemu.outboundzmq.port:5557}")
	private short outboundZmqPort; // internal (used only between proxy and qemu, no need to be configurable from orchestrator
	
	@Value("${raspinloop.qemu.raspberry3b.kernelpath:bootpart_raspios/kernel8.img}")	
	private String kernelPath;
	
	@Value("${raspinloop.qemu.raspberry3b.dtbpath:bootpart_raspios/bcm2710-rpi-3-b2.dtb}")	
	private String dtbPath;

	public Class<? extends EmulatorParam> getParamType() {
		return Raspberrypi3bOptions.class;
	}
	
	@Override
	public List<String> toCommandList() {
		if (parameter == null) {
			throw new RuntimeException("parameter not set.");
		}
		return Arrays.asList(
				qemuPath, 
				"-kernel", kernelPath,
				"-dtb", dtbPath,
				"-M", "raspi3",
				"-m", "1024",
				"-append", "root=/dev/mmcblk0p2 rw rootwait rootfstype=ext4",
				"-sd", parameter.getImage(),
				"-device", "usb-net,netdev=net0",
				"-netdev", String.format("user,id=net0,hostfwd=tcp::%d-:22", sshPort),
				"-chardev", String.format("socket,id=sock0,host=0.0.0.0,port=%d,server,nowait,telnet", simulTimeSocketPort),
				"-device", "usb-serial,chardev=sock0",
				"-nographic");
	}


}
