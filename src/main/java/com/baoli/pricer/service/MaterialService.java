package com.baoli.pricer.service;

import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.mapper.VersionMapper;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.mapper.MaterialMapper;
import com.baoli.pricer.pojo.ProcessMethod;
import com.baoli.pricer.pojo.Version;
import com.baoli.pricer.utils.WpsFloatedImageExtractor;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final VersionMapper versionMapper;
    private final SimpMessagingTemplate messaging;


    @Value("${minio.bucket}")
    private String bucketName;

    public MaterialService(MinioClient minioClient, MaterialMapper mapper, VersionMapper versionMapper,
                           SimpMessagingTemplate messaging) {
        this.minioClient = minioClient;
        this.mapper = mapper;
        this.versionMapper = versionMapper;
        this.messaging = messaging;
    }

    /**
     * 解析 Excel、上传图片、持久化数据
     * 异步导入：上传后立刻返回taskId（controller中），后台执行并推送进度
     * @param tempFile 上传的 .xlsx 文件的路径
     *
     */
    @Async
    public void asyncImportMaterials(String taskId, Path tempFile) throws IOException {
        log.info("开始解析文件，taskId={}，path={}", taskId, tempFile);

        try {
            // 建立版本信息
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");
            String currentTime = LocalDateTime.now().format(formatter);
            String tempName = "material_" + currentTime;
            Version newVersion = new Version((byte) 0, tempName,"材料表");
            versionMapper.insert( newVersion );
            newVersion = versionMapper.getByVersionName(tempName);
            log.info("新版本已创建：{}", newVersion);
            int versionId = newVersion.getId();

            // 读取文件内容
            byte[] bytes = Files.readAllBytes(tempFile);
            // 提取图片数据
            Map<String, XSSFPictureData> images = WpsFloatedImageExtractor.getPictures(bytes);

            try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
                // 创建公式计算器
                FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

                Sheet sheet = wb.getSheetAt(0);

                // 1. 预收集“材料名称”列 (index=2) 的所有合并区域
                List<CellRangeAddress> nameMergeRegions = new ArrayList<>();
                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i);
                    if (region.getFirstColumn() == 2 && region.getLastColumn() == 2) {
                        nameMergeRegions.add(region);
                    }
                }

                int totalRows = sheet.getLastRowNum() - 1;  // 扣除表头
                int processed = 0;
                List<Material> batch = new ArrayList<>(200);

                // 2. 主循环：从第2行 (r=2) 开始
                for (int r = 2; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    String name = getCellString(row == null ? null : row.getCell(3));

                    // 2.1 判断是否为合并区域起始行
                    int finalR = r;
                    CellRangeAddress hitRegion = nameMergeRegions.stream()
                            .filter(reg -> reg.getFirstRow() == finalR)
                            .findFirst()
                            .orElse(null);

                    if (hitRegion != null) {
                        // 2.2 拆分合并区域：与规格列 (index=6) 拼接
                        String mergedName = name;
                        for (int sub = hitRegion.getFirstRow(); sub <= hitRegion.getLastRow(); sub++) {
                            Row subRow = sheet.getRow(sub);
                            if (subRow == null) continue;
                            String spec = getCellString(subRow.getCell(7)); // 规格列
                            if (spec == null || spec.isEmpty()) continue;

                            Material m = buildMaterialFromRow(subRow, images, sub, evaluator);
                            m.setMaterialName(mergedName + "：" + spec);
                            m.setVersionId(versionId);
                            batch.add(m);
                            if (batch.size() >= 200) {
                                mapper.insertBatch(batch);
                                batch.clear();
                            }
                        }
                    } else {
                        // 2.3 普通行：跳过材料名称为空的
                        if (name == null || name.isEmpty()) {
                            // 仅统计进度
                            processed++;
                            if (processed % 5 == 0) notifyProgress(taskId, processed, totalRows);
                            continue;
                        }
                        // 2.4 正常处理一行
                        Material m = buildMaterialFromRow(row, images, r, evaluator);
                        m.setVersionId(versionId);
                        batch.add(m);
                        if (batch.size() >= 200) {
                            mapper.insertBatch(batch);
                            batch.clear();
                        }
                    }

                    // 2.5 进度推送：每5行一次
                    processed++;
                    if ( processed < 50 && processed % 5 == 0 ) {
                        notifyProgress(taskId, processed, totalRows);
                    }
                    if (processed % 50 == 0) {
                        notifyProgress(taskId, processed, totalRows);
                    }
                }

                // 3. 写入剩余批次
                if (!batch.isEmpty()) {
                    mapper.insertBatch(batch);
                }

                // 4. 完成推 100%
                messaging.convertAndSend(
                        "/topic/progress/" + taskId,
                        Map.of("percent", 100, "status", "done")
                );

                log.info("已处理{}条数据",processed);
            }
        } catch (Exception ex) {
            log.error("任务 {} 异常", taskId, ex);
            messaging.convertAndSend(
                    "/topic/progress/" + taskId,
                    Map.of("error", ex.getMessage())
            );
        } finally {
            try { Files.deleteIfExists(tempFile); } catch (IOException ignore) {}
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
                                          int r,
                                          FormulaEvaluator evaluator) throws Exception {
        Material m = new Material();
        m.setMaterialBigCategory(String.valueOf(row.getCell(1)));
        m.setMaterialCategory(getCellString(row.getCell(2)));
        m.setMaterialName(getCellString(row.getCell(3)));
        m.setPrice(getCellNumeric(row.getCell(14), evaluator));

        m.setPhotoDaban(
                uploadIfPresent(images, r, 4)
        );
        m.setPhotoChengpin(
                uploadIfPresent(images, r, 5)
        );
        m.setPhotoXiaoguo(
                uploadIfPresent(images, r, 6)
        );
        return m;
    }

    /** 如果有图片就上传到minIO */
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

    /** 读取数值单元格，若是公式，则先评估再取值 */
    // TODO 先不设置为截取2位小数，价格计算比较复杂，尽量避免引入误差
    private Double getCellNumeric(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return null;
//        System.out.println(cell.getCellType());
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.valueOf(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            case FORMULA:
                // 先计算公式，再按结果类型取值
                CellValue cv = evaluator.evaluate(cell);
                if (cv.getCellType() == CellType.NUMERIC) {
                    return cv.getNumberValue();
                } else if (cv.getCellType() == CellType.STRING) {
                    try {
                        return Double.valueOf(cv.getStringValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            default:
                return null;
        }
    }


    /**
     * 获取材料分页数据
     * @param page 1-based 页码
     * @param size 每页记录数
     */
    public PageInfo<Material> getAll(int page, int size) {
        PageHelper.startPage(page, size);
        List<Material> list = mapper.getAll();

        return new PageInfo<>(list);
    }


    /** 根据材料名称模糊查询 */
    public PageInfo<Material> getByMaterialName(int page, int size, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new PageInfo<>(List.of());
        }
        PageHelper.startPage(page, size);
        List<Material> list = mapper.getByMaterialName(keyword);
        return new PageInfo<>(list);
    }

    /** 根据材料品类模糊查询 */
    public PageInfo<Material> getByMaterialCategory(int page, int size, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new PageInfo<>(List.of());
        }
        PageHelper.startPage(page, size);
        List<Material> list = mapper.getByMaterialCategory(keyword);
        return new PageInfo<>(list);
    }

    /**
     * 确定了<材料品类>就模糊查找<材料名>，否则就模糊查找<材料品类>
     */
    public PageInfo<Material> getByKeyword(int page, int size, String keyword, String category) {
        if (keyword == null || keyword.isBlank()) {
            return new PageInfo<>(List.of());
        }
        PageHelper.startPage(page, size);
        List<Material> list = mapper.getByKeyword(keyword, category);
        return new PageInfo<>(list);
    }

    /**
     * 大类 + 小类 + 材料名 查询
     */
    public PageInfo<Material> getByTriple(int page, int size, String bigCategory, String category, String name) {
        if (bigCategory == null || bigCategory.isBlank() || category == null || category.isBlank() || name == null || name.isBlank()) {
            return new PageInfo<>(List.of());
        }
        PageHelper.startPage(page, size);
        List<Material> list = mapper.getByTriple(bigCategory, category, name);
        return new PageInfo<>(list);
    }



    /**
     *  查找所有不同的材料品类
     */
    public PageInfo<String> getAllCategories(int page, int size) {
        PageHelper.startPage(page, size);
        List<String> list= mapper.getAllCategories();
        return new PageInfo<>(list);
    }

    /**
     * 查找所有不同的材料大类
     */
    public PageInfo<String> getAllBigCategories(int page, int size) {
        PageHelper.startPage(page, size);
        List<String> list= mapper.getAllBigCategories();
        return new PageInfo<>(list);
    }


}