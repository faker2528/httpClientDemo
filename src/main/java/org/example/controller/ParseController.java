package org.example.controller;

import org.example.generic.GenericResult;
import org.example.utils.ExcelUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ParseController {

    private final ExcelUtils excelUtils;

    public ParseController(ExcelUtils excelUtils) {
        this.excelUtils = excelUtils;
    }

    @GetMapping("/parse")
    public GenericResult parse(@RequestParam String id) throws Exception {
        GenericResult result = new GenericResult();
        excelUtils.parseExcel(Integer.parseInt(id));
        result.setSuccess();
        return result;
    }
}
