package co.com.franchise.usecase;

import co.com.franchise.model.Branch;
import co.com.franchise.model.BranchRepository;
import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.FranchiseRepository;
import reactor.core.publisher.Mono;

public class AddBranchUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public AddBranchUseCase(FranchiseRepository franchiseRepository, BranchRepository branchRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
    }

    public Mono<Branch> execute(String franchiseId, String branchName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Franchise not found: " + franchiseId)))
                .flatMap(franchise -> Branch.create(branchName, franchise.getId()))
                .flatMap(branchRepository::save);
    }
}
