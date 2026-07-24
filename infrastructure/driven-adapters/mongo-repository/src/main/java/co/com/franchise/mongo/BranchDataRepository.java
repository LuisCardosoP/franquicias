package co.com.franchise.mongo;

import co.com.franchise.mongo.document.BranchDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BranchDataRepository extends ReactiveMongoRepository<BranchDocument, String> {
    Flux<BranchDocument> findByFranchiseId(String franchiseId);
}
