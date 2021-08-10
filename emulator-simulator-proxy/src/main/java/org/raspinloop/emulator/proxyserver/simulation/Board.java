package org.raspinloop.emulator.proxyserver.simulation;

import org.raspinloop.emulator.fmi.ClassLoaderBuilderFactory;
import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.GsonProperties;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.HardwareClassFactory;
import org.raspinloop.emulator.hardwareemulation.GpioProvider;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulationException;
import org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties;
import org.raspinloop.emulator.proxyserver.messaging.GpiostateOutboundAdapter;
import org.raspinloop.emulator.proxyserver.qemu.EmulatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Board {

	private GpiostateOutboundAdapter gpioOutboundAdapter;

	public Board(@Autowired GpiostateOutboundAdapter gpioOutboundAdapter) {
		this.gpioOutboundAdapter = gpioOutboundAdapter;
	}

	@Setter
	private Object hardwareProperties;
	
	// HwEmulation is the interface used by the simulation side of proxy.
	@Getter
	private HardwareEmulation hardwareEmulation;

	// GpioProvider is the interface used by the Qemu side of proxy
	@Getter
	private GpioProvider gpioProvider;

	private boolean running = false;

	@OnStateEntry(target = "EMULATING")
	public void start() {
		if (!running) {
			running = true;
			gpioProvider.outboundIoChanges(gpioOutboundAdapter.publishIoChanges());
			log.info("Board started");
		}
	}

	@OnStateEntry(target = "STOPPING")
	public void stop() {
		running = false;
		hardwareEmulation.terminate();
		log.info("Board stopped");
	}

	// TODO improve this to prevent serialization of param.getHardwareproperties()
	// then deserialization to hardwareProperties.
	public void configure() throws JsonProcessingException, HardwareEmulationException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(hardwareProperties);
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties deserialiser = new GsonProperties(hcl);
		BoardHardwareProperties hardwareProperties = deserialiser.read(json);
		HardwareBuilderFactory hbf = new ClassLoaderBuilderFactory();
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		hardwareEmulation = hbf.createBuilder(hardwareProperties, referenceRegister).build(null);
		if (hardwareEmulation instanceof GpioProvider) {
			gpioProvider = (GpioProvider) hardwareEmulation;
		} else {
			log.error("Provided HardwareProperties: {} must have a board component that implement GpioProvider.class", hardwareEmulation);
			throw new HardwareEmulationException(
					"Provided HardwareProperties must have a board component that implement GpioProvider.class");
		}
	}

}
