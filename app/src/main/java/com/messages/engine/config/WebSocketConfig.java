package com.messages.engine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration for WebSocket messaging using STOMP.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker.
     *
     * @param registry the MessageBrokerRegistry to configure.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory broker for subscriptions on /topic.
        registry.enableSimpleBroker("/topic");
        // Application destination prefix for mapping controller methods.
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the WebSocket endpoint for client connections.
     *
     * @param registry the StompEndpointRegistry to register endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint clients will use to connect (with SockJS fallback).
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

}
