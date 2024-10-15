package com.example.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.WeatherInfoDto;
import com.example.service.ExternalApiService;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private ExternalApiService externalApiService;

    @GetMapping("/{pincode}/{forDate}")
    public ResponseEntity<?> getWeatherData(@PathVariable String pincode, @PathVariable String forDate) {
        try {
            WeatherInfoDto weatherInfo = externalApiService.getWeatherData(pincode, forDate);
            return ResponseEntity.ok(weatherInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}
