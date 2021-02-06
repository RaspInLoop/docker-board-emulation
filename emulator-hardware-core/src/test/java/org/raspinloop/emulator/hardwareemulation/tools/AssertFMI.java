package org.raspinloop.emulator.hardwareemulation.tools;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;

public class AssertFMI {

	public static void assertIsInputVariable(Fmi2ScalarVariable fmi2ScalarVariable) {
		assertEquals("input", fmi2ScalarVariable.getCausality(), "Variable "+fmi2ScalarVariable.getName()+" must be declared as input");
		assertEquals("continuous",fmi2ScalarVariable.getVariability(), "Variable "+fmi2ScalarVariable.getName()+" must be declared as continuous");	
	}
	
	public static void assertIsOutputVariable(Fmi2ScalarVariable fmi2ScalarVariable) {
		assertEquals("output", fmi2ScalarVariable.getCausality(), "Variable "+fmi2ScalarVariable.getName()+" must be declared as output");
		assertEquals("continuous",fmi2ScalarVariable.getVariability(), "Variable "+fmi2ScalarVariable.getName()+" must be declared as continuous");	
	}

	public static void assertIsRealVariable(Fmi2ScalarVariable fmi2ScalarVariable) {
		assertNotNull(fmi2ScalarVariable.getReal(), "Variable "+fmi2ScalarVariable.getName()+" must be Real" );		
	}
	
	public static void assertIsBooleanVariable(Fmi2ScalarVariable fmi2ScalarVariable) {
		assertNotNull( fmi2ScalarVariable.getBoolean(), "Variable "+fmi2ScalarVariable.getName()+" must be Boolean" );		
	}

	
}
