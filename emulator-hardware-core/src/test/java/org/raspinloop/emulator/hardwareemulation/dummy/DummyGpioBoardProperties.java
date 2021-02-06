package org.raspinloop.emulator.hardwareemulation.dummy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.raspinloop.emulator.hardwareproperties.AbstractBoardHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinImpl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DummyGpioBoardProperties extends AbstractBoardHardwareProperties {

	public DummyGpioBoardProperties() {
		super("1234-1234-1234");
		supportedPins = List.of(new PinImpl(simulatedProviderName, "IO0", (short) 0),
				new PinImpl(simulatedProviderName, "IO1", (short) 1),
				new PinImpl(simulatedProviderName, "IO2", (short) 2),
				new PinImpl(simulatedProviderName, "IO3", (short) 3),
				new PinImpl(simulatedProviderName, "IO4", (short) 4),
				new PinImpl(simulatedProviderName, "IO5", (short) 5));
		supportedPins.forEach(this::unUsePin);
	}

	private final String type = "TEST DUMY BOARD";
	private String componentName = "Dummy board used for test";
	private final String simulatedProviderName = "Dummy board Provider";

	@Override
	public String getImplementationClassName() {
		return DummyGpioBoard.class.getName();
	}

	@Override
	public Collection<Pin> getI2CPins() {
		return Collections.emptyList();
	}

	@Override
	public Collection<Pin> getSpiPins() {
		return Collections.emptyList();
	}
}
