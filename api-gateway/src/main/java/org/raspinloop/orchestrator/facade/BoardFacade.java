package org.raspinloop.orchestrator.facade;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.raspinloop.orchestrator.api.BadRequestException;
import org.raspinloop.orchestrator.api.InternalErrorException;
import org.raspinloop.orchestrator.api.NotFoundException;
import org.raspinloop.orchestrator.business.BoardBusiness;
import org.raspinloop.orchestrator.business.InvalidBoardProperties;
import org.raspinloop.orchestrator.business.InvalidId;
import org.raspinloop.orchestrator.data.document.Board;
import org.raspinloop.orchestrator.model.BoardIdResponse;
import org.raspinloop.orchestrator.model.Connectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/**
 * This service operate between the restConstroller (with its own model (DTO)) 
 * and business classes which handle database's documents.
 * 
 * Some Non business assertion can be made here.
 * @author fmahiant
 *
 */
public class BoardFacade {

	@Autowired
	BoardBusiness boardBusiness;
	
	@Autowired
	private Mapper mapper;
	
	public BoardIdResponse add(@Valid Map<String, Object> body) throws BadRequestException {
		Board board = new Board().setProperties(body);
		try {
			return new BoardIdResponse().id(boardBusiness.add(board));
		} catch (InvalidBoardProperties e) {
			throw new BadRequestException(400, e.getMessage());
		}
	}

	public Connectors getConnectors(String boardId) throws NotFoundException, InternalErrorException {
		List<org.raspinloop.orchestrator.data.Connector> connectors;
		try {
			connectors = boardBusiness.getConnectors(boardId);
			if (connectors.isEmpty()) {
				throw new NotFoundException(404, "Cannot found board with id "+ boardId);
			}
			return mapper.mapConnectors(connectors);
		} catch (InvalidId  e) {
			throw new NotFoundException(404, "Cannot found board with id "+ boardId);
		} catch (InvalidBoardProperties e) {
			throw new InternalErrorException(500, "Invalid properties stored for board " + boardId + " :" + e.getMessage());
		}
	}

	public void update(String boardId, @Valid Map<String, Object> body) throws NotFoundException {
		try {
			boardBusiness.update(boardId, body);
		} catch (InvalidId e) {
			throw new NotFoundException(404, "Cannot update board: "+ e.getMessage());
		}
	}

	public Map<String, Object> get(String boardId) throws NotFoundException {
		Optional<Board> board = boardBusiness.get(boardId);
		if (board.isEmpty()) {
			throw new NotFoundException(404, "Cannot found board with id "+ boardId);
		}
		return board.get().getProperties();
	}

}
