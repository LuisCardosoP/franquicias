package co.com.franchise.usecase;

import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.ProductRepository;
import reactor.core.publisher.Mono;

public class RemoveProductUseCase {

    private final ProductRepository productRepository;

    public RemoveProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Void> execute(String productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Product not found: " + productId)))
                .flatMap(product -> productRepository.deleteById(product.getId()));
    }
}
