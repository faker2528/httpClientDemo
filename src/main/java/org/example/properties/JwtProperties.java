package org.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "demo.jwt")
@Data
public class JwtProperties {

    /**
     * 和风天气生成jwt令牌相关配置
     */
    private String kid; // 凭据ID，在和风天气控制台-项目管理中查看
    private String sub; //签发主体，这个值是凭据的项目ID，在控制台-项目管理中查看
    private long ttl; //jwt令牌过期时间，单位毫秒
    private String tokenName; //前端请求的jwt令牌名称
    private String filePath; // 存储jwt密钥的文件路径
}
