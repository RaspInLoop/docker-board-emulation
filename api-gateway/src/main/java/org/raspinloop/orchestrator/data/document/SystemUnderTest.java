package org.raspinloop.orchestrator.data.document;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SystemUnderTest {
	private String boardRef = null;

	private String publicKey = null;

	private String access = null;

	@Id
	private String id;
}
