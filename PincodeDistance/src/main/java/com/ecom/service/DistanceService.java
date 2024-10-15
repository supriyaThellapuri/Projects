package com.ecom.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.DistanceResponse;

@Service
public class DistanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DistanceService.class);
    private final GoogleMapsService googleMapsService;

    // Thread-safe caching structure to hold distance responses with timestamps
    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRATION_MINUTES = 60; // Set cache expiration time

    @Autowired
    public DistanceService(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }

    public DistanceResponse calculateDistance(String fromPincode, String toPincode) {
        String key = fromPincode + "_" + toPincode;

        // Check if the distance is already cached and not expired
        CacheEntry entry = cache.get(key);
        if (entry != null && !isExpired(entry.timestamp)) {
            logger.info("Cache hit for key: {}", key);
            return entry.response;
        }

        // Fetch distance from Google Maps Service
        logger.info("Cache miss for key: {}, fetching from Google Maps", key);
        DistanceResponse response = googleMapsService.fetchDistance(fromPincode, toPincode);
        
        if (response != null) {
            cache.put(key, new CacheEntry(response, LocalDateTime.now())); // Cache the response with timestamp
            logger.info("Distance fetched and cached for key: {}", key);
        } else {
            logger.warn("Failed to fetch distance for key: {}", key);
        }
        
        return response;
    }

    // Method to check if the cache entry has expired
    private boolean isExpired(LocalDateTime timestamp) {
        return LocalDateTime.now().isAfter(timestamp.plusMinutes(CACHE_EXPIRATION_MINUTES));
    }

    // Inner class to hold cache entries with their response and timestamp
    private static class CacheEntry {
        DistanceResponse response;
        LocalDateTime timestamp;

        CacheEntry(DistanceResponse response, LocalDateTime timestamp) {
            this.response = response;
            this.timestamp = timestamp;
            logger.debug("Cached entry created for response: {}", response); // Log cache entry creation
        }
    }
}
