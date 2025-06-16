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

    @Value("${minio.bucket}")
    private String bucketName;

    public MaterialService(MinioClient minioClient, MaterialMapper mapper) {
        this.minioClient = minioClient;
        this.mapper = mapper;
    }

    /**
     * 解析 Excel、上传图片、持久化数据，并返回处理条数
     *
     * @param file 上传的 .xlsx 文件
     * @return 成功处理的数据行数
     */
    public int importMaterials(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        Map<String, XSSFPictureData> images = WpsFloatedImageExtractor.getPictures(bytes);

        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            Sheet sheet = wb.getSheetAt(0);

            int successCount = 0;
            List<Material> batch = new ArrayList<>(200);

            log.info("预计处理{}行数据",sheet.getLastRowNum());
            for (int r = 2; r <= sheet.getLastRowNum(); r++) {
                log.info( "处理第{}行", r);
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Material m = new Material();
                m.setMaterialCategory(getCellString(row.getCell(1)));
                m.setMaterialName(getCellString(row.getCell(2)));
                m.setPrice(getCellNumeric(row.getCell(13)));

                // 三列图片：列索引 3,4,5
                m.setPhotoDaban(uploadImageIfPresent(images, r, 3));
                m.setPhotoChengpin(uploadImageIfPresent(images, r, 4));
                m.setPhotoXiaoguo(uploadImageIfPresent(images, r, 5));

                log.info("Material:{}", m.getMaterialName());

                batch.add(m);
                if (batch.size() >= 200) {
                    mapper.insertBatch(batch);
                    successCount += batch.size();
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                mapper.insertBatch(batch);
                successCount += batch.size();
            }

            return successCount;
        }
    }

    private String uploadImageIfPresent(Map<String, XSSFPictureData> images, int r, int c) throws Exception {
        String key = r + "-" + c;
        XSSFPictureData pic = images.get(key);
        if (pic == null) return null;

        byte[] data = pic.getData();
        String ext = pic.suggestFileExtension();
        String obj = "materials/" + UUID.randomUUID() + "." + ext;

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
     * 模糊查询施工工艺或材料品类
     */
    public List<Material> searchMethods(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return mapper.searchByKeyword(keyword.trim());
    }
}