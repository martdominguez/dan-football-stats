package com.miniscore.stats.controller;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/test")
@Tag(
    name = "Test",
    description = "Diagnostic endpoints for identifying which service instance handled a request."
)
public class TestController {

    private final Environment environment;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TestController.class);
    public TestController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/context")
    @Operation(
        summary = "Get request context",
        description = "Returns instance and request metadata useful for validating load balancing across replicas."
    )
    public Map<String, Object> getContext(HttpServletRequest request) {
        Map<String, Object> context = new LinkedHashMap<>();
        Instant now = Instant.now();
        logger.info("Received request for /api/test/context at {}", now);
        context.put(
            "serviceName",
            environment.getProperty("spring.application.name")
        );
        context.put(
            "instanceId",
            environment.getProperty("eureka.instance.instance-id")
        );
        logger.info("test {} ",context);
        //context.put("serverPort", environment.getProperty("local.server.port", request.getLocalPort()));
        context.put("serverHostName", resolveHostName());
        context.put("serverIp", resolveHostAddress());
        context.put("timestampIso", now.toString());
        context.put(
            "jvmProcess",
            ManagementFactory.getRuntimeMXBean().getName()
        );
        context.put("requestUri", request.getRequestURI());
        context.put("queryString", request.getQueryString());
        context.put("remoteAddress", request.getRemoteAddr());
        context.put("remoteHost", request.getRemoteHost());
        context.put("threadName", Thread.currentThread().getName());

        return context;
    }

    private String resolveHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return "unknown";
        }
    }

    private String resolveHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return "unknown";
        }
    }
}
