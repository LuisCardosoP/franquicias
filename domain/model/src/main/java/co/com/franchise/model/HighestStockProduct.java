package co.com.franchise.model;

public record HighestStockProduct(
        String branchId,
        String branchName,
        String productId,
        String productName,
        int stock
) {
}
