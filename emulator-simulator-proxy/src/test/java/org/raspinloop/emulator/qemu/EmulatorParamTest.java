package org.raspinloop.emulator.qemu;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.junit.jupiter.api.Test;
import org.raspinloop.emulator.proxyserver.qemu.EmulatorParam;
import org.raspinloop.emulator.proxyserver.qemu.Raspberrypi3bOptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmulatorParamTest {

	@Test
	void emulatorParamSerialize() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();		
		EmulatorParam parameters = new Raspberrypi3bOptions("myFile.img");
		String json =mapper.writeValueAsString(parameters);
		assertThatJson(json).node("type").isEqualTo("raspberry_pi_3b");
		assertThatJson(json).node("image").isEqualTo("myFile.img");
	}
}
