package org.raspinloop.orchestrator.integration;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.raspinloop.orchestrator.data.document.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class BoardIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	ObjectMapper jsonMapper;

	private String newBoard = "{\n"
			+ "  \"java_type\": \"org.raspinloop.emulator.hardwareemulation.dummy.DummyGpioBoardProperties\",\n"
			+ "  \"type\": \"TEST DUMY BOARD\",\n" + "  \"componentName\": \"My Custom Board!\",\n"
			+ "  \"simulatedProviderName\": \"Dummy board Provider\",\n" + "  \"guid\": \"1234-1234-1234\",\n"
			+ "  \"unusedPins\": [\n" + "    {\n" + "      \"provider\": \"Dummy board Provider\",\n"
			+ "      \"address\": 1,\n" + "      \"name\": \"IO1\",\n" + "      \"supportedPinMode\": [],\n"
			+ "      \"supportedResistance\": [],\n" + "      \"supportedEdges\": []\n" + "    },\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 3,\n"
			+ "      \"name\": \"IO3\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    },\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 4,\n"
			+ "      \"name\": \"IO4\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    }\n" + "  ],\n" + "  \"outputPins\": [\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 2,\n"
			+ "      \"name\": \"IO2\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    }\n" + "  ],\n" + "  \"inputPins\": [\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 0,\n"
			+ "      \"name\": \"IO0\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    }\n" + "  ],\n" + "  \"extentionComponents\": [\n" + "    {\n"
			+ "      \"java_type\": \"org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtensionProperties\",\n"
			+ "      \"componentName\": \"My Dummy Hardware Extention\",\n"
			+ "      \"type\": \"Hardware_pluged_in_pin_header\",\n" + "      \"guid\": \"9999-9999-9999\",\n"
			+ "      \"implementationClassName\": \"org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtension\",\n"
			+ "      \"simulatedProviderName\": \"Dummy Hardware Extention\",\n" + "      \"coeficient\": 3.14\n"
			+ "    }\n" + "  ],\n" + "  \"uartComponents\": [],\n" + "  \"spiComponents\": [],\n"
			+ "  \"i2cComponents\": []\n" + "}";
	
	private String updatedBoard = "{\n"
			+ "  \"java_type\": \"org.raspinloop.emulator.hardwareemulation.dummy.DummyGpioBoardProperties\",\n"
			+ "  \"type\": \"TEST DUMY BOARD\",\n" + "  \"componentName\": \"My Custom Board!\",\n"
			+ "  \"simulatedProviderName\": \"Dummy board Provider\",\n" + "  \"guid\": \"1234-1234-1234\",\n"
			+ "  \"unusedPins\": [\n" + "    {\n" + "      \"provider\": \"Dummy board Provider\",\n"
			+ "      \"address\": 1,\n" + "      \"name\": \"I1\",\n" + "      \"supportedPinMode\": [],\n"
			+ "      \"supportedResistance\": [],\n" + "      \"supportedEdges\": []\n" + "    },\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 3,\n"
			+ "      \"name\": \"IO3\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    },\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 4,\n"
			+ "      \"name\": \"IO4\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    }\n" + "  ],\n" + "  \"outputPins\": [\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 5,\n"
			+ "      \"name\": \"IO5\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    }\n" + "  ],\n" + "  \"inputPins\": [\n" + "    {\n"
			+ "      \"provider\": \"Dummy board Provider\",\n" + "      \"address\": 0,\n"
			+ "      \"name\": \"IO0\",\n" + "      \"supportedPinMode\": [],\n" + "      \"supportedResistance\": [],\n"
			+ "      \"supportedEdges\": []\n" + "    }\n" + "  ],\n" + "  \"extentionComponents\": [\n" + "    {\n"
			+ "      \"java_type\": \"org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtensionProperties\",\n"
			+ "      \"componentName\": \"My Dummy Hardware Extention\",\n"
			+ "      \"type\": \"Hardware_pluged_in_pin_header\",\n" + "      \"guid\": \"9999-9999-9999\",\n"
			+ "      \"implementationClassName\": \"org.raspinloop.emulator.hardwareemulation.dummy.DummyBoardExtension\",\n"
			+ "      \"simulatedProviderName\": \"Dummy Hardware Extention\",\n" + "      \"coeficient\": 3.14\n"
			+ "    }\n" + "  ],\n" + "  \"uartComponents\": [],\n" + "  \"spiComponents\": [],\n"
			+ "  \"i2cComponents\": []\n" + "}";

	@Test
	void testAddBoard() throws UnsupportedEncodingException, Exception {
		String response = mockMvc
				.perform(post("/board").contentType("application/json").content(newBoard)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		String id = jsonMapper.readTree(response).at("/id").asText();
		Board stored = mongoTemplate.findById(id, Board.class);
		assertEquals("My Custom Board!", stored.getProperties().get("componentName"));
	}

	@Test
	void testReadBoard() throws Exception {
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};
		HashMap<String, Object> boardMap = jsonMapper.readValue(newBoard, typeRef);
		Board board = new Board().setProperties(boardMap);
		Board saved = mongoTemplate.save(board);
		mockMvc.perform(get("/board/{boardId}", saved.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.componentName", is("My Custom Board!")))
				.andExpect(jsonPath("$.outputPins[0].address", is(2)))
				.andExpect(jsonPath("$.outputPins[0].name", is("IO2")))
				.andExpect(jsonPath("$.inputPins[0].address", is(0)))
				.andExpect(jsonPath("$.inputPins[0].name", is("IO0")));
	}

	@Test
	void testUpdateBoard() throws Exception {
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};
		HashMap<String, Object> boardMap = jsonMapper.readValue(newBoard, typeRef);
		Board board = new Board().setProperties(boardMap);
		Board saved = mongoTemplate.save(board);
		mockMvc.perform(put("/board/{boardId}", saved.getId())
				.contentType("application/json").content(updatedBoard)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		mockMvc.perform(get("/board/{boardId}", saved.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.componentName", is("My Custom Board!")))
				.andExpect(jsonPath("$.outputPins[0].address", is(5)))
				.andExpect(jsonPath("$.outputPins[0].name", is("IO5")))
				.andExpect(jsonPath("$.inputPins[0].address", is(0)))
				.andExpect(jsonPath("$.inputPins[0].name", is("IO0")));
	}

	@Test
	void testReadeBoardConnectors() throws UnsupportedEncodingException, Exception {
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};
		HashMap<String, Object> boardMap = jsonMapper.readValue(newBoard, typeRef);
		Board board = new Board().setProperties(boardMap);
		Board saved = mongoTemplate.save(board);
		mockMvc.perform(get("/board/{boardId}/connectors", saved.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(4)))
				.andExpect(jsonPath("$[?(@.name==\"IO0\")].kind", is("input")))
				.andExpect(jsonPath("$[?(@.name==\"IO2\")].kind", is("output")));
	}
}
