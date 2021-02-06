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

import java.util.Collection;

public interface HardwareProperties {
	
	/**
	 * 	
	 * @return the name of this instance of hardware simulator
	 */
	public String getComponentName();
	
	/**
	 * 
	 * @param the name of this instance of hardware simulator
	 */
	HardwareProperties setComponentName(String string);
	/**
	 * 
	 * @return the name of this kind of hardware simulator
	 */
	public String getType();
	
	//TODO: add some doc
	public String getGuid();
	
	/**
	 * 
	 * @return the fully qualified Class name of the implementation of this class 
	 */
	public String getImplementationClassName();
	
	/**
	 * 
	 * @return the name of the board or component which is simulated by this class
	 */
	public String getSimulatedProviderName();
	
	/**
	 * 
	 * @return the Pins used by this hardware board/extension
	 * must return a subset of Pins available from parent.getSupportedPins()
	 */
	
	public Collection<Pin> getRequiredPins();

}