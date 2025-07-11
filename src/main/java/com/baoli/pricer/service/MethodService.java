package com.baoli.pricer.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baoli.pricer.context.VersionContextHolder;
import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.mapper.MethodMapper;
import com.baoli.pricer.mapper.VersionMapper;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
import com.baoli.pricer.pojo.ProcessMethodParse;
import com.baoli.pricer.pojo.Version;
import com.baoli.pricer.utils.ExcelUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MethodService {

    private final MethodMapper mapper;
    private final VersionMapper versionMapper;
    private final VersionContextHolder versionContextHolder;
    private static final int BATCH_SIZE = 200;


    /**
     * 解析前端上传的 XLSX 文件，
     * 1) 创建一个新的版本并记录到 version 表；
     * 2) 逐行读取解析施工工艺数据；
     * 3) 批量插入到 process_method 表，并将 versionId 写入每条记录；
     *
     * @param file 前端上传的 .xlsx 文件
     * @return 实际成功插入的记录数
     * @throws Exception 读取或写入过程若发生异常将抛出
     */
    public int importMethods(MultipartFile file) throws Exception {
        // —— 1. 创建新版本 —— //
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String versionName = "method_" + LocalDateTime.now().format(fmt);
        Version version = new Version((byte) 1, versionName, LocalDateTime.now().format(fmt)+"施工工艺表");
        versionMapper.insert(version);
        version = versionMapper.getByVersionName(versionName);
        int versionId = version.getId();
        // 将当前线程版本写入上下文，后续查询可复用
        versionContextHolder.setVersionId(versionId);
        log.info("新版本创建成功：id={}，name={}", versionId, versionName);

        // —— 2. 读取并解析 Excel —— //
        List<ProcessMethod> batch = new ArrayList<>(BATCH_SIZE);
        int successCount = 0;

        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);


            // 跳过表头，从第 1 行开始
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }

                // 解析每列字段
                String category = getCellString(row.getCell(1));
                String method   = getCellString(row.getCell(2));
                Double price = ExcelUtils.getDisplayedNumber(row.getCell(8));

                // 若核心字段缺失，则跳过
                if (category == null || method == null) {
                    log.warn("第 {} 行数据不完整，已跳过", r + 1);
                    continue;
                }

                // 构建实体并设置 versionId
                ProcessMethod pm = new ProcessMethod();
                pm.setVersionId(versionId);
                pm.setMaterialCategory(category);
                pm.setMethod(method);
                pm.setPrice(price);

                batch.add(pm);
                // 达到批量阈值，立即写库并清空
                if (batch.size() >= BATCH_SIZE) {
                    mapper.insertBatch(batch);
                    successCount += batch.size();
                    batch.clear();
                }
            }

            // 插入剩余未满批次的数据
            if (!batch.isEmpty()) {
                mapper.insertBatch(batch);
                successCount += batch.size();
            }

            log.info("施工工艺导入完成，共插入 {} 条记录", successCount);
            return successCount;

        } catch (Exception ex) {
            log.error("importMethods 执行失败", ex);
            throw ex;
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private Double getCellNumeric(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.valueOf(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            case FORMULA:
                CellValue cv = evaluator.evaluate(cell);
                if (cv.getCellType() == CellType.NUMERIC) {
                    return cv.getNumberValue();
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
    public PageInfo<ProcessMethod> getAll(int page, int size) {
        PageHelper.startPage(page, size);
        int versionId = versionContextHolder.getVersionId();
        List<ProcessMethod> list = mapper.getAll(versionId);
        return new PageInfo<>(list);
    }

    /**
     * 模糊查询施工工艺或材料品类
     */
    public PageInfo<ProcessMethod> getByKeyword(int page, int size, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new PageInfo<>(List.of());
        }
        int versionId = versionContextHolder.getVersionId();
        PageHelper.startPage(page, size);
        List<ProcessMethod> list = mapper.getByKeyword(versionId, keyword);
        return new PageInfo<>(list);
    }

    /**
     * 根据材料品类查询 Material 列表
     * @param category 材料品类
     */
    public PageInfo<ProcessMethod> getByCategory(int page, int size, String category) {
        int versionId = versionContextHolder.getVersionId();
        PageHelper.startPage(page, size);
        List<ProcessMethod> list = mapper.getByCategory(versionId, category);
        return new PageInfo<>(list);
    }

    /**
     *  查找所有不同的材料品类
     */
    public PageInfo<String> getAllCategories(int page, int size) {
        int versionId = versionContextHolder.getVersionId();
        PageHelper.startPage(page, size);
        List<String> list= mapper.getAllCategories(versionId);
        return new PageInfo<>(list);
    }
}
