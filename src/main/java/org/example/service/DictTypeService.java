package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.DictType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DictTypeService extends IService<DictType> {
    List<DictType> getByFileId(String fileId);
}
