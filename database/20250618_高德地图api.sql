
DELETE FROM api_services WHERE name = '高德地图API';
-- 插入高德地图API服务
INSERT INTO api_services (name, description, base_url, api_key, auth_type, headers, timeout, retry_count, retry_delay, is_active)
VALUES
    ('高德地图API',
     '根据地址获取详细位置信息',
     'https://restapi.amap.com',
     '71a9bb4995e523d9cd5ec2a31e7a7288',
     'none',
     '{"Content-Type": "application/json"}',
     5000,
     2,
     1,
     1);

-- 插入语音转换端点
INSERT INTO api_endpoints (service_id, name, path, method, description, request_schema, default_params, is_active)
VALUES
    (3,
     '根据地址获取详细位置信息',
     '/v3/geocode/geo',
     'GET',
     '根据地址获取详细位置信息',
     '',
     '',
     1);
