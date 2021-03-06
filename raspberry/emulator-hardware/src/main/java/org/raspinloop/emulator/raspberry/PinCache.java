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
package org.raspinloop.emulator.raspberry;

import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinMode;
import org.raspinloop.emulator.hardwareproperties.PinPullResistance;
import org.raspinloop.emulator.hardwareproperties.PinState;

public class PinCache {
	
private Pin pin;
private PinMode mode;
private PinState state;
private PinPullResistance resistance;
private int pwmValue;

public PinCache(Pin pin) {
	this.pin = pin;	
}

public PinMode getMode() {
	return mode;
}

public void setState(PinState state) {
	this.state = state;
}

public PinState getState() {
	return state;
}

public void setResistance(PinPullResistance resistance) {
	this.resistance = resistance;	
}

public PinPullResistance getResistance() {
	// TODO Auto-generated method stub
	return null;
}

public void setPwmValue(int pwmValue) {
	this.pwmValue = pwmValue;
}

public int getPwmValue() {
	return pwmValue;
}


}
