package co.com.franchise.model;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(String id);
    Mono<Branch> update(Branch branch);
    Flux<Branch> findByFranchiseId(String franchiseId);
}
