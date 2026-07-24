package co.com.franchise.model;

import reactor.core.publisher.Mono;

public class Product {

    private String id;
    private String branchId;
    private String name;
    private int stock;

    private Product() {
    }

    public static Mono<Product> create(String name, int stock, String branchId) {
        if (name == null || name.isBlank()) {
            return Mono.error(new InvalidInputException(TechnicalMessage.PRODUCT_NAME_EMPTY));
        }
        if (stock < 0) {
            return Mono.error(new InvalidInputException(TechnicalMessage.PRODUCT_STOCK_NEGATIVE));
        }
        Product product = new Product();
        product.name = name.trim();
        product.stock = stock;
        product.branchId = branchId;
        return Mono.just(product);
    }

    public static Mono<Product> updateStock(Product existing, int newStock) {
        if (newStock < 0) {
            return Mono.error(new InvalidInputException(TechnicalMessage.PRODUCT_STOCK_NEGATIVE));
        }
        existing.stock = newStock;
        return Mono.just(existing);
    }

    public static Mono<Product> updateName(Product existing, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new InvalidInputException(TechnicalMessage.PRODUCT_NAME_EMPTY));
        }
        existing.name = newName.trim();
        return Mono.just(existing);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }
}
