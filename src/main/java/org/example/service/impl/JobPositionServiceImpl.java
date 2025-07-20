package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.JobPosition;
import org.example.mapper.JobPositionMapper;
import org.example.service.JobPositionService;
import org.springframework.stereotype.Service;

@Service
public class JobPositionServiceImpl extends ServiceImpl<JobPositionMapper, JobPosition> implements JobPositionService {

    @Override
    public IPage<JobPosition> pageQuery(Page<JobPosition> page, JobPosition query) {


        // 构建Lambda查询条件（避免硬编码字段名，更安全）
        LambdaQueryWrapper<JobPosition> queryWrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            // 1. 职位层级：模糊查询（字符串类型）
            if (query.getPositionLevel() != null && !query.getPositionLevel().isEmpty()) {
                queryWrapper.eq(JobPosition::getPositionLevel, query.getPositionLevel());
            }

            // 2. 职位类别：模糊查询
            if (query.getPositionCategory() != null && !query.getPositionCategory().isEmpty()) {
                queryWrapper.eq(JobPosition::getPositionCategory, query.getPositionCategory());
            }

            // 3. 部门名称：模糊查询
            if (query.getDepartmentName() != null && !query.getDepartmentName().isEmpty()) {
                queryWrapper.like(JobPosition::getDepartmentName, query.getDepartmentName());
            }

            // 4. 联系电话：精确查询（电话通常唯一，适合精确匹配）
            if (query.getContactPhone() != null && !query.getContactPhone().isEmpty()) {
                queryWrapper.eq(JobPosition::getContactPhone, query.getContactPhone());
            }

            // 5. 职位代码：精确查询（唯一键，必须精确匹配）
            if (query.getPositionCode() != null && !query.getPositionCode().isEmpty()) {
                queryWrapper.eq(JobPosition::getPositionCode, query.getPositionCode());
            }

            // 6. 职位名称：模糊查询
            if (query.getPositionName() != null && !query.getPositionName().isEmpty()) {
                queryWrapper.like(JobPosition::getPositionName, query.getPositionName());
            }

            // 7. 职位简介：模糊查询（长文本，适合包含关键词查询）
            if (query.getPositionDescription() != null && !query.getPositionDescription().isEmpty()) {
                queryWrapper.like(JobPosition::getPositionDescription, query.getPositionDescription());
            }

            // 8. 人数：精确查询（数值类型）
            if (query.getRequiredNumber() != null) {
                queryWrapper.eq(JobPosition::getRequiredNumber, query.getRequiredNumber());
            }

            // 9. 政治面貌：精确查询（固定选项，如“不限”“中共党员”）
            if (query.getPoliticalStatus() != null && !query.getPoliticalStatus().isEmpty()) {
                queryWrapper.eq(JobPosition::getPoliticalStatus, query.getPoliticalStatus());
            }

            // 10. 专业要求：模糊查询（包含某专业）
            if (query.getMajorRequirements() != null && !query.getMajorRequirements().isEmpty()) {
                queryWrapper.like(JobPosition::getMajorRequirements, query.getMajorRequirements());
            }

            // 11. 学历：精确查询（固定选项，如“本科及以上”）
            if (query.getEducation() != null && !query.getEducation().isEmpty()) {
                queryWrapper.eq(JobPosition::getEducation, query.getEducation());
            }

            // 12. 年龄要求：模糊查询（如“35周岁以下”）
            if (query.getAgeRequirement() != null && !query.getAgeRequirement().isEmpty()) {
                queryWrapper.eq(JobPosition::getAgeRequirement, query.getAgeRequirement());
            }

            // 13. 其他条件：模糊查询
            if (query.getOtherConditions() != null && !query.getOtherConditions().isEmpty()) {
                queryWrapper.like(JobPosition::getOtherConditions, query.getOtherConditions());
            }

            // 14. 科目数量：精确查询（数值类型）
            if (query.getExamSubjectCount() != null) {
                queryWrapper.eq(JobPosition::getExamSubjectCount, query.getExamSubjectCount());
            }

            // 15. 考试科目：模糊查询（如包含“申论”）
            if (query.getExamSubjects() != null && !query.getExamSubjects().isEmpty()) {
                queryWrapper.like(JobPosition::getExamSubjects, query.getExamSubjects());
            }

            // 16. 备注：模糊查询
            if (query.getRemarks() != null && !query.getRemarks().isEmpty()) {
                queryWrapper.like(JobPosition::getRemarks, query.getRemarks());
            }

            // 17. 文件ID（外键）：精确查询
            if (query.getFileId() != null) {
                queryWrapper.eq(JobPosition::getFileId, query.getFileId());
            }
        }

        // 排序：按职位代码升序（可根据需求调整）
        queryWrapper.orderByAsc(JobPosition::getId);

        // 执行分页查询
        return baseMapper.selectPage(page, queryWrapper);
    }


}