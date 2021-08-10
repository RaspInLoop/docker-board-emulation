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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.raspinloop.emulator.hardwareproperties.HardwareProperties;
import org.reflections.Reflections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HardwareClassFactory implements HardwareEnumerator {

	public static HardwareClassFactory instance(){
		if (instance == null)
			instance = new HardwareClassFactory();
		return instance;
	}
	
	private static HardwareClassFactory instance;
	private Reflections reflections;
	
	private HardwareClassFactory() {
		reflections = new Reflections("org.raspinloop.emulator");
	}

	@Override
	public Collection<HardwareProperties> buildListImplementing(Class<? extends HardwareProperties> class1) {
		Set<?> propertiesClasses = reflections.getSubTypesOf(class1);
		
		HashSet<HardwareProperties> propertiesObjects = new HashSet<>();
		for (Object hardwarePropertiesClases : propertiesClasses) {
			if ((hardwarePropertiesClases instanceof Class<?>) && 
					! Modifier.isAbstract(((Class<?>)hardwarePropertiesClases).getModifiers())){
				Object object;
				try {
					object = ((Class<?>)hardwarePropertiesClases).getDeclaredConstructor().newInstance();				
				if (object instanceof HardwareProperties)
					propertiesObjects.add((HardwareProperties) object);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {					
					log.warn("Cannot Instanciate {}", hardwarePropertiesClases);
					log.trace("stacktrace:", e);
				}
			}
		}		
		return propertiesObjects;
	}

}
