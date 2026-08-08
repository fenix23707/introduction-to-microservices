package com.epam.gateway.controller;

import java.net.ConnectException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.epam.gateway.dto.ExceptionDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerResponse.Context;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Order(-2)
@Component
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final Context context;

    public GlobalExceptionHandler(ServerCodecConfigurer codecConfigurer) {
        this.context = new ServerResponse.Context() {
            @Override
            public List<HttpMessageWriter<?>> messageWriters() {
                return codecConfigurer.getWriters();
            }

            @Override
            public List<ViewResolver> viewResolvers() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        var status = resolveStatus(ex);

        var body = new ExceptionDto(
            String.valueOf(status.value()),
            message(status),
            Collections.emptyMap()
        );

        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .flatMap(res -> res.writeTo(exchange, context));
    }

    private HttpStatusCode resolveStatus(Throwable ex) {
        if (ex instanceof ErrorResponse er) {
            return er.getStatusCode();
        }
        if (ex instanceof ConnectException) {
            return HttpStatus.BAD_GATEWAY;
        }
        if (ex instanceof TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String message(HttpStatusCode status) {
        return switch (status.value()) {
            case 404 -> "No route configured for this path";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 429 -> "Too many requests";
            case 502 -> "Unable to reach downstream service";
            case 503 -> "Service temporarily unavailable";
            case 504 -> "Downstream service timed out";
            default -> status.is5xxServerError() ? "Internal gateway error" : "Bad request";
        };
    }
}
