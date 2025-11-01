package io.harman.flight_be.service;

import java.util.Map;

public interface HomeService {
    
    /**
     * Get home statistics including:
     * - Number of active flights today
     * - Number of bookings created today
     * - Number of registered airlines
     */
    Map<String, Object> getHomeStatistics();
}
