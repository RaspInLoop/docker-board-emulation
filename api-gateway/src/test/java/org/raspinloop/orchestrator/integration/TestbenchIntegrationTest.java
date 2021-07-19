package org.raspinloop.orchestrator.integration;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.raspinloop.orchestrator.data.document.Connection;
import org.raspinloop.orchestrator.data.document.Connection.ConnectedElment;
import org.raspinloop.orchestrator.data.document.Probe;
import org.raspinloop.orchestrator.data.document.Probe.Scale;
import org.raspinloop.orchestrator.data.document.Probe.SignalKind;
import org.raspinloop.orchestrator.data.document.Simulation;
import org.raspinloop.orchestrator.data.document.SystemUnderTest;
import org.raspinloop.orchestrator.data.document.Testbench;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TestbenchIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	ObjectMapper jsonMapper;

	private String newTestBench = "{\n" + "  \"name\": \"My simple project\",\n" + "  \"systemUnderTest\": {\n"
			+ "    \"boardRef\": 84,\n" + "    \"publicKey\": \"ssh-ed25519 AAC3+if5Sc4wJ/fr+pqXKr\",\n"
			+ "    \"access\": \"ssh://em-587.ssh-access.default.svc.raspinloop.org\",\n"
			+ "    \"id\": \"408-867-5309\"\n" + "  },\n" + "  \"simulation\": {\n" + "    \"fmuRef\": 42,\n"
			+ "    \"instrument\": [\n" + "      {\n" + "        \"signalName\": \"temperature 3\",\n"
			+ "        \"signalKind\": \"float\",\n" + "        \"min\": 0,\n" + "        \"max\": 37.5,\n"
			+ "        \"scale\": \"defined\"\n" + "      }\n" + "    ],\n" + "    \"connections\": [\n" + "      {\n"
			+ "        \"startElement\": \"model\",\n" + "        \"startConnector\": \"temp_ext\",\n"
			+ "        \"endElement\": \"board\",\n" + "        \"endConnector\": \"AN_01\"\n" + "      }\n" + "    ]\n"
			+ "  }\n" + "}";

	@Test
	void testBenchPost() throws Exception {
		String response = mockMvc
				.perform(post("/testbench")
				.contentType("application/json").content(newTestBench)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		String id = jsonMapper.readTree(response).at("/id").asText();
		Testbench stored = mongoTemplate.findById(id, Testbench.class);
		assertNotNull(stored);
		assertEquals("My simple project", stored.getName());
		assertNotNull(stored.getSystemUnderTest());
		assertEquals("84", stored.getSystemUnderTest().getBoardRef());
		assertEquals("ssh-ed25519 AAC3+if5Sc4wJ/fr+pqXKr", stored.getSystemUnderTest().getPublicKey());
		assertEquals("ssh://em-587.ssh-access.default.svc.raspinloop.org", stored.getSystemUnderTest().getAccess());
		assertEquals("408-867-5309", stored.getSystemUnderTest().getId());
		assertNotNull(stored.getSimulation());
		assertEquals("42", stored.getSimulation().getFmuRef());
		assertNotNull(stored.getSimulation().getConnections());
		assertEquals(1, stored.getSimulation().getConnections().size());
		Connection connection = stored.getSimulation().getConnections().stream().findFirst().get();
		assertEquals(ConnectedElment.MODEL, connection.getStartElement());
		assertEquals("temp_ext", connection.getStartConnector());
		assertEquals(ConnectedElment.BOARD, connection.getEndElement());
		assertEquals("AN_01", connection.getEndConnector());
		assertNotNull(stored.getSimulation().getInstrument());
		assertEquals(1, stored.getSimulation().getInstrument().size());
		Probe probe = stored.getSimulation().getInstrument().stream().findFirst().get();
		assertEquals(SignalKind.FLOAT, probe.getSignalKind());
		assertEquals("temperature 3", probe.getSignalName());
		assertEquals(Scale.DEFINED, probe.getScale());
		assertEquals(new BigDecimal(0), probe.getMin());
		assertEquals(new BigDecimal(37.5), probe.getMax());
	}

	@Test
	void testBenchGet() throws UnsupportedEncodingException, Exception {

		Testbench testBench = new Testbench()
				.setName(
						"MyBench")
				.setSystemUnderTest(
						new SystemUnderTest().setAccess("ssh://bla-bla-bla").setBoardRef(
								"GUID-BOARD").setPublicKey(
										"PUBKEY"))
				.setSimulation(new Simulation().setFmuRef("FMUREF")
						.setConnections(Arrays.asList(
								new Connection().setStartElement(ConnectedElment.BOARD).setStartConnector("CON1")
										.setEndElement(ConnectedElment.MODEL).setEndConnector("OUT1")))
						.setInstrument(Arrays.asList(
								new Probe().setSignalName("Probe1").setSignalKind(SignalKind.FLOAT)
										.setScale(Scale.AUTO),
								new Probe().setSignalName("Probe2").setSignalKind(SignalKind.INTEGER)
										.setScale(Scale.AUTO))));

		Testbench stored = mongoTemplate.save(testBench);

		mockMvc.perform(get("/testbench/{testBenchId}", stored.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(stored.getId())))
				.andExpect(jsonPath("$.name", is("MyBench")))
				.andExpect(jsonPath("$.systemUnderTest.boardRef", is("GUID-BOARD")))
				.andExpect(jsonPath("$.systemUnderTest.publicKey", is("PUBKEY")))
				.andExpect(jsonPath("$.systemUnderTest.access", is("ssh://bla-bla-bla")))
				.andExpect(jsonPath("$.simulation.fmuRef", is("FMUREF")))
				.andExpect(jsonPath("$.simulation.instrument.length()", is(2)))
				.andExpect(jsonPath("$.simulation.connections.length()", is(1)));
	}

	@Test
	void testReadConnections() throws UnsupportedEncodingException, Exception {
		Testbench testBench = new Testbench()
				.setName(
						"MyBench")
				.setSystemUnderTest(
						new SystemUnderTest().setAccess("ssh://bla-bla-bla").setBoardRef(
								"GUID-BOARD").setPublicKey(
										"PUBKEY"))
				.setSimulation(new Simulation().setFmuRef("FMUREF")
						.setConnections(Arrays.asList(
								new Connection().setStartElement(ConnectedElment.BOARD).setStartConnector("CON1")
										.setEndElement(ConnectedElment.MODEL).setEndConnector("OUT1"),
								new Connection().setStartElement(ConnectedElment.BOARD).setStartConnector("CON2")
										.setEndElement(ConnectedElment.MODEL).setEndConnector("OUT2"),
								new Connection().setStartElement(ConnectedElment.MODEL).setStartConnector("IN1")
										.setEndElement(ConnectedElment.BOARD).setEndConnector("ANA1")))
						.setInstrument(Arrays.asList(
								new Probe().setSignalName("Probe1").setSignalKind(SignalKind.FLOAT)
										.setScale(Scale.AUTO),
								new Probe().setSignalName("Probe2").setSignalKind(SignalKind.INTEGER)
										.setScale(Scale.AUTO))));

		Testbench stored = mongoTemplate.save(testBench);

		mockMvc.perform(get("/testbench/{testBenchId}/simulation/connections", stored.getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(3)))
				.andExpect(jsonPath("$[0].startElement", is("board")))
				.andExpect(jsonPath("$[0].startConnector", is("CON1")))
				.andExpect(jsonPath("$[0].endElement", is("model")))
				.andExpect(jsonPath("$[0].endConnector", is("OUT1")))
				.andExpect(jsonPath("$[1].startElement", is("board")))
				.andExpect(jsonPath("$[1].startConnector", is("CON2")))
				.andExpect(jsonPath("$[1].endElement", is("model")))
				.andExpect(jsonPath("$[1].endConnector", is("OUT2")))
				.andExpect(jsonPath("$[2].startElement", is("model")))
				.andExpect(jsonPath("$[2].startConnector", is("IN1")))
				.andExpect(jsonPath("$[2].endElement", is("board")))
				.andExpect(jsonPath("$[2].endConnector", is("ANA1")));
	}

	@Test
	void testReadInstrument() throws Exception {

		Testbench testBench = new Testbench()
				.setName(
						"MyBench")
				.setSystemUnderTest(
						new SystemUnderTest().setAccess("ssh://bla-bla-bla").setBoardRef(
								"GUID-BOARD").setPublicKey(
										"PUBKEY"))
				.setSimulation(new Simulation().setFmuRef("FMUREF")
						.setConnections(Arrays.asList(
								new Connection().setStartElement(ConnectedElment.BOARD).setStartConnector("CON1")
										.setEndElement(ConnectedElment.MODEL).setEndConnector("OUT1"),
								new Connection()))
						.setInstrument(Arrays.asList(
								new Probe().setSignalName("Probe1").setSignalKind(SignalKind.FLOAT)
										.setScale(Scale.AUTO),
								new Probe().setSignalName("Probe2").setSignalKind(SignalKind.INTEGER)
										.setScale(Scale.AUTO),
								new Probe().setSignalName("Probe3").setSignalKind(SignalKind.BOOLEAN)
										.setScale(Scale.DEFINED).setMin(BigDecimal.ZERO).setMax(BigDecimal.ONE),
								new Probe().setSignalName("Probe4").setSignalKind(SignalKind.FLOAT)
										.setScale(Scale.DEFINED).setMin(BigDecimal.ZERO)
										.setMax(new BigDecimal("3.1415")))));

		Testbench stored = mongoTemplate.save(testBench);

		mockMvc.perform(get("/testbench/{testBenchId}/simulation/instrument", stored.getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(4)))
				.andExpect(jsonPath("$[0].signalName", is("Probe1")))
				.andExpect(jsonPath("$[0].signalKind", is("float")))
				.andExpect(jsonPath("$[0].min", emptyOrNullString()))
				.andExpect(jsonPath("$[0].max", emptyOrNullString())).andExpect(jsonPath("$[0].scale", is("auto")))

				.andExpect(jsonPath("$[1].signalName", is("Probe2")))
				.andExpect(jsonPath("$[1].signalKind", is("integer")))
				.andExpect(jsonPath("$[1].min", emptyOrNullString()))
				.andExpect(jsonPath("$[1].max", emptyOrNullString())).andExpect(jsonPath("$[1].scale", is("auto")))

				.andExpect(jsonPath("$[2].signalName", is("Probe3")))
				.andExpect(jsonPath("$[2].signalKind", is("boolean"))).andExpect(jsonPath("$[2].min", is(0)))
				.andExpect(jsonPath("$[2].max", is(1))).andExpect(jsonPath("$[2].scale", is("defined")))

				.andExpect(jsonPath("$[3].signalName", is("Probe4")))
				.andExpect(jsonPath("$[3].signalKind", is("float"))).andExpect(jsonPath("$[3].min", is(0)))
				.andExpect(jsonPath("$[3].max", is(3.1415))).andExpect(jsonPath("$[3].scale", is("defined")));
	}

	@Test
	void testReadInstrumentConnections() {
		fail("Not implemented");
	}

	@Test
	void testSimulationStatus() {
		fail("Not implemented");
	}

	@Test
	void testStartSimulation() {
		fail("Not implemented");
	}

	@Test
	void testStopSimulation() {
		fail("Not implemented");
	}

	@Test
	void testUpdateSimulation() {
		fail("Not implemented");
	}
}
