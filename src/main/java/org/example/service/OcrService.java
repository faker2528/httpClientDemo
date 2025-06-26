package org.example.service;

import org.example.generic.GenericResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface OcrService {

    GenericResult recognition(MultipartFile file);
}
