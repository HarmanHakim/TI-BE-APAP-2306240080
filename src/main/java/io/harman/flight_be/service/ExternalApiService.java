package io.harman.flight_be.service;

import java.util.UUID;

import io.harman.flight_be.dto.UserProfileDTO;

public interface ExternalApiService {
    UserProfileDTO getUserProfile(UUID userId);
}
