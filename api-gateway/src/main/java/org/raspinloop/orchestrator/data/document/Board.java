package org.raspinloop.orchestrator.data.document;
import java.util.Map;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Board {

  @Id
  private String id;
  private Map<String, Object> properties;

}
