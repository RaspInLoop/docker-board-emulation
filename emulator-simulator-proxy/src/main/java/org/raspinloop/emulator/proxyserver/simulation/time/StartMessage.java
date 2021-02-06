package org.raspinloop.emulator.proxyserver.simulation.time;

import lombok.Value;

/**
 * 
 * @author Frédéric Mahiant
 *
 */
@Value
public class StartMessage implements SimulatedTimeMessage {

	
	public static SimulatedTimeMessage parse(String message) {
		if (! message.contains("START")) {
			return null;
		}
		return new StartMessage();
	}

	public String toString() {
		return String.format("START"); 
	}
	
}
