package org.raspinloop.emulator.hardwareemulation;

@SuppressWarnings("serial")
public class HardwareEmulationException extends Exception {

	public HardwareEmulationException(String string) {
		super(string);
	}

	public HardwareEmulationException(Exception e) {
		super(e);
	}

}
