package co.com.franchise.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(-1)
public class TracingWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(TracingWebFilter.class);
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/webjars")) {
            return chain.filter(exchange);
        }

        String correlationId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        MDC.put(CORRELATION_ID_KEY, correlationId);
        log.info("Request entry: {} {} correlationId={}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath(),
                correlationId);
        MDC.clear();

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(CORRELATION_ID_KEY, correlationId))
                .doOnSuccess(unused -> {
                    long duration = System.currentTimeMillis() - startTime;
                    MDC.put(CORRELATION_ID_KEY, correlationId);
                    log.info("Response exit: {} {} status={} duration={}ms correlationId={}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath(),
                            exchange.getResponse().getStatusCode() != null
                                    ? exchange.getResponse().getStatusCode().value()
                                    : "unknown",
                            duration,
                            correlationId);
                    MDC.clear();
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    MDC.put(CORRELATION_ID_KEY, correlationId);
                    log.info("Response exit: {} {} status=error duration={}ms correlationId={} error={}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath(),
                            duration,
                            correlationId,
                            error.getMessage());
                    MDC.clear();
                });
    }
}
