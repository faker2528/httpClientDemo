DROP TABLE IF EXISTS job_positions;
CREATE TABLE job_positions (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               file_id INT,
                               position_level VARCHAR(64) COMMENT '职位层级',
                               position_category VARCHAR(128) COMMENT '职位类别',
                               department_name VARCHAR(128) COMMENT '部门名称',
                               contact_phone VARCHAR(32) COMMENT '联系电话',
                               position_code VARCHAR(32) COMMENT '职位代码',
                               position_name VARCHAR(128) COMMENT '职位名称',
                               position_description TEXT COMMENT '职位简介',
                               required_number TINYINT COMMENT '人数',
                               political_status VARCHAR(32) COMMENT '政治面貌',
                               major_requirements TEXT COMMENT '专业要求',
                               education VARCHAR(64) COMMENT '学历',
                               age_requirement VARCHAR(64) COMMENT '年龄',
                               other_conditions TEXT COMMENT '其他条件',
                               exam_subject_count VARCHAR(32) COMMENT '科目数量',
                               exam_subjects TEXT COMMENT '考试科目',
                               remarks TEXT COMMENT '备注'
);
