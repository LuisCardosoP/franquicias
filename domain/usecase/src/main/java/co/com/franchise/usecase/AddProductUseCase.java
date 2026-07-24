package co.com.franchise.usecase;

import co.com.franchise.model.BranchRepository;
import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.Product;
import co.com.franchise.model.ProductRepository;
import reactor.core.publisher.Mono;

public class AddProductUseCase {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public AddProductUseCase(BranchRepository branchRepository, ProductRepository productRepository) {
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    public Mono<Product> execute(String branchId, String productName, int stock) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Branch not found: " + branchId)))
                .flatMap(branch -> Product.create(productName, stock, branch.getId()))
                .flatMap(productRepository::save);
    }
}
