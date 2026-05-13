package com.miniscore.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String position;

    @Column(name = "shirt_number", nullable = false)
    private Integer shirtNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    protected Player() {
    }

    public Player(UUID playerId, String firstName, String lastName, String position, Integer shirtNumber, Team team) {
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.shirtNumber = shirtNumber;
        this.team = team;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position;
    }

    public Integer getShirtNumber() {
        return shirtNumber;
    }

    public Team getTeam() {
        return team;
    }
}
