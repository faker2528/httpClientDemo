package org.example.service.impl;

import org.example.generic.GenericResult;
import org.example.service.OcrService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class OcrServiceImpl implements OcrService {
    @Override
    public GenericResult recognition(MultipartFile file) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "张三");
        dataMap.put("age", 18);
        dataMap.put("sex", "男");
        GenericResult result = new GenericResult();
        result.addData(dataMap);
        return result;
    }
}
