package co.com.franchise.usecase;

import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.Franchise;
import co.com.franchise.model.FranchiseRepository;
import co.com.franchise.model.TechnicalMessage;
import reactor.core.publisher.Mono;

public class UpdateFranchiseNameUseCase {

    private final FranchiseRepository franchiseRepository;

    public UpdateFranchiseNameUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    public Mono<Franchise> execute(String franchiseId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> Franchise.updateName(franchise, newName))
                .flatMap(franchiseRepository::update);
    }
}
