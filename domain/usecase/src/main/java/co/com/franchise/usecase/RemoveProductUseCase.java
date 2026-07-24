package co.com.franchise.usecase;

import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.ProductRepository;
import co.com.franchise.model.TechnicalMessage;
import reactor.core.publisher.Mono;

public class RemoveProductUseCase {

    private final ProductRepository productRepository;

    public RemoveProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Void> execute(String productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(TechnicalMessage.PRODUCT_NOT_FOUND)))
                .flatMap(product -> productRepository.deleteById(product.getId()));
    }
}
