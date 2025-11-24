package io.harman.flight_be.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.harman.flight_be.dto.UserProfileDTO;
import io.harman.flight_be.dto.rest.BaseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.sidating-app-be-url}")
    private String be1Url;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authHeader);
            }
        }

        return headers;
    }

    public UserProfileDTO getUserProfile(UUID userId) {
        try {
            String url = be1Url + "/api/profile/" + userId;
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<BaseResponseDTO<UserProfileDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<BaseResponseDTO<UserProfileDTO>>() {}
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }

            System.err.println("User profile response body is null for userId: " + userId);
            return null;
        } catch (Exception e) {
            System.err.println("Error fetching user profile from BE1 for userId " + userId + ": " + e.getMessage());
            return null;
        }
    }
}
