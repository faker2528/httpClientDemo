package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
    Map addUser(Map params);
    Map getUser(Map params);
    Map updateUser(Map params);
    Map deleteUser(Map params);
}
