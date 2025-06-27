package org.example.controller;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.generic.GenericResult;
import org.example.mapper.UserMapper;
import org.example.properties.JwtProperties;
import org.example.utils.JwtUtils;
import org.example.utils.RedisUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final String LOGIN_FAIL_KEY_PREFIX = "login_fails:";
    private static final int MAX_LOGIN_ATTEMPTS = 5; // 最大尝试次数
    private static final long LOCK_TIME = 1; // 锁定时间（小时）

    private final RedisUtils redisUtils;
    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public GenericResult login(@RequestParam("username") String username, @RequestParam("password") String password,
                            @RequestParam("uuid") String uuid, @RequestParam("captcha") String captcha) throws Exception {

        log.info("请求登录：username: {}, password: {}, uuid: {}, captcha: {}", username, password, uuid, captcha);
        GenericResult result = new GenericResult();
        // 检查是否被锁定
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;
        Integer failCount = redisUtils.get(failKey);

        if (failCount != null && failCount >= MAX_LOGIN_ATTEMPTS) {
            result.setFlag("1");
            result.setPrompt("登录尝试次数过多，请1小时后再试");
            return result;
        }

        // 验证验证码
        String captchaKey = CAPTCHA_KEY_PREFIX + uuid;
        String storedCaptcha = redisUtils.get(captchaKey);

        if (storedCaptcha == null) {
            result.setFlag("1");
            result.setPrompt("验证码已过期！请重试");
            return result;
        }

        if (!storedCaptcha.equalsIgnoreCase(captcha.toUpperCase())) {
            // 验证码错误，增加失败计数
            redisUtils.increment(failKey, 1);
            redisUtils.setExpireTime(failKey, LOCK_TIME, TimeUnit.HOURS);

            // 删除验证码，防止复用
            redisUtils.delete(captchaKey);
            log.info("验证码错误，用户名：{}，验证码：{}", username, captcha);
            result.setFlag("1");
            result.setPrompt("验证码错误");
            return result;
        }

        // 验证码验证通过，删除验证码
        redisUtils.delete(captchaKey);

        Map user = userMapper.getUser(Collections.singletonMap("username", username));
        if (user == null || user.isEmpty()) {
            // 登录失败，增加失败计数
            redisUtils.increment(failKey, 1);
            redisUtils.setExpireTime(failKey, LOCK_TIME, TimeUnit.HOURS);
            log.info("登录失败，用户不存在，用户名：{}，密码：{}", username,password);
            result.setFlag("1");
            result.setPrompt("用户不存在");
            return result;
        }

        String status = (String) user.get("status");
        if (!"0".equals(status)) {
            // 登录失败，增加失败计数
            redisUtils.increment(failKey, 1);
            redisUtils.setExpireTime(failKey, LOCK_TIME, TimeUnit.HOURS);
            log.info("登录失败，用户状态异常，用户名：{}，密码：{}", username,password);
            result.setFlag("1");
            result.setPrompt("用户状态异常");
            return result;
        }

        if (!BCrypt.checkpw(password, (String) user.get("password"))) {
            // 登录失败，增加失败计数
            redisUtils.increment(failKey, 1);
            redisUtils.setExpireTime(failKey, LOCK_TIME, TimeUnit.HOURS);
            log.info("登录失败，用户名或密码错误，用户名：{}，密码：{}", username,password);
            result.setFlag("1");
            result.setPrompt("用户名或密码错误");
            return result;
        }

        // 登录成功，清除失败计数
        redisUtils.delete(failKey);

        // TODO: 生成并返回Token
        log.info("登录成功，{},{},{},{}", username, password, uuid, captcha);
        result.setFlag("0");
        result.setPrompt("登录成功");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(jwtProperties.getTokenName(), jwtUtils.generateHeWeatherJwt());
        result.addData(dataMap);

        return result;
    }
}