package com.miniscore.live.service;

import com.miniscore.live.config.RabbitMqConfig;
import com.miniscore.live.event.GoalScoredEvent;
import com.miniscore.live.event.MatchEndedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishGoalScored(GoalScoredEvent event) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EVENTS_EXCHANGE, RabbitMqConfig.GOAL_ROUTING_KEY, event);
    }

    public void publishMatchEnded(MatchEndedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EVENTS_EXCHANGE, RabbitMqConfig.MATCH_ENDED_ROUTING_KEY, event);
    }
}
