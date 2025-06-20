package org.example;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.example.generic.GenericResult;
import org.example.httpclient.HttpClient;
import org.example.httpclient.HttpClientUtils;
import org.example.service.ApiService;
import org.example.service.EndPointsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashMap;
import java.util.Map;

//@SpringBootTest
public class HttpClientTest {
    @Autowired
    private ApiService apiService;
    @Autowired
    private EndPointsService endPointsService;
    @Test
    public void doGetTest() throws Exception {
        Map reqParams = new HashMap<>();
        reqParams.put("SERVICE_URL", "https://v.api.aa1.cn");
        reqParams.put("PATH", "/api/zhihu-news/");
        reqParams.put("TIMEOUT", 5000);
        Map queryMap = new HashMap<>();
        queryMap.put("aa1", "xiarou");
        reqParams.put("PARAMS", queryMap);
        Map result = HttpClientUtils.doGet(reqParams);
        JSONObject jsonObject = JSONObject.parseObject(result.get("result").toString());
        System.out.println(result);
    }

    @Test
    public void doGetTest2() throws Exception {
        Map reqParams = new HashMap<>();
        reqParams.put("serviceId", "1");
        Map service = apiService.getServices(reqParams);
        Map endPoints = endPointsService.getEndPoints(reqParams);

        reqParams.clear();
        reqParams.put("SERVICE_URL", service.get("base_url"));
        reqParams.put("PATH", endPoints.get("path"));
        reqParams.put("TIMEOUT", service.get("timeout")); // 显式设置30秒超时

        Map params = new HashMap<>();
        params.put("key", service.get("api_key"));
        params.put("speed", "6");
        params.put("msg", "如梦令");
        reqParams.put("PARAMS", params);

        Map result = HttpClientUtils.doGet(reqParams);
        System.out.println("响应结果: " + result);

        // 解析响应JSON（如果需要）
        if ("0".equals(result.get("code"))) {
            String responseJson = (String) result.get("result");
            // 使用FastJSON2解析响应
            Map responseMap = JSON.parseObject(responseJson, Map.class);
            System.out.println("解析结果: " + responseMap);
        }
    }

    @Test
    public void doPostTest() throws Exception {
        Map reqParams = new HashMap<>();
        reqParams.put("serviceId", "1");
        Map service = apiService.getServices(reqParams);
        Map endPoints = endPointsService.getEndPoints(reqParams);

        reqParams.clear();
        reqParams.put("SERVICE_URL", service.get("base_url"));
        reqParams.put("PATH", endPoints.get("path"));
        reqParams.put("TIMEOUT", service.get("timeout")); // 显式设置30秒超时

        Map params = new HashMap<>();
        params.put("key", service.get("api_key"));
        params.put("speed", "6");
        params.put("msg", "同是天涯沦落人，相逢何必曾相识");
        reqParams.put("BODY", params);

        // 显式设置内容类型为JSON
        reqParams.put("CONTENT_TYPE", "application/json");

        Map result = HttpClientUtils.doPost(reqParams);
        System.out.println("响应结果: " + result);

        // 解析响应JSON（如果需要）
        if ("0".equals(result.get("code"))) {
            String responseJson = (String) result.get("result");
            // 使用FastJSON2解析响应
            Map responseMap = JSON.parseObject(responseJson, Map.class);
            System.out.println("解析结果: " + responseMap);
        }
    }

    @Test
    public void doPostTest3() throws Exception {
        HttpClient.doPostTest2();
    }


}
