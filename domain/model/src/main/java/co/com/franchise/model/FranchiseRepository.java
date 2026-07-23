package co.com.franchise.model;

import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Mono<Franchise> update(Franchise franchise);
}
