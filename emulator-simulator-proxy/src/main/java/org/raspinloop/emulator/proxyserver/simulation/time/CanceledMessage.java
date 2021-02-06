package org.raspinloop.emulator.proxyserver.simulation.time;

import lombok.Value;

/**
 * 
 * @author Frédéric Mahiant
 *
 */
@Value
public class CanceledMessage implements SimulatedTimeMessage {

	
	public static SimulatedTimeMessage parse(String message) {
		if (! message.contains("CANCELED")) {
			return null;
		}
		return new CanceledMessage();
	}

	public String toString() {
		return String.format("CANCELED"); 
	}
	
}
