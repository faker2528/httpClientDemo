package org.example.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.generic.GenericResult;
import org.example.httpclient.HttpClientUtils;
import org.example.service.ApiService;
import org.example.service.AudioService;
import org.example.service.EndPointsService;
import org.example.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioServiceImpl implements AudioService {
    private final ApiService apiService;
    private final EndPointsService endPointsService;
    private final RedisUtils redisUtils;

    @Override
    public GenericResult transferText(String text, String speed) throws Exception {
        GenericResult result = new GenericResult();

        GenericResult cacheRes = redisUtils.get("audio_" + text);
        if (cacheRes != null && !cacheRes.getDataList().isEmpty()) {
            log.info("从缓存中获取语音数据: {}", cacheRes);
            return cacheRes;
        }

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
        int speedInt = NumberUtils.toInt(speed,6);
        params.put("speed", speedInt);
        params.put("msg", text);
        reqParams.put("PARAMS", params);

        Map audioMap = HttpClientUtils.doGet(reqParams);
        JSONObject data = JSONObject.parseObject(audioMap.get("result").toString());
        Map dataMap = new HashMap<>();
        dataMap.put("data", data);
        result.addData(dataMap);
        redisUtils.set("audio_" + text, result, 60*60*24*30);
        return result;
    }
}
