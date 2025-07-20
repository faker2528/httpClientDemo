-- 字典类型表
DROP TABLE IF EXISTS dict_type;
CREATE TABLE dict_type (
                           id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                           file_id INT COMMENT '关联文件',
                           type_code VARCHAR(50) NOT NULL COMMENT '类型编码',
                           type_name VARCHAR(50) NOT NULL COMMENT '类型名称',
                           description VARCHAR(255) COMMENT '类型描述',
                           sort_order INT DEFAULT 0 COMMENT '排序号',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                           UNIQUE KEY uk_type_code (type_code)
) COMMENT='字典类型表';

-- 字典项表
DROP TABLE IF EXISTS dict_item;
CREATE TABLE dict_item (
                           id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                           type_code VARCHAR(50) NOT NULL COMMENT '字典类型编码',
                           item_value VARCHAR(100) NOT NULL COMMENT '字典项值',
                           item_name VARCHAR(100) NOT NULL COMMENT '字典项名称',
                           description VARCHAR(255) COMMENT '字典项描述',
                           sort_order INT DEFAULT 0 COMMENT '排序号',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                           UNIQUE KEY uk_type_value (type_code, item_value)
) COMMENT='字典项表';


-- 插入字典类型数据
INSERT INTO dict_type (file_id ,type_code, type_name, description) VALUES
                                                              (4,'positionLevel', '职位层级', '公务员职位层级分类'),
                                                              (4,'positionCategory', '职位类别', '公务员职位类别分类'),
                                                              (4,'politicalStatus', '政治面貌', '政治面貌要求'),
                                                              (4,'education', '学历', '学历要求'),
                                                              (4,'ageRequirement', '年龄', '年龄要求');

-- 插入职位层级字典项
INSERT INTO dict_item (type_code, item_value, item_name) VALUES
                                                             ('positionLevel', 'provincial', '省级'),
                                                             ('positionLevel', 'vertical_unit', '省直垂管单位'),
                                                             ('positionLevel', 'municipality', '设区市'),
                                                             ('positionLevel', 'county', '县（市）区'),
                                                             ('positionLevel', 'township', '乡镇');

-- 插入职位类别字典项
INSERT INTO dict_item (type_code, item_value, item_name) VALUES
                                                             ('positionCategory', 'public_institution', '参照管理事业单位'),
                                                             ('positionCategory', 'administrative', '行政机关'),
                                                             ('positionCategory', 'mass_organization', '参照管理群团机关'),
                                                             ('positionCategory', 'party_committee', '党委机关'),
                                                             ('positionCategory', 'democratic_party', '民主党派和工商联机关'),
                                                             ('positionCategory', 'judicial', '审判机关'),
                                                             ('positionCategory', 'procuratorial', '检察机关'),
                                                             ('positionCategory', 'public_security', '公安机关'),
                                                             ('positionCategory', 'people_congress', '人大机关'),
                                                             ('positionCategory', 'township_org', '乡镇机关'),
                                                             ('positionCategory', 'political_consultative', '政协机关');

-- 插入政治面貌字典项
INSERT INTO dict_item (type_code, item_value, item_name) VALUES
                                                             ('politicalStatus', 'no_limit', '不限'),
                                                             ('politicalStatus', 'communist', '限中国共产党党员(含预备党员)'),
                                                             ('politicalStatus', 'non_communist', '限非中国共产党党员');

-- 插入学历字典项
INSERT INTO dict_item (type_code, item_value, item_name) VALUES
                                                             ('education', 'bachelor', '本科及以上学历'),
                                                             ('education', 'master', '硕士研究生及以上学历'),
                                                             ('education', 'college', '大专及以上学历');

-- 插入年龄字典项
INSERT INTO dict_item (type_code, item_value, item_name) VALUES
                                                             ('ageRequirement', 'under_35_phd_40', '年龄35周岁以下（博士研究生40周岁以下）'),
                                                             ('ageRequirement', 'under_30', '30周岁以下'),
                                                             ('ageRequirement', 'under_35', '35周岁以下'),
                                                             ('ageRequirement', 'under_40', '40周岁以下'),
                                                             ('ageRequirement', 'under_25', '25周岁以下');