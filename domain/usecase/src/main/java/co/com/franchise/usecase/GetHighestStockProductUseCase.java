package co.com.franchise.usecase;

import co.com.franchise.model.BranchRepository;
import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.FranchiseRepository;
import co.com.franchise.model.HighestStockProduct;
import co.com.franchise.model.Product;
import co.com.franchise.model.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetHighestStockProductUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public GetHighestStockProductUseCase(FranchiseRepository franchiseRepository,
                                         BranchRepository branchRepository,
                                         ProductRepository productRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    public Flux<HighestStockProduct> execute(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Franchise not found: " + franchiseId)))
                .flatMapMany(franchise -> branchRepository.findByFranchiseId(franchise.getId()))
                .flatMap(branch -> productRepository.findByBranchId(branch.getId())
                        .reduce((p1, p2) -> p1.getStock() >= p2.getStock() ? p1 : p2)
                        .map(product -> new HighestStockProduct(
                                branch.getId(),
                                branch.getName(),
                                product.getId(),
                                product.getName(),
                                product.getStock()
                        ))
                );
    }
}
