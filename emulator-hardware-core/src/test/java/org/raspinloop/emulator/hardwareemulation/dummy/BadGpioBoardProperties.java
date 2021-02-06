package org.raspinloop.emulator.hardwareemulation.dummy;

import java.util.Collection;

import org.raspinloop.emulator.hardwareproperties.AbstractBoardHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinImpl;

public class BadGpioBoardProperties extends AbstractBoardHardwareProperties {

	public BadGpioBoardProperties(String argThatdoNotRespectSpecification) {
		super(GUID);
	}

	private static final String TYPE = "TEST BAD BOARD";
	private static final String GUID = "1234-1234-1234";
	private static final String NAME = "Badly implemented board used for test";
	private static final String SIMULATED_PROVIDER_NAME = "BAD board Provider";

	Pin pin1 = new PinImpl("Test Board", "I0", (short) 0);
	Pin pin2 = new PinImpl("Test Board", "I1", (short) 1);
	Pin pin3 = new PinImpl("Test Board", "O0", (short) 2);
	private String componentName = NAME;

	@Override
	public String getGuid() {
		return GUID;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	@Override
	public HardwareProperties setComponentName(String string) {
		componentName = string;
		return this;
	}

	@Override
	public String getImplementationClassName() {
		return DummyGpioBoard.class.getName();
	}

	@Override
	public String getSimulatedProviderName() {
		return SIMULATED_PROVIDER_NAME;
	}

	@Override
	public Collection<Pin> getI2CPins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Pin> getSpiPins() {
		// TODO Auto-generated method stub
		return null;
	}
}
