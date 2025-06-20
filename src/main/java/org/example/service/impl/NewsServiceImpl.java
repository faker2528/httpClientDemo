package org.example.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.generic.GenericResult;
import org.example.httpclient.HttpClientUtils;
import org.example.service.NewsService;
import org.example.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {

    private final RedisUtils redisUtils;

    @Override
    public GenericResult getNews() {
        GenericResult result = new GenericResult();

        // 先查缓存
        GenericResult cacheRes = redisUtils.get("news");
        if (cacheRes != null && !cacheRes.getDataList().isEmpty()) {
            log.info("从缓存中获取数据: {}", cacheRes);
            return cacheRes;
        }

        // 如果缓存没有，再查数据库
        Map reqParams = new HashMap<>();
        reqParams.put("SERVICE_URL", "https://v.api.aa1.cn");
        reqParams.put("PATH", "/api/zhihu-news/");
        reqParams.put("TIMEOUT", 5000);
        Map queryMap = new HashMap<>();
        queryMap.put("aa1", "xiarou");
        reqParams.put("PARAMS", queryMap);
        try {
            Map res = HttpClientUtils.doGet(reqParams);
            JSONObject data = JSONObject.parseObject(res.get("result").toString());
            Map dataMap = new HashMap<>();
            dataMap.put("data", data);
            result.addData(dataMap);
            result.setFlag("0");
            result.setSuccess();
        }catch (Exception e){
            result.setError("1", e.getMessage());
            return result;
        }
        // 将数据存入缓存
        redisUtils.set("news", result, 60*60*6);
        return result;
    }
}
