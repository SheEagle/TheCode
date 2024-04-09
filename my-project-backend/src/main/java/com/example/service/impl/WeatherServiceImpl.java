package com.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Resource
    RestTemplate rest;
    @Value("${spring.weather.key}")
    String key;

    @Resource
    StringRedisTemplate template;

    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {

        return fetchFromCache(longitude, latitude);
    }

    private WeatherVO fetchFromCache(double longitude, double latitude) {
//        byte[] data = rest.getForObject
//                ("https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key, byte[].class);
        JSONObject geo = this.decompressStringToJson(rest.getForObject
                ("https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key, byte[].class));

        if (geo == null)
            return null;
        JSONObject location = geo.getJSONArray("location").getJSONObject(0);
        String id = location.getString("id");
        String key = "weather:" + id;
        String cache = template.opsForValue().get(key);
        if (cache != null)
            return JSONObject.parseObject(cache).to(WeatherVO.class);
        WeatherVO vo = this.fetchFromAPI(id, location);
        if (vo == null)
            return null;
        template.opsForValue().set(key, JSONObject.from(vo).toString(), 1, TimeUnit.HOURS);
        return vo;


    }

    private WeatherVO fetchFromAPI(String id, JSONObject location) {
        WeatherVO vo = new WeatherVO();
        vo.setLocation(location);

        JSONObject now = this.decompressStringToJson(rest.getForObject
                ("https://devapi.qweather.com/v7/weather/now?location=" + id + "&key=" + key, byte[].class));
        if (now == null)
            return null;
        vo.setNow(now.getJSONObject("now"));
        JSONObject hourly = this.decompressStringToJson(rest.getForObject
                ("https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + key, byte[].class));
        if (hourly == null)
            return null;
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));

        return vo;

    }


    private JSONObject decompressStringToJson(byte[] data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gzip.read(buffer)) != -1)
                stream.write(buffer, 0, read);
            gzip.close();
            stream.close();
            return JSONObject.parseObject(stream.toString());
        } catch (IOException e) {
            return null;
        }


    }

}
