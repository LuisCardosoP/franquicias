package co.com.franchise.usecase;

import co.com.franchise.model.Branch;
import co.com.franchise.model.BranchRepository;
import co.com.franchise.model.EntityNotFoundException;
import reactor.core.publisher.Mono;

public class UpdateBranchNameUseCase {

    private final BranchRepository branchRepository;

    public UpdateBranchNameUseCase(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public Mono<Branch> execute(String branchId, String newName) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Branch not found: " + branchId)))
                .flatMap(branch -> Branch.updateName(branch, newName))
                .flatMap(branchRepository::update);
    }
}
