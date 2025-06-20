package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.example.properties.JwtProperties;
import org.springframework.stereotype.Component;
import net.i2p.crypto.eddsa.*;
import net.i2p.crypto.eddsa.spec.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    // 从PEM文件加载私钥
    private PrivateKey getPrivateKeyFromPem() throws Exception {
        File file = new File(jwtProperties.getFilePath());
        StringBuilder privateKeyContent = new StringBuilder();

        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    // 跳过 PEM 文件的开始和结束标记行
                    if (!line.startsWith("-----")) {
                        privateKeyContent.append(line);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("读取私钥文件失败", e);
            }
        } else {
            throw new IOException("私钥文件不存在或不是有效文件: " + jwtProperties.getFilePath());
        }

        String privateKeyPem = privateKeyContent.toString();
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return new EdDSAPrivateKey(keySpec);
    }

    // 生成和风天气JWT
    public String generateHeWeatherJwt() throws Exception {
        PrivateKey privateKey = getPrivateKeyFromPem();

        // 构建 JWT Header
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "EdDSA");
        if (jwtProperties.getKid() != null && !jwtProperties.getKid().isEmpty()) {
            headers.put("kid", jwtProperties.getKid());
        }
        String headerJson = toJson(headers);
        String encodedHeader = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));

        // 构建 JWT Payload
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", jwtProperties.getSub());
        Instant now = Instant.now();
        claims.put("iat", now.minusSeconds(30).getEpochSecond());
        claims.put("exp", now.plusSeconds(jwtProperties.getTtl() / 1000).getEpochSecond()); // 毫秒转秒
        String payloadJson = toJson(claims);
        String encodedPayload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        // 生成签名
        String signingInput = encodedHeader + "." + encodedPayload;
        byte[] signatureBytes = signEd25519(signingInput.getBytes(StandardCharsets.UTF_8), privateKey);
        String encodedSignature = base64UrlEncode(signatureBytes);

        // 组装完整的 JWT
        return signingInput + "." + encodedSignature;
    }

    // 生成带Bearer前缀的授权头
    public String generateAuthorizationHeader() throws Exception {
        return "Bearer " + generateHeWeatherJwt();
    }

    // 使用 Ed25519 算法签名
    private byte[] signEd25519(byte[] data, PrivateKey privateKey) throws Exception {
        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        Signature s = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
        s.initSign(privateKey);
        s.update(data);
        return s.sign();
    }

    // 验证JWT签名
    public boolean verifyJwt(String jwt, String publicKeyPem) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) return false;

        String encodedHeader = parts[0];
        String encodedPayload = parts[1];
        String encodedSignature = parts[2];

        String signingInput = encodedHeader + "." + encodedPayload;
        byte[] signatureBytes = Base64.getUrlDecoder().decode(encodedSignature);

        PublicKey publicKey = getPublicKeyFromPem(publicKeyPem);

        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        Signature verifier = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
        verifier.initVerify(publicKey);
        verifier.update(signingInput.getBytes(StandardCharsets.UTF_8));

        return verifier.verify(signatureBytes);
    }

    // 从PEM格式获取公钥
    private PublicKey getPublicKeyFromPem(String publicKeyPem) throws Exception {
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return new EdDSAPublicKey(keySpec);
    }

    // 辅助方法：将对象转换为 JSON 字符串
    private String toJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    // 辅助方法：执行 Base64URL 编码
    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}