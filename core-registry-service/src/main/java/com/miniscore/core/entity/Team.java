package com.miniscore.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(nullable = false)
    private String name;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "league_id")
    private League league;

    protected Team() {
    }

    public Team(UUID teamId, String name, String shortName, League league) {
        this.teamId = teamId;
        this.name = name;
        this.shortName = shortName;
        this.league = league;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public League getLeague() {
        return league;
    }
}
