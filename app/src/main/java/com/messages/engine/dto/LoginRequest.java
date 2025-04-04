package com.messages.engine.dto;

public record LoginRequest(String username, String password, Integer totpCode) {}