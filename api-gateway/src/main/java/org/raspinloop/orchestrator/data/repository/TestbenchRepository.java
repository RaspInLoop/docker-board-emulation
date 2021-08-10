package org.raspinloop.orchestrator.data.repository;

import org.raspinloop.orchestrator.data.document.Testbench;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestbenchRepository extends MongoRepository<Testbench, String> {


}
