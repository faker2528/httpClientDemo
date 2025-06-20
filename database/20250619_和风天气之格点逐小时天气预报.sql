
DELETE FROM api_services WHERE name = '和风天气API-2';
-- 插入和风天气API-2服务
INSERT INTO api_services (name, description, base_url, api_key, auth_type, headers, timeout, retry_count, retry_delay, is_active)
VALUES
    ('和风天气API-2',
     '格点逐小时天气预报',
     'https://m96cdmxg53.re.qweatherapi.com',
     '',
     'none',
     '{"Content-Type": "application/json"}',
     5000,
     2,
     1,
     1);

-- 插入和风天气API-2端点
INSERT INTO api_endpoints (service_id, name, path, method, description, request_schema, default_params, is_active)
VALUES
    (4,
     '格点逐小时天气预报',
     '/v7/grid-weather',
     'GET',
     '格点逐小时天气预报',
     '',
     '',
     1);
