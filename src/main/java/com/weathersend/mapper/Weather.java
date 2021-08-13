package com.weathersend.mapper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

@Component
public class Weather {
    private final static int TODAYWEATHER = 0;
    private final static int TOMMORROWWEATHER = 1;
    private final static int AFTERTOMMORROWWEATHER = 2;
    private JSONArray jsonArray;
    private ArrayList<String> arrayList;
    private Gson gson;
    private StringBuffer stringBuffer;
    private Map<String , Object> weatherMap;
    private HttpServletResponse response;
    private Writer writer;

    public void printWeather(int cityID, HttpServletResponse response){
        this.response = response;
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        writer = this.getWriter();
        jsonArray = getWeatherJSON(cityID);
        printTodayWeather();
        printTomorrowWeather();
        printAfterTomorrowWeather();
    }
    public Writer getWriter(){
        try {
            return response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray getWeatherJSON(int cityID) {
        //URL url = new URL("http://wthrcdn.etouch.cn/weather_mini?city="+cityName);
        URL url = URLSet(cityID);
        URLConnection urlConnection = getURLConnection(url);
        String dates = getURLJSONString(urlConnection);
        JSONArray jsonArray = getJSONArray(dates);
        return jsonArray;
    }
    public URL URLSet(int cityID) {
        URL url = null;
        try {
            url = new URL("http://wthrcdn.etouch.cn/weather_mini?citykey="+cityID);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    return url;
    }
    public URLConnection getURLConnection(URL url){
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setConnectTimeout(1000);
        return connection;
    }
    public String getURLJSONString(URLConnection connection){
        String datas = null;
        try {
            datas = getJsonStringFromGZIP(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }
    public JSONArray getJSONArray(String dates){
        JSONObject jsonObject = JSONObject.parseObject(dates);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray forecast = data.getJSONArray("forecast");
        return forecast;
    }
    public void printTodayWeather(){
        ChangeListToMap(TODAYWEATHER);
        TodayWeatherString();
    }
    public void printTomorrowWeather(){
        ChangeListToMap(TOMMORROWWEATHER);
        TomorrowWeatherString();
    }
    public void printAfterTomorrowWeather(){
        ChangeListToMap(AFTERTOMMORROWWEATHER);
        AfterTomorrowWeatherString();
    }
    public void ChangeListToMap(int dayNum){
        getDateJson();
        getDateMap(dayNum);
    }
    public void getDateJson(){
        arrayList = new ArrayList<>();
        String pattern = "\\{(.*?)\\}";
        String text = jsonArray.toString();
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        while (m.find()) {
            String temp = m.group();
            arrayList.add(temp);
        }
    }
    //List表单转换为Map
    public void getDateMap(int value){
            gson = new Gson();
            if(value == 0){
                getTodayMap();
            }
            if(value == 1){
                getTomorrowMap();
            }
            if(value == 2){
                getAfterTomorrowMap();
            }
    }
    public void getTodayMap(){
        String dateString = arrayList.get(TODAYWEATHER).toString();
        weatherMap = new HashMap<>();
        weatherMap = gson.fromJson(dateString,weatherMap.getClass());
    }
    public void getTomorrowMap(){
        String dateString = arrayList.get(TOMMORROWWEATHER).toString();
        weatherMap = gson.fromJson(dateString, weatherMap.getClass());
    }
    public void getAfterTomorrowMap(){
        String dateString = arrayList.get(AFTERTOMMORROWWEATHER).toString();
        weatherMap = gson.fromJson(dateString,weatherMap.getClass());
    }

    public void TodayWeatherString(){
        getTodayTime();
        getWeather();
    }
    public void TomorrowWeatherString(){
        getTomorrowTime();
        getWeather();
    }
    public void AfterTomorrowWeatherString() {
        getAfterTomorrowTime();
        getWeather();
    }

    public void getTodayTime(){
        Calendar calendar = Calendar.getInstance();
        try {
            writer.write("获取天气中" + "<br>");
            writer.write("-----天气预报-----");
            writer.write("今天是" + getYear(calendar) + "年" + (getMonth(calendar) + 1) +"月" + (getDay(calendar)) + "日"+ getWeekOfDay(calendar)  + "<br>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getTomorrowTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE) + 1);
        try {
            writer.write("-----明日天气-----"  + "<br>");
            writer.write("明日是" + getYear(calendar) + "年" + (getMonth(calendar) + 1) +"月" + (getDay(calendar)) + "日"+ getWeekOfDay(calendar)  + "<br>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getAfterTomorrowTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE) + 2);
        try {
            writer.write("-----后日天气-----"  + "<br>");
            writer.write("后日是" + getYear(calendar) + "年" + (getMonth(calendar) + 1) +"月" + (getDay(calendar)) + "日"+ getWeekOfDay(calendar)  + "<br>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取时间
    public int getYear(Calendar calendar){
        return calendar.get(Calendar.YEAR);
    }
    public int getMonth(Calendar calendar){
        return calendar.get(Calendar.MONTH);
    }
    public int getDay(Calendar calendar){
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    public String getWeekOfDay(Calendar calendar){
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == 1) { return "星期日";}
        if(dayOfWeek == 2) { return "星期一";}
        if(dayOfWeek == 3) { return "星期二";}
        if(dayOfWeek == 4) { return "星期三";}
        if(dayOfWeek == 5) { return "星期四";}
        if(dayOfWeek == 6) { return "星期五";}
        if(dayOfWeek == 7) { return "星期六";}
        return null;
    }
    //获取日期
    public void getWeather(){
        getWeatherType();
        getWeatherTemperature();
        getWeatherWindForce();
    }
    public void getWeatherType(){
        String weatherType = weatherMap.get("type").toString();
        try {
            writer.write("天气：" + weatherType  + "<br>");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void getWeatherTemperature(){
        try {
            writer.write("最低温度"+ getWeatherLow() + ",最高温度" + getWeatherHigh()  + "<br>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getWeatherLow(){
        String weatherLow = weatherMap.get("low").toString();
        stringBuffer = new StringBuffer(weatherLow);
        weatherLow = stringBuffer.substring(2);
        return weatherLow;
    }
    public String getWeatherHigh(){
        String weatherHigh = weatherMap.get("high").toString();
        stringBuffer = new StringBuffer(weatherHigh);
        weatherHigh = stringBuffer.substring(2);
        return weatherHigh;
    }
    public void getWeatherWindForce(){
        String weatherWind = weatherMap.get("fengli").toString();
        stringBuffer = new StringBuffer(weatherWind);
        weatherWind = stringBuffer.substring(9,11);
        try {
            writer.write("风力" + weatherWind  + "<br>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //对获取json解码
    private String getJsonStringFromGZIP(InputStream is)  throws Exception{
        String jsonString = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.mark(2);
        // 取前两个字节
        byte[] header = new byte[2];
        int result = bis.read(header);
        // reset输入流到开始位置
        bis.reset();
        // 判断是否是GZIP格式
        int headerData = getShort(header);
        if (result != -1 && headerData == 0x1f8b) {
            is = new GZIPInputStream(bis);
        } else {
            is = bis;
        }
        InputStreamReader reader = new InputStreamReader(is, "utf-8");
        char[] data = new char[100];
        int readSize;
        StringBuffer sb = new StringBuffer();
        while ((readSize = reader.read(data)) > 0) {
            sb.append(data, 0, readSize);
        }
        jsonString = sb.toString();
        bis.close();
        reader.close();

        return jsonString;
    }
    private int getShort(byte[] data) {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }
}


