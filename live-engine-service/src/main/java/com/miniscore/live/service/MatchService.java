package com.miniscore.live.service;

import com.miniscore.live.document.MatchDocument;
import com.miniscore.live.document.MatchStatus;
import com.miniscore.live.document.TimelineEvent;
import com.miniscore.live.dto.CardRequest;
import com.miniscore.live.dto.CreateMatchRequest;
import com.miniscore.live.dto.GoalRequest;
import com.miniscore.live.dto.MatchResponse;
import com.miniscore.live.dto.TimelineEventResponse;
import com.miniscore.live.event.GoalScoredEvent;
import com.miniscore.live.event.MatchEndedEvent;
import com.miniscore.live.exception.BusinessRuleException;
import com.miniscore.live.exception.ResourceNotFoundException;
import com.miniscore.live.repository.MatchRepository;
import java.time.Instant;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final EventPublisherService eventPublisherService;

    public MatchService(MatchRepository matchRepository, EventPublisherService eventPublisherService) {
        this.matchRepository = matchRepository;
        this.eventPublisherService = eventPublisherService;
    }

    public MatchResponse createMatch(CreateMatchRequest request) {
        MatchDocument match = new MatchDocument(
                request.leagueId(),
                request.leagueName(),
                request.homeTeamId(),
                request.homeTeamName(),
                request.awayTeamId(),
                request.awayTeamName(),
                request.kickoffTime()
        );
        return toResponse(matchRepository.save(match));
    }

    public MatchResponse getMatch(String matchId) {
        return toResponse(getMatchDocument(matchId));
    }

    public MatchResponse startMatch(String matchId) {
        MatchDocument match = getMatchDocument(matchId);
        if (match.getStatus() != MatchStatus.CREATED) {
            throw new BusinessRuleException("Only matches in CREATED state can be started.");
        }
        match.setStatus(MatchStatus.STARTED);
        match.setStartedAt(Instant.now());
        return toResponse(matchRepository.save(match));
    }

    public MatchResponse registerGoal(String matchId, GoalRequest request) {
        MatchDocument match = getStartedMatch(matchId);
        String teamName = resolveTeamName(match, request.teamId());
        Instant occurredAt = Instant.now();

        match.getTimeline().add(new TimelineEvent(
                "GOAL",
                request.minute(),
                request.teamId(),
                teamName,
                request.playerId(),
                request.playerName(),
                null,
                occurredAt
        ));

        if (request.teamId().equals(match.getHomeTeamId())) {
            match.setHomeScore(match.getHomeScore() + 1);
        } else {
            match.setAwayScore(match.getAwayScore() + 1);
        }

        MatchDocument saved = matchRepository.save(match);
        eventPublisherService.publishGoalScored(new GoalScoredEvent(
                saved.getId().toHexString(),
                saved.getLeagueId(),
                saved.getLeagueName(),
                request.teamId(),
                teamName,
                request.playerId(),
                request.playerName(),
                request.minute(),
                saved.getHomeScore(),
                saved.getAwayScore(),
                occurredAt
        ));
        return toResponse(saved);
    }

    public MatchResponse registerCard(String matchId, CardRequest request) {
        MatchDocument match = getStartedMatch(matchId);
        String teamName = resolveTeamName(match, request.teamId());

        match.getTimeline().add(new TimelineEvent(
                "CARD",
                request.minute(),
                request.teamId(),
                teamName,
                request.playerId(),
                request.playerName(),
                request.cardType(),
                Instant.now()
        ));

        return toResponse(matchRepository.save(match));
    }

    public MatchResponse endMatch(String matchId) {
        MatchDocument match = getStartedMatch(matchId);
        match.setStatus(MatchStatus.ENDED);
        match.setEndedAt(Instant.now());
        MatchDocument saved = matchRepository.save(match);

        eventPublisherService.publishMatchEnded(new MatchEndedEvent(
                saved.getId().toHexString(),
                saved.getLeagueId(),
                saved.getLeagueName(),
                saved.getHomeTeamId(),
                saved.getHomeTeamName(),
                saved.getHomeScore(),
                saved.getAwayTeamId(),
                saved.getAwayTeamName(),
                saved.getAwayScore(),
                saved.getEndedAt()
        ));

        return toResponse(saved);
    }

    private MatchDocument getMatchDocument(String matchId) {
        return matchRepository.findById(toObjectId(matchId))
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));
    }

    private MatchDocument getStartedMatch(String matchId) {
        MatchDocument match = getMatchDocument(matchId);
        if (match.getStatus() != MatchStatus.STARTED) {
            throw new BusinessRuleException("Only matches in STARTED state can receive live actions.");
        }
        return match;
    }

    private String resolveTeamName(MatchDocument match, Long teamId) {
        if (teamId.equals(match.getHomeTeamId())) {
            return match.getHomeTeamName();
        }
        if (teamId.equals(match.getAwayTeamId())) {
            return match.getAwayTeamName();
        }
        throw new BusinessRuleException("The provided teamId does not belong to this match.");
    }

    private MatchResponse toResponse(MatchDocument match) {
        List<TimelineEventResponse> timeline = match.getTimeline().stream()
                .map(event -> new TimelineEventResponse(
                        event.getType(),
                        event.getMinute(),
                        event.getTeamId(),
                        event.getTeamName(),
                        event.getPlayerId(),
                        event.getPlayerName(),
                        event.getCardType(),
                        event.getRecordedAt()
                ))
                .toList();

        return new MatchResponse(
                match.getId().toHexString(),
                match.getLeagueId(),
                match.getLeagueName(),
                match.getHomeTeamId(),
                match.getHomeTeamName(),
                match.getAwayTeamId(),
                match.getAwayTeamName(),
                match.getStatus().name(),
                match.getHomeScore(),
                match.getAwayScore(),
                match.getKickoffTime(),
                match.getStartedAt(),
                match.getEndedAt(),
                timeline
        );
    }

    private ObjectId toObjectId(String matchId) {
        if (!ObjectId.isValid(matchId)) {
            throw new ResourceNotFoundException("Match not found: " + matchId);
        }
        return new ObjectId(matchId);
    }
}
