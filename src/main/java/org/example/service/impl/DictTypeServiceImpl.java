package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.DictType;
import org.example.mapper.DictTypeMapper;
import org.example.service.DictTypeService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {
    @Override
    public List<DictType> getByFileId(String fileId) {
        QueryWrapper<DictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        return this.list(queryWrapper);
    }
}