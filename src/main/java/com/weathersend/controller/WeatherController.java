package com.weathersend.controller;

import com.weathersend.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : Quick_Li
 * @date : 2021/8/14 0:41
 */
@Controller
public class WeatherController {
    @Autowired
    WeatherService weatherService;

    //根据城市ID获取天气
    @RequestMapping("getWeather")
    public void getWeather(HttpServletResponse response){
        weatherService.getWeather(101010100,response);
    }
}
