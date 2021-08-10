package org.raspinloop.emulator.hardwareemulation.dummy;

import static org.raspinloop.emulator.hardwareproperties.PinState.HIGH;
import static org.raspinloop.emulator.hardwareproperties.PinState.LOW;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.Reference.ReferenceBuilder;
import org.raspinloop.emulator.hardwareemulation.AbstractBoardHardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.GpioProvider;
import org.raspinloop.emulator.hardwareemulation.IoChange;
import org.raspinloop.emulator.hardwareemulation.IoChange.Change;
import org.raspinloop.emulator.hardwareemulation.IoTimedChange;
import org.raspinloop.emulator.hardwareproperties.Pin;
import org.raspinloop.emulator.hardwareproperties.PinState;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class DummyGpioBoard extends AbstractBoardHardwareEmulation<DummyGpioBoardProperties> implements GpioProvider  {

	
	public DummyGpioBoard(FmiReferenceRegister referenceRegister, HardwareBuilderFactory builderFactory, DummyGpioBoardProperties properties) {
		super(referenceRegister, builderFactory, properties);
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
	
	private boolean terminated = false;
	
	private Sinks.Many<IoChange> changesSink;
	
	List<Boolean> values = new ArrayList<>(Collections.nCopies(properties.getSupportedPins().size(), Boolean.FALSE));	
	
	@Override
	public Flux<IoChange> publishIoChanges() {
		return changesSink.asFlux();
	}
	
	@Override
	public boolean enterInitialize() {
		log.info("initializing {}" , properties.getComponentName());
		
		return true;
	}

	@Override
	public boolean exitInitialize() {
		log.info("{} initialized" , properties.getComponentName());
		return false;
	}

	@Override
	public void terminate() {
		log.info("terminating {}" , properties.getComponentName());
		changesSink.emitComplete(FAIL_FAST);
		terminated = true;
	}

	@Override
	public void reset() {
		log.info("{} reset" , properties.getComponentName());
	}

	@Override
	public boolean isTerminated() {		
		return terminated;
	}
	
	@Override
	public PinState getState(Pin pin) {
		log.debug("get state for pin {}", pin);
		if (hasPin(pin) ) {
			return values.get(pin.getAddress())? HIGH : LOW;
		} else  {
			return null;
		}
	}
	
	@Override
	public void setState(Pin pin, PinState state) {
		log.debug("set state {} for pin {}",state,  pin);
		if (hasPin(pin) ) {
			values.add(pin.getAddress(), HIGH.equals(state));
			Change changeFlag = state.equals(PinState.HIGH) ? Change.SET : Change.CLEAR;			
			IoChange change = new IoChange(changeFlag, (byte)pin.getAddress());
			changesSink.emitNext(change, FAIL_FAST);
		}
	}
	
	private void saveState(IoTimedChange change) {
		log.trace("set state: {}", change);		
		if (hasAddress(change.getBit()) ) {
			values.add(change.getBit(), IoChange.Change.SET == change.getChange()) ;
		}
	}
		
	private final Scheduler publisherScheduler = Schedulers.newSingle("IoStateScheduler");
	
	@Override
	public void outboundIoChanges(Flux<IoTimedChange> ioChanges) {
			ioChanges.subscribeOn(publisherScheduler)
			.subscribe(this::saveState);
	}
}
