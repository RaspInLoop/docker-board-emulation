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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.raspinloop.emulator.hardwareemulation.tools.AssertFMI.assertIsBooleanVariable;
import static org.raspinloop.emulator.hardwareemulation.tools.AssertFMI.assertIsInputVariable;
import static org.raspinloop.emulator.hardwareemulation.tools.AssertFMI.assertIsOutputVariable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raspinloop.emulator.fmi.ClassLoaderBuilderFactory;
import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.GsonProperties;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.HardwareClassFactory;
import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;
import org.raspinloop.emulator.hardwareemulation.GpioProvider;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.IoChange;
import org.raspinloop.emulator.hardwareemulation.IoTimedChange;
import org.raspinloop.emulator.hardwareemulation.tools.FMUTester;
import org.raspinloop.emulator.hardwareproperties.AlreadyUsedPin;
import org.raspinloop.emulator.hardwareproperties.PinState;

import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@ExtendWith(MockitoExtension.class)
class RaspiGpioSimulatorTest {

	private RaspiGpioSimulatorProperties buildProperty() throws AlreadyUsedPin {
		RaspiGpioSimulatorProperties prop = new RaspiGpioSimulatorProperties();
		prop.useOutputPin(RaspiPin.GPIO_01);
		prop.useOutputPin(RaspiPin.GPIO_03);
		prop.useInputPin(RaspiPin.GPIO_02);		
		return prop;
	}
	
	@Test
	void testGetModelVariables() throws AlreadyUsedPin {
		
		// HwEmulation is the interface used by the simulation tool.
		// to get the description (model)
		// to get the ID 
		// to initialize, reset and terminate the simulation
		// and to read and write variable (setBoolean, setInteger,..., getBoolean, getInteger,...)
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		HardwareBuilderFactory pcbf = new ClassLoaderBuilderFactory();
		HardwareEmulation dut = new RaspiGpioSimulator(referenceRegister, pcbf, buildProperty());
			
		List<Fmi2ScalarVariable> variables = dut.getModelVariables();
		
		assertEquals(3,variables.size() );
		
		Fmi2ScalarVariable input1Var = variables.get(0);
		
		Fmi2ScalarVariable output1Var = variables.get(1);
		
		Fmi2ScalarVariable output2Var = variables.get(2);
		
		for (Fmi2ScalarVariable fmi2ScalarVariable : variables) {
			assertIsBooleanVariable(fmi2ScalarVariable);			
		}
		
		assertEquals("GPIO 2",input1Var.getName() );
		assertIsInputVariable(input1Var);
		
		assertEquals("GPIO 1",output1Var.getName() );
		assertIsOutputVariable(output1Var);
		
		assertEquals("GPIO 3",output2Var.getName() );
		assertIsOutputVariable(output2Var);
	}
	
	@Test
	void gpioTest() throws AlreadyUsedPin, InterruptedException {
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		HardwareBuilderFactory pcbf = new ClassLoaderBuilderFactory();
		RaspiGpioSimulator dut = new RaspiGpioSimulator(referenceRegister, pcbf, buildProperty());
		
		// HwEmulation is the interface used by the simulation side of proxy.
				// to get the description (model)
				// to get the ID 
				// to initialize, reset and terminate the simulation
				// and to read and write variable (setBoolean, setInteger,..., getBoolean, getInteger,...)
		HardwareEmulation usedBySimulator = dut; 
		usedBySimulator.enterInitialize();
		
		// GpioProvider is the interface used by the Qemu side of proxy
		GpioProvider usedByQemu = dut;
		final TestPublisher<IoTimedChange> testPublisher = TestPublisher.create();
		usedByQemu.outboundIoChanges(testPublisher.flux());
				
		FMUTester.setVariable(usedBySimulator, "GPIO 2", true);

		assertFalse(FMUTester.getVariable(usedBySimulator, "GPIO 1", Boolean.class), "variable GPIO 1 should be reset");
		
		testPublisher.next(IoTimedChange.timeTag(IoChange.set((byte)RaspiPin.GPIO_01.getAddress()), Instant.now()));

		assertTrue(FMUTester.getVariable(usedBySimulator, "GPIO 1", Boolean.class), "variable GPIO 1 should be set");
		
		testPublisher.next(IoTimedChange.timeTag(IoChange.clear((byte)RaspiPin.GPIO_01.getAddress()), Instant.now()));
		
		assertFalse(FMUTester.getVariable(usedBySimulator, "GPIO 1", Boolean.class), "variable GPIO 1 should be reset");
		usedBySimulator.terminate();
	}
	
	@Test
	void gpioProviderReactiveTest() throws AlreadyUsedPin {
		
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		HardwareBuilderFactory pcbf = new ClassLoaderBuilderFactory();
		RaspiGpioSimulator dut = new RaspiGpioSimulator(referenceRegister, pcbf, buildProperty());
		
		// HwEmulation is the interface used by the simulation side of proxy.
				// to get the description (model)
				// to get the ID 
				// to initialize, reset and terminate the simulation
				// and to read and write variable (setBoolean, setInteger,..., getBoolean, getInteger,...)
		HardwareEmulation usedBySimulator = dut; 
		usedBySimulator.enterInitialize();
		
		// GpioProvider is the interface used by the Qemu side of proxy
		GpioProvider usedByQemu = dut;		
	
		FMUTester.setVariable(usedBySimulator, "GPIO 2", true);
		FMUTester.setVariable(usedBySimulator, "GPIO 2", false);
		usedBySimulator.terminate();
		
    	StepVerifier.create(usedByQemu.publishIoChanges())
    	.expectNext(IoChange.set((byte)2))
    	.expectNext(IoChange.clear((byte)2))
    	.expectComplete()
    	.verify(Duration.ofSeconds(1));
		

	}
	
	@Test
	void toJsonTest() throws AlreadyUsedPin {
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties serialiser = new GsonProperties(hcl);
		String json = serialiser.write( buildProperty());
		assertThatJson(json).node("componentName").isEqualTo("RaspberryPi GPIO Simulator");
		assertThatJson(json).node("supportedPins").isArray().hasSize(32);
	}
	
}
