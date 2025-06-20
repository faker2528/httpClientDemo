
DELETE FROM api_services WHERE name = '语音转文字API';
-- 插入语音转文字API服务
INSERT INTO api_services (name, description, base_url, api_key, auth_type, headers, timeout, retry_count, retry_delay, is_active)
VALUES
    ('语音转文字API',
     '通过输入文字，将文字转换成语音文件的API服务',
     'https://api.shwgij.com/api/yuyin',
     'f0e1IfKQsh8qlWwyEXq2ZPOD0q',
     'none',
     '{"Content-Type": "application/json"}',
     5000,
     2,
     1,
     1);

-- 插入语音转换端点
INSERT INTO api_endpoints (service_id, name, path, method, description, request_schema, default_params, is_active)
VALUES
    (1,
     '文字转语音',
     '/yuyin',
     'GET',
     '将输入的文字内容转换为语音文件',
     '{
       "type": "object",
       "required": ["key", "msg", "speed"],
       "properties": {
         "key": {
           "type": "string",
           "description": "API访问密钥"
         },
         "msg": {
           "type": "string",
           "description": "要转换的文本内容"
         },
         "speed": {
           "type": "string",
           "description": "语音速度，范围1-9，默认6"
         }
       }
     }',
     '{"speed": "6"}',
     1);
