package co.com.franchise.usecase;

import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.Product;
import co.com.franchise.model.ProductRepository;
import co.com.franchise.model.TechnicalMessage;
import reactor.core.publisher.Mono;

public class UpdateProductNameUseCase {

    private final ProductRepository productRepository;

    public UpdateProductNameUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Product> execute(String productId, String newName) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(TechnicalMessage.PRODUCT_NOT_FOUND)))
                .flatMap(product -> Product.updateName(product, newName))
                .flatMap(productRepository::update);
    }
}
