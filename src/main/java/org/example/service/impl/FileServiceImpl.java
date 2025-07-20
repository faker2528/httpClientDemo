package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.example.entity.FileRecord;
import org.example.mapper.FileRecordMapper;
import org.example.service.FileService;
import org.example.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord> implements FileService {

    @Value("${file.storage.location}")
    private String storageLocation;
    private final FileRecordMapper fileRecordMapper;

    public FileServiceImpl(FileRecordMapper fileRecordMapper) {
        this.fileRecordMapper = fileRecordMapper;
    }

    @Override
    public FileRecord uploadFile(MultipartFile file, Integer uploaderId) throws Exception {
        // 创建存储目录（如果不存在）
        Path storagePath = Paths.get(storageLocation);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = storagePath.resolve(uniqueFileName);

        // 保存文件到磁盘
        Files.write(filePath, file.getBytes());

        // 计算文件哈希
        String fileHash = calculateFileHash(file.getBytes());

        // 构建文件记录
        FileRecord fileRecord = new FileRecord();
        fileRecord.setFileName(originalFilename);
        fileRecord.setFileHash(fileHash);
        fileRecord.setFileSize(file.getSize());
        fileRecord.setFileExtension(fileExtension);
        fileRecord.setStoragePath(uniqueFileName);
        fileRecord.setUploadTime(LocalDateTime.now());
        fileRecord.setUploaderId(uploaderId);
        fileRecord.setFileStatus(1);
        fileRecord.setDownloadCount(0);

        // 保存到数据库
        fileRecordMapper.insert(fileRecord);

        return fileRecord;
    }

    @Override
    public FileRecord uploadFile(MultipartFile file, Map params) throws Exception {
        String uploader = ObjectUtils.toString(params.get("uploader_id"));
        String parseFlag = ObjectUtils.toString(params.get("parse_flag"));
        if (parseFlag.equals("0")) {
            return uploadFile(file, Integer.parseInt(uploader));
        }else {
            FileRecord fileRecord = uploadFile(file, Integer.parseInt(uploader));
            ExcelUtils.parseExcel(fileRecord.getId());
            return fileRecord;
        }
    }

    @Override
    public void downloadFile(Integer fileId, HttpServletResponse response) throws Exception {
        // 查询文件记录
        FileRecord fileRecord = fileRecordMapper.selectById(fileId);
        if (fileRecord == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }

        // 构建文件路径
        Path filePath = Paths.get(storageLocation, fileRecord.getStoragePath());
        File file = filePath.toFile();

        // 检查文件是否存在
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileRecord.getFileName() + "\"");
        response.setContentLengthLong(fileRecord.getFileSize());

        // 输出文件内容
        try (InputStream in = Files.newInputStream(file.toPath());
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            // 更新下载计数
            fileRecord.setDownloadCount(fileRecord.getDownloadCount() + 1);
            fileRecord.setLastDownloadTime(LocalDateTime.now());
            fileRecordMapper.updateById(fileRecord);
        }
    }

    @Override
    public FileRecord getFile(Integer fileId) {
        return fileRecordMapper.selectById(fileId);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastIndex = fileName.lastIndexOf('.');
        return lastIndex >= 0 ? fileName.substring(lastIndex + 1).toLowerCase() : "";
    }

    private String calculateFileHash(byte[] fileBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无法计算文件哈希", e);
        }
    }
}    