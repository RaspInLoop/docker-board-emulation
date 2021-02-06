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
public class WaitingMessage implements SimulatedTimeMessage {

	int seconds;
	int nanos;
	
	public static SimulatedTimeMessage parse(String message) {
		if (! message.startsWith("WAITING")) {
			return null;
		}
		String[] tokens = message.split("\\s");
		if (tokens.length != 3) {
			return null;
		}
		try {
			return new WaitingMessage(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public String toString() {
		return String.format("WAITING %d %d", seconds, nanos); 
	}
	
}
