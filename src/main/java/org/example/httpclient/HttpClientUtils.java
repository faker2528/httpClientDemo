package org.example.httpclient;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HttpClientUtils {


    public static Map<String, Object> doGet(Map<String, Object> reqParams) throws Exception {
        return doGetWithRetry(reqParams, 3);
    }

    public static Map<String, Object> doGetWithRetry(Map<String, Object> reqParams, int maxRetries) throws Exception {
        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                return executeGetRequest(reqParams);
            } catch (Exception e) {
                lastException = e;
                retries++;
                log.warn("GET请求失败，重试 {} / {}: {}", retries, maxRetries, e.getMessage());
                // 指数退避策略
                if (retries < maxRetries) {
                    TimeUnit.SECONDS.sleep(1L << retries);
                }
            }
        }

        throw lastException;
    }

    private static Map<String, Object> executeGetRequest(Map<String, Object> reqParams) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String serviceUrl = buildUrl(reqParams);
        log.info("doGet请求地址：{}", serviceUrl);
        Map<String, String> headers = getHeaders(reqParams);
        int timeout = getTimeout(reqParams);
        SSLConnectionSocketFactory sslsf = createSSLConnectionSocketFactory();

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultRequestConfig(config)
                .build()) {

            HttpGet httpGet = new HttpGet(serviceUrl);

            // 设置请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }

            // 执行请求并处理响应
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseBody = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";

                log.info("GET响应状态码: {}, 响应内容: {}", statusCode, responseBody);

                if (statusCode == 200) {
                    result.put("code", "0");
                    result.put("result", responseBody);
                    result.put("headers", extractHeaders(response));
                } else {
                    result.put("code", String.valueOf(statusCode));
                    result.put("result", "请求失败，状态码: " + statusCode);
                    result.put("errorDetails", responseBody);
                }
            }
        } catch (IOException e) {
            log.error("HTTP GET请求IO异常: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("HTTP GET请求未知异常: {}", e.getMessage(), e);
            throw e;
        }

        return result;
    }

    public static Map<String, Object> doPost(Map<String, Object> reqParams) throws Exception {
        return doPostWithRetry(reqParams, 3);
    }

    public static Map<String, Object> doPostWithRetry(Map<String, Object> reqParams, int maxRetries) throws Exception {
        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                return executePostRequest(reqParams);
            } catch (Exception e) {
                lastException = e;
                retries++;
                log.warn("POST请求失败，重试 {} / {}: {}", retries, maxRetries, e.getMessage());
                // 指数退避策略
                if (retries < maxRetries) {
                    TimeUnit.SECONDS.sleep(1L << retries);
                }
            }
        }

        throw lastException;
    }

    private static Map<String, Object> executePostRequest(Map<String, Object> reqParams) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String baseUrl = Objects.toString(reqParams.get("SERVICE_URL"), "");
        String path = Objects.toString(reqParams.get("PATH"), "");
        String serviceUrl = baseUrl + path;
        log.info("doPost请求地址：{}", serviceUrl);
        Map<String, String> headers = getHeaders(reqParams);
        int timeout = getTimeout(reqParams);
        String contentType = Objects.toString(reqParams.get("CONTENT_TYPE"), "application/json");
        Map bodyMap = (Map) reqParams.get("PARAMS");

        SSLConnectionSocketFactory sslsf = createSSLConnectionSocketFactory();

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultRequestConfig(config)
                .build()) {

            HttpPost httpPost = new HttpPost(serviceUrl);

            // 设置请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }

            // 设置请求体
            HttpEntity entity = buildPostEntity(bodyMap, contentType);
            if (entity != null) {
                httpPost.setEntity(entity);
            }

            log.info("执行POST请求: {}, 内容类型: {}, 请求体: {}",
                    serviceUrl, contentType, bodyMap != null ? bodyMap.toString() : "null");

            // 执行请求并处理响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = responseEntity != null ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8) : "";

                log.info("POST响应状态码: {}, 响应内容: {}", statusCode, responseBody);

                if (statusCode == 200) {
                    result.put("code", "0");
                    result.put("result", responseBody);
                    result.put("headers", extractHeaders(response));
                } else {
                    result.put("code", String.valueOf(statusCode));
                    result.put("result", "请求失败，状态码: " + statusCode);
                    result.put("errorDetails", responseBody);
                }
            }
        } catch (IOException e) {
            log.error("HTTP POST请求IO异常: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("HTTP POST请求未知异常: {}", e.getMessage(), e);
            throw e;
        }

        return result;
    }

    private static HttpEntity buildPostEntity(Object bodyObj, String contentType) {
        if (bodyObj == null) {
            return null;
        }

        try {
            if (contentType.equals("application/json")) {
                String jsonBody;
                if (bodyObj instanceof String) {
                    jsonBody = (String) bodyObj;
                } else if (bodyObj instanceof Map) {
                    // 使用FastJSON2正确转换Map为JSON
                    jsonBody = JSON.toJSONString(bodyObj);
                } else {
                    throw new IllegalArgumentException("JSON请求体必须是Map或String类型");
                }
                log.debug("构建JSON请求体: {}", jsonBody);
                return new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            }
            else if (contentType.equals("application/x-www-form-urlencoded")) {
                if (bodyObj instanceof Map) {
                    Map<String, Object> formParams = (Map<String, Object>) bodyObj;
                    List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : formParams.entrySet()) {
                        if (entry.getValue() != null) {
                            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                        }
                    }
                    return new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
                }
                throw new IllegalArgumentException("表单请求体必须是Map类型");
            }
            else if (contentType.startsWith("multipart/form-data")) {
                // 多部分表单数据处理
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setCharset(StandardCharsets.UTF_8);

                if (bodyObj instanceof Map) {
                    Map<String, Object> formParams = (Map<String, Object>) bodyObj;
                    for (Map.Entry<String, Object> entry : formParams.entrySet()) {
                        // 这里简化处理，实际项目中应区分文件和普通字段
                        builder.addTextBody(entry.getKey(), entry.getValue().toString());
                    }
                }
                return builder.build();
            }
            else {
                throw new IllegalArgumentException("不支持的内容类型: " + contentType);
            }
        } catch (Exception e) {
            log.error("构建请求体异常: {}", e.getMessage(), e);
            return null;
        }
    }

    private static String buildUrl(Map<String, Object> reqParams) throws URISyntaxException {
        String baseUrl = Objects.toString(reqParams.get("SERVICE_URL"), "");
        String path = Objects.toString(reqParams.get("PATH"), "");
        String fullUrl = baseUrl + path;

        // 处理查询参数
        Map<String, Object> params = (Map<String, Object>) reqParams.getOrDefault("PARAMS", new HashMap<>());
        if (!params.isEmpty()) {
            URIBuilder builder = new URIBuilder(fullUrl);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    builder.addParameter(entry.getKey(), entry.getValue().toString());
                }
            }
            return builder.toString();
        }

        return fullUrl;
    }

    private static Map<String, String> getHeaders(Map<String, Object> reqParams) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0"); // 默认浏览器UA

        // 从参数中获取自定义请求头
        Map<String, String> customHeaders = (Map<String, String>) reqParams.getOrDefault("HEADERS", new HashMap<>());
        headers.putAll(customHeaders);

        return headers;
    }

    private static int getTimeout(Map<String, Object> reqParams) {
        Object timeoutObj = reqParams.get("TIMEOUT");
        if (timeoutObj instanceof Number) {
            return ((Number) timeoutObj).intValue();
        }
        return 30000; // 默认30秒超时，根据实际情况调整
    }

    private static SSLConnectionSocketFactory createSSLConnectionSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());

            return new SSLConnectionSocketFactory(
                    sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("创建SSL连接工厂失败: {}", e.getMessage(), e);
            // 返回null，让HttpClient使用默认配置
            return null;
        }
    }

    private static Map<String, String> extractHeaders(HttpResponse response) {
        Map<String, String> headers = new HashMap<>();
        for (Header header : response.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        return headers;
    }
}