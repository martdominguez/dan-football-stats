package com.miniscore.live.document;

import java.time.Instant;
import java.util.UUID;

public class TimelineEvent {

    private String type;
    private int minute;
    private UUID teamId;
    private String teamName;
    private UUID playerId;
    private String playerName;
    private String cardType;
    private Instant recordedAt;

    public TimelineEvent(String type, int minute, UUID teamId, String teamName, UUID playerId, String playerName,
                         String cardType, Instant recordedAt) {
        this.type = type;
        this.minute = minute;
        this.teamId = teamId;
        this.teamName = teamName;
        this.playerId = playerId;
        this.playerName = playerName;
        this.cardType = cardType;
        this.recordedAt = recordedAt;
    }

    protected TimelineEvent() {
    }

    public String getType() {
        return type;
    }

    public int getMinute() {
        return minute;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCardType() {
        return cardType;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
