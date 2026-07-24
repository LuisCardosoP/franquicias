package co.com.franchise.mongo;

import co.com.franchise.mongo.document.ProductDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ProductDataRepository extends ReactiveMongoRepository<ProductDocument, String> {
    Flux<ProductDocument> findByBranchId(String branchId);
}
