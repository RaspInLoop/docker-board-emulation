package org.raspinloop.emulator.hardwareemulation;

import static org.raspinloop.emulator.fmi.modeldescription.DescriptionBuilder.CausalityType.INPUT;
import static org.raspinloop.emulator.fmi.modeldescription.DescriptionBuilder.CausalityType.OUTPUT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.Reference;
import org.raspinloop.emulator.fmi.modeldescription.DescriptionBuilder;
import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;
import org.raspinloop.emulator.fmi.modeldescription.DescriptionBuilder.BooleanBuilder;
import org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinState;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Raspinloop
 *
 * @param <T> HardwareProperties implementation that describe the hardware.
 * 
 *            This class contains base implementation related to board
 *            specificities; (e.g. handle linked components)
 */
@Slf4j
public abstract class AbstractBoardHardwareEmulation<T extends BoardHardwareProperties> extends AbstractHardwareEmulation<T> {

	protected AbstractBoardHardwareEmulation(FmiReferenceRegister referenceRegister, HardwareBuilderFactory builderFactory, T properties) {
		super(referenceRegister, builderFactory, properties);
	}
	
	protected abstract void setState(Pin pin, PinState newState);
	protected abstract PinState getState(Pin pin);

	@Override
	public List<Fmi2ScalarVariable> getModelVariables() throws ReferenceNotFound {
		List<Fmi2ScalarVariable> list = new LinkedList<>();
		DescriptionBuilder db = new DescriptionBuilder();
		BooleanBuilder inputBuilder = db.getBooleanBuilder(INPUT);
		for (org.raspinloop.emulator.hardwareproperties.Pin pin : properties.getInputPins()) {
			list.add(inputBuilder.setDescritpion("Input signal related to pin " + pin.getName()).setName(pin.getName())
					.setRef(getInputReference(pin)).build());
		}
		BooleanBuilder outputBuilder = db.getBooleanBuilder(OUTPUT);
		for (org.raspinloop.emulator.hardwareproperties.Pin pin : properties.getOutputPins()) {
			list.add(outputBuilder.setDescritpion("Output signal related to pin " + pin.getName())
					.setName(pin.getName()).setRef(getOutputReference(pin)).build());
		}

		for (HardwareProperties comp : properties.getAllComponents()) {

			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp != null)
				list.addAll(emulationComp.getModelVariables());
		}
		return list;
	}

	private long getOutputReference(Pin pin) throws ReferenceNotFound{
		Reference<PinState> registreredRef = referenceRegister.get(pin, "output", PinState.class);
		if (registreredRef == null) {
			throw new ReferenceNotFound("Could not find reference for pin: " + pin);
		}
		return registreredRef.getRef();
	}

	private long getInputReference(Pin pin) throws ReferenceNotFound {
		Reference<PinState> registreredRef =  referenceRegister.get(pin, "input", PinState.class);
		if (registreredRef == null) {
			throw new ReferenceNotFound("Could not find reference for pin: " + pin);
		}
		return registreredRef.getRef();
	}
	
	private Pin getPin(int ref) {
		Object obj = referenceRegister.get(ref, PinState.class).getObject();
		if (obj instanceof Pin) {
			return (Pin)obj;
		} else {
			return null;
		}
	}
	
	public boolean hasPin(Pin pin) {		
		return  properties.getSupportedPins().contains(pin);
	}
	
	public boolean hasAddress(short address) {		
		return  properties.getSupportedPins().stream().map(Pin::getAddress).filter(a -> a==address).count() > 0;
	}
	
	public Pin getPin(short address) {
		return  properties.getSupportedPins().stream().filter(p -> p.getAddress() == address).findFirst().orElseThrow();
	}
	
	private boolean isUsedByBoard(Pin pin) {		
		return properties.getInputPins().contains(pin) || properties.getOutputPins().contains(pin);
	}

	private HardwareEmulation getSimulatedCompUsingRef(long ref) throws ReferenceNotFound {
		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp != null) {
				for (Fmi2ScalarVariable modelVariable : emulationComp.getModelVariables()) {
					if (modelVariable.getValueReference() == ref)
						return emulationComp;
				}
			}
		}
		return null;
	}

	@Override
	public List<Double> getReal(List<Integer> refs) throws ReferenceNotFound {
		// NO check in pin: there is no pin with real value

		List<Double> result = new ArrayList<>(refs.size());
		for (Integer ref : refs) {
			// TODO: we should group refs by component in order to make only one
			// call
			HardwareEmulation comp = getSimulatedCompUsingRef(ref);
			if (comp != null) {
				List<Double> compResult = comp.getReal(Arrays.asList(ref));
				for (Double real1 : compResult) {
					result.add(real1);
				}
			} else {
				log.warn("ref:" + ref + " not used in application");
				result.add(0.0); // Invalid value defined for real ?
			}
		}
		return result;
	}

	@Override
	public List<Integer> getInteger(List<Integer> refs) throws ReferenceNotFound {
		// NO check in pin: there is no pin with integer value
		List<Integer> result = new ArrayList<>(refs.size());
		for (Integer ref : refs) {
			// TODO: we should group refs by component in order to make only one
			// call
			HardwareEmulation comp = getSimulatedCompUsingRef(ref);
			if (comp != null) {
				List<Integer> compResult = comp.getInteger(Arrays.asList(ref));
				for (Integer int1 : compResult) {
					result.add(int1);
				}
			} else {
				log.warn("ref:" + ref + " not used in application");
				result.add(0); // Invalid value defined for integer ?
			}
		}
		return result;
	}

	@Override
	public List<Boolean> getBoolean(List<Integer> refs) throws ReferenceNotFound {
		// Boolean value may be either a pin state or a value given by a
		// component
		List<Boolean> result = new LinkedList<>();
		for (Integer ref : refs) {
			Pin pin = getPin(ref);
			if (pin != null && isUsedByBoard(pin))
				result.add(getState(pin) == PinState.HIGH);
			else {
				// TODO: we should group refs by component in order to make only
				// one call
				HardwareEmulation comp = getSimulatedCompUsingRef(ref);
				if (comp != null) {
					List<Boolean> compResult = comp.getBoolean(Arrays.asList(ref));
					for (Boolean boolean1 : compResult) {
						result.add(boolean1);
					}
				} else {
					log.warn("ref:" + ref + " not used in application");
					result.add(false); // default pull up/down resistor ??
				}
			}
		}
		return result;
	}

	@Override
	public boolean setReal(Map<Integer, Double> refValues) throws ReferenceNotFound {
		// NO check in pin: there is no pin with real value
		for (Entry<Integer, Double> ref_value : refValues.entrySet()) {
			// TODO: we should group ref_value by component in order to make
			// only one call
			HardwareEmulation comp = getSimulatedCompUsingRef(ref_value.getKey());
			if (comp != null) {
				HashMap<Integer, Double> map = new HashMap<>(1);
				map.put(ref_value.getKey(), ref_value.getValue());
				comp.setReal(map);
			} else {
				log.warn("PIN[ref:" + ref_value.getKey() + "] not used in application");
			}
		}
		return true;
	}

	@Override
	public boolean setInteger(Map<Integer, Integer> refValues) throws ReferenceNotFound {
		// NO check in pin: there is no pin with integer value
		for (Entry<Integer, Integer> ref_value : refValues.entrySet()) {
			// TODO: we should group ref_value by component in order to make
			// only one call
			HardwareEmulation comp = getSimulatedCompUsingRef(ref_value.getKey());
			if (comp != null) {
				HashMap<Integer, Integer> map = new HashMap<>(1);
				map.put(ref_value.getKey(), ref_value.getValue());
				comp.setInteger(map);
			} else {
				log.warn("PIN[ref:" + ref_value.getKey() + "] not used in application");
			}
		}
		return true;
	}
	
	@Override
	public boolean setBoolean(Map<Integer, Boolean> refValues) throws ReferenceNotFound {
		// Boolean value may be either a pin state or a component value to set.
		for (Entry<Integer, Boolean> ref_value : refValues.entrySet()) {
			Pin pin = getPin(ref_value.getKey());
			if (pin != null && isUsedByBoard(pin)) {
				PinState newState = ref_value.getValue().equals(Boolean.TRUE) ? PinState.HIGH : PinState.LOW;
				setState(pin, newState);
			} else {
				// TODO: we should group ref_value by component in order to make
				// only one call
				HardwareEmulation comp = getSimulatedCompUsingRef(ref_value.getKey());
				if (comp != null) {
					HashMap<Integer, Boolean> map = new HashMap<>(1);
					map.put(ref_value.getKey(), ref_value.getValue());
					comp.setBoolean(map);
				} else {
					log.warn("PIN[ref:" + ref_value.getKey() + "] not used in application");					
				}
			}
		}
		return true;
	}
}
