package org.raspinloop.orchestrator.business;

import java.text.MessageFormat;

@SuppressWarnings("serial")
public class InvalidId extends Exception {

	public InvalidId(String pattern, String boardId) {
		super( MessageFormat.format(pattern, boardId));
	}

}
