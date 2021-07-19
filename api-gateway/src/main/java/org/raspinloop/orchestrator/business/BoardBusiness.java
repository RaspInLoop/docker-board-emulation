package org.raspinloop.orchestrator.business;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.raspinloop.emulator.fmi.ClassLoaderBuilderFactory;
import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.GsonProperties;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.HardwareClassFactory;
import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulationException;
import org.raspinloop.emulator.hardwareemulation.ReferenceNotFound;
import org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties;
import org.raspinloop.orchestrator.data.Connector;
import org.raspinloop.orchestrator.data.Connector.KindEnum;
import org.raspinloop.orchestrator.data.Connector.TypeEnum;
import org.raspinloop.orchestrator.data.document.Board;
import org.raspinloop.orchestrator.data.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BoardBusiness {

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	BoardRepository repo;
	public String add(Board board) throws InvalidBoardProperties {
		validate(board);
		Board saved = repo.save(board);
		return saved.getId();
	}

	public List<Connector> getConnectors(String boardId) throws InvalidId, InvalidBoardProperties {
		Optional<Board> board = repo.findById(boardId);
		if (board.isEmpty()) {
			throw new InvalidId("Cannot find board for id {}", boardId);
		}
		BoardHardwareProperties hardwareProperties = buildBoardHardwareProperties(board.get());
		HardwareBuilderFactory hbf = new ClassLoaderBuilderFactory();
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		HardwareEmulation hardwareEmulation;
		try {
			hardwareEmulation = hbf.createBuilder(hardwareProperties, referenceRegister).build(null);
			return hardwareEmulation.getModelVariables().stream()
					.map(this::toConnector).collect(Collectors.toList());
		} catch (HardwareEmulationException | ReferenceNotFound e) {
			throw new InvalidBoardProperties(e);
		}
	}

	public void update(String boardId, @Valid Map<String, Object> body) throws InvalidId {
		Optional<Board> board = repo.findById(boardId);
		if (board.isEmpty()) {
			throw new InvalidId("Cannot find board for id {}", boardId);
		}
		else
			board.get().setProperties(body);
		repo.save(board.get());
	}

	public Optional<Board> get(String boardId) {
		return repo.findById(boardId);
	}

	private void validate(Board board) throws InvalidBoardProperties {
		BoardHardwareProperties hardwareProperties = buildBoardHardwareProperties(board);
		if (hardwareProperties.getGuid() == null || hardwareProperties.getGuid().isBlank()) {
			throw new InvalidBoardProperties();
		}
			
	}

	private BoardHardwareProperties buildBoardHardwareProperties(Board board) throws InvalidBoardProperties {
		String json;
		try {
			json = mapper.writeValueAsString(board.getProperties());
		} catch (JsonProcessingException e) {
			throw new InvalidBoardProperties(e);
		}
		HardwareClassFactory hcl = HardwareClassFactory.instance();
		GsonProperties deserialiser = new GsonProperties(hcl);
		return deserialiser.read(json);
	}

	private Connector toConnector(Fmi2ScalarVariable fmi2scalarvariable) {
		
		KindEnum kind = Connector.KindEnum.valueOf(fmi2scalarvariable.getCausality().toUpperCase());
		TypeEnum type = TypeEnum.BOOLEAN; // default
		if (fmi2scalarvariable.getBoolean()!= null) {
			type = TypeEnum.BOOLEAN;
		} else if (fmi2scalarvariable.getInteger()!= null) {
			type = TypeEnum.INTEGER;
		} else if (fmi2scalarvariable.getReal()!= null) {
			type = TypeEnum.REAL;
		}
		return new Connector(fmi2scalarvariable.getName(), kind, type);
	}
}
