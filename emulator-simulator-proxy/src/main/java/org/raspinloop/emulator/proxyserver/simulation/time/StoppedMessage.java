package org.raspinloop.emulator.proxyserver.simulation.time;

import lombok.Value;

/**
 * 
 * @author Frédéric Mahiant
 *
 */
@Value
public class StoppedMessage implements SimulatedTimeMessage {

	
	public static SimulatedTimeMessage parse(String message) {
		if (! message.contains("STOPPED")) {
			return null;
		}
		return new StoppedMessage();
	}

	public String toString() {
		return String.format("STOPPED"); 
	}
	
}
