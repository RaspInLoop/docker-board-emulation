package org.raspinloop.orchestrator.data.document;

import java.math.BigDecimal;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Probe {

	public enum SignalKind {
		BOOLEAN,
		INTEGER,
		FLOAT;
	}
	
	public enum Scale {
		AUTO,
		DEFINED;
	}
	
	private String signalName = null;

	private SignalKind signalKind = null;

	private Scale scale = null;

	private BigDecimal min = null;

	private BigDecimal max = null;
}
