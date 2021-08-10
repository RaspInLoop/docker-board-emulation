package org.raspinloop.orchestrator.data.document;

import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Testbench {

	@Id
	private String id = null;

	private String name = null;

	private SystemUnderTest systemUnderTest = null;

	private Simulation simulation = null;

	private List<String> previousTestRuns = null;
}
