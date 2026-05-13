package com.miniscore.stats.service;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miniscore.stats.config.RabbitMqConfig;
import com.miniscore.stats.entity.PlayerScorer;
import com.miniscore.stats.entity.TeamStanding;
import com.miniscore.stats.event.GoalScoredEvent;
import com.miniscore.stats.event.MatchEndedEvent;
import com.miniscore.stats.repository.PlayerScorerRepository;
import com.miniscore.stats.repository.TeamStandingRepository;

@Service
public class StatsConsumerService {


    private final PlayerScorerRepository playerScorerRepository;
    private final TeamStandingRepository teamStandingRepository;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(StatsConsumerService.class);

    public StatsConsumerService(PlayerScorerRepository playerScorerRepository,
                                TeamStandingRepository teamStandingRepository) {
        this.playerScorerRepository = playerScorerRepository;
        this.teamStandingRepository = teamStandingRepository;
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.GOAL_QUEUE)
    public void onGoalScored(GoalScoredEvent event) {
        logger.info("Received GoalScoredEvent: {}", event);
        PlayerScorer scorer = playerScorerRepository.findByPlayerId(event.playerId())
                .orElseGet(() -> new PlayerScorer(
                        event.playerId(),
                        event.playerName(),
                        event.teamId(),
                        event.teamName()
                ));
        scorer.incrementGoals();
        playerScorerRepository.save(scorer);
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.MATCH_ENDED_QUEUE)
    public void onMatchEnded(MatchEndedEvent event) {
        logger.info("Received MatchEndedEvent: {}", event);
        TeamStanding homeStanding = teamStandingRepository.findByTeamId(event.homeTeamId())
                .orElseGet(() -> new TeamStanding(event.homeTeamId(), event.homeTeamName(), event.leagueName()));
        TeamStanding awayStanding = teamStandingRepository.findByTeamId(event.awayTeamId())
                .orElseGet(() -> new TeamStanding(event.awayTeamId(), event.awayTeamName(), event.leagueName()));

        homeStanding.registerResult(event.homeScore(), event.awayScore());
        awayStanding.registerResult(event.awayScore(), event.homeScore());

        teamStandingRepository.save(homeStanding);
        teamStandingRepository.save(awayStanding);
    }
}
