package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.generic.GenericResult;
import org.example.mapper.ApiServicesMapper;
import org.example.service.ApiService;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

    private final ApiServicesMapper apiServicesMapper;

    @Override
    public Map getServices(Map params) {
        return apiServicesMapper.getServices(params);
    }
}
