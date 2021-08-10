package org.raspinloop.orchestrator.data.document;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Connection {

	@Id
	private String id;

	public enum ConnectedElment {
		MODEL, BOARD;
	}

	private ConnectedElment startElement;
	private String startConnector;
	private ConnectedElment endElement;
	private String endConnector;

}
