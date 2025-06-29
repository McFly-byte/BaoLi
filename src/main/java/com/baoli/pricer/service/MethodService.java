package com.baoli.pricer.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baoli.pricer.context.VersionContextHolder;
import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.mapper.MethodMapper;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
import com.baoli.pricer.pojo.ProcessMethodParse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MethodService {

    private final MethodMapper mapper;
    private final VersionContextHolder versionContextHolder;
    private static final int BATCH_SIZE = 200;


    /**
     * 解析前端上传的 xlsx，将每行文本字段批量写入 baoli.process_method
     * @param file 前端上传的 .xlsx 文件
     * @return 成功插入的行数
     * @throws IOException 读取文件失败
     */
    public int importMethods(MultipartFile file) throws IOException {
        List<ProcessMethod> batch = new ArrayList<>(BATCH_SIZE);
        ImportListener listener = new ImportListener(batch, mapper);
        EasyExcel.read(file.getInputStream(), ProcessMethodParse.class, listener)
                .sheet().doRead();
        return listener.getSuccessCount();
    }

    private static class ImportListener extends AnalysisEventListener<ProcessMethodParse> {
        private final List<ProcessMethod> batch;
        private final MethodMapper mapper;
        @Getter
        private int successCount = 0;

        public ImportListener(List<ProcessMethod> batch, MethodMapper mapper) {
            this.batch = batch;
            this.mapper = mapper;
        }

        @Override
        public void invoke(ProcessMethodParse row, AnalysisContext ctx) {
            try {
                ProcessMethod m = new ProcessMethod();
                m.setMaterialCategory(row.getMaterialCategory());
                m.setMethod(row.getMethod());
                m.setPrice(row.getPrice());
                batch.add(m);

                if (batch.size() >= BATCH_SIZE) {
                    mapper.insertBatch(batch);
                    successCount += batch.size();
                    batch.clear();
                }
            } catch (Exception e) {
                // 记录异常行，可扩展为日志
                System.err.println("Row " + ctx.readRowHolder().getRowIndex() + " import failed: " + e.getMessage());
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext ctx) {
            if (!batch.isEmpty()) {
                mapper.insertBatch(batch);
                successCount += batch.size();
            }
        }

    }

    /**
     * 获取材料分页数据
     * @param page 1-based 页码
     * @param size 每页记录数
     */
    public PageInfo<ProcessMethod> getAll(int page, int size) {
        int versionId = versionContextHolder.getVersionId();
        PageHelper.startPage(page, size);
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
