package com.skipcart.dto;

/**
 * @author Anish Panthi
 */
public record TokenResponse(String accessToken, String tokenType, String refreshToken) {}
