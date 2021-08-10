package org.raspinloop.orchestrator.facade;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.raspinloop.orchestrator.api.NotFoundException;
import org.raspinloop.orchestrator.business.TestbenchBusiness;
import org.raspinloop.orchestrator.data.document.Testbench;
import org.raspinloop.orchestrator.model.Connection;
import org.raspinloop.orchestrator.model.Connections;
import org.raspinloop.orchestrator.model.Instrument;
import org.raspinloop.orchestrator.model.TestBenchIdResponse;
import org.raspinloop.orchestrator.model.TestBenchItem;
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
public class TestbenchFacade {

	@Autowired
	TestbenchBusiness testbenchBusiness;
	
	@Autowired
	private Mapper mapper;

	public TestBenchIdResponse add(@Valid TestBenchItem body) {
		Testbench testbench = mapper.map(body);
		return new TestBenchIdResponse().id(testbenchBusiness.add(testbench));
	}

	public Connections getConnections(String testBenchId) throws NotFoundException {
		List<org.raspinloop.orchestrator.data.document.Connection> connections = testbenchBusiness.getConnections(testBenchId);
		if (connections.isEmpty()) {
			throw new NotFoundException(404, "Cannot found testbench with id "+ testBenchId);
		}
		return mapper.mapConnections(connections);
	}

	public Instrument getInstruments(String testBenchId) throws NotFoundException {
		List<org.raspinloop.orchestrator.data.document.Probe> instrument = testbenchBusiness.getInstruments(testBenchId);
		if (instrument.isEmpty()) {
			throw new NotFoundException(404, "Cannot found testbench with id "+ testBenchId);
		}
		return mapper.mapProbes(instrument);
	}

	public Connections getInstrumentsConnections(String testBenchId) throws NotFoundException {
		List<org.raspinloop.orchestrator.data.document.Connection> connections = testbenchBusiness.getInstrumentConnections(testBenchId);
		if (connections.isEmpty()) {
			throw new NotFoundException(404, "Cannot found testbench with id "+ testBenchId);
		}
		return mapper.mapConnections(connections);
	}

	public TestBenchItem get(String testBenchId) throws NotFoundException {
		Optional<Testbench> bench = testbenchBusiness.get(testBenchId);
		if (bench.isEmpty()) {
			throw new NotFoundException(404, "Cannot found testbench with id "+ testBenchId);
		}
		return mapper.map(bench.get());
	}
	
}
