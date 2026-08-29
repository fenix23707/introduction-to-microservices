package com.epam.resource.config;


import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class TraceIdConfig {

    public static final String X_TRACE_ID = "X-Trace-Id";

    @Bean
    FilterRegistrationBean<Filter> traceIdHeaderFilter(Tracer tracer) {
        Filter filter = (req, res, chain) -> {
            var span = tracer.currentSpan();
            if (span != null) {
                ((HttpServletResponse) res).setHeader(X_TRACE_ID, span.context().traceId());
            }
            chain.doFilter(req, res);
        };

        var reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return reg;
    }
}