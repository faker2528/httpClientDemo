package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_repository")
public class FileRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String fileName;
    private String fileHash;
    private Long fileSize;
    private String fileExtension;
    private String storagePath;
    private LocalDateTime uploadTime;
    private Integer uploaderId;
    private String uploaderIp;
    private Integer fileStatus;
    private String fileType;
    private String fileCategory;
    private Integer accessLevel;
    private Integer downloadCount;
    private LocalDateTime lastDownloadTime;
    private String description;
    private String metadata;
}    