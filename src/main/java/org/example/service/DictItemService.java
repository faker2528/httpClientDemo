package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.DictItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DictItemService extends IService<DictItem> {
    /**
     * 根据类型编码获取字典项列表
     * @param typeCode 类型编码
     * @return 字典项列表
     */
    List<DictItem> getItemsByTypeCode(String typeCode);
}
