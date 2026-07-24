package co.com.franchise.api;

import co.com.franchise.api.dto.BranchResponse;
import co.com.franchise.api.dto.CreateBranchRequest;
import co.com.franchise.api.dto.CreateFranchiseRequest;
import co.com.franchise.api.dto.CreateProductRequest;
import co.com.franchise.api.dto.FranchiseResponse;
import co.com.franchise.api.dto.HighestStockProductResponse;
import co.com.franchise.api.dto.ProductResponse;
import co.com.franchise.api.dto.UpdateNameRequest;
import co.com.franchise.api.dto.UpdateStockRequest;
import co.com.franchise.model.Branch;
import co.com.franchise.model.Franchise;
import co.com.franchise.model.HighestStockProduct;
import co.com.franchise.model.Product;
import co.com.franchise.model.ServiceUnavailableException;
import co.com.franchise.usecase.AddBranchUseCase;
import co.com.franchise.usecase.AddProductUseCase;
import co.com.franchise.usecase.CreateFranchiseUseCase;
import co.com.franchise.usecase.GetHighestStockProductUseCase;
import co.com.franchise.usecase.RemoveProductUseCase;
import co.com.franchise.usecase.UpdateBranchNameUseCase;
import co.com.franchise.usecase.UpdateFranchiseNameUseCase;
import co.com.franchise.usecase.UpdateProductNameUseCase;
import co.com.franchise.usecase.UpdateProductStockUseCase;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class FranchiseHandler {

    private static final Logger log = LoggerFactory.getLogger(FranchiseHandler.class);
    private static final String CIRCUIT_BREAKER_NAME = "franchiseCircuitBreaker";
    private static final String FALLBACK_METHOD = "fallback";
    private static final String CORRELATION_ID_KEY = "correlationId";

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final GetHighestStockProductUseCase getHighestStockProductUseCase;

    public FranchiseHandler(CreateFranchiseUseCase createFranchiseUseCase,
                            AddBranchUseCase addBranchUseCase,
                            AddProductUseCase addProductUseCase,
                            RemoveProductUseCase removeProductUseCase,
                            UpdateProductStockUseCase updateProductStockUseCase,
                            UpdateProductNameUseCase updateProductNameUseCase,
                            UpdateBranchNameUseCase updateBranchNameUseCase,
                            UpdateFranchiseNameUseCase updateFranchiseNameUseCase,
                            GetHighestStockProductUseCase getHighestStockProductUseCase) {
        this.createFranchiseUseCase = createFranchiseUseCase;
        this.addBranchUseCase = addBranchUseCase;
        this.addProductUseCase = addProductUseCase;
        this.removeProductUseCase = removeProductUseCase;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.updateProductNameUseCase = updateProductNameUseCase;
        this.updateBranchNameUseCase = updateBranchNameUseCase;
        this.updateFranchiseNameUseCase = updateFranchiseNameUseCase;
        this.getHighestStockProductUseCase = getHighestStockProductUseCase;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            return request.bodyToMono(CreateFranchiseRequest.class)
                    .flatMap(req -> createFranchiseUseCase.execute(req.name()))
                    .map(this::toFranchiseResponse)
                    .flatMap(response -> ServerResponse.status(201).bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> addBranch(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String franchiseId = request.pathVariable("franchiseId");
            return request.bodyToMono(CreateBranchRequest.class)
                    .flatMap(req -> addBranchUseCase.execute(franchiseId, req.name()))
                    .map(this::toBranchResponse)
                    .flatMap(response -> ServerResponse.status(201).bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> addProduct(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String branchId = request.pathVariable("branchId");
            return request.bodyToMono(CreateProductRequest.class)
                    .flatMap(req -> addProductUseCase.execute(branchId, req.name(), req.stock()))
                    .map(this::toProductResponse)
                    .flatMap(response -> ServerResponse.status(201).bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String productId = request.pathVariable("productId");
            return removeProductUseCase.execute(productId)
                    .then(ServerResponse.noContent().build())
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String productId = request.pathVariable("productId");
            return request.bodyToMono(UpdateStockRequest.class)
                    .flatMap(req -> updateProductStockUseCase.execute(productId, req.stock()))
                    .map(this::toProductResponse)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String productId = request.pathVariable("productId");
            return request.bodyToMono(UpdateNameRequest.class)
                    .flatMap(req -> updateProductNameUseCase.execute(productId, req.name()))
                    .map(this::toProductResponse)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String branchId = request.pathVariable("branchId");
            return request.bodyToMono(UpdateNameRequest.class)
                    .flatMap(req -> updateBranchNameUseCase.execute(branchId, req.name()))
                    .map(this::toBranchResponse)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String franchiseId = request.pathVariable("franchiseId");
            return request.bodyToMono(UpdateNameRequest.class)
                    .flatMap(req -> updateFranchiseNameUseCase.execute(franchiseId, req.name()))
                    .map(this::toFranchiseResponse)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ServerResponse> getHighestStockProducts(ServerRequest request) {
        long startTime = System.currentTimeMillis();
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault(CORRELATION_ID_KEY, "N/A");
            setMdc(correlationId);
            log.info("Request entry: {} {}", request.method(), request.path());
            clearMdc();

            String franchiseId = request.pathVariable("franchiseId");
            return getHighestStockProductUseCase.execute(franchiseId)
                    .map(this::toHighestStockResponse)
                    .collectList()
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .doOnSuccess(resp -> logResponseExit(correlationId, resp, startTime));
        });
    }

    // --- Fallback methods ---

    public Mono<ServerResponse> fallback(ServerRequest request, CallNotPermittedException exception) {
        return Mono.error(new ServiceUnavailableException("Service temporarily unavailable"));
    }

    public Mono<ServerResponse> fallback(ServerRequest request, Exception exception) {
        return Mono.error(exception);
    }

    // --- Mapping helpers ---

    private FranchiseResponse toFranchiseResponse(Franchise franchise) {
        return new FranchiseResponse(franchise.getId(), franchise.getName());
    }

    private BranchResponse toBranchResponse(Branch branch) {
        return new BranchResponse(branch.getId(), branch.getFranchiseId(), branch.getName());
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(product.getId(), product.getBranchId(), product.getName(), product.getStock());
    }

    private HighestStockProductResponse toHighestStockResponse(HighestStockProduct result) {
        return new HighestStockProductResponse(
                result.branchId(),
                result.branchName(),
                result.productId(),
                result.productName(),
                result.stock()
        );
    }

    // --- Logging helpers ---

    private void logResponseExit(String correlationId, ServerResponse response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        setMdc(correlationId);
        log.info("Response exit: status={} duration={}ms", response.statusCode().value(), duration);
        clearMdc();
    }

    private void setMdc(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    private void clearMdc() {
        MDC.clear();
    }
}
