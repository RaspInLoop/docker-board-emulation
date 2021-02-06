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
package org.raspinloop.emulator.raspberry;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.Reference.ReferenceBuilder;
import org.raspinloop.emulator.hardwareemulation.AbstractBoardHardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.GpioCompHardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.GpioProvider;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.IoChange;
import org.raspinloop.emulator.hardwareemulation.IoTimedChange;
import org.raspinloop.emulator.hardwareemulation.IoChange.Change;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinEdge;
import org.raspinloop.emulator.hardwareproperties.PinMode;
import org.raspinloop.emulator.hardwareproperties.PinState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * This class act has the Raspberry board itself. It implements
 * GpioProviderHwEmulation which is compound of {@link HardwareEmulation} and
 * {@link GpioProvider} <br>
 * <br>
 * {@link HardwareEmulation} is the interface used by the simulation tool. To get the
 * description (model), to get the ID, to initialize, reset and terminate the
 * simulation and to read and write variable (setBoolean, setInteger,...,
 * getBoolean, getInteger,...) <br>
 * While {@link GpioProvider} is the interface used by the code to debug. (it is
 * adapted by an adapter at runtime and replace the library which access the
 * hardware)
 * 
 * A lot of this code is inspired from the
 * {@link com.pi4j.io.gpio.GpioProviderBase} , the Pi4j abstract class emulated
 * by this one.
 */
public class RaspiGpioSimulator extends AbstractBoardHardwareEmulation<RaspiGpioSimulatorProperties> implements GpioProvider {

	public static final String GUID = RaspiGpioSimulatorProperties.GUID;

	static final Logger logger = LoggerFactory.getLogger(RaspiGpioSimulator.class);

	private static final int DEFAULT_CACHE_SIZE = 100;

	protected final Map<Pin, PinEdge> edgeDetectionCache = new ConcurrentHashMap<>();




	protected PinCache[] cache = new PinCache[DEFAULT_CACHE_SIZE];

	public RaspiGpioSimulator(FmiReferenceRegister referenceRegister, HardwareBuilderFactory builderFactory, RaspiGpioSimulatorProperties raspiGpioSimulatorProperties) {
		super(referenceRegister, builderFactory, raspiGpioSimulatorProperties);
		ReferenceBuilder<PinState> referenceBuilder = referenceRegister.getBuilder(PinState.class);
		properties.getSupportedPins().stream()
			.forEach( p -> {
				referenceBuilder.tags("input")
				.object(p)
				.setter((obj,value) -> this.setState((Pin)obj, value))
				.getter( obj -> this.getState((Pin)obj))
				.build();});
		properties.getSupportedPins().stream()
		.forEach( p -> {
			referenceBuilder.tags("output")
			.object(p)
			.setter((obj,value) -> this.setState((Pin)obj, value))
			.getter( obj -> this.getState((Pin)obj))
			.build();});	
		changesSink = Sinks.many().multicast().onBackpressureBuffer();
	}

	private Sinks.Many<IoChange> changesSink;

	public boolean hasPin(Pin pin) {
		return (pin.getProvider().equals(properties.getSimulatedProviderName()));
	}

	public void setMode(Pin pin, PinMode mode) {
		// if this is an input pin, then configure edge detection
		if (PinMode.allInputs().contains(mode)) {
			System.out.println("-- set setEdgeDetection [" + pin + "] to  [" + PinEdge.BOTH + "]");
			edgeDetectionCache.put(pin, PinEdge.BOTH);
		}
	}

	public void setState(Pin pin, PinState state) {
		PinState oldState = getPinCache(pin).getState();
		getPinCache(pin).setState(state);
		
		if (!state.equals(oldState)) {
			Change changeFlag = state.equals(PinState.HIGH) ? Change.SET : Change.CLEAR;			
			IoChange change = new IoChange(changeFlag, (byte)pin.getAddress());
			changesSink.emitNext(change, FAIL_FAST);			
		}
		
		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp instanceof GpioCompHardwareEmulation) {
				GpioCompHardwareEmulation compHwE = ((GpioCompHardwareEmulation) emulationComp);
				if (compHwE.usePin(pin))
					compHwE.setState(pin, state);
			}
		}
	}

	public PinState getState(Pin pin) {

		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp instanceof GpioCompHardwareEmulation) {
				GpioCompHardwareEmulation compHwE = ((GpioCompHardwareEmulation) emulationComp);
				if (compHwE.usePin(pin))
					return compHwE.getState(pin);
			}
		}
		PinState state = getPinCache(pin).getState();
		
		return state;
	}


	@Override
	public boolean enterInitialize() {
		boolean result = true;		
		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp != null)
				result &= emulationComp.enterInitialize();
		}
		return result;
	}

	@Override
	public boolean exitInitialize() {
		boolean result = true;
		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp != null)
				result &= emulationComp.exitInitialize();
		}
		return result;
	}

	@Override
	public void terminate() {
		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp != null)
				emulationComp.terminate();
		}
		changesSink.emitComplete(FAIL_FAST);
	}

	@Override
	public void reset() {
		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp != null)
				emulationComp.reset();
		}
	}



	public PinMode getMode(Pin pin) {
		if (!hasPin(pin)) {
			throw new RuntimeException("unknown Pin");
		}

		// return cached mode value
		return getPinCache(pin).getMode();
	}

	protected PinCache getPinCache(Pin pin) {

		int address = pin.getAddress();

		// dynamically resize pin cache storage if needed based on pin address
		if (address > cache.length) {
			// create a new array with existing contents
			// that is 100 elements larger than the requested address
			// (we add the extra 100 elements to provide additional overhead
			// capacity in
			// an attempt to minimize further array expansion)
			cache = Arrays.copyOf(cache, address + 100);
		}

		// get the cached pin object from the cache
		PinCache pc = cache[address];

		// if no pin object is found in the cache, then we need to create one at
		// this address index in the cache array
		if (pc == null) {
			pc = cache[pin.getAddress()] = new PinCache(pin);
		}
		return pc;
	}


	@Override
	public boolean isTerminated() {
		return false;
	}

	public String getName() {
		return properties.getSimulatedProviderName();
	}

	public void setState(IoTimedChange change) {
		Pin pin = getPin(change.getBit());
		PinState state = Change.SET.equals(change.getChange()) ? PinState.HIGH : PinState.LOW;
		getPinCache(pin).setState(state);
				
		for (HardwareProperties comp : properties.getAllComponents()) {
			HardwareEmulation emulationComp = getEmulationInstance(comp);
			if (emulationComp instanceof GpioCompHardwareEmulation) {
				GpioCompHardwareEmulation compHwE = ((GpioCompHardwareEmulation) emulationComp);
				if (compHwE.usePin(pin))
					compHwE.setState(pin, state);
			}
		}
	}


	@Override
	public Flux<IoChange> publishIoChanges() {
		return changesSink.asFlux();
	}

	private final Scheduler publisherScheduler = Schedulers.newSingle("RaspiBoardIoStateScheduler");
	
	@Override
	public void outboundIoChanges(Flux<IoTimedChange> ioChanges) {
			ioChanges
			//.subscribeOn(publisherScheduler)
			.subscribe(this::setState);
	}
}
