package co.com.franchise.usecase;

import co.com.franchise.model.Franchise;
import co.com.franchise.model.FranchiseRepository;
import reactor.core.publisher.Mono;

public class CreateFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public CreateFranchiseUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    public Mono<Franchise> execute(String name) {
        return Franchise.create(name)
                .flatMap(franchiseRepository::save);
    }
}
