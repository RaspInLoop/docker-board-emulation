package org.raspinloop.emulator.proxyserver.simulation.time;

import lombok.Value;

/**
 * 
 * @author Frédéric Mahiant
 *
 */
@Value
public class StopMessage implements SimulatedTimeMessage {

	
	public static SimulatedTimeMessage parse(String message) {
		if (! message.contains("STOP")) {
			return null;
		}
		return new StopMessage();
	}

	public String toString() {
		return String.format("STOP"); 
	}
	
}
