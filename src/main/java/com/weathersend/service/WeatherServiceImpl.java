package com.weathersend.service;

import com.weathersend.mapper.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : Quick_Li
 * @date : 2021/8/14 0:24
 */
@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    private Weather weather;

    @Override
    public void getWeather(int cityID, HttpServletResponse response) {
        weather.printWeather(cityID,response);
    }
}
