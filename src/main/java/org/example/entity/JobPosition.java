package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("job_positions")
public class JobPosition {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer fileId;
    private String positionLevel;
    private String positionCategory;
    private String departmentName;
    private String contactPhone;
    private String positionCode;
    private String positionName;
    private String positionDescription;
    private Integer requiredNumber;
    private String politicalStatus;
    private String majorRequirements;
    private String education;
    private String ageRequirement;
    private String otherConditions;
    private String examSubjectCount;
    private String examSubjects;
    private String remarks;
}
