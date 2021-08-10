package org.raspinloop.emulator.hardwareemulation.dummy;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.raspinloop.emulator.hardwareproperties.BoardExtentionHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinImpl;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DummyBoardExtensionProperties implements BoardExtentionHardwareProperties {

	private String componentName="My Dummy Hardware Extention";
	final private String type ="Hardware_pluged_in_pin_header";
	final private String guid = "9999-9999-9999";
	final private String implementationClassName = "org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtension" ;
	final private String simulatedProviderName ="Dummy Hardware Extention";
	// need to be transient or called parent to prevent any loop in json
	private transient BoardHardwareProperties parentComponent;

	private double coeficient = 3.14; // sample parameters

	@Override
	public Collection<Pin> getRequiredPins() {
		if (parentComponent == null) {
			throw new RuntimeException("Parent must be set before querying for required pin");
		}
		return parentComponent.getSupportedPins().stream()
		.filter(this::isConnected)
		.collect(Collectors.toList());
	}
	
	private boolean isConnected(Pin pin) {
		return pin.getName().equals("IO3") || pin.getName().equals("IO4");
	}
}
