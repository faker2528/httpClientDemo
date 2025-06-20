
drop table if exists api_services;
CREATE TABLE api_services (
                              id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(128) NOT NULL COMMENT 'API服务名称',
                              description TEXT COMMENT 'API服务描述',
                              base_url VARCHAR(256) NOT NULL COMMENT 'API基础URL',
                              api_key VARCHAR(256) COMMENT 'API密钥',
                              api_secret VARCHAR(256) COMMENT 'API密钥秘钥',
                              auth_type ENUM('none', 'basic', 'api_key', 'oauth1', 'oauth2', 'bearer') DEFAULT 'none' COMMENT '认证类型',
                              headers TEXT COMMENT '默认请求头(JSON格式)',
                              timeout INT DEFAULT 30 COMMENT '请求超时时间(秒)',
                              retry_count TINYINT DEFAULT 0 COMMENT '失败重试次数',
                              retry_delay INT DEFAULT 1 COMMENT '重试间隔(秒)',
                              is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              UNIQUE KEY uk_name (name),
                              INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API服务配置表';

-- 存储API端点的表
drop table if exists api_endpoints;
CREATE TABLE api_endpoints (
                               id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               service_id BIGINT UNSIGNED NOT NULL COMMENT '关联的API服务ID',
                               name VARCHAR(128) NOT NULL COMMENT '端点名称',
                               path VARCHAR(256) NOT NULL COMMENT '端点路径',
                               method ENUM('GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD', 'OPTIONS') NOT NULL DEFAULT 'GET' COMMENT 'HTTP方法',
                               description TEXT COMMENT '端点描述',
                               request_schema TEXT COMMENT '请求参数JSON Schema',
                               response_schema TEXT COMMENT '响应数据JSON Schema',
                               default_params TEXT COMMENT '默认参数(JSON格式)',
                               is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               UNIQUE KEY uk_service_name (service_id, name),
                               INDEX idx_service_id (service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API端点配置表';
