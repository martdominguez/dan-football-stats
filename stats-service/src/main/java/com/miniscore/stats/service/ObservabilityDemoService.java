package com.miniscore.stats.service;

import com.miniscore.stats.client.CoreRegistryClient;
import com.miniscore.stats.client.dto.CorePlayerResponse;
import com.miniscore.stats.client.dto.CoreTeamResponse;
import com.miniscore.stats.dto.ObservabilityDemoResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ObservabilityDemoService {

    private final CoreRegistryClient coreRegistryClient;
    private final Tracer tracer;
    private final Environment environment;

    public ObservabilityDemoService(CoreRegistryClient coreRegistryClient, Tracer tracer, Environment environment) {
        this.coreRegistryClient = coreRegistryClient;
        this.tracer = tracer;
        this.environment = environment;
    }

    public ObservabilityDemoResponse runHttpTraceDemo(Long teamId, Long playerId) {
        CoreTeamResponse team = coreRegistryClient.getTeam(teamId);
        CorePlayerResponse player = coreRegistryClient.getPlayer(playerId);
        Span currentSpan = tracer.currentSpan();

        return new ObservabilityDemoResponse(
                environment.getProperty("spring.application.name"),
                currentSpan == null ? "unavailable" : currentSpan.context().traceId(),
                currentSpan == null ? "unavailable" : currentSpan.context().spanId(),
                team.teamId(),
                team.name(),
                team.shortName(),
                player.playerId(),
                joinName(player.firstName(), player.lastName()),
                "Esta respuesta existe para generar una traza simple stats-service -> core-registry-service."
        );
    }

    private String joinName(String firstName, String lastName) {
        return (firstName + " " + lastName).trim();
    }
}
