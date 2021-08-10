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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulationException;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassLoaderBuilder implements HardwareBuilder {

	@Getter
	@Setter
	@Accessors(chain = true)
	private Object properties = null;

	@Getter
	@Setter
	@Accessors(chain = true)
	private HardwareBuilderFactory builderFactory;

	@Getter
	@Setter
	@Accessors(chain = true)
	private FmiReferenceRegister referenceRegister;

	private final String implementationClassName;

	public ClassLoaderBuilder(String implementationClassName) {
		this.implementationClassName = implementationClassName;
	}

	@Override
	public HardwareEmulation build(HardwareEmulation parent) throws HardwareEmulationException {
		try {
			if (parent == null) {
				return buildBoard();
			} else {
				return buildExtension(parent);
			}
		} catch (Exception e) {
			throw new HardwareEmulationException(e);
		}
	}

	private HardwareEmulation buildBoard() throws SecurityException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?>[] constructors = Class.forName(implementationClassName).getConstructors();
		Constructor<?> constructor = Stream.of(constructors)
				.filter(c -> c.getParameterCount() == 3)
				.findFirst()
				.orElseThrow();
		Object istance = constructor.newInstance(getReferenceRegister(), getBuilderFactory(),
				(HardwareProperties) getProperties());
		if (!(istance instanceof HardwareEmulation)) { // we cannot go further
			log.error("Hardware ClassLoaderBuilder : Not a {} implementaion type: {}",
					HardwareEmulation.class.getSimpleName(), implementationClassName);
			return null;
		}
		return (HardwareEmulation) istance;
	}

	private HardwareEmulation buildExtension(HardwareEmulation parent) throws SecurityException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?>[] constructors = Class.forName(implementationClassName).getConstructors();
		Constructor<?> constructor = Stream.of(constructors)
				.filter(c -> c.getParameterCount() == 2)
				.findFirst()
				.orElseThrow();
		Object istance = constructor.newInstance(parent, (HardwareProperties) getProperties());
		if (!(istance instanceof HardwareEmulation)) { // we cannot go further
			log.error("Hardware ClassLoaderBuilder : Not a {} implementaion type: {}",
					HardwareEmulation.class.getSimpleName(), implementationClassName);
			return null;
		}
		return (HardwareEmulation) istance;
	}
}
