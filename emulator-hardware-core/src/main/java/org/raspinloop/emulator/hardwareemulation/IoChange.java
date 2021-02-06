package org.raspinloop.emulator.hardwareemulation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter 
@AllArgsConstructor 
@ToString 
@EqualsAndHashCode
public class IoChange {

	private Change change;
	private byte bit;
	
	public static IoChange set(byte bit) {
		return new IoChange(Change.SET, bit);
	}

	public static IoChange clear(byte bit) {
		return new IoChange(Change.CLEAR, bit);
	}
	
	public enum Change {
		SET, CLEAR;
	}

}