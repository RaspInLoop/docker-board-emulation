package org.raspinloop.orchestrator.data.document;

import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Simulation {
	@Id
	private String id;
	private String fmuRef = null;
	private List<Probe> instrument = null;
	private List<Connection> connections = null;
}
