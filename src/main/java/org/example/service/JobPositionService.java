package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.JobPosition;
import org.springframework.stereotype.Service;


@Service
public interface JobPositionService extends IService<JobPosition> {
    IPage<JobPosition> pageQuery(Page<JobPosition> page, JobPosition query);
}
