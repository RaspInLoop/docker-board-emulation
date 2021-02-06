package org.raspinloop.emulator.proxyserver.simulation.time;

import lombok.Value;

/**
 * Message that indicates a thread in emulator that is waiting for secondes and nanoseconds
 * This wait is blocking on emulator and wait for a STEP message. 
 * 
 * @author Frédéric Mahiant
 *
 */
@Value
public class ErrorMessage implements SimulatedTimeMessage {

	String reason;	
	
	public static SimulatedTimeMessage parse(String message) {
		if (! message.startsWith("ERROR")) {
			return null;
		}		
		try {
			return new ErrorMessage(message.substring("ERROR".length()));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public String toString() {
		return String.format("ERROR %s", reason); 
	}
	
}
