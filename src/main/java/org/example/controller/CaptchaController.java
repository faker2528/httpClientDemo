package org.example.controller;

import com.google.code.kaptcha.Producer;
import lombok.RequiredArgsConstructor;
import org.example.utils.RedisUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60; // 5分钟，单位秒

    private final Producer kaptchaProducer;
    private final RedisUtils redisUtils;

    @GetMapping
    public void generateCaptcha(HttpServletResponse response) throws IOException {

        // 生成唯一ID
        String uuid = UUID.randomUUID().toString();

        // 生成验证码文本
        String captchaText = kaptchaProducer.createText();

        // 存储验证码
        redisUtils.set(CAPTCHA_KEY_PREFIX + uuid, captchaText.toUpperCase(), CAPTCHA_EXPIRE_TIME);

        // 将uuid通过响应头返回给前端
        response.setHeader("X-Captcha-UUID", uuid);
        // 设置响应头
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 生成验证码图片
        BufferedImage image = kaptchaProducer.createImage(captchaText);

        // 输出图片
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "png", out);
            out.flush();
        }
    }
}