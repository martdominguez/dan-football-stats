package com.miniscore.live.document;

import java.time.Instant;

public class TimelineEvent {

    private String type;
    private int minute;
    private Long teamId;
    private String teamName;
    private Long playerId;
    private String playerName;
    private String cardType;
    private Instant recordedAt;

    public TimelineEvent(String type, int minute, Long teamId, String teamName, Long playerId, String playerName,
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

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public Long getPlayerId() {
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
