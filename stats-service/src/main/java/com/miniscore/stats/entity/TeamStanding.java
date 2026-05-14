package com.miniscore.stats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "team_standings")
public class TeamStanding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false, unique = true)
    private Long teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "league_name", nullable = false)
    private String leagueName;

    @Column(nullable = false)
    private Integer played;

    @Column(nullable = false)
    private Integer won;

    @Column(nullable = false)
    private Integer drawn;

    @Column(nullable = false)
    private Integer lost;

    @Column(name = "goals_for", nullable = false)
    private Integer goalsFor;

    @Column(name = "goals_against", nullable = false)
    private Integer goalsAgainst;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TeamStanding() {
    }

    public TeamStanding(Long teamId, String teamName, String leagueName) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.leagueName = leagueName;
        this.played = 0;
        this.won = 0;
        this.drawn = 0;
        this.lost = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.points = 0;
        this.updatedAt = Instant.now();
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public Integer getPlayed() {
        return played;
    }

    public Integer getWon() {
        return won;
    }

    public Integer getDrawn() {
        return drawn;
    }

    public Integer getLost() {
        return lost;
    }

    public Integer getGoalsFor() {
        return goalsFor;
    }

    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }

    public Integer getPoints() {
        return points;
    }

    public int goalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void registerResult(int scored, int conceded) {
        this.played = this.played + 1;
        this.goalsFor = this.goalsFor + scored;
        this.goalsAgainst = this.goalsAgainst + conceded;

        if (scored > conceded) {
            this.won = this.won + 1;
            this.points = this.points + 3;
        } else if (scored == conceded) {
            this.drawn = this.drawn + 1;
            this.points = this.points + 1;
        } else {
            this.lost = this.lost + 1;
        }

        this.updatedAt = Instant.now();
    }
}
