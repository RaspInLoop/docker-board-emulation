package org.raspinloop.orchestrator.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.raspinloop.orchestrator.facade.TestbenchFacade;
import org.raspinloop.orchestrator.model.Connections;
import org.raspinloop.orchestrator.model.Instrument;
import org.raspinloop.orchestrator.model.Simulation;
import org.raspinloop.orchestrator.model.SimulationStartParam;
import org.raspinloop.orchestrator.model.SimulationStatus;
import org.raspinloop.orchestrator.model.TestBenchIdResponse;
import org.raspinloop.orchestrator.model.TestBenchItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")
@RestController
public class TestbenchApiController implements TestbenchApi {

	private static final Logger log = LoggerFactory.getLogger(TestbenchApiController.class);

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@Autowired
	TestbenchFacade facade;

	@org.springframework.beans.factory.annotation.Autowired
	public TestbenchApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	public ResponseEntity<TestBenchIdResponse> addTestBench(
			@Parameter(in = ParameterIn.DEFAULT, description = "TestBench item to add", schema = @Schema()) @Valid @RequestBody TestBenchItem body) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			return new ResponseEntity<TestBenchIdResponse>(facade.add(body), HttpStatus.CREATED);
		}

		return new ResponseEntity<TestBenchIdResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<Connections> readConnections(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<Connections>(facade.getConnections(testBenchId), HttpStatus.OK);
			} catch (NotFoundException e) {
				return new ResponseEntity<Connections>(HttpStatus.NOT_FOUND);
			}
		}

		return new ResponseEntity<Connections>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<Instrument> readInstrument(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<Instrument>(facade.getInstruments(testBenchId), HttpStatus.OK);
			} catch (NotFoundException e) {
				return new ResponseEntity<Instrument>(HttpStatus.NOT_FOUND);
			}
		}

		return new ResponseEntity<Instrument>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<Connections> readInstrumentConnections(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<Connections>(facade.getInstrumentsConnections(testBenchId), HttpStatus.OK);
			} catch (NotFoundException e) {
				return new ResponseEntity<Connections>(HttpStatus.NOT_FOUND);
			}
		}

		return new ResponseEntity<Connections>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<TestBenchItem> readTestbench(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<TestBenchItem>(facade.get(testBenchId), HttpStatus.OK);
			} catch (NotFoundException e) {
				return new ResponseEntity<TestBenchItem>(HttpStatus.NOT_FOUND);
			}
		}

		return new ResponseEntity<TestBenchItem>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<SimulationStatus> simulationStatus(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<SimulationStatus>(objectMapper.readValue(
						"{\n  \"currentTestRun\" : {\n    \"currentStep\" : 7.3,\n    \"StepSize\" : 0.1,\n    \"stopTime\" : 100,\n    \"probes\" : [ {\n      \"name\" : \"Temperature\",\n      \"y-scale\" : [ -100, 100 ],\n      \"x-scale\" : [ 0, 10 ],\n      \"points\" : [ 0.8008281904610115, 0.8008281904610115 ]\n    }, {\n      \"name\" : \"Temperature\",\n      \"y-scale\" : [ -100, 100 ],\n      \"x-scale\" : [ 0, 10 ],\n      \"points\" : [ 0.8008281904610115, 0.8008281904610115 ]\n    } ]\n  },\n  \"status\" : \"running\"\n}",
						SimulationStatus.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<SimulationStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<SimulationStatus>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<Void> startSimulation(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId,
			@Parameter(in = ParameterIn.DEFAULT, description = "Simulation to configure", schema = @Schema()) @Valid @RequestBody SimulationStartParam body) {
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<Void> stopSimulation(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId) {
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<Void> updateSimulation(
			@Parameter(in = ParameterIn.PATH, description = "TestBench ID", required = true, schema = @Schema()) @PathVariable("testBenchId") String testBenchId,
			@Parameter(in = ParameterIn.DEFAULT, description = "Simulation to configure", schema = @Schema()) @Valid @RequestBody Simulation body) {
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

}
