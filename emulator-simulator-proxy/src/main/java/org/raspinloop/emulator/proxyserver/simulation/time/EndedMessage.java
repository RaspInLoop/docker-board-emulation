package org.raspinloop.emulator.proxyserver.simulation.time;

import lombok.Value;

/**
 * 
 * @author Frédéric Mahiant
 *
 */
@Value
public class EndedMessage implements SimulatedTimeMessage {

	
	public static SimulatedTimeMessage parse(String message) {
		if (! message.contains("ENDED")) {
			return null;
		}
		return new EndedMessage();
	}

	public String toString() {
		return String.format("ENDED"); 
	}
	
}
