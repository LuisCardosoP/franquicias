package co.com.franchise.model;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Mono<Product> update(Product product);
    Mono<Void> deleteById(String id);
    Flux<Product> findByBranchId(String branchId);
}
