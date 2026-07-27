package co.com.franchise.api.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersConfig implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (isSwaggerPath(path)) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("Referrer-Policy", "strict-origin-when-cross-origin");
        headers.set("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'");
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");

        return chain.filter(exchange);
    }

    private boolean isSwaggerPath(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/static/");
    }
}
