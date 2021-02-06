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

public class PinImpl implements Pin {

	private String provider;
	private short address;
	private String name;
	private Set<PinMode> supportedPinMode;
	private Set<PinPullResistance> supportedResistance;

	public PinImpl(String providerName, short address, String name, Set<PinMode> modes,
			Set<PinPullResistance> resistance) {
		super();
		this.provider = providerName;
		this.name = name;
		this.address = address;
		this.supportedPinMode = modes;
		this.supportedResistance = resistance;
	}

	public PinImpl(String provider, short address, String name, Set<PinMode> modes) {
		this(provider, address, name, modes, EnumSet.noneOf(PinPullResistance.class));
	}

	public PinImpl(String provider, String name, short address) {
		this(provider, address, name, EnumSet.noneOf(PinMode.class));
	}

	public PinImpl(Pin v) {
		this(v.getProvider(), v.getName(), v.getAddress());
	}

	public PinImpl() {
		this("", "", (short) 0);
	}

	@Override
	public String toString() {
		return "PinImpl [name=" + name + ", provider=" + provider + ", address=" + address + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + address;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PinImpl other = (PinImpl) obj;
		if (address != other.address)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#getProvider()
	 */
	@Override
	public String getProvider() {
		return provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#getAddress()
	 */
	@Override
	public short getAddress() {
		return address;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#getSupportedPinModes()
	 */
	@Override
	public Set<PinMode> getSupportedPinModes() {
		return supportedPinMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#getSupportedPinPullResistance()
	 */
	@Override
	public Set<PinPullResistance> getSupportedPinPullResistance() {
		return supportedResistance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#setProvider(java.lang.String)
	 */
	@Override
	public void setProvider(String provider) {
		this.provider = provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#setAddress(int)
	 */
	@Override
	public void setAddress(short address) {
		this.address = address;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raspinloop.emulator.emulator.config.SerializablePin#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

}
