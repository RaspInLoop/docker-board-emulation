package org.raspinloop.orchestrator.api;

import org.raspinloop.orchestrator.model.TestRun;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")
@RestController
public class TestrunApiController implements TestrunApi {

    private static final Logger log = LoggerFactory.getLogger(TestrunApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public TestrunApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<TestRun> readTestRunResult(@Parameter(in = ParameterIn.PATH, description = "Test Run ID", required=true, schema=@Schema()) @PathVariable("testRunId") Long testRunId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<TestRun>(objectMapper.readValue("{\n  \"currentStep\" : 7.3,\n  \"StepSize\" : 0.1,\n  \"stopTime\" : 100,\n  \"probes\" : [ {\n    \"name\" : \"Temperature\",\n    \"y-scale\" : [ -100, 100 ],\n    \"x-scale\" : [ 0, 10 ],\n    \"points\" : [ 0.8008281904610115, 0.8008281904610115 ]\n  }, {\n    \"name\" : \"Temperature\",\n    \"y-scale\" : [ -100, 100 ],\n    \"x-scale\" : [ 0, 10 ],\n    \"points\" : [ 0.8008281904610115, 0.8008281904610115 ]\n  } ]\n}", TestRun.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<TestRun>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<TestRun>(HttpStatus.NOT_IMPLEMENTED);
    }

}
