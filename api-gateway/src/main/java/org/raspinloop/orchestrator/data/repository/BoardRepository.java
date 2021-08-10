package org.raspinloop.orchestrator.data.repository;

import org.raspinloop.orchestrator.data.document.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {
}
