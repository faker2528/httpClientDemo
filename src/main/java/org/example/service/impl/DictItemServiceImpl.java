package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.DictItem;
import org.example.mapper.DictItemMapper;
import org.example.service.DictItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper,DictItem> implements DictItemService {
    @Override
    public List<DictItem> getItemsByTypeCode(String typeCode) {
        QueryWrapper<DictItem> wrapper = new QueryWrapper<>();
        wrapper.eq("type_code", typeCode)
                .eq("status", 1)
                .orderByAsc("sort_order", "id");
        return this.list(wrapper);
    }
}
