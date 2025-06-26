package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.generic.GenericResult;
import org.example.service.OcrService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr")
@Slf4j
@RequiredArgsConstructor
public class OcrController {
    private final OcrService ocrService;

    @PostMapping()
    public GenericResult recognition(@RequestParam("file") MultipartFile file) {
        log.info("ocr文件上传: {}", file.getOriginalFilename());
        return ocrService.recognition(file);
    }
}
