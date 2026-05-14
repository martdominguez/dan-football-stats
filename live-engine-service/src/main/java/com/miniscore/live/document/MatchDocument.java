package com.miniscore.live.document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "matches")
public class MatchDocument {

    @Id
    private ObjectId id;

    private Long leagueId;
    private String leagueName;
    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;
    private MatchStatus status;
    private Instant kickoffTime;
    private Instant startedAt;
    private Instant endedAt;
    private int homeScore;
    private int awayScore;
    private List<TimelineEvent> timeline = new ArrayList<>();

    public MatchDocument(Long leagueId, String leagueName, Long homeTeamId, String homeTeamName,
                         Long awayTeamId, String awayTeamName, Instant kickoffTime) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.kickoffTime = kickoffTime;
        this.status = MatchStatus.CREATED;
    }

    protected MatchDocument() {
    }

    public ObjectId getId() {
        return id;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public Long getAwayTeamId() {
        return awayTeamId;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Instant getKickoffTime() {
        return kickoffTime;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public List<TimelineEvent> getTimeline() {
        return timeline;
    }
}
