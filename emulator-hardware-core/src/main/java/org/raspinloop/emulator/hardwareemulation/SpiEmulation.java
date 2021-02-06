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
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SpiEmulation {
	// Singleton
	INST;

	private SpiDeviceEmulation[] spiDevices = new SpiDeviceEmulation[SpiChannel.values().length];
		
	public static SpiDevice getInstance(SpiChannel channel) {
		return INST.get(channel);
	}
	
	public static SpiDevice getInstance(SpiChannel channel, Integer speed, SpiMode mode ) {
		SpiDeviceEmulation instance = INST.get(channel);
		instance.setMode(mode);		
		instance.setSpeed(speed);
		return instance;
	}
	
	public static SpiDevice getInstance(SpiChannel channel, SpiMode mode) {
		SpiDeviceEmulation instance = INST.get(channel);
		instance.setMode(mode);		
		return instance;
	}
	
	public static SpiDevice getInstance(SpiChannel channel, Integer speed) {
		SpiDeviceEmulation instance = INST.get(channel);
		instance.setSpeed(speed);	
		return instance;
	}

	private SpiDeviceEmulation get(SpiChannel channel) {
		if( spiDevices[channel.getChannel()] == null)
			spiDevices[channel.getChannel()] = new SpiDeviceEmulation(this, channel);
		return spiDevices[channel.getChannel()];
	}

	
	Map<Short, SpiCompHardwareEmulation> devOnChannel = new HashMap<>();
	
	public void registerHwEmulation(short channel, SpiCompHardwareEmulation hw){
		devOnChannel.put(channel, hw);
	}
	
	public int wiringPiSPIDataRW(short channel, byte[] buffer) {
		try {
			if (devOnChannel.containsKey(channel))
				return devOnChannel.get(channel).spiDataRW(buffer);
		} catch (Exception e) {
			log.error("cannot write on SPI #{}", channel );
		}
		return 0;
	}
			
	public int wiringPiSPIDataRW(short channel, short[] buffer) {
		try {
			if (devOnChannel.containsKey(channel))
				return devOnChannel.get(channel).spiDataRW(buffer);
		} catch (Exception e) {
			log.error("cannot write on SPI #{}", channel );
		}
		return 0;
	}
	
	

}
