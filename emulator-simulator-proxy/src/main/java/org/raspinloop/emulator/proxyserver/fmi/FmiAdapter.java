package org.raspinloop.emulator.proxyserver.fmi;

import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.FMU_DO_STEP;
import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.FMU_GET_VALUE;
import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.FMU_INSTANCIATE;
import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.FMU_SET_VALUE;
import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.STOP;
import static org.raspinloop.emulator.proxyserver.simulation.SimulationEvents.FMU_TERMINATE_RESULT;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.thrift.TException;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.proxyserver.fmi.CoSimulation.Iface;
import org.raspinloop.emulator.proxyserver.simulation.Board;
import org.raspinloop.emulator.proxyserver.simulation.SimulationEvents;
import org.raspinloop.emulator.proxyserver.simulation.SimulationStates;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FmiAdapter implements Iface {

	private StateMachine<SimulationStates, SimulationEvents> stateMachine;

	private SimulatedClock clock;
	
	private Board board;

	public FmiAdapter(@Value("${fmi.server.version:3.0}") String version,
			@Value("${fim.server.platformtype:raspberry}") String typesPlatform,
			@Autowired Board board,
			@Autowired StateMachine<SimulationStates, SimulationEvents> stateMachine, 
			@Autowired SimulatedClock clock) {
		super();
		this.version = version;
		this.typesPlatform = typesPlatform;
		this.board = board;
		this.stateMachine = stateMachine;
		this.clock = clock;
	}

	private String version;

	private String typesPlatform;

	private HardwareEmulation hardwareEmulation;

	@Override
	public String getVersion() throws TException {
		return version;
	}

	@Override
	public String getTypesPlatform() throws TException {
		return typesPlatform;
	}

	@Override
	public Status setRealInputDerivatives(Instance c, Map<Integer, Integer> refOrders, Map<Integer, Double> refValues)
			throws TException {
		// not supported
		return Status.Discard;
	}

	@Override
	public Status setRealOutputDerivatives(Instance c, Map<Integer, Integer> refOrders,
			Map<Integer, Double> refValues) throws TException {
		// not supported
		return Status.Discard;
	}

	@Override
	public Instance instanciate(String instanceName, Type fmuType, String fmuGUID, String fmuResourceLocation,
			boolean visible, boolean loggingOn) throws TException {
		stateMachine.sendEvent(FMU_INSTANCIATE);

		ModelState state = ModelState.modelInitializationMode;
		try {
		board.configure();
		hardwareEmulation = board.getHardwareEmulation();
		// Instance aren't used in this application because only one Emulated Board are instanced per simulation run.
		return new Instance(instanceName, board.hashCode(), fmuGUID, state);
		} catch (Exception e) {
			String msg = MessageFormat.format("Cannot instanciate {0} ({1}) due to: {2})", instanceName, fmuGUID, e.getMessage());
			log.error(msg);
			throw new TException(msg, e);
		}
	}

	/**
	 * Informs the FMU to setup the experiment. This function must be called after
	 * fmi2Instantiate and before fmi2EnterInitializationMode is called. Arguments
	 * toleranceDefined and tolerance depend on the FMU type: The arguments
	 * startTime and stopTime can be used to check whether the model is valid within
	 * the given boundaries or to allocate memory which is necessary for storing
	 * results. Argument startTime is the fixed initial value of the independent
	 * variable5 [if the independent variable is “time”, startTime is the starting
	 * time of initializaton]. If stopTimeDefined = fmi2True, then stopTime is the
	 * defined final value of the independent variable [if the independent variable
	 * is “time”, stopTime is the stop time of the simulation] and if the
	 * environment tries to compute past stopTime the FMU has to return fmi2Status =
	 * fmi2Error. If stopTimeDefined = fmi2False, then no final value of the
	 * independent variable is defined and argument stopTime is meaningless.
	 */
	@Override
	public Status setupExperiment(Instance c, boolean toleranceDefined, double tolerance, double startTime,
			boolean stopTimeDefined, double stopTime) throws TException {

		Optional<Double> toleranceOpt = Optional.empty();
		if (toleranceDefined) {
			toleranceOpt = Optional.of(tolerance);
		}
		long sec = (int) startTime;
		long nano = (int) ((startTime - sec) * 1000000000.0);
		Instant start = Instant.ofEpochSecond(sec, nano);
		Optional<Instant> stop = Optional.empty();
		if (stopTimeDefined) {
			toleranceOpt = Optional.of(stopTime);
		}
		try {
			// TODO; maybe we should set those values to the SimulatedClock
			return Status.OK;
		} catch (Exception e) {
			log.error("Cannot setup Experiment: {}", e.getMessage());
			return Status.Error;
		}
	}

	/**
	 * Informs the FMU to enter Initialization Mode. Before calling this function,
	 * all variables with attribute <ScalarVariable initial = "exact" or "approx">
	 * can be set with the “setXXX” functions (the ScalarVariable attributes are
	 * defined in the Model Description File, see section 2.2.7). Setting other
	 * variables is not allowed. Furthermore, setupExperiment must be called at
	 * least once before calling enterInitializationMode, in order that startTime is
	 * defined.
	 */
	@Override
	public Status enterInitializationMode(Instance c) throws TException {
		return Status.OK;
	}

	/**
	 * Informs the FMU to exit Initialization Mode.
	 */
	@Override
	public Status exitInitializationMode(Instance c) throws TException {
		return Status.OK;
	}

	/**
	 * Informs the FMU that the simulation run is terminated. After calling this
	 * function, the final values of all variables can be inquired with the
	 * getXXX(..) functions. It is not allowed to call this function after one of
	 * the functions returned with a status flag of Error or Fatal.
	 */
	@Override
	public Status terminate(Instance c) throws TException {
		stateMachine.sendEvent(STOP);
		return Status.OK;
	}

	/**
	 * Is called by the environment to reset the FMU after a simulation run. The FMU
	 * goes into the same state as if fmi2Instantiate would have been called. All
	 * variables have their default values. Before starting a new run,
	 * fmi2SetupExperiment and fmi2EnterInitializationMode have to be called.
	 */
	@Override
	public Status reset(Instance c) throws TException {
		// not supported: only one simulation run per instance
		return Status.Discard;
	}

	@Override
	public void freeInstance(Instance c) throws TException {
		stateMachine.sendEvent(FMU_TERMINATE_RESULT);
	}

	@Override
	public List<Double> getReal(Instance c, List<Integer> refs) throws TException {
		stateMachine.sendEvent(FMU_GET_VALUE);
		return hardwareEmulation.getReal(refs);
	}

	@Override
	public List<Integer> getInteger(Instance c, List<Integer> refs) throws TException {
		stateMachine.sendEvent(FMU_GET_VALUE);
		return hardwareEmulation.getInteger(refs);
	}

	@Override
	public List<Boolean> getBoolean(Instance c, List<Integer> refs) throws TException {
		stateMachine.sendEvent(FMU_GET_VALUE);
		return hardwareEmulation.getBoolean(refs);
	}

	@Override
	public List<String> getString(Instance c, List<Integer> refs) throws TException {
		stateMachine.sendEvent(FMU_GET_VALUE);
		return Collections.emptyList();
	}

	@Override
	public Status setReal(Instance c, Map<Integer, Double> refValues) throws TException {
		stateMachine.sendEvent(FMU_SET_VALUE);
		return hardwareEmulation.setReal(refValues)? Status.OK : Status.Error;
	}

	@Override
	public Status setInteger(Instance c, Map<Integer, Integer> refValues) throws TException {
		stateMachine.sendEvent(FMU_SET_VALUE);
		return hardwareEmulation.setInteger(refValues)? Status.OK : Status.Error;
	}

	@Override
	public Status setBoolean(Instance c, Map<Integer, Boolean> refValues) throws TException {
		stateMachine.sendEvent(FMU_SET_VALUE);
		return hardwareEmulation.setBoolean(refValues)? Status.OK : Status.Error;
	}

	@Override
	public Status setString(Instance c, Map<Integer, String> refValues) throws TException {
		stateMachine.sendEvent(FMU_SET_VALUE);
		// not supported
		return Status.Discard;
	}

	@Override
	public Status cancelStep(Instance c) throws TException {
		// not supported
		return Status.Discard;
	}

	@Override
	public Status doStep(Instance c, double currentCommunicationPoint, double communicationStepSize,
			boolean noSetFMUStatePriorToCurrentPoint) throws TException {
		clock.doStep(communicationStepSize);
		stateMachine.sendEvent(FMU_DO_STEP);
		try {
			clock.waitForBlocking();
		} catch (InterruptedException e) {
			log.debug("do_step interrupted: {}", e.getMessage());
			return Status.Error;
		}
		return Status.OK;
	}

	@Override
	public Status getStatus(Instance c, StatusKind s) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIntegerStatus(Instance c, StatusKind s) throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRealStatus(Instance c, StatusKind s) throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getBooleanStatus(Instance c, StatusKind s) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getStringStatus(Instance c, StatusKind s) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
