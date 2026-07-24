package co.com.franchise.usecase;

import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.Product;
import co.com.franchise.model.ProductRepository;
import reactor.core.publisher.Mono;

public class UpdateProductStockUseCase {

    private final ProductRepository productRepository;

    public UpdateProductStockUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Product> execute(String productId, int newStock) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Product not found: " + productId)))
                .flatMap(product -> Product.updateStock(product, newStock))
                .flatMap(productRepository::update);
    }
}
