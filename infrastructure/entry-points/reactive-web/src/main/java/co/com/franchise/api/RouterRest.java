package co.com.franchise.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(PATCH("/api/franchises/{franchiseId}/name"), handler::updateFranchiseName)
                .andRoute(POST("/api/franchises/{franchiseId}/branches"), handler::addBranch)
                .andRoute(PATCH("/api/branches/{branchId}/name"), handler::updateBranchName)
                .andRoute(POST("/api/branches/{branchId}/products"), handler::addProduct)
                .andRoute(DELETE("/api/branches/{branchId}/products/{productId}"), handler::removeProduct)
                .andRoute(PATCH("/api/products/{productId}/stock"), handler::updateProductStock)
                .andRoute(PATCH("/api/products/{productId}/name"), handler::updateProductName)
                .andRoute(GET("/api/franchises/{franchiseId}/highest-stock"), handler::getHighestStockProducts);
    }
}
