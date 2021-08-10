package org.raspinloop.orchestrator.data;

import lombok.Value;

@Value
public class Connector {

	public enum KindEnum {
		INPUT, OUTPUT;
	}
	
	public enum TypeEnum {
		REAL, INTEGER, BOOLEAN;
	}
	
	String name;
	KindEnum kind;
	TypeEnum type;
}
