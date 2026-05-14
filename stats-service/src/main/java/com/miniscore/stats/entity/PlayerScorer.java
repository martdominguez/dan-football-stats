package com.miniscore.stats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "player_scorers")
public class PlayerScorer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false, unique = true)
    private Long playerId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(nullable = false)
    private Integer goals;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PlayerScorer() {
    }

    public PlayerScorer(Long playerId, String playerName, Long teamId, String teamName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.goals = 0;
        this.updatedAt = Instant.now();
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Long getTeamId() {
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
