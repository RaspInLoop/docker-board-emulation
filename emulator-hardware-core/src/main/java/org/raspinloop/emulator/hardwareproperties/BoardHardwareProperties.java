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

public interface BoardHardwareProperties extends HardwareProperties, I2CParentProperties, UARTParentProperties, SPIParentProperties, GPIOHardwareProperties{	

	public abstract Collection<Pin> getUsedByCompPins();

	public abstract void addGPIOComponent(BoardExtentionHardwareProperties ext) throws AlreadyUsedPin;

	public abstract void removeGPIOComponent(BoardExtentionHardwareProperties ext);

	public abstract Collection<BoardExtentionHardwareProperties> getGPIOComponents();

	/* return BoardExtension, i2c, spi and uarts
	 * 
	 */
	public Collection<HardwareProperties> getAllComponents();

}
