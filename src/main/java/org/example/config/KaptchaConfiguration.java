package org.example.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.Random;

@Configuration
public class KaptchaConfiguration {

    @Bean
    public DefaultKaptcha kaptcha() {
        Properties properties = new Properties();

        // 基础样式配置
        properties.put("kaptcha.border", "yes");
        properties.put("kaptcha.border.color", "192,192,192");
        properties.put("kaptcha.image.width", "180");
        properties.put("kaptcha.image.height", "60");

        // 文本生成配置（数字字母混合）
        properties.put("kaptcha.textproducer.impl", "com.google.code.kaptcha.text.impl.DefaultTextCreator");
        properties.put("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.put("kaptcha.textproducer.char.length", "4");
        properties.put("kaptcha.textproducer.char.space", "8");

        // 字体配置（使用随机RGB范围，通过配置间接实现颜色随机）
        properties.put("kaptcha.textproducer.font.names", "Arial,宋体,微软雅黑,黑体");
        properties.put("kaptcha.textproducer.font.size", "36");
        properties.put("kaptcha.textproducer.font.color", "200,15,160"); // 红色字体

        // 干扰元素配置（固定颜色干扰线）
        properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        properties.put("kaptcha.noise.color", "100,100,150"); // 灰色干扰线

        // 图片效果配置（增强干扰）
        properties.put("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        properties.put("kaptcha.word.impl", "com.google.code.kaptcha.text.impl.DefaultWordRenderer");

        // 背景配置（宽范围渐变）
        properties.put("kaptcha.background.impl", "com.google.code.kaptcha.impl.DefaultBackground");
        properties.put("kaptcha.background.clear.from", "200,230,255"); // 浅蓝
        properties.put("kaptcha.background.clear.to", "255,255,240");   // 米白

        // 验证码有效期
        properties.put("kaptcha.timeout", "300000"); // 5分钟

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(new Config(properties));
        return kaptcha;
    }
}