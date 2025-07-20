package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.DictItem;
import org.example.entity.DictType;
import org.example.generic.GenericResult;
import org.example.service.DictItemService;
import org.example.service.DictTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class DictController {

    private final DictTypeService dictTypeService;
    private final DictItemService dictItemService;

    public DictController(DictTypeService dictTypeService, DictItemService dictItemService) {
        this.dictTypeService = dictTypeService;
        this.dictItemService = dictItemService;
    }

    @GetMapping("/dictType")
    public GenericResult getDictType(@RequestParam String fileId){
        log.info("查询DictType，fileId：{}", fileId);
        List<DictType> dictTypeList = dictTypeService.getByFileId(fileId);
        GenericResult result = new GenericResult();
        result.addData(Collections.singletonMap("data", dictTypeList));
        return result;
    }

    @GetMapping("/dictItem")
    public GenericResult getDictItem(@RequestParam String typeCode){
        log.info("查询DictItem，typeCode:{}", typeCode);
        GenericResult result = new GenericResult();
        List<DictItem> items = dictItemService.getItemsByTypeCode(typeCode);
        result.addData(Collections.singletonMap("data", items));
        return result;
    }
}
