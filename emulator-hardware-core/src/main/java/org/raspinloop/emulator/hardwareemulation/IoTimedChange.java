package org.raspinloop.emulator.hardwareemulation;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString 
@EqualsAndHashCode(callSuper=true)

public class IoTimedChange extends IoChange {

	private Instant timeStamp;

	private IoTimedChange(IoChange change, Instant timeStamp) {
		super(change.getChange(), change.getBit());
		this.timeStamp = timeStamp;
	}
	
	public static IoTimedChange timeTag(IoChange change, Instant timeStamp) {
		return new IoTimedChange(change, timeStamp);
	}

}
