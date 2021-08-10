package org.raspinloop.orchestrator.business;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.raspinloop.orchestrator.data.document.Connection;
import org.raspinloop.orchestrator.data.document.Probe;
import org.raspinloop.orchestrator.data.document.Testbench;
import org.raspinloop.orchestrator.data.repository.TestbenchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestbenchBusiness {
	
	@Autowired
	TestbenchRepository repo;

	public String add(Testbench testbench) {
		return repo.save(testbench).getId();
	}

	public List<Connection> getConnections(String testBenchId) {
		Optional<Testbench> testbench = repo.findById(testBenchId);
		if (testbench.isEmpty()) {
			return Collections.emptyList();
		}
		return testbench.get().getSimulation().getConnections();
	}

	public List<Probe> getInstruments(String testBenchId) {
		Optional<Testbench> testbench = repo.findById(testBenchId);
		if (testbench.isEmpty()) {
			return Collections.emptyList();
		}
		return testbench.get().getSimulation().getInstrument();
	}

	public List<Connection> getInstrumentConnections(String testBenchId) {
		Optional<Testbench> testbench = repo.findById(testBenchId);
		if (testbench.isEmpty()) {
			return Collections.emptyList();
		}
		return testbench.get().getSimulation().getConnections();
	}

	public Optional<Testbench> get(String testBenchId) {
		return repo.findById(testBenchId);
	}

}
