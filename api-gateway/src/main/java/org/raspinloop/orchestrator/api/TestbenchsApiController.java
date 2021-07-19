package org.raspinloop.orchestrator.api;

import org.raspinloop.orchestrator.model.TestBenchItem;
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
public class TestbenchsApiController implements TestbenchsApi {

    private static final Logger log = LoggerFactory.getLogger(TestbenchsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public TestbenchsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<TestBenchItem>> searchTestbench(@Parameter(in = ParameterIn.QUERY, description = "pass an optional search string for looking up testbench" ,schema=@Schema()) @Valid @RequestParam(value = "searchString", required = false) String searchString) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<TestBenchItem>>(objectMapper.readValue("[ {\n  \"simulation\" : {\n    \"fmuRef\" : 42,\n    \"instrument\" : [ {\n      \"signalName\" : \"temperature 3\",\n      \"min\" : 0.8008281904610115,\n      \"max\" : 37.5,\n      \"scale\" : \"defined\",\n      \"signalKind\" : \"float\"\n    }, {\n      \"signalName\" : \"temperature 3\",\n      \"min\" : 0.8008281904610115,\n      \"max\" : 37.5,\n      \"scale\" : \"defined\",\n      \"signalKind\" : \"float\"\n    } ],\n    \"connections\" : [ {\n      \"startConnector\" : \"temp_ext\",\n      \"endElement\" : \"board\",\n      \"startElement\" : \"model\",\n      \"endConnector\" : \"AN_01\"\n    }, {\n      \"startConnector\" : \"temp_ext\",\n      \"endElement\" : \"board\",\n      \"startElement\" : \"model\",\n      \"endConnector\" : \"AN_01\"\n    } ]\n  },\n  \"name\" : \"My simple project\",\n  \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n  \"systemUnderTest\" : {\n    \"access\" : \"ssh://em-587.ssh-access.default.svc.raspinloop.org\",\n    \"publicKey\" : \"ssh-ed25519 AAC3+if5Sc4wJ/fr+pqXKr\",\n    \"id\" : \"408-867-5309\",\n    \"boardRef\" : 84\n  },\n  \"previousTestRuns\" : [ 45875, 665874, 2222668, 9854785 ]\n}, {\n  \"simulation\" : {\n    \"fmuRef\" : 42,\n    \"instrument\" : [ {\n      \"signalName\" : \"temperature 3\",\n      \"min\" : 0.8008281904610115,\n      \"max\" : 37.5,\n      \"scale\" : \"defined\",\n      \"signalKind\" : \"float\"\n    }, {\n      \"signalName\" : \"temperature 3\",\n      \"min\" : 0.8008281904610115,\n      \"max\" : 37.5,\n      \"scale\" : \"defined\",\n      \"signalKind\" : \"float\"\n    } ],\n    \"connections\" : [ {\n      \"startConnector\" : \"temp_ext\",\n      \"endElement\" : \"board\",\n      \"startElement\" : \"model\",\n      \"endConnector\" : \"AN_01\"\n    }, {\n      \"startConnector\" : \"temp_ext\",\n      \"endElement\" : \"board\",\n      \"startElement\" : \"model\",\n      \"endConnector\" : \"AN_01\"\n    } ]\n  },\n  \"name\" : \"My simple project\",\n  \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n  \"systemUnderTest\" : {\n    \"access\" : \"ssh://em-587.ssh-access.default.svc.raspinloop.org\",\n    \"publicKey\" : \"ssh-ed25519 AAC3+if5Sc4wJ/fr+pqXKr\",\n    \"id\" : \"408-867-5309\",\n    \"boardRef\" : 84\n  },\n  \"previousTestRuns\" : [ 45875, 665874, 2222668, 9854785 ]\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<TestBenchItem>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<TestBenchItem>>(HttpStatus.NOT_IMPLEMENTED);
    }

}
