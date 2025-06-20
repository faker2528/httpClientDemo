package org.example.httpclient;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

public class HttpClient {

    // 静态初始化SSL上下文，信任所有证书（测试环境使用）
    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
        } catch (Exception e) {
            throw new RuntimeException("SSL初始化失败", e);
        }
    }

    /**
     * 调用语音合成API的测试方法
     */
    public static void doPostTest2() throws Exception {
        String apiUrl = "https://api.shwgij.com/api/yuyin/yuyin?key=f0e1IfKQsh8qlWwyEXq2ZPOD0q&msg=同是天涯沦落人，相逢何必曾相识&speed=6";

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            // 配置请求
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);  // 10秒连接超时
            connection.setReadTimeout(15000);       // 15秒读取超时
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            // 发送请求
            connection.connect();

            // 处理响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                processSuccessResponse(connection);
            } else {
                processErrorResponse(connection, responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 处理成功响应
     */
    private static void processSuccessResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            System.out.println("API调用成功，响应码: " + connection.getResponseCode());
            System.out.println("响应内容:");
            System.out.println(response.toString());
        }
    }

    /**
     * 处理错误响应
     */
    private static void processErrorResponse(HttpURLConnection connection, int responseCode) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream(), "UTF-8"))) {

            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorResponse.append(line).append("\n");
            }

            System.err.println("API调用失败，响应码: " + responseCode);
            System.err.println("错误信息:");
            System.err.println(errorResponse.toString());
        }
    }
}