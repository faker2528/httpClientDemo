package org.example.service;

import org.example.generic.GenericResult;
import org.springframework.stereotype.Service;

@Service
public interface WeatherService {
    GenericResult getWeatherNow(String location) throws Exception;

    GenericResult getWeatherForecast(String location, String hours) throws Exception;
}
