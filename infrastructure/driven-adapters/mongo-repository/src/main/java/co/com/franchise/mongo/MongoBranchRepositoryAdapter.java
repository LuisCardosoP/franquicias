package co.com.franchise.mongo;

import co.com.franchise.model.Branch;
import co.com.franchise.model.BranchRepository;
import co.com.franchise.mongo.document.BranchDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoBranchRepositoryAdapter implements BranchRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoBranchRepositoryAdapter.class);
    private static final String COLLECTION = "branches";

    private final BranchDataRepository dataRepository;
    private final ObjectMapper mapper;

    public MongoBranchRepositoryAdapter(BranchDataRepository dataRepository, ObjectMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB save: collection={}, entity={}", COLLECTION, branch.getId());
            MDC.clear();
            return dataRepository.save(toDocument(branch))
                    .map(this::toDomain);
        });
    }

    @Override
    public Mono<Branch> findById(String id) {
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
    public Mono<Branch> update(Branch branch) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB update: collection={}, entity={}", COLLECTION, branch.getId());
            MDC.clear();
            return dataRepository.save(toDocument(branch))
                    .map(this::toDomain);
        });
    }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return Flux.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlationId", "N/A");
            MDC.put("correlationId", correlationId);
            log.info("MongoDB findByFranchiseId: collection={}, franchiseId={}", COLLECTION, franchiseId);
            MDC.clear();
            return dataRepository.findByFranchiseId(franchiseId)
                    .map(this::toDomain);
        });
    }

    private BranchDocument toDocument(Branch branch) {
        BranchDocument document = new BranchDocument();
        document.setId(branch.getId());
        document.setFranchiseId(branch.getFranchiseId());
        document.setName(branch.getName());
        return document;
    }

    private Branch toDomain(BranchDocument document) {
        return mapper.map(document, Branch.class);
    }
}
