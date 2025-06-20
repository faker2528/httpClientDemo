package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.generic.GenericResult;

import java.util.Map;

@Mapper
public interface ApiServicesMapper {
    Map getServices(Map params);
}
