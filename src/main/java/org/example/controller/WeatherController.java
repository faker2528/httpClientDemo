package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.generic.GenericResult;
import org.example.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;
    @GetMapping("/now")
    public GenericResult getWeatherNow(@RequestParam("location") String location) throws Exception {
        log.info("location: {}", location);
        return weatherService.getWeatherNow(location);
    }

    @GetMapping("/forecast")
    public GenericResult getWeatherForecast(@RequestParam("location") String location,
                                            @RequestParam("hours") String hours) throws Exception {
        log.info("location: {}, hours: {}", location, hours);
        return weatherService.getWeatherForecast(location, hours);
    }
}
