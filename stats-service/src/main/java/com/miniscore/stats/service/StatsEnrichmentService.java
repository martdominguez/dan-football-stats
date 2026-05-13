package com.miniscore.stats.service;

import com.miniscore.stats.client.CoreRegistryClient;
import com.miniscore.stats.client.dto.CorePlayerResponse;
import com.miniscore.stats.client.dto.CoreTeamResponse;
import com.miniscore.stats.dto.StandingResponse;
import com.miniscore.stats.dto.TopScorerResponse;
import com.miniscore.stats.entity.PlayerScorer;
import com.miniscore.stats.entity.TeamStanding;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;

@Service
public class StatsEnrichmentService {

    private static final Duration TOP_SCORER_TIMEOUT = Duration.ofMillis(1000);

    private final CoreRegistryClient coreRegistryClient;
    private final ExecutorService timeoutExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public StatsEnrichmentService(CoreRegistryClient coreRegistryClient) {
        this.coreRegistryClient = coreRegistryClient;
    }

    @CircuitBreaker(name = "coreRegistry", fallbackMethod = "fallbackStanding")
    public StandingResponse enrichStanding(TeamStanding row) {
        CoreTeamResponse team = coreRegistryClient.getTeam(row.getTeamId());
        return new StandingResponse(
                row.getTeamId(),
                team.name(),
                team.shortName(),
                team.leagueName(),
                row.getPlayed(),
                row.getWon(),
                row.getDrawn(),
                row.getLost(),
                row.getGoalsFor(),
                row.getGoalsAgainst(),
                row.getPoints()
        );
    }

    @CircuitBreaker(name = "coreRegistry", fallbackMethod = "fallbackTopScorer")
    public TopScorerResponse enrichTopScorer(PlayerScorer row) {
        return executeWithinTimeout(() -> {
            CorePlayerResponse player = coreRegistryClient.getPlayer(row.getPlayerId());
            CoreTeamResponse team = resolveTeam(row.getTeamId(), row.getTeamName());

            return new TopScorerResponse(
                    row.getPlayerId(),
                    joinName(player.firstName(), player.lastName(), row.getPlayerName()),
                    player.position(),
                    player.shirtNumber(),
                    row.getTeamId(),
                    team.name(),
                    team.shortName(),
                    row.getGoals()
            );
        }, TOP_SCORER_TIMEOUT);
    }

    private CoreTeamResponse resolveTeam(UUID teamId, String fallbackTeamName) {
        try {
            return coreRegistryClient.getTeam(teamId);
        } catch (RuntimeException exception) {
            return new CoreTeamResponse(teamId, fallbackTeamName, null, null, null);
        }
    }

    @SuppressWarnings("unused")
    private StandingResponse fallbackStanding(TeamStanding row, Throwable throwable) {
        return new StandingResponse(
                row.getTeamId(),
                row.getTeamName(),
                null,
                row.getLeagueName(),
                row.getPlayed(),
                row.getWon(),
                row.getDrawn(),
                row.getLost(),
                row.getGoalsFor(),
                row.getGoalsAgainst(),
                row.getPoints()
        );
    }

    @SuppressWarnings("unused")
    private TopScorerResponse fallbackTopScorer(PlayerScorer row, Throwable throwable) {
        return new TopScorerResponse(
                row.getPlayerId(),
                row.getPlayerName(),
                null,
                null,
                row.getTeamId(),
                row.getTeamName(),
                null,
                row.getGoals()
        );
    }

    private String joinName(String firstName, String lastName, String fallback) {
        String fullName = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        return fullName.isBlank() ? fallback : fullName;
    }

    private <T> T executeWithinTimeout(Callable<T> action, Duration timeout) {
        Future<T> future = timeoutExecutor.submit(action);
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            future.cancel(true);
            throw new RuntimeException("Top scorer enrichment timed out after " + timeout.toMillis() + " ms", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Top scorer enrichment was interrupted", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException("Top scorer enrichment failed", cause);
        }
    }

    @PreDestroy
    void shutdownTimeoutExecutor() {
        timeoutExecutor.shutdown();
    }
}
