package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherInfoDto {
	
	private double temperature;
    private double humidity;
    private String description;
    private double windSpeed;

}
