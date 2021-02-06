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

import java.util.Arrays;
import java.util.Collection;

import org.raspinloop.emulator.hardwareproperties.AbstractBoardHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;


public class RaspiGpioSimulatorProperties extends AbstractBoardHardwareProperties {

	public RaspiGpioSimulatorProperties( ) {
		super(GUID);
		supportedPins = Arrays.asList(RaspiPin.allPins());
	}

	public static final String TYPE = "raspberryPiGpioSimulator";
	public static final String DISPLAY_NAME = "RaspberryPi GPIO Simulator";
	public static final String SIMULATED_PROVIDER_NAME = "RaspberryPi GPIO Provider";

	public static final String GUID = "{5571c639-6438-4eee-839e-ff8442e3bbbc}"; 
	
	static Collection<Pin> spiPins = Arrays.asList(RaspiPin.GPIO_10, RaspiPin.GPIO_11, RaspiPin.GPIO_12, RaspiPin.GPIO_13, RaspiPin.GPIO_14);			
	static Collection<Pin> i2cPins  = Arrays.asList(RaspiPin.GPIO_08, RaspiPin.GPIO_09);
	static Collection<Pin> uartPins  =  Arrays.asList(RaspiPin.GPIO_15, RaspiPin.GPIO_16);
	
	private String componentName = DISPLAY_NAME;
	
	@Override
	public String getComponentName() {
		return componentName ;
	}

	@Override
	public HardwareProperties setComponentName(String string) {
		this.componentName = string;
		return this;
	}

	@Override
	public String getType() {		
		return TYPE;
	}

	@Override
	public String getImplementationClassName() {		
		return RaspiGpioSimulator.class.getName();
	}

	@Override
	public String getSimulatedProviderName() {
		return SIMULATED_PROVIDER_NAME;
	}

	@Override
	public Collection<Pin> getSpiPins() {
		return spiPins;
	}


	@Override
	public Collection<Pin> getI2CPins() {		
		return i2cPins;
	}



}
