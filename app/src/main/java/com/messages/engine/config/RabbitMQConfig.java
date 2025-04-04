package com.messages.engine.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for RabbitMQ used in chat messaging.
 */
@Configuration
public class RabbitMQConfig {

    /** Name of the chat exchange. */
    public static final String CHAT_EXCHANGE = "chatExchange";

    /** Name of the chat queue. */
    public static final String CHAT_QUEUE = "chatQueue";

    /** Routing key for chat messages. */
    public static final String CHAT_ROUTING_KEY = "chat.message";

    /**
     * Declares a topic exchange for chat messages.
     *
     * @return the chat TopicExchange.
     */
    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE);
    }

    /**
     * Declares the chat queue.
     *
     * @return the chat Queue.
     */
    @Bean
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE);
    }

    /**
     * Binds the chat queue to the exchange using a routing key.
     *
     * @param queue the chat queue.
     * @param exchange the chat exchange.
     * @return the Binding between the queue and exchange.
     */
    @Bean
    public Binding chatBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CHAT_ROUTING_KEY);
    }

}
