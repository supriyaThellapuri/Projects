package com.ecom.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceResponse {
    
    private List<Route> routes; // List of routes

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Route {
        private List<Leg> legs; // Each route can have multiple legs
        
        // Other fields can be added here if needed
        private String summary; // Summary of the route
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Leg {
        private Distance distance; // Distance object
        private Duration duration; // Duration object
        
        // You can add other attributes such as start_location, end_location, etc.
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Distance {
        private String text; // Text representation of distance (e.g., "13 km")
        private int value; // Value representation of distance in meters
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Duration {
        private String text; // Text representation of duration (e.g., "15 mins")
        private int value; // Value representation of duration in seconds
    }
}
