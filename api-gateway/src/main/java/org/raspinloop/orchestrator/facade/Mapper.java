package org.raspinloop.orchestrator.facade;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.raspinloop.orchestrator.data.Connector;
import org.raspinloop.orchestrator.data.document.Connection;
import org.raspinloop.orchestrator.data.document.Probe;
import org.raspinloop.orchestrator.data.document.Simulation;
import org.raspinloop.orchestrator.data.document.Testbench;
import org.raspinloop.orchestrator.model.Connections;
import org.raspinloop.orchestrator.model.Connectors;
import org.raspinloop.orchestrator.model.Instrument;
import org.raspinloop.orchestrator.model.TestBenchItem;
import org.springframework.stereotype.Service;

@Service
public class Mapper {
	private ModelMapper mm = new ModelMapper();

	public Mapper() {
		mm.typeMap(Simulation.class, org.raspinloop.orchestrator.model.Simulation.class).
		addMappings(mapper -> mapper.skip(org.raspinloop.orchestrator.model.Simulation::setInstrument)).
		addMappings(mapper -> mapper.skip(org.raspinloop.orchestrator.model.Simulation::setConnections));
	}

	public Connections mapConnections(List<Connection> connections) {
		Connections cons = new Connections();
		connections.stream().map(c ->  mm.map(c, org.raspinloop.orchestrator.model.Connection.class)).forEach(cons::add);
		return cons;
	}

	public Instrument mapProbes(List<Probe> instrument) {
		Instrument inst = new Instrument();
		instrument.stream().map(p -> mm.map(p, org.raspinloop.orchestrator.model.Probe.class)).forEach(inst::add);
		return inst;
	}

	public Testbench map(@Valid TestBenchItem testbench) {
		return mm.map(testbench, Testbench.class);
	}

	public TestBenchItem map(Testbench testbench) {
		TestBenchItem item = mm.map(testbench, TestBenchItem.class);
		// TODO: must be added in modelmapper config.
		Connections cons = new Connections();
		testbench.getSimulation().getConnections().stream()
		.map(c -> mm.map(c, org.raspinloop.orchestrator.model.Connection.class)).forEach(cons::add);
		item.getSimulation().setConnections(cons);
		Instrument inst = new Instrument(); 
		testbench.getSimulation().getInstrument().stream()
		.map(p -> mm.map(p, org.raspinloop.orchestrator.model.Probe.class)).forEach(inst::add);
		item.getSimulation().setInstrument(inst);
		return item;
	}

	public Connectors mapConnectors(List<Connector> dataConnectors) {
		Connectors connectors = new Connectors();
		dataConnectors.stream().map(c -> mm.map(c, org.raspinloop.orchestrator.model.Connector.class)).forEach(connectors::add);
		return connectors;
	}
}
