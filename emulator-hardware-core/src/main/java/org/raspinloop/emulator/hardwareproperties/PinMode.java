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
package org.raspinloop.emulator.hardwareproperties;

import java.util.EnumSet;
import java.util.Set;


public enum PinMode {
	DIGITAL_INPUT(0, "input"), 
	DIGITAL_OUTPUT(1, "output"), 
	PWM_OUTPUT(2, "pwm_output"), 
	SOFT_PWM_OUTPUT(4, "soft_pwm_output");

	private final int value;
	private final String name;

	PinMode(int value, String name) {
		this.value = value;
		this.name = name;

	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public static Set<PinMode> allDigital() {
		return EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT);
	}

	public static Set<PinMode> all() {
		return EnumSet.allOf(PinMode.class);
	}

	public static Set<PinMode> allInputs() {
		return EnumSet.of(PinMode.DIGITAL_INPUT);
	}
	

    public static Set<PinMode> allOutput() {
        return EnumSet.of(PinMode.DIGITAL_OUTPUT,
                          PinMode.PWM_OUTPUT);
    }
}
