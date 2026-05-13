package com.miniscore.stats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "player_scorers")
public class PlayerScorer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false, unique = true)
    private UUID playerId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(nullable = false)
    private Integer goals;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PlayerScorer() {
    }

    public PlayerScorer(UUID playerId, String playerName, UUID teamId, String teamName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.goals = 0;
        this.updatedAt = Instant.now();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getGoals() {
        return goals;
    }

    public void incrementGoals() {
        this.goals = this.goals + 1;
        this.updatedAt = Instant.now();
    }
}
