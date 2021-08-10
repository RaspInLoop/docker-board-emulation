package org.raspinloop.emulator.hardwareproperties;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.raspinloop.emulator.fmi.GsonProperties;
import org.raspinloop.emulator.fmi.HardwareClassFactory;
import org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtensionProperties;
import org.raspinloop.emulator.hardwareemulation.dummy.DummyGpioBoardProperties;

import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class GsonPropertiesTest {

	@ParameterizedTest
	@CsvSource( value = {
			"{},cannot deserialize interface org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties because it does not define a field named java_type",
			"{\"java_type\": \"org.raspinloop.emulator.hardwareemulation.test.Unknown\"},cannot deserialize interface org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties subtype named org.raspinloop.emulator.hardwareemulation.test.Unknown; did you forget to register a subtype?",
			"{\"java_type\": \"org.raspinloop.emulator.hardwareemulation.test.BadGpioBoardProperties\"},cannot deserialize interface org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties subtype named org.raspinloop.emulator.hardwareemulation.test.BadGpioBoardProperties; did you forget to register a subtype?"})
	void testJson(String json, String message) {
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties deserialiser = new GsonProperties(hcl);
		Exception thrown = Assertions.assertThrows(JsonParseException.class, () -> deserialiser.read(json));
		assertThat(thrown).hasMessage(message);
	}

	@Test
	void testWriteDefaultBoard() {
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties serialiser = new GsonProperties(hcl);
		String json = serialiser.write(new DummyGpioBoardProperties());
		assertThatJson(json).node("componentName").isEqualTo("Dummy board used for test");
	}

	@Test
	void testReadDefaultBoard() throws IOException {
		File jsonConfigFileName = new File(getClass().getClassLoader().getResource("defaultDummyBoard.json").getFile());
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties deserialiser = new GsonProperties(hcl);
		BoardHardwareProperties boardHardwareProperties = deserialiser.read(Files.readString(jsonConfigFileName.toPath()));
		assertThat(boardHardwareProperties.getGuid()).isEqualTo("1234-1234-1234");
		assertThat(boardHardwareProperties.getComponentName()).isEqualTo("Dummy board used for test");
		assertThat(boardHardwareProperties.getAllComponents()).isEmpty();
		assertThat(boardHardwareProperties.getInputPins()).isEmpty();
		assertThat(boardHardwareProperties.getOutputPins()).isEmpty();
		assertThat(boardHardwareProperties.getUnUsedPins()).hasSize(6);
	}

	@Test
	void testWriteConfiguredBoardOnly() {
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties serialiser = new GsonProperties(hcl);
		DummyGpioBoardProperties board = new DummyGpioBoardProperties();
		board.setComponentName("My Custom Board!");
		List<Pin> pinsToConfigure = new ArrayList<>(board.getUnUsedPins());
		try {
			board.useInputPin(pinsToConfigure.get(0));
			board.useOutputPin(pinsToConfigure.get(2));

		} catch (AlreadyUsedPin e) {
			log.error(e.getMessage());
		}

		String json = serialiser.write(board);

		assertThatJson(json).node("componentName").isEqualTo("My Custom Board!");
		assertThatJson(json).node("inputPins").isArray().hasSize(1);
		assertThatJson(json).node("unusedPins").isArray().hasSize(4);
		assertThatJson(json).node("outputPins").isArray().hasSize(1);
	}

	@Test
	void testReadConfiguredBoard() throws IOException {
		File jsonConfigFileName = new File(
				getClass().getClassLoader().getResource("configuredDummyBoard.json").getFile());
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties deserialiser = new GsonProperties(hcl);
		BoardHardwareProperties boardHardwareProperties = deserialiser.read(Files.readString(jsonConfigFileName.toPath()));
		assertThat(boardHardwareProperties.getGuid()).isEqualTo("1234-1234-1234");
		assertThat(boardHardwareProperties.getComponentName()).isEqualTo("My Custom Board!");
		assertThat(boardHardwareProperties.getAllComponents()).isEmpty();
		assertThat(boardHardwareProperties.getInputPins()).hasSize(1);
		assertThat(boardHardwareProperties.getOutputPins()).hasSize(1);
		assertThat(boardHardwareProperties.getUnUsedPins()).hasSize(4);
	}

	@Test
	void testWriteConfiguredBoardWithComponent() {
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties serialiser = new GsonProperties(hcl);
		DummyGpioBoardProperties board = new DummyGpioBoardProperties();
		board.setComponentName("My Custom Board!");
		List<Pin> pinsToConfigure = new ArrayList<>(board.getUnUsedPins());
		try {
			board.useInputPin(pinsToConfigure.get(0));
			board.useOutputPin(pinsToConfigure.get(1));

		} catch (AlreadyUsedPin e) {
			log.error(e.getMessage());
		}
		board.addGPIOComponent(new DummyBoardExtensionProperties());

		String json = serialiser.write(board);

		assertThatJson(json).node("componentName").isEqualTo("My Custom Board!");
		assertThatJson(json).node("supportedPins").isArray().hasSize(6);
		assertThatJson(json).node("unusedPins").isArray().hasSize(2);;
		assertThatJson(json).node("inputPins").isArray().hasSize(1);
		assertThatJson(json).node("outputPins").isArray().hasSize(1);
		assertThatJson(json).node("unusedPins").isArray().hasSize(2); // = 6 - 1(input) - 1(output) - 2(used by extentions)
		assertThatJson(json).node("extentionComponents").isArray().hasSize(1);
		assertThatJson(json).node("extentionComponents").isArray().hasSize(1);
		assertThatJson(json).node("extentionComponents[0].componentName").isEqualTo("My Dummy Hardware Extention");
		assertThatJson(json).node("extentionComponents[0].type").isEqualTo("Hardware_pluged_in_pin_header");
		assertThatJson(json).node("extentionComponents[0].guid").isEqualTo("9999-9999-9999");
		assertThatJson(json).node("extentionComponents[0].implementationClassName")
				.isEqualTo("org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtension");
		assertThatJson(json).node("extentionComponents[0].simulatedProviderName").isEqualTo("Dummy Hardware Extention");
		assertThatJson(json).node("extentionComponents[0].coeficient").isEqualTo(3.14);
	}

	@Test
	void testReadConfiguredBoardWithComponent() throws IOException {
		File jsonConfigFileName = new File(
				getClass().getClassLoader().getResource("configuredDummyBoardWithComponent.json").getFile());
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties deserialiser = new GsonProperties(hcl);
		BoardHardwareProperties boardHardwareProperties = deserialiser.read(Files.readString(jsonConfigFileName.toPath()));
		assertThat(boardHardwareProperties.getGuid()).isEqualTo("1234-1234-1234");
		assertThat(boardHardwareProperties.getComponentName()).isEqualTo("My Custom Board!");
		assertThat(boardHardwareProperties.getAllComponents()).hasSize(1);
		assertThat(boardHardwareProperties.getInputPins()).hasSize(1);
		assertThat(boardHardwareProperties.getOutputPins()).hasSize(1);
		assertThat(boardHardwareProperties.getUnUsedPins()).hasSize(3);
		assertThat(boardHardwareProperties.getGPIOComponents()).hasSize(1);
		assertThat(boardHardwareProperties.getGPIOComponents().iterator().next().getGuid()).isEqualTo("9999-9999-9999");
		assertThat(boardHardwareProperties.getGPIOComponents().iterator().next().getImplementationClassName()).isEqualTo("org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtension");
		assertThat(boardHardwareProperties.getGPIOComponents().iterator().next().getComponentName()).isEqualTo("My Dummy Hardware Extention");
		assertThat(boardHardwareProperties.getGPIOComponents().iterator().next().getType()).isEqualTo("Hardware_pluged_in_pin_header");
		assertThat(((DummyBoardExtensionProperties) boardHardwareProperties.getGPIOComponents().iterator().next()).getCoeficient()).isCloseTo(3.14, offset(0.001));
	}

}
