package org.raspinloop.emulator.fmi;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.xmlunit.assertj.XmlAssert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.ReferenceNotFound;
import org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtensionProperties;
import org.raspinloop.emulator.hardwareemulation.dummy.DummyGpioBoard;
import org.raspinloop.emulator.hardwareemulation.dummy.DummyGpioBoardProperties;
import org.raspinloop.emulator.hardwareproperties.AlreadyUsedPin;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;
import org.raspinloop.emulator.hardwareproperties.Pin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TestFmu {

	@Test
	void testGenerate() throws IOException, FMUGenerateException {
		Path tmp = Files.createTempFile("raspinloop", "_UT");
		tmp.toFile().deleteOnExit();
		FMU fmu = new FMU(tmp.toFile());

		HardwareProperties hwProperties = buildSampleHWEmulationProperties();
		fmu.generate(hwProperties);
		// TODO: unzip and browse tmp to check if all is present
		assertTrue(tmp.toFile().exists());

	}

	@Test
	void testGenerateError() throws IOException {
		Path tmp = Files.createTempFile("raspinloop", "_UT");
		tmp.toFile().deleteOnExit();
		FMU fmu = new FMU(tmp.toFile());

		Assertions.assertThrows(FMUGenerateException.class, () -> {
			fmu.generate(null);
		});
	}

	@Test
	void testGenerateDescription() throws IOException, JAXBException, ReferenceNotFound {
		Path tmp = Files.createTempFile("raspinloop", "_UT");
		FMU fmu = new FMU(tmp.toFile());

		HardwareEmulation hwEmulation = buildSampleHWEmulation(buildSampleHWEmulationProperties());
		byte[] rawDesc = fmu.generateDescription(hwEmulation);
		String desc = new String(rawDesc, StandardCharsets.UTF_8);
		// test Variable definition
		assertThat(desc).valueByXPath("count(//ScalarVariable)").isEqualTo("2");
		assertThat(desc).valueByXPath("count(//ScalarVariable[@causality=\"output\"])").isEqualTo("1");
		assertThat(desc).valueByXPath("count(//ScalarVariable[@causality=\"input\"])").isEqualTo("1");

		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@valueReference").isNotEqualTo("0");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@description")
				.isEqualTo("Input signal related to pin IO0");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@variability").isEqualTo("continuous");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@causality").isEqualTo("input");

	}

	@Test
	void testGenerateDescriptionWithComponent() throws IOException, JAXBException, ReferenceNotFound {
		Path tmp = Files.createTempFile("raspinloop", "_UT");
		FMU fmu = new FMU(tmp.toFile());

		HardwareEmulation hwEmulation = buildSampleHWEmulation(buildSampleHWEmulationWithComponentProperties());
		byte[] rawDesc = fmu.generateDescription(hwEmulation);
		String desc = new String(rawDesc, StandardCharsets.UTF_8);
		// test Variable definition
		assertThat(desc).valueByXPath("count(//ScalarVariable)").isEqualTo("4");
		assertThat(desc).valueByXPath("count(//ScalarVariable[@causality=\"output\"])").isEqualTo("2");
		assertThat(desc).valueByXPath("count(//ScalarVariable[@causality=\"input\"])").isEqualTo("2");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@valueReference").isEqualTo("1");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@description")
				.isEqualTo("Input signal related to pin IO0");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@variability").isEqualTo("continuous");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO0\"]/@causality").isEqualTo("input");
		assertThat(desc).valueByXPath("//ScalarVariable[@name=\"IO2\"]/@valueReference").isEqualTo("9");
		assertThat(desc)
				.valueByXPath("//ScalarVariable[@name=\"Hardware_pluged_in_pin_header position\"]/@valueReference")
				.isEqualTo("14");
		assertThat(desc)
				.valueByXPath("//ScalarVariable[@name=\"Hardware_pluged_in_pin_header torque\"]/@valueReference")
				.isEqualTo("13");

	}

	private DummyGpioBoardProperties buildSampleHWEmulationProperties() {
		DummyGpioBoardProperties boardProperties = new DummyGpioBoardProperties();
		List<Pin> pinsToConfigure = new ArrayList<>(boardProperties.getUnUsedPins());
		try {
			boardProperties.useInputPin(pinsToConfigure.get(0));
			boardProperties.useOutputPin(pinsToConfigure.get(1));

		} catch (AlreadyUsedPin e) {
			log.error(e.getMessage());
		}
		return boardProperties;
	}

	private HardwareEmulation buildSampleHWEmulation(DummyGpioBoardProperties properties) {
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		HardwareBuilderFactory pcbf = new ClassLoaderBuilderFactory();
		return new DummyGpioBoard(referenceRegister, pcbf, properties);
	}

	private DummyGpioBoardProperties buildSampleHWEmulationWithComponentProperties() {

		DummyGpioBoardProperties board = new DummyGpioBoardProperties();
		board.setComponentName("My Custom Board!");
		List<Pin> pinsToConfigure = new ArrayList<>(board.getUnUsedPins());
		try {
			board.useInputPin(pinsToConfigure.get(0));
			board.useOutputPin(pinsToConfigure.get(2));

		} catch (AlreadyUsedPin e) {
			log.error(e.getMessage());
		}
		board.addGPIOComponent(new DummyBoardExtensionProperties());
		return board;
	}

}
