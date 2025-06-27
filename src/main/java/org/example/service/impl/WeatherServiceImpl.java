package org.example.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.generic.GenericResult;
import org.example.httpclient.HttpClientUtils;
import org.example.service.ApiService;
import org.example.service.EndPointsService;
import org.example.service.WeatherService;
import org.example.utils.JwtUtils;
import org.example.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final ApiService apiService;
    private final EndPointsService endPointsService;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;
    private final static String WEATHER_KEY_PREFIX = "weather:";
    private final static String FORECAST_KEY_PREFIX = "forecast:";
    private final static String LOCATION_KEY_PREFIX = "location:";
    @Override
    public GenericResult getWeatherNow(String location) throws Exception {
        GenericResult result = new GenericResult();

        // 先从缓存中获取数据
        GenericResult cacheRes = redisUtils.get(WEATHER_KEY_PREFIX + location);
        if (cacheRes != null && !cacheRes.getDataList().isEmpty()) {
            log.info("从缓存中获取天气数据: {}", cacheRes);
            return cacheRes;
        }

        // 如果没有，再发送请求
        Map reqParams = new HashMap<>();
        reqParams.put("serviceId", "2");
        Map service = apiService.getServices(reqParams);
        Map endPoints = endPointsService.getEndPoints(reqParams);

        reqParams.clear();
        reqParams.put("SERVICE_URL", service.get("base_url"));
        reqParams.put("PATH", endPoints.get("path"));
        reqParams.put("TIMEOUT", service.get("timeout")); // 显式设置30秒超时

        Map haders = new HashMap<>();
        haders.put("Authorization", jwtUtils.generateAuthorizationHeader());
        reqParams.put("HEADERS", haders);

        Map params = new HashMap<>();
        params.put("location", getLocation(location));
        reqParams.put("PARAMS", params);

        Map audioMap = HttpClientUtils.doGet(reqParams);
        JSONObject data = JSONObject.parseObject(audioMap.get("result").toString());
        Map dataMap = new HashMap<>();
        dataMap.put("data", data);
        result.addData(dataMap);
        redisUtils.set("weather_" + location, result, 60);
        return result;
    }

    @Override
    public GenericResult getWeatherForecast(String location, String hours) throws Exception {
        GenericResult result = new GenericResult();

        // 先从缓存中获取数据
        GenericResult cacheRes = redisUtils.get(FORECAST_KEY_PREFIX + location + hours);
        if (cacheRes != null && !cacheRes.getDataList().isEmpty()) {
            log.info("从缓存中获取天气预报数据: {},{}", location, hours);
            return cacheRes;
        }

        // 如果没有，再发送请求
        Map reqParams = new HashMap<>();
        reqParams.put("serviceId", "4");
        Map service = apiService.getServices(reqParams);
        Map endPoints = endPointsService.getEndPoints(reqParams);

        reqParams.clear();
        reqParams.put("SERVICE_URL", service.get("base_url"));
        reqParams.put("PATH", endPoints.get("path") + "/" + hours);
        reqParams.put("TIMEOUT", service.get("timeout")); // 显式设置30秒超时

        Map haders = new HashMap<>();
        haders.put("Authorization", jwtUtils.generateAuthorizationHeader());
        reqParams.put("HEADERS", haders);

        Map params = new HashMap<>();
        params.put("location", getLocation(location));
        reqParams.put("PARAMS", params);

        Map audioMap = HttpClientUtils.doGet(reqParams);
        JSONObject data = JSONObject.parseObject(audioMap.get("result").toString());
        Map dataMap = new HashMap<>();
        dataMap.put("data", data);
        result.addData(dataMap);
        redisUtils.set(FORECAST_KEY_PREFIX + location + hours, result, 60);
        return result;
    }

    public String getLocation(String location) throws Exception {
        // 获取该地址的经纬度坐标
        // 先从缓存取
        String locationCache = redisUtils.get(LOCATION_KEY_PREFIX + location);
        if (StringUtils.isNotBlank(locationCache)) {
            log.info("从缓存中获取地址坐标: {}", locationCache);
            return locationCache;
        }
        // 如果没有，再发送请求
        Map reqParams = new HashMap<>();
        reqParams.put("serviceId", "3");
        Map service = apiService.getServices(reqParams);
        Map endPoints = endPointsService.getEndPoints(reqParams);
        reqParams.clear();
        reqParams.put("SERVICE_URL", service.get("base_url"));
        reqParams.put("PATH", endPoints.get("path"));
        reqParams.put("TIMEOUT", service.get("timeout")); // 显式设置30秒超时
        Map queryMap = new HashMap<>();
        queryMap.put("address", location);
        queryMap.put("key", service.get("api_key"));
        reqParams.put("PARAMS", queryMap);

        //获取店铺的经纬度坐标
        Map res = HttpClientUtils.doGet(reqParams);

        JSONObject jsonObject = JSONObject.parseObject(res.get("result").toString());
        //数据解析
        String coordinate  = (String) jsonObject.getJSONArray("geocodes")
                .getJSONObject(0)
                .get("location");
        //将经纬度坐标存入缓存
        if (StringUtils.isNotBlank(coordinate)) {
            log.info("从接口中获取地址坐标: {}", coordinate);
            redisUtils.set(LOCATION_KEY_PREFIX + location, coordinate, 60*60*24*30);
        }
        return coordinate;
    }
}
