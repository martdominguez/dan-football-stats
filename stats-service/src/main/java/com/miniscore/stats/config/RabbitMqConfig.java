package com.miniscore.stats.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EVENTS_EXCHANGE = "mini-score.events";
    public static final String GOAL_QUEUE = "stats.goal.queue";
    public static final String MATCH_ENDED_QUEUE = "stats.match-ended.queue";
    public static final String GOAL_ROUTING_KEY = "match.goal.scored";
    public static final String MATCH_ENDED_ROUTING_KEY = "match.ended";

    @Bean
    public DirectExchange eventsExchange() {
        return new DirectExchange(EVENTS_EXCHANGE);
    }

    @Bean
    public Queue goalQueue() {
        return QueueBuilder.durable(GOAL_QUEUE).build();
    }

    @Bean
    public Queue matchEndedQueue() {
        return QueueBuilder.durable(MATCH_ENDED_QUEUE).build();
    }

    @Bean
    public Binding goalBinding(Queue goalQueue, DirectExchange eventsExchange) {
        return BindingBuilder.bind(goalQueue).to(eventsExchange).with(GOAL_ROUTING_KEY);
    }

    @Bean
    public Binding matchEndedBinding(Queue matchEndedQueue, DirectExchange eventsExchange) {
        return BindingBuilder.bind(matchEndedQueue).to(eventsExchange).with(MATCH_ENDED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
