package co.com.franchise.mongo;

import co.com.franchise.model.Product;
import co.com.franchise.model.ProductRepository;
import co.com.franchise.mongo.document.ProductDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoProductRepositoryAdapter implements ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoProductRepositoryAdapter.class);
    private static final String COLLECTION = "products";

    private final ProductDataRepository dataRepository;
    private final ObjectMapper mapper;

    public MongoProductRepositoryAdapter(ProductDataRepository dataRepository, ObjectMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Product> save(Product product) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB save: collection={}, entity={}", COLLECTION, product.getId());
            MDC.clear();
            return dataRepository.save(toDocument(product))
                    .map(this::toDomain);
        });
    }

    @Override
    public Mono<Product> findById(String id) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB findById: collection={}, id={}", COLLECTION, id);
            MDC.clear();
            return dataRepository.findById(id)
                    .map(this::toDomain);
        });
    }

    @Override
    public Mono<Product> update(Product product) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB update: collection={}, entity={}", COLLECTION, product.getId());
            MDC.clear();
            return dataRepository.save(toDocument(product))
                    .map(this::toDomain);
        });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB deleteById: collection={}, id={}", COLLECTION, id);
            MDC.clear();
            return dataRepository.deleteById(id);
        });
    }

    @Override
    public Flux<Product> findByBranchId(String branchId) {
        return Flux.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB findByBranchId: collection={}, branchId={}", COLLECTION, branchId);
            MDC.clear();
            return dataRepository.findByBranchId(branchId)
                    .map(this::toDomain);
        });
    }

    private ProductDocument toDocument(Product product) {
        ProductDocument document = new ProductDocument();
        document.setId(product.getId());
        document.setBranchId(product.getBranchId());
        document.setName(product.getName());
        document.setStock(product.getStock());
        return document;
    }

    private Product toDomain(ProductDocument document) {
        return mapper.map(document, Product.class);
    }
}
