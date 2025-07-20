package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.FileRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Service
public interface FileService extends IService<FileRecord> {
    FileRecord uploadFile(MultipartFile file, Integer uploaderId) throws Exception;
    FileRecord uploadFile(MultipartFile file, Map params) throws Exception;
    void downloadFile(Integer fileId, HttpServletResponse response) throws Exception;
    FileRecord getFile(Integer fileId);
}    