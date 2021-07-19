package org.raspinloop.emulator.hardwareemulation.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.ReferenceNotFound;

public class FMUTester {

	public static void setVariable(HardwareEmulation hardware, String variableName, double d) throws ReferenceNotFound {
		Integer ref = getVariableRef(hardware, variableName);
		if (ref != null) {
			Map<Integer, Double> ref_values = new HashMap<Integer, Double>();
			ref_values.put(ref, d);
			assertTrue(hardware.setReal(ref_values), "Cannot set Real Variable for " + variableName);
		}
	}


	public static void setVariable(HardwareEmulation hardware, String variableName, int d) throws ReferenceNotFound {
		Integer ref = getVariableRef(hardware, variableName);
		if (ref != null) {
			Map<Integer, Integer> ref_values = new HashMap<Integer, Integer>();
			ref_values.put(ref, d);
			assertTrue(hardware.setInteger(ref_values), "Cannot set Integer Variable for " + variableName);
		}
	}
	
	public static void setVariable(HardwareEmulation hardware, String variableName, boolean d) throws ReferenceNotFound {
		Integer ref = getVariableRef(hardware, variableName);
		if (ref != null) {
			Map<Integer, Boolean> ref_values = new HashMap<Integer, Boolean>();
			ref_values.put(ref, d);
			assertTrue(hardware.setBoolean(ref_values), "Cannot set Boolean Variable for " + variableName);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getVariable(HardwareEmulation hardware, String variableName, Class<T> type) throws ReferenceNotFound {
		Integer ref = getVariableRef(hardware, variableName);
		if (type.isAssignableFrom(Double.class)) {
			List<Double> value = hardware.getReal(Arrays.asList(ref));
			if (value != null && !value.isEmpty())
				return (T) value.get(0);
		}
		if (type.isAssignableFrom(Integer.class)) {
			List<Integer> value = hardware.getInteger(Arrays.asList(ref));
			if (value != null && !value.isEmpty())
				return (T) value.get(0);
		}
		if (type.isAssignableFrom(Boolean.class)) {
			List<Boolean> value = hardware.getBoolean(Arrays.asList(ref));
			if (value != null && !value.isEmpty())
				return (T) value.get(0);
		}
		return null;
	}
	
	
	private static Map<String, Integer> cache = new HashMap<String, Integer>();

	private static Integer getVariableRef(HardwareEmulation hardware, String variableName) throws ReferenceNotFound {
		if (!cache.containsKey(variableName))
		{
			for (Fmi2ScalarVariable modelVariable : hardware.getModelVariables()) {
				if (modelVariable.getName().equals(variableName)){
					cache.put(variableName, (int)modelVariable.getValueReference());
				}
			}
		}
		return cache.get(variableName);
	}
	
}
