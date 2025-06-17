package com.baoli.pricer.service;

import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.mapper.MaterialMapper;
import com.baoli.pricer.pojo.ProcessMethod;
import com.baoli.pricer.utils.WpsFloatedImageExtractor;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 服务层：解析 Excel 并上传图片至 MinIO，持久化至 MySQL
 */
@Slf4j
@Service
public class MaterialService {

    private final MinioClient minioClient;
    private final MaterialMapper mapper;
    private final SimpMessagingTemplate messaging;


    @Value("${minio.bucket}")
    private String bucketName;

    public MaterialService(MinioClient minioClient, MaterialMapper mapper,
                           SimpMessagingTemplate messaging) {
        this.minioClient = minioClient;
        this.mapper = mapper;
        this.messaging = messaging;
    }

    /**
     * 解析 Excel、上传图片、持久化数据
     * 异步导入：上传后立刻返回taskId（controller中），后台执行并推送进度
     * @param file 上传的 .xlsx 文件
     *
     */
    @Async
    public void asyncImportMaterials(String taskId, MultipartFile file) {
        log.info("开始解析文件，taskId={}", taskId);
        try {
            byte[] bytes = file.getBytes();
            Map<String, XSSFPictureData> images = WpsFloatedImageExtractor.getPictures(bytes);

            try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
                Sheet sheet = wb.getSheetAt(0);
                int totalRows = sheet.getLastRowNum() - 1;  // 扣除表头
                int processed = 0;
                List<Material> batch = new ArrayList<>(200);

                // 从第2行（r=2）开始处理
                for (int r = 2; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) { notifyProgress(taskId, ++processed, totalRows); continue; }

                    Material m = buildMaterialFromRow(row, images, r);
                    batch.add(m);

                    if (batch.size() >= 200) {
                        mapper.insertBatch(batch);
                        log.info("成功上传 {} 条记录", batch.size());
                        batch.clear();
                    }

//                    notifyProgress(taskId, ++processed, totalRows);
                    if (processed % 5 == 0) { // 每5行推送一次，减少频率
                        notifyProgress(taskId, processed, totalRows);
                    }
                }
                if (!batch.isEmpty()) {
                    mapper.insertBatch(batch);
                    log.info("成功上传 {} 条记录", batch.size());
                }
                // 结束时推 100%
                messaging.convertAndSend(
                        "/topic/progress/" + taskId,
                        Map.of("percent", 100, "status", "done")
                );
            }
        } catch (Exception ex) {
            log.error("任务 {} 异常", taskId, ex);
            messaging.convertAndSend(
                    "/topic/progress/" + taskId,
                    Map.of("error", ex.getMessage())
            );
        }
    }

    private void notifyProgress(String taskId, int done, int total) {
        int pct = (int)(done * 100.0 / total);
        log.info( "解析进度：{}%", pct);
        messaging.convertAndSend(
                "/topic/progress/" + taskId,
                Map.of("percent", pct)
        );
    }

    private Material buildMaterialFromRow(Row row,
                                          Map<String, XSSFPictureData> images,
                                          int r) throws Exception {
        Material m = new Material();
        m.setMaterialCategory(getCellString(row.getCell(1)));
        m.setMaterialName(getCellString(row.getCell(2)));
        m.setPrice(getCellNumeric(row.getCell(13)));

        m.setPhotoDaban(
                uploadIfPresent(images, r, 3)
        );
        m.setPhotoChengpin(
                uploadIfPresent(images, r, 4)
        );
        m.setPhotoXiaoguo(
                uploadIfPresent(images, r, 5)
        );
        return m;
    }

    // 如果有图片就上传到minIO
    private String uploadIfPresent(Map<String, XSSFPictureData> images, int r, int c) throws Exception {
        String key = r + "-" + c;
        XSSFPictureData pic = images.get(key);
        if (pic == null) return null;
        byte[] data = pic.getData();
        String ext  = pic.suggestFileExtension();
        String obj  = "materials/" + UUID.randomUUID() + "." + ext;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(obj)
                        .stream(new ByteArrayInputStream(data), data.length, -1)
                        .contentType(pic.getMimeType())
                        .build()
        );
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(obj)
                        .method(Method.GET)
                        .expiry(7, TimeUnit.DAYS)
                        .build()
        );
    }

    private String getCellString(Cell cell) {
        return (cell != null) ? cell.toString().trim() : null;
    }

    private Double getCellNumeric(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> Double.valueOf(cell.getStringCellValue());
            default -> null;
        };
    }

    /**
     * 获取材料分页数据
     * @param page 1-based 页码
     * @param size 每页记录数
     */
    public PageResult<Material> list(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;
        List<Material> list = mapper.findByPage(offset, size);
        long total = mapper.countAll();

        return new PageResult<>(total, page, size, list);
    }

    /**
     * 根据材料品类查询 Material 列表
     * @param category 材料品类
     */
    public List<Material> getByCategory(String category) {
        return mapper.findByCategory(category);
    }

    /**
     * 模糊查询施工工艺或材料品类
     */
    public List<Material> searchMethods(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return mapper.searchByKeyword(keyword.trim());
    }
}