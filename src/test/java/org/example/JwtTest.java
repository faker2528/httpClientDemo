package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.example.controller.WeatherController;
import org.example.generic.GenericResult;
import org.example.httpclient.HttpClientUtils;
import org.example.properties.JwtProperties;
import org.example.service.WeatherService;
import org.example.service.impl.WeatherServiceImpl;
import org.example.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JwtTest {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private WeatherService weatherService;

    @Test
    public void testPrivateKey() throws FileNotFoundException {
        File file = new File(jwtProperties.getFilePath());
        if (file.exists() && file.isFile()){
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    // 跳过 PEM 文件的开始和结束标记行
                    if (!line.startsWith("-----")) {
                        content.append(line);
                    }
                }
                System.out.println(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testJwt() throws Exception {
        String jwt = jwtUtils.generateHeWeatherJwt();
        System.out.println(jwt);
    }

    @Test
    public void testGetNow() throws Exception {
        Map reqParams = new HashMap<>();
        reqParams.put("SERVICE_URL", "https://m96cdmxg53.re.qweatherapi.com");
        reqParams.put("PATH", "/v7/weather/now");
        reqParams.put("TIMEOUT", 5000);
        Map haders = new HashMap<>();
        haders.put("Authorization", jwtUtils.generateAuthorizationHeader());
        reqParams.put("HEADERS", haders);
        Map queryMap = new HashMap<>();
        queryMap.put("location", "115.857972,28.682976");
        reqParams.put("PARAMS", queryMap);
        Map result = HttpClientUtils.doGet(reqParams);
        JSONObject jsonObject = JSONObject.parseObject(result.get("result").toString());
        System.out.println(result);
    }

    @Test
    public void testGetLocation() throws Exception {
        String gaodeKey = "71a9bb4995e523d9cd5ec2a31e7a7288";
        Map reqParams = new HashMap<>();
        reqParams.put("SERVICE_URL", "https://restapi.amap.com");
        reqParams.put("PATH", "/v3/geocode/geo");
        reqParams.put("TIMEOUT", 5000);
        Map queryMap = new HashMap<>();
        queryMap.put("address", "南昌市");
        queryMap.put("key", gaodeKey);
        reqParams.put("PARAMS", queryMap);

        //获取店铺的经纬度坐标
        Map res = HttpClientUtils.doGet(reqParams);

        JSONObject jsonObject = JSONObject.parseObject(res.get("result").toString());
        //数据解析
        String location = (String) jsonObject.getJSONArray("geocodes")
                .getJSONObject(0)
                .get("location");
//		String lat = location.getString("lat");
//		String lng = location.getString("lng");
//		//店铺经纬度坐标
//		String shopLngLat = lat + "," + lng;

        System.out.println(location);
    }

    @Test
    public void testGetForecast() throws Exception {
        WeatherController weatherController = new WeatherController(weatherService);
        GenericResult result = weatherController.getWeatherForecast("南昌市", "24h");
        System.out.println(result.toString());
    }
}
