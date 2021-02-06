package org.raspinloop.emulator.hardwareemulation.dummy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raspinloop.emulator.fmi.Reference;
import org.raspinloop.emulator.fmi.Reference.ReferenceBuilder;
import org.raspinloop.emulator.fmi.modeldescription.DescriptionBuilder;
import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;
import org.raspinloop.emulator.fmi.modeldescription.DescriptionBuilder.CausalityType;
import org.raspinloop.emulator.hardwareemulation.AbstractHardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.GpioCompHardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummyBoardExtension extends AbstractHardwareEmulation<DummyBoardExtensionProperties>
		implements GpioCompHardwareEmulation {

	private boolean terminated;
	
	private Double internalTorque;
	private Double internalAngularSpeed;

	private Reference<Double> refTorque;

	private Reference<Double> refPosition;

	public DummyBoardExtension(HardwareEmulation parent, DummyBoardExtensionProperties properties) {
		super(parent, properties);
		ReferenceBuilder<Double> refBuilder = referenceRegister.getBuilder(Double.class);
		refTorque = refBuilder.object(internalTorque)
				.getter(o -> {return (Double)o;})
				.setter((o,v) -> o = v)
				.tags("Sample Extension")
				.build();
		refPosition = refBuilder.object(internalAngularSpeed)
				.getter(o -> {return (Double)o;})
				.setter((o,v) -> o = v)
				.tags("Sample Extension")
				.build();		
	}

	@Override
	public List<Fmi2ScalarVariable> getModelVariables() {
		ArrayList<Fmi2ScalarVariable> result = new ArrayList<Fmi2ScalarVariable>(2);
		DescriptionBuilder db = new DescriptionBuilder();
		result.add(db.getRealBuilder(CausalityType.OUTPUT)
				.setName(properties.getType() + " position")
				.setRef(refPosition.getRef())
				.build());
		result.add(db.getRealBuilder(CausalityType.INPUT)
				.setName(properties.getType() + " torque")
				.setRef(refTorque.getRef())
				.build());
		return result;
	}

	@Override
	public boolean enterInitialize() {
		log.info("initializing {}", properties.getComponentName());
		return true;
	}

	@Override
	public boolean exitInitialize() {
		log.info("{} initialized", properties.getComponentName());
		return true;
	}

	@Override
	public void terminate() {
		log.info("terminating {}", properties.getComponentName());
		terminated = true;
	}

	@Override
	public void reset() {
		log.info("{} reset", properties.getComponentName());
	}

	@Override
	public List<Double> getReal(List<Integer> refs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getInteger(List<Integer> refs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Boolean> getBoolean(List<Integer> refs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setReal(Map<Integer, Double> ref_values) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setInteger(Map<Integer, Integer> ref_values) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setBoolean(Map<Integer, Boolean> ref_values) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	@Override
	public boolean usePin(Pin pin) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setState(Pin pin, PinState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public PinState getState(Pin pin) {
		// TODO Auto-generated method stub
		return null;
	}
}
