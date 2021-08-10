package org.raspinloop.orchestrator.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.raspinloop.orchestrator.facade.BoardFacade;
import org.raspinloop.orchestrator.model.BoardIdResponse;
import org.raspinloop.orchestrator.model.Connectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")
@RestController
public class BoardApiController implements BoardApi {

	private final HttpServletRequest request;

	@Autowired
	BoardFacade boardFacade;

	@org.springframework.beans.factory.annotation.Autowired
	public BoardApiController(HttpServletRequest request) {
		this.request = request;
	}

	public ResponseEntity<BoardIdResponse> addBoard(
			@Parameter(in = ParameterIn.DEFAULT, description = "Board item to add", schema = @Schema()) @Valid @RequestBody Map<String, Object> body) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<BoardIdResponse>(boardFacade.add(body), HttpStatus.CREATED);
			} catch (BadRequestException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
			}
		}

		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,"We have still some work to do to implement this method");
	}

	public ResponseEntity<Connectors> readBoardConnectors(
			@Parameter(in = ParameterIn.PATH, description = "Board ID", required = true, schema = @Schema()) @PathVariable("boardId") String boardId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<Connectors>(boardFacade.getConnectors(boardId), HttpStatus.OK);
			} catch (NotFoundException e) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
			} catch (InternalErrorException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
			}
		}

		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,"We have still some work to do to implement this method");
	}

	public ResponseEntity<Void> updateBoard(
			@Parameter(in = ParameterIn.PATH, description = "Board ID", required = true, schema = @Schema()) @PathVariable("boardId") String boardId,
			@Parameter(in = ParameterIn.DEFAULT, description = "Board item to add", schema = @Schema()) @Valid @RequestBody Map<String, Object> body) {
		try {
			boardFacade.update(boardId, body);
		} catch (NotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Map<String, Object>> readBoard(String boardId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<Map<String, Object>>(boardFacade.get(boardId), HttpStatus.OK);
			} catch (NotFoundException e) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,"We have still some work to do to implement this method");
	}

}
