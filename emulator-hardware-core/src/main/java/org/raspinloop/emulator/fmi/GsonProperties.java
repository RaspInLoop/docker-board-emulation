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
package org.raspinloop.emulator.fmi;

import java.util.Collection;

import org.raspinloop.emulator.hardwareproperties.BoardExtentionHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;
import org.raspinloop.emulator.hardwareproperties.I2CComponentProperties;
import org.raspinloop.emulator.hardwareproperties.I2CParentProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinImpl;
import org.raspinloop.emulator.hardwareproperties.SPIComponentProperties;
import org.raspinloop.emulator.hardwareproperties.SPIParentProperties;
import org.raspinloop.emulator.hardwareproperties.UARTComponentProperties;
import org.raspinloop.emulator.hardwareproperties.UARTParentProperties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

public class GsonProperties {

	

	private Gson gsonExt;
	
	public GsonProperties(HardwareEnumerator enumerator) {		
		
		GsonBuilder builder = new GsonBuilder();

		Collection<HardwareProperties> boards = enumerator.buildListImplementing(BoardHardwareProperties.class);

		builder = registerImpl(boards, builder, BoardHardwareProperties.class);
		builder = registerImpl(enumerator.buildListImplementing(BoardExtentionHardwareProperties.class), builder, BoardExtentionHardwareProperties.class);
		builder = registerImpl(enumerator.buildListImplementing(UARTComponentProperties.class), builder, UARTComponentProperties.class);
		builder = registerImpl(enumerator.buildListImplementing(I2CComponentProperties.class), builder, I2CComponentProperties.class);
		builder = registerImpl(enumerator.buildListImplementing(SPIComponentProperties.class), builder, SPIComponentProperties.class);
		InstanceCreator<PinImpl> creator =  type -> new PinImpl();
		builder.registerTypeAdapter(Pin.class, creator);
		
		gsonExt = builder.setPrettyPrinting().create();
	}

	@SuppressWarnings("unchecked")
	private <T> GsonBuilder registerImpl(Collection<HardwareProperties> objects, GsonBuilder builder, Class<T> type) {
		RuntimeTypeAdapterFactory<T> typeFactory = RuntimeTypeAdapterFactory.of(type, "java_type");

		for (HardwareProperties obj : objects) {
			if (type.isInstance(obj)) {
				typeFactory.registerSubtype((Class<? extends T>)obj.getClass(), obj.getClass().getName());
			}
		}
		return builder.registerTypeAdapterFactory(typeFactory);
	}

	public String write(BoardHardwareProperties hd) {
		return gsonExt.toJson(hd, hd.getClass());
	}

	public BoardHardwareProperties read(String json) {
		BoardHardwareProperties board =  gsonExt.fromJson(json, BoardHardwareProperties.class);		
		restoreParentLink(board); // because parent field is ignored in json serialisation
		return board;
	}

	private void restoreParentLink(BoardHardwareProperties board) {
		for (BoardExtentionHardwareProperties extention : board.getGPIOComponents()) {
			extention.setParentComponent(board);
			if (extention instanceof I2CParentProperties)
				restoreParentLink((I2CParentProperties)extention);
			if (extention instanceof UARTParentProperties)
				restoreParentLink((UARTParentProperties)extention);
			if (extention instanceof SPIParentProperties)
				restoreParentLink((SPIParentProperties)extention);
		}		
		if (board instanceof I2CParentProperties)
			restoreParentLink((I2CParentProperties)board);
		if (board instanceof UARTParentProperties)
			restoreParentLink((UARTParentProperties)board);
		if (board instanceof SPIParentProperties)
			restoreParentLink((SPIParentProperties)board);
	}
	
	private void restoreParentLink(SPIParentProperties extention) {
		for (SPIComponentProperties child : extention.getSPIComponent()) {
			if (child instanceof I2CParentProperties)
				restoreParentLink((I2CParentProperties)child);
			if (child instanceof UARTParentProperties)
				restoreParentLink((UARTParentProperties)child);
			if (child instanceof SPIParentProperties)
				restoreParentLink((SPIParentProperties)child);
		}		
	}

	private void restoreParentLink(UARTParentProperties extention) {
		for (UARTComponentProperties child : extention.getUARTComponent()) {
			if (child instanceof I2CParentProperties)
				restoreParentLink((I2CParentProperties)child);
			if (child instanceof UARTParentProperties)
				restoreParentLink((UARTParentProperties)child);
			if (child instanceof SPIParentProperties)
				restoreParentLink((SPIParentProperties)child);
		}		
	}

	private void restoreParentLink(I2CParentProperties i2cParent){
		for (I2CComponentProperties child : i2cParent.getI2CComponent()) {
			if (child instanceof I2CParentProperties)
				restoreParentLink((I2CParentProperties)child);
			if (child instanceof UARTParentProperties)
				restoreParentLink((UARTParentProperties)child);
			if (child instanceof SPIParentProperties)
				restoreParentLink((SPIParentProperties)child);
		}				
	}
}
