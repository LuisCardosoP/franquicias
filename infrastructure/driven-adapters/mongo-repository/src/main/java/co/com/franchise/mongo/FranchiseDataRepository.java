package co.com.franchise.mongo;

import co.com.franchise.mongo.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FranchiseDataRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}
