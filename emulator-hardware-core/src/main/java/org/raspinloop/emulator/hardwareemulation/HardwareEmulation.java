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
package org.raspinloop.emulator.hardwareemulation;

import java.util.List;
import java.util.Map;

import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;

/**
 * 
 *	HwEmulation is the interface used by the simulation tool.
 *	<br> to get the description (model)
 *	<br> to get the ID 
 *	<br> to initialize, reset and terminate the simulation
 *	<br> and to read and write variable (setBoolean, setInteger,..., getBoolean, getInteger,...)
 *
 */
public interface HardwareEmulation {

	
	//TODO add documentation
	public HardwareProperties getProperties();
	
	public FmiReferenceRegister getReferenceRegister();
	
	public HardwareBuilderFactory getBuilderFactory();
	

	public List<Fmi2ScalarVariable> getModelVariables();
	
	/**
	 * Informs the FMUTester to enter Initialization Mode.
	 * Before calling this function , all variables with attribute <ScalarVariable initial ="exact" or "approx"> can be set with the
	 * "SetXXX" functions (the ScalarVariable attributes are defined in the ModelDescription File, 
	 * see section 2.2.7). Setting other variables is not allowed.
	 * @return succeed or not
	 */
	boolean enterInitialize();

	
	/**
	 * Informs the FMUTester to exit Initialization Mode.	
	 * @return succeed or not
	 */
	boolean exitInitialize();

	/**
	 * Informs the FMUTester that the simulation run is terminated. After calling this function, the final
	 * values of all variables can be inquired with the GetXXX(..) functions.
	 */
	void terminate();

	/**
	 * Is called by the environment to reset the FMUTester after a simulation run. The FMUTester goes into the
	 * same state as when it is created. All variables have their default
	 * values. Before starting a new run, enterInitialization have to be called.
	 */
	void reset();
	
	/**
	 * Get actual values of variables by providing their variable references. [These functions are
	 * especially used to get the actual values of output variables if a model is connected with other
	 * models.
	 * @param refs : list of variable reference
	 * @return Real values in the same order than refs 
	 */
	List<Double> getReal(List<Integer> refs);

	/**
	 * Get actual values of variables by providing their variable references. [These functions are
	 * especially used to get the actual values of output variables if a model is connected with other
	 * models.
	 * @param refs : list of variable reference
	 * @return int values in the same order than refs 
	 */
	List<Integer> getInteger(List<Integer> refs);
	
	/**
	 * Get actual values of variables by providing their variable references. [These functions are
	 * especially used to get the actual values of output variables if a model is connected with other
	 * models.
	 * @param refs : list of variable reference
	 * @return Boolean values in the same order than refs 
	 */
	List<Boolean> getBoolean(List<Integer> refs);

	/**
	 * Set parameters, inputs, start values and re-initialize caching of variables that depend on these variables
	 * @param ref_values mapped value/reference collection
	 * @return succeed or not 
	 */
	boolean  setReal(Map<Integer, Double> refValues);
	
	/**
	 * Set parameters, inputs, start values and re-initialize caching of variables that depend on these variables
	 * @param ref_values mapped value/reference collection
	 * @return succeed or not 
	 */
	boolean  setInteger(Map<Integer, Integer> refValues);
	
	/**
	 * Set parameters, inputs, start values and re-initialize caching of variables that depend on these variables
	 * @param refValues mapped value/reference collection
	 * @return succeed or not 
	 */
	boolean  setBoolean(Map<Integer, Boolean> refValues);


	
	/**
	 * Returns true, if the slave wants to terminate the simulation. 
	 * Can be called after doStep(...) returned Discard. 
	 * Use fmi2LastSuccessfulTime to determine the time	instant at which the slave terminated.
	*/
	boolean isTerminated();
	
}
