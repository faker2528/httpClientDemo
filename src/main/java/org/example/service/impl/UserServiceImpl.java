package org.example.service.impl;
import com.alibaba.druid.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.generic.GenericResult;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public GenericResult addUser(Map params) {
        GenericResult result = new GenericResult();
        // 新增前先校验参数
        String userName = (String) params.get("username");
        String password = (String) params.get("password");
        String status = (String) params.get("status");
        if (StringUtils.isEmpty(userName)|| StringUtils.isEmpty(password)|| StringUtils.isEmpty(status)) {
            log.info("增加用户参数错误");
            result.setError("1", "参数错误");
        }

        // 先判断用户是否存在
        Map userMap = userMapper.getUser(Collections.singletonMap("username", userName));
        if (!userMap.isEmpty()) {
            log.info("用户已存在");
            result.setError("1", "用户已存在");
        }

        // 新增用户
        userMapper.addUser(params);
        result.setSuccess();
        return result;
    }
}
