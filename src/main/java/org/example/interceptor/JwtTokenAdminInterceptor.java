package org.example.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.properties.JwtProperties;
import org.example.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final JwtUtils jwtUtils;
    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String authorization = request.getHeader(jwtProperties.getTokenName());
        String jwtToken = authorization.replace("Bearer ", "");
        //2、校验令牌
        try {
            log.info("jwt校验:{}", jwtToken);
            jwtUtils.verifyJwt(jwtToken, jwtProperties.getPublicKeyPem());
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            log.error("jwt校验失败:{}", ex.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}
