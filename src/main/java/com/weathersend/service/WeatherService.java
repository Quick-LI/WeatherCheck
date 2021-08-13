package com.weathersend.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : Quick_Li
 * @date : 2021/8/14 0:22
 */
public interface WeatherService {

    void getWeather(int cityID, HttpServletResponse response);

}
