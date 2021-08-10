package org.raspinloop.emulator.proxyserver.messaging;

import lombok.Value;

@Value
public class GpioAction {

	private Action action;
	private byte bit;

	public static GpioAction set(byte bit) {
		return new GpioAction(Action.SET, bit);
	}

	public static GpioAction clear(byte bit) {
		return new GpioAction(Action.CLEAR, bit);
	}
	
	public enum Action {
		SET, CLEAR;
	}

}
