package com.miniscore.stats.client;

import com.miniscore.stats.client.dto.CorePlayerResponse;
import com.miniscore.stats.client.dto.CoreTeamResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "core-registry-service")
public interface CoreRegistryClient {

    @GetMapping("/api/teams/{teamId}")
    CoreTeamResponse getTeam(@PathVariable Long teamId);

    @GetMapping("/api/players/{playerId}")
    CorePlayerResponse getPlayer(@PathVariable Long playerId);
}
