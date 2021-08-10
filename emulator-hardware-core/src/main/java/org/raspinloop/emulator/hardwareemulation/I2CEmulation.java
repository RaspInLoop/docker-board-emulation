/*******************************************************************************
 * Copyright 2018 RaspInLoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.raspinloop.emulator.hardwareemulation;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public enum I2CEmulation {
	// Singleton
	INST;

	private HashMap<Short, I2CBusEmulation> i2cBuses = new HashMap<>();

	public static I2CBus getInstance(Object[] args) throws HardwareEmulationException {
		return INST.getBusInstance(args);
	}

	private I2CBus getBusInstance(Object[] args) throws HardwareEmulationException {
		I2CBusEmulation instance;
		if (args.length >= 1 && args[0] instanceof Short) {
			instance = get((short) args[0]);

			if ((args.length == 3) && (args[1] instanceof Long && args[2] instanceof TimeUnit)) {
				instance.setLockAquireTimeout((long) args[1], (TimeUnit) args[2]);
			}
			return instance;
		} else
			throw new HardwareEmulationException("Invalid parameter in call to I2CFactory.getInstance.");

	}

	private I2CBusEmulation get(short busNb) {
		return i2cBuses.computeIfAbsent(busNb, I2CBusEmulation::new);
	}

	public static void registerHwEmulation(short busNumber, I2CCompHardwareEmulation hw) throws HardwareEmulationException {
		if (INST.i2cBuses.containsKey(busNumber)) {
			INST.i2cBuses.get(busNumber).register(hw);
		} else
			throw new HardwareEmulationException("no bus defined on" + busNumber);
	}
}
