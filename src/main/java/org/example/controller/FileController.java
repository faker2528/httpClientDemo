package org.example.controller;

import com.alibaba.druid.util.StringUtils;
import org.example.entity.FileRecord;
import org.example.generic.GenericResult;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public GenericResult uploadFile(@RequestParam("file") MultipartFile file,
                                    HttpServletRequest request) throws Exception {
        String uploader = request.getParameter("uploader_id");
        String parseFlag = request.getParameter("parse_flag");
        if (StringUtils.isEmpty(parseFlag)) {
            parseFlag = "0";
        }
        Map params = new HashMap();
        params.put("uploader_id", uploader);
        params.put("parse_flag", parseFlag);
        FileRecord fileRecord = fileService.uploadFile(file, params);
        GenericResult result = new GenericResult();
        result.addData(Collections.singletonMap("file", fileRecord));
        return result;
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam Integer fileId,
                             HttpServletResponse response) throws Exception {
        fileService.downloadFile(fileId, response);
    }
}    