package com.miniscore.stats.controller;

import com.miniscore.stats.dto.ObservabilityDemoResponse;
import com.miniscore.stats.service.ObservabilityDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats/demo")
@Tag(name = "Observability Demo", description = "Endpoints pensados para mostrar trazas distribuidas.")
public class ObservabilityDemoController {

    private final ObservabilityDemoService observabilityDemoService;

    public ObservabilityDemoController(ObservabilityDemoService observabilityDemoService) {
        this.observabilityDemoService = observabilityDemoService;
    }

    @GetMapping("/trace")
    @Operation(
            summary = "Genera una traza distribuida simple",
            description = "Llama desde stats-service a core-registry-service y devuelve el traceId para buscarlo en Grafana."
    )
    public ObservabilityDemoResponse traceDemo(
            @RequestParam(defaultValue = "1") Long teamId,
            @RequestParam(defaultValue = "1") Long playerId
    ) {
        return observabilityDemoService.runHttpTraceDemo(teamId, playerId);
    }
}
