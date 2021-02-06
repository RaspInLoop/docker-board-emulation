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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public abstract class  AbstractBoardHardwareProperties implements BoardHardwareProperties {	
	
	private String guid;

	protected AbstractBoardHardwareProperties(String guid) {

		this.setGuid(guid);
	}
	protected List<Pin> supportedPins = new ArrayList<>();
	protected List<Pin> unusedPins = new ArrayList<>();
	protected List<PinImpl> outputPins = new ArrayList<>();
	protected List<PinImpl> inputPins = new ArrayList<>();
	protected List<BoardExtentionHardwareProperties> extentionComponents = new ArrayList<>();
	protected List<UARTComponentProperties> uartComponents = new ArrayList<>();
	protected List<SPIComponentProperties> spiComponents = new ArrayList<>();
	protected List<I2CComponentProperties> i2cComponents = new ArrayList<>();

	boolean usedByComp(Pin pin) {
		if (pin == null)
			return false;
		
		for (HardwareProperties simulatedComponent : getAllComponents()) {
			for (Pin usedpin : simulatedComponent.getRequiredPins()) {
				if (usedpin != null && usedpin.equals(pin))
					return true;
			}			
		}
	
		return false;
	}
	@Override
	public Collection<Pin> getSupportedPins() {
		return Collections.<Pin>unmodifiableCollection(supportedPins);
	}
	
	@Override
	public void useInputPin(Pin pin) throws AlreadyUsedPin {
		if (usedByComp(pin))
			throw new AlreadyUsedPin(pin);

		inputPins.add(new PinImpl(pin));
		unusedPins.remove(pin);
	}

	@Override
	public void useOutputPin(Pin pin) throws AlreadyUsedPin {
		if (usedByComp(pin))
			throw new AlreadyUsedPin(pin);
		outputPins.add(new PinImpl(pin));
		unusedPins.remove(pin);
	}

	@Override
	public void unUsePin(Pin pin) {
		if (pin == null)
			return;
		outputPins.remove(pin);
		inputPins.remove(pin);
		unusedPins.add(pin);
	}

	public void usePin(HardwareProperties comp) {
		comp.getRequiredPins().forEach( unusedPins::remove);
	}
	
	public void unUsePin(HardwareProperties comp) {
		comp.getRequiredPins().forEach(unusedPins::add);
	}
	
	@Override
	public void addGPIOComponent(BoardExtentionHardwareProperties sd ) {
		extentionComponents.add(sd);
		sd.setParentComponent(this);
		usePin(sd);
	}

	@Override
	public void removeGPIOComponent(BoardExtentionHardwareProperties sd) {
		extentionComponents.remove(sd);
		unUsePin(sd);
	}

	@Override
	public Collection<Pin> getInputPins() {
		return Collections.<Pin>unmodifiableCollection(inputPins);
	}

	@Override
	public Collection<Pin> getOutputPins() {
		return Collections.<Pin>unmodifiableCollection(outputPins);
	}

	@Override
	public Collection<Pin> getUsedByCompPins() {
		LinkedList<Pin> usedPins = new LinkedList<>();
		
		for (HardwareProperties simulatedComponent : getAllComponents()) {
			usedPins.addAll(simulatedComponent.getRequiredPins());		
		}		
		
		return Collections.unmodifiableCollection(usedPins);
	}
	
	@Override
	public Collection<Pin> getUnUsedPins() {
		return Collections.<Pin>unmodifiableCollection(unusedPins);
	}
	
	
	@Override
	public Collection<BoardExtentionHardwareProperties> getGPIOComponents() {
		return Collections.unmodifiableCollection(extentionComponents);
	}

	@Override
	public Collection<I2CComponentProperties> getI2CComponent() {
		return Collections.unmodifiableCollection(i2cComponents);		
	}

	@Override
	public void addComponent(I2CComponentProperties comp) {
		i2cComponents.add(comp);
		usePin(comp);
	}

	@Override
	public void removeComponent(I2CComponentProperties comp) {
		i2cComponents.remove(comp);
		unUsePin(comp);
	}

	@Override
	public Collection<UARTComponentProperties> getUARTComponent() {
		return Collections.unmodifiableCollection(uartComponents);
	}

	@Override
	public void addComponent(UARTComponentProperties comp) {
		uartComponents.add(comp);
		usePin(comp);
	}

	@Override
	public void removeComponent(UARTComponentProperties comp) {
		uartComponents.remove(comp);
		unUsePin(comp);
	}

	@Override
	public Collection<SPIComponentProperties> getSPIComponent() {
		return Collections.unmodifiableCollection(spiComponents);
	}

	@Override
	public void addComponent(SPIComponentProperties comp) {
		spiComponents.add(comp);
		usePin(comp);
	}

	@Override
	public void removeComponent(SPIComponentProperties comp) {
		spiComponents.remove(comp);
		unUsePin(comp);
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public Collection<HardwareProperties> getAllComponents() {
		HashSet<HardwareProperties> col = new HashSet<>(getGPIOComponents());
		col.addAll(getSPIComponent());
		col.addAll(getI2CComponent());
		col.addAll(getUARTComponent());
		return col;
	}

	@Override
	public Collection<Pin> getRequiredPins() {
		return getUsedByCompPins();
	}
}
