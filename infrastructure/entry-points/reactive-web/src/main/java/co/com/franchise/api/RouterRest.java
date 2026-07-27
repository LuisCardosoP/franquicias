package co.com.franchise.api;

import co.com.franchise.api.dto.BranchResponse;
import co.com.franchise.api.dto.CreateBranchRequest;
import co.com.franchise.api.dto.CreateFranchiseRequest;
import co.com.franchise.api.dto.CreateProductRequest;
import co.com.franchise.api.dto.ErrorResponse;
import co.com.franchise.api.dto.FranchiseResponse;
import co.com.franchise.api.dto.HighestStockProductResponse;
import co.com.franchise.api.dto.ProductResponse;
import co.com.franchise.api.dto.UpdateNameRequest;
import co.com.franchise.api.dto.UpdateStockRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            description = "Creates a new franchise with the given name and returns the created franchise with its generated identifier.",
                            tags = {"Franchises"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Franchise creation payload",
                                    content = @Content(schema = @Schema(implementation = CreateFranchiseRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input - name is empty or blank",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update a franchise name",
                            description = "Updates the name of an existing franchise identified by its ID.",
                            tags = {"Franchises"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the franchise",
                                            schema = @Schema(type = "string"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "New name payload",
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise name updated successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input - name is empty or blank",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "addBranch",
                    operation = @Operation(
                            operationId = "addBranch",
                            summary = "Add a branch to a franchise",
                            description = "Creates a new branch associated with the specified franchise.",
                            tags = {"Branches"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the franchise",
                                            schema = @Schema(type = "string"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Branch creation payload",
                                    content = @Content(schema = @Schema(implementation = CreateBranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input - name is empty or blank",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update a branch name",
                            description = "Updates the name of an existing branch identified by its ID.",
                            tags = {"Branches"},
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the branch",
                                            schema = @Schema(type = "string"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "New name payload",
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch name updated successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input - name is empty or blank",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProduct",
                            summary = "Add a product to a branch",
                            description = "Creates a new product associated with the specified branch.",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the branch",
                                            schema = @Schema(type = "string"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Product creation payload",
                                    content = @Content(schema = @Schema(implementation = CreateProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input - name is empty/blank or stock is negative",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "removeProduct",
                    operation = @Operation(
                            operationId = "removeProduct",
                            summary = "Remove a product from a branch",
                            description = "Removes an existing product identified by its ID from the specified branch.",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the branch",
                                            schema = @Schema(type = "string")),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the product",
                                            schema = @Schema(type = "string"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Product removed successfully"),
                                    @ApiResponse(responseCode = "404", description = "Product or branch not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update product stock",
                            description = "Updates the stock quantity of an existing product identified by its ID.",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the product",
                                            schema = @Schema(type = "string"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "New stock value payload",
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product stock updated successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input - stock is negative",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update product name",
                            description = "Updates the name of an existing product identified by its ID.",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the product",
                                            schema = @Schema(type = "string"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "New name payload",
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product name updated successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input - name is empty or blank",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/highest-stock",
                    method = RequestMethod.GET,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "getHighestStockProducts",
                    operation = @Operation(
                            operationId = "getHighestStockProducts",
                            summary = "Get highest stock product per branch",
                            description = "Returns the product with the highest stock for each branch within the specified franchise. "
                                    + "Branches with no products are excluded from the results.",
                            tags = {"Franchises"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true,
                                            description = "Unique identifier of the franchise",
                                            schema = @Schema(type = "string"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Highest stock products retrieved successfully",
                                            content = @Content(array = @ArraySchema(
                                                    schema = @Schema(implementation = HighestStockProductResponse.class)))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
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
