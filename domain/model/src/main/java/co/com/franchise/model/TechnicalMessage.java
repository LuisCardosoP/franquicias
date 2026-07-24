package co.com.franchise.model;

public enum TechnicalMessage {

    FRANCHISE_NAME_EMPTY("Franchise name must not be empty"),
    BRANCH_NAME_EMPTY("Branch name must not be empty"),
    PRODUCT_NAME_EMPTY("Product name must not be empty"),
    PRODUCT_STOCK_NEGATIVE("Product stock must not be negative"),
    FRANCHISE_NOT_FOUND("Franchise not found"),
    BRANCH_NOT_FOUND("Branch not found"),
    PRODUCT_NOT_FOUND("Product not found"),
    SERVICE_UNAVAILABLE("Service temporarily unavailable"),
    INTERNAL_ERROR("An unexpected error occurred");

    private final String message;

    TechnicalMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
