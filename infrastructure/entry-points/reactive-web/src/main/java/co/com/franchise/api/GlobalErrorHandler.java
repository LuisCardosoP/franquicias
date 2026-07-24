package co.com.franchise.api;

import co.com.franchise.api.dto.ErrorResponse;
import co.com.franchise.model.EntityNotFoundException;
import co.com.franchise.model.InvalidInputException;
import co.com.franchise.model.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    private static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        ErrorResponse errorResponse;

        if (ex instanceof InvalidInputException invalidInputEx) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = new ErrorResponse(VALIDATION_ERROR, invalidInputEx.getMessage());
        } else if (ex instanceof EntityNotFoundException entityNotFoundEx) {
            status = HttpStatus.NOT_FOUND;
            errorResponse = new ErrorResponse(NOT_FOUND, entityNotFoundEx.getMessage());
        } else if (ex instanceof ServiceUnavailableException serviceUnavailableEx) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            errorResponse = new ErrorResponse(SERVICE_UNAVAILABLE, serviceUnavailableEx.getMessage());
        } else {
            log.error("Unhandled exception", ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = new ErrorResponse(INTERNAL_ERROR, GENERIC_ERROR_MESSAGE);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = toJson(errorResponse).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String toJson(ErrorResponse errorResponse) {
        return "{\"code\":\"" + escapeJson(errorResponse.code())
                + "\",\"message\":\"" + escapeJson(errorResponse.message()) + "\"}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
