package com.ecom.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecom.model.DistanceResponse;
import com.ecom.model.DistanceResponse.Distance;
import com.ecom.model.DistanceResponse.Leg;
import com.ecom.model.DistanceResponse.Route;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleMapsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper for JSON parsing

    @Value("${google.api.key}") // Assuming you have this set in your application.properties
    private String apiKey;

    public GoogleMapsService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper; // Initialize ObjectMapper
    }

    public DistanceResponse fetchDistance(String fromPincode, String toPincode) {
        // Construct the URL for the Directions API
        String url = String.format(
            "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
            fromPincode, toPincode, apiKey
        );

        // Make the API call
        String jsonResponse = restTemplate.getForObject(url, String.class);

        // Log the JSON response
        System.out.println("Google Maps API Response: " + jsonResponse); // Log the complete response

        // Parse the JSON response
        return parseDistanceResponse(jsonResponse);
    }


    private DistanceResponse parseDistanceResponse(String json) {
        List<Route> routes = new ArrayList<>();

        try {
            // Parse JSON using ObjectMapper
            JsonNode rootNode = objectMapper.readTree(json);
            
            // Log any error messages from the response
            if (rootNode.has("error_message")) {
                logger.error("Google Maps API Error: {}", rootNode.path("error_message").asText());
            }

            JsonNode routesNode = rootNode.path("routes");
            if (routesNode.isArray() && routesNode.size() > 0) {
                JsonNode routeNode = routesNode.get(0);
                List<Leg> legs = new ArrayList<>();

                JsonNode legsNode = routeNode.path("legs");
                if (legsNode.isArray() && legsNode.size() > 0) {
                    for (JsonNode legNode : legsNode) {
                        String distanceText = legNode.path("distance").path("text").asText();
                        int distanceValue = legNode.path("distance").path("value").asInt();
                        String durationText = legNode.path("duration").path("text").asText();
                        int durationValue = legNode.path("duration").path("value").asInt();

                        Distance distance = new Distance(distanceText, distanceValue);
                        com.ecom.model.DistanceResponse.Duration duration = 
                            new com.ecom.model.DistanceResponse.Duration(durationText, durationValue);
                        Leg leg = new Leg(distance, duration);

                        legs.add(leg);
                    }
                }

                // Create a Route object
                Route route = new Route(legs, routeNode.path("summary").asText());
                routes.add(route);
            }
        } catch (IOException e) {
            logger.error("Error parsing JSON response: {}", e.getMessage());
        }

        return new DistanceResponse(routes);
    }

}
