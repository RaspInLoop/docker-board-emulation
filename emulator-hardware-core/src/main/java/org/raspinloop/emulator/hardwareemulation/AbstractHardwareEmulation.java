package org.raspinloop.emulator.hardwareemulation;

import java.util.HashMap;
import java.util.Map;

import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract  class AbstractHardwareEmulation<T extends HardwareProperties> implements HardwareEmulation{

	private HardwareBuilderFactory builderFactory;
	private Map<HardwareProperties, HardwareEmulation> classCache = new HashMap<>();
	
	protected T properties;
	protected FmiReferenceRegister referenceRegister;

	protected AbstractHardwareEmulation(HardwareEmulation parent, T properties) {
		this.builderFactory = parent.getBuilderFactory();
		this.properties = properties;
		this.referenceRegister = parent.getReferenceRegister();
	}
	
	protected AbstractHardwareEmulation(FmiReferenceRegister referenceRegister, HardwareBuilderFactory builderFactory, T properties) {
		this.builderFactory = builderFactory;
		this.properties = properties;
		this.referenceRegister = referenceRegister;
	}

	@Override
	public FmiReferenceRegister getReferenceRegister() {
		return this.referenceRegister;
	}

	@Override
	public HardwareBuilderFactory getBuilderFactory() {
		return this.builderFactory;
	}
	
	@Override
	public T getProperties() {
		return properties;
	}

	protected HardwareEmulation getEmulationInstance(HardwareProperties comp) {
		if (classCache.containsKey(comp))
			return classCache.get(comp);

		HardwareEmulation hardware;
		try {
			hardware = builderFactory.createBuilder(comp, referenceRegister).build(this);
		} catch (Exception e) {
			log.error("Cannot build class for Hardware extension named \"{}\" reason:{}", comp.getComponentName(), e.getMessage());
			return null;
		}
		classCache.put(comp, hardware);
		return hardware;
	}
}
