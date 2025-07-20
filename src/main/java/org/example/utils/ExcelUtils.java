package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.example.entity.FileRecord;
import org.example.entity.JobPosition;
import org.example.service.FileService;
import org.example.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Component
public class ExcelUtils {
    private static String storageLocation;
    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();
    private static final Set<String> BASE_HEADERS = new HashSet<>();
    private static JobPositionService jobPositionService;
    private static FileService fileService;

    static {
        FIELD_MAPPING.put("职位层级", "positionLevel");
        FIELD_MAPPING.put("职位类别", "positionCategory");
        FIELD_MAPPING.put("部门名称", "departmentName");
        FIELD_MAPPING.put("联系电话", "contactPhone");
        FIELD_MAPPING.put("职位代码", "positionCode");
        FIELD_MAPPING.put("职位名称", "positionName");
        FIELD_MAPPING.put("职位简介", "positionDescription");
        FIELD_MAPPING.put("人数", "requiredNumber");
        FIELD_MAPPING.put("政治面貌", "politicalStatus");
        FIELD_MAPPING.put("专业要求", "majorRequirements");
        FIELD_MAPPING.put("学历", "education");
        FIELD_MAPPING.put("年龄", "ageRequirement");
        FIELD_MAPPING.put("其他条件", "otherConditions");
        FIELD_MAPPING.put("科目数量", "examSubjectCount");
        FIELD_MAPPING.put("考试科目", "examSubjects");
        FIELD_MAPPING.put("备注", "remarks");

        // 初始化基准表头集合
        BASE_HEADERS.addAll(FIELD_MAPPING.keySet());
    }

    @Autowired
    public ExcelUtils(JobPositionService jobPositionService,
                      FileService fileService,
                      @Value("${file.storage.location}") String storageLocation) {
        ExcelUtils.jobPositionService = jobPositionService;
        ExcelUtils.fileService = fileService;
        ExcelUtils.storageLocation = storageLocation;
    }

    public static void parseExcel(int id) throws Exception {
        log.info("开始解析文件，文件ID: {}", id);
        FileRecord fileRecord = fileService.getFile(id);
        if (fileRecord == null) {
            log.error("根据文件ID: {} 未找到对应的FileRecord", id);
            throw new RuntimeException("根据文件ID未找到对应的FileRecord");
        }
        String filePath = storageLocation + File.separator + fileRecord.getStoragePath();
        log.info("文件路径: {}", filePath);
        String fileExtension = fileRecord.getFileExtension();
        if (!"xls".equals(fileExtension) &&! "xlsx".equals(fileExtension)) {
            log.error("文件类型错误，只能解析excel文件！当前文件类型:{}", fileExtension);
            throw new RuntimeException("文件类型错误");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在，id:{}", id);
            throw new FileNotFoundException("文件不存在");
        }
        parseExcel(file, id);
    }

    public static void parseExcel(File file, int id) throws Exception {
        log.info("开始解析文件对象，文件ID: {}", id);
        try (Workbook workbook = WorkbookFactory.create(file)) {
            log.info("成功创建Workbook对象");
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 0) {
                log.warn("Excel文件为空");
                throw new RuntimeException("Excel文件为空");
            }

            // 直接指定表头行为第2行（索引为1）
            int headerRowIndex = 2;

            // 数据行从表头行的下一行开始
            int startRow = headerRowIndex + 1;
            if (startRow > sheet.getLastRowNum()) {
                log.warn("表头行下无数据行");
                throw new RuntimeException("表头行下无数据行");
            }

            // 获取表头行并构建映射
            Row headerRow = sheet.getRow(headerRowIndex);
            Map<Integer, String> headerMap = new HashMap<>();
            log.info("开始构建表头映射，表头行号: {}", headerRowIndex + 1);
            for (int cellNum = 0; cellNum <= headerRow.getLastCellNum(); cellNum++) {
                Cell cell = headerRow.getCell(cellNum);
                if (cell != null) {
                    String headerValue = getCellStringValue(cell);
                    String fieldName = FIELD_MAPPING.get(headerValue);
                    if (fieldName != null) {
                        headerMap.put(cellNum, fieldName);
                        log.info("表头映射: 列号{} -> 字段名{}", cellNum, fieldName);
                    }
                }
            }
            log.info("表头映射构建完成，映射数量: {}", headerMap.size());

            // 解析数据行
            List<JobPosition> jobPositions = new ArrayList<>();
            log.info("开始解析数据行，数据起始行号: {}", startRow + 1);
            for (int rowNum = startRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row dataRow = sheet.getRow(rowNum);
                if (dataRow == null) {
                    log.warn("数据行 {} 为空，跳过", rowNum + 1);
                    continue;
                }

                JobPosition jobPosition = new JobPosition();
                jobPosition.setFileId(id);
                boolean hasData = false;

                for (Map.Entry<Integer, String> entry : headerMap.entrySet()) {
                    int cellNum = entry.getKey();
                    String fieldName = entry.getValue();

                    Cell cell = dataRow.getCell(cellNum);
                    if (cell != null) {
                        String cellValue = getCellStringValue(cell);
                        if (cellValue != null &&!cellValue.trim().isEmpty()) {
                            setFieldValue(jobPosition, fieldName, cellValue);
                            hasData = true;
                        }
                    }
                }

                if (hasData) {
                    jobPositions.add(jobPosition);
                }
            }
            log.info("数据行解析完成，解析得到的职位数据数量: {}", jobPositions.size());

            // 批量保存到数据库
            if (!jobPositions.isEmpty()) {
                jobPositionService.saveBatch(jobPositions);
                log.info("成功导入{}条职位数据（表头行：第{}行）", jobPositions.size(), headerRowIndex + 1);
            } else {
                log.warn("数据行中未找到有效数据");
            }

        } catch (Exception e) {
            log.error("解析Excel文件失败", e);
            throw new RuntimeException("解析Excel文件失败：" + e.getMessage(), e);
        }
    }

    /**
     * 动态查找表头行（匹配度最高的行）
     */
    private static int findHeaderRow(Sheet sheet) {
        log.info("开始查找表头行");
        int maxMatchCount = 0;
        int headerRowIndex = -1;
        int totalBaseHeaders = BASE_HEADERS.size();
        if (totalBaseHeaders == 0) {
            log.error("未配置基准表头，无法识别表头行");
            return -1;
        }

        // 遍历前10行寻找表头（可根据实际情况调整）
        int maxScanRows = Math.min(5, sheet.getLastRowNum() + 1);
        log.info("将扫描前 {} 行寻找表头", maxScanRows);
        for (int rowNum = 0; rowNum < maxScanRows; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                log.warn("行 {} 为空，跳过", rowNum + 1);
                continue;
            }

            int matchCount = 0;
            for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (cell != null) {
                    String cellValue = getCellStringValue(cell);
                    if (BASE_HEADERS.contains(cellValue)) {
                        matchCount++;
                        log.info("行 {}，列号 {}，值 {} 与基准表头匹配", rowNum + 1, cellNum, cellValue);
                    }
                }
            }

            if (matchCount > maxMatchCount) {
                maxMatchCount = matchCount;
                headerRowIndex = rowNum;
            }
        }

        // 校验匹配度（至少匹配60%的基准表头）
        double matchRate = (double) maxMatchCount / totalBaseHeaders;
        if (matchRate < 0.6) {
            log.error("未找到有效表头行，最高匹配度：{}%", (int) (matchRate * 100));
            return -1;
        }

        log.info("识别表头行为第{}行，匹配字段数：{}，匹配率：{}%",
                headerRowIndex + 1, maxMatchCount, (int) (matchRate * 100));
        return headerRowIndex;
    }

    private static String getCellStringValue(Cell cell) {
        return new DataFormatter().formatCellValue(cell).trim();
    }

    private static void setFieldValue(Object obj, String fieldName, String value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            Class<?> fieldType = field.getType();
            if (fieldType == Integer.class || fieldType == int.class) {
                field.set(obj, Integer.valueOf(value));
            } else if (fieldType == Long.class || fieldType == long.class) {
                field.set(obj, Long.valueOf(value));
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                field.set(obj, Boolean.valueOf(value));
            } else {
                field.set(obj, value);
            }
        } catch (Exception e) {
            log.error("设置对象属性值失败: field={}, value={}", fieldName, value, e);
        }
    }
}