package co.com.franchise.mongo;

import co.com.franchise.model.Franchise;
import co.com.franchise.model.FranchiseRepository;
import co.com.franchise.mongo.document.FranchiseDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MongoFranchiseRepositoryAdapter implements FranchiseRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoFranchiseRepositoryAdapter.class);
    private static final String COLLECTION = "franchises";

    private final FranchiseDataRepository dataRepository;
    private final ObjectMapper mapper;

    public MongoFranchiseRepositoryAdapter(FranchiseDataRepository dataRepository, ObjectMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB save: collection={}, entity={}", COLLECTION, franchise.getId());
            MDC.clear();
            return dataRepository.save(toDocument(franchise))
                    .map(this::toDomain);
        });
    }

    @Override
    public Mono<Franchise> findById(String id) {
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
    public Mono<Franchise> update(Franchise franchise) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB update: collection={}, entity={}", COLLECTION, franchise.getId());
            MDC.clear();
            return dataRepository.save(toDocument(franchise))
                    .map(this::toDomain);
        });
    }

    private FranchiseDocument toDocument(Franchise franchise) {
        FranchiseDocument document = new FranchiseDocument();
        document.setId(franchise.getId());
        document.setName(franchise.getName());
        return document;
    }

    private Franchise toDomain(FranchiseDocument document) {
        return mapper.map(document, Franchise.class);
    }
}
