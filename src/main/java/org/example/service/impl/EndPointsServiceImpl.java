package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.generic.GenericResult;
import org.example.mapper.EndPointsMapper;
import org.example.service.EndPointsService;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class EndPointsServiceImpl implements EndPointsService {
    private final EndPointsMapper endPointsMapper;
    @Override
    public Map getEndPoints(Map params) {
        return endPointsMapper.getEndPoints(params);
    }
}
