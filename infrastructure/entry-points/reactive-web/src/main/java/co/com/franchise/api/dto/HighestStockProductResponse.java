package co.com.franchise.api.dto;

public record HighestStockProductResponse(String branchId, String branchName, String productId, String productName, int stock) {
}
