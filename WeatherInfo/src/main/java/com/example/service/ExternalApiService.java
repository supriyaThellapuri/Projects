package com.example.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.dto.LatLong;
import com.example.dto.WeatherInfoDto;
import com.example.model.PincodeInfo;
import com.example.model.WeatherResponse;
import com.example.repo.PincodeRepo;

@Service
public class ExternalApiService {

    @Value("${openweather.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PincodeRepo pincodeInfoRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public WeatherInfoDto getWeatherData(String pincode, String forDate) {
        logger.info("Received pincode: {}, forDate: {}", pincode, forDate);  // Log the received values

        LocalDate parsedDate;
        // Validate date format and parse it
        try {
            // Log the incoming date string
            logger.info("Attempting to parse date: {}", forDate);
            parsedDate = LocalDate.parse(forDate, DateTimeFormatter.ofPattern(DATE_FORMAT).withLocale(Locale.ENGLISH));
        } catch (DateTimeParseException e) {
            logger.error("Failed to parse date: {}. Expected format: {}", forDate, DATE_FORMAT);
            throw new IllegalArgumentException("Invalid date format. Please use " + DATE_FORMAT + ".");
        }

        logger.info("Fetching weather data for pincode: {}, for date: {}", pincode, parsedDate);
        LatLong latLong = getLatLong(pincode);

        // Ensure latLong is not null
        if (latLong == null) {
            throw new RuntimeException("No latitude/longitude found for pincode: " + pincode);
        }
        
        // Fetch weather data based on latitude and longitude
        WeatherInfoDto weatherInfo = fetchWeatherData(latLong.getLatitude(), latLong.getLongitude(), parsedDate);
        
        // Save weather info to the database
        saveWeatherInfo(pincode, latLong, weatherInfo);
        return weatherInfo;
    }


    private LocalDate validateAndParseDate(String date) {
        // Trim the date to remove any leading or trailing spaces
        date = date.trim();

        // Validate date format
        if (date.length() != 10 || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Please use " + DATE_FORMAT + ".");
        }

        // Parse date
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT).withLocale(Locale.ENGLISH));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use " + DATE_FORMAT + ".");
        }
    }

    private void saveWeatherInfo(String pincode, LatLong latLong, WeatherInfoDto weatherInfo) {
        logger.info("Saving weather info for pincode: {}, latitude: {}, longitude: {}, temperature: {}, humidity: {}",
                pincode, latLong.getLatitude(), latLong.getLongitude(), weatherInfo.getTemperature(), weatherInfo.getHumidity());

        PincodeInfo pincodeInfo = new PincodeInfo();
        pincodeInfo.setPincode(pincode);
        pincodeInfo.setLatitude(latLong.getLatitude());
        pincodeInfo.setLongitude(latLong.getLongitude());
        pincodeInfo.setTemperature(weatherInfo.getTemperature());
        pincodeInfo.setHumidity(weatherInfo.getHumidity());
        pincodeInfo.setDescription(weatherInfo.getDescription());
        pincodeInfo.setWindSpeed(weatherInfo.getWindSpeed());
        pincodeInfoRepository.save(pincodeInfo);
    }

    private LatLong getLatLong(String pincode) {
        logger.info("Getting latitude and longitude for pincode: {}", pincode);

        String url = "https://api.openweathermap.org/geo/1.0/zip?zip=" + pincode + ",IN&appid=" + apiKey;
        try {
            Map<String, Double> response = restTemplate.getForObject(url, HashMap.class);

            if (response == null || !response.containsKey("lat") || !response.containsKey("lon")) {
                throw new RuntimeException("Could not fetch latitude/longitude for pincode: " + pincode);
            }
            return new LatLong(response.get("lat"), response.get("lon"));
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching latitude and longitude: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Could not fetch latitude/longitude for pincode: " + pincode + ". Reason: " + e.getStatusCode());
        }
    }

    private WeatherInfoDto fetchWeatherData(double latitude, double longitude, LocalDate forDate) {
        logger.info("Fetching weather data for latitude: {}, longitude: {}, on date: {}", latitude, longitude, forDate);

        // Calculate the timestamp for the requested date
        long timestamp = forDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        String url = "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat={lat}&lon={lon}&dt={dt}&appid=" + apiKey;

        // Log the complete URL with parameters
        logger.info("Constructed URL: {}", url);
        
        Map<String, Object> params = new HashMap<>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("dt", timestamp);

        try {
            // Fetch the weather data using the constructed URL and parameters
            WeatherResponse weatherResponse = restTemplate.getForObject(url, WeatherResponse.class, params);
            
            // Check if the response and current weather data are valid
            if (weatherResponse != null && weatherResponse.getCurrent() != null) {
                return new WeatherInfoDto(
                    weatherResponse.getCurrent().getTemp(),
                    weatherResponse.getCurrent().getHumidity(),
                    weatherResponse.getCurrent().getWeather().get(0).getDescription(),
                    weatherResponse.getCurrent().getWindSpeed()
                );
            } else {
                logger.warn("No current weather data found for latitude: {}, longitude: {}", latitude, longitude);
            }
        } catch (HttpClientErrorException e) {
            // Log the error response from the API
            logger.error("Error fetching weather data: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            // Specific handling for 401 UNAUTHORIZED
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                logger.error("Authorization error. Please check your API key.");
                throw new RuntimeException("Could not fetch weather data for the given location. Reason: " + e.getStatusCode() + ". Please check your API key.");
            }
            
            // General error handling
            throw new RuntimeException("Could not fetch weather data for the given location. Reason: " + e.getStatusCode());
        }

        // Return null if weather data is not found or if an exception occurs
        logger.warn("Weather data not found for latitude: {}, longitude: {}", latitude, longitude);
        return null;
    }

}
