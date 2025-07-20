package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.JobPosition;
import org.example.generic.GenericResult;
import org.example.service.JobPositionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

@RestController
@RequestMapping("/api/job")
@Slf4j
public class JobController {

    private final JobPositionService jobPositionService;

    public JobController(JobPositionService jobPositionService) {
        this.jobPositionService = jobPositionService;
    }

    /**
     * 多条件分页查询（支持job_positions所有字段作为条件）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param query 查询条件实体（所有字段可选，null则不参与查询）
     * @return 分页结果
     */
    @PostMapping("/page")
    public GenericResult pageQuery(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestBody(required = false) JobPosition query) { // 用实体类接收所有查询条件
        log.info("查询岗位表信息，pageNum:{},pageSize:{},JobPosition:{}", pageNum, pageSize, query);
        try {
            Page<JobPosition> page = new Page<>(pageNum, pageSize);
            IPage<JobPosition> resultPage = jobPositionService.pageQuery(page, query);

            GenericResult result = new GenericResult();
            result.addData(Collections.singletonMap("data", resultPage));
            return result;

        } catch (Exception e) {
            log.error("多条件分页查询失败", e);
            return new GenericResult();
        }
    }
}