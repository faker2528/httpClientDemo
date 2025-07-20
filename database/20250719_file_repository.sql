DROP TABLE IF EXISTS file_repository;
CREATE TABLE file_repository (
                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                 file_name VARCHAR(256) NOT NULL COMMENT '文件名',
                                 file_hash VARCHAR(64) UNIQUE NOT NULL COMMENT '文件哈希值（SHA-256）',
                                 file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
                                 file_extension VARCHAR(16) COMMENT '文件扩展名',
                                 storage_path VARCHAR(512) NOT NULL COMMENT '存储路径',
                                 upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                                 uploader_id INT NOT NULL COMMENT '上传者ID',
                                 uploader_ip VARCHAR(64) COMMENT '上传者IP地址',
                                 file_status TINYINT DEFAULT 1 COMMENT '文件状态（1-正常，0-已删除）',
                                 file_type VARCHAR(32) COMMENT '文件类型',
                                 file_category VARCHAR(64) COMMENT '文件分类',
                                 access_level TINYINT DEFAULT 2 COMMENT '访问级别（1-公开，2-内部，3-保密）',
                                 download_count INT DEFAULT 0 COMMENT '下载次数',
                                 last_download_time DATETIME COMMENT '最后下载时间',
                                 description TEXT COMMENT '文件描述',
                                 metadata JSON COMMENT '文件元数据'
);

-- 创建索引
CREATE INDEX idx_file_hash ON file_repository(file_hash);
CREATE INDEX idx_upload_time ON file_repository(upload_time);
CREATE INDEX idx_file_status ON file_repository(file_status);
