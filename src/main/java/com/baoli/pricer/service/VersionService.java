package com.baoli.pricer.service;

import com.baoli.pricer.mapper.MaterialMapper;
import com.baoli.pricer.mapper.MethodMapper;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.Version;
import com.baoli.pricer.mapper.VersionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VersionService {

    @Autowired
    private VersionMapper mapper;
    @Autowired
    private MethodMapper methodMapper;
    @Autowired
    private MaterialMapper materialMapper;

    /**
     * 插入一条新版本，插入后会自动回填到 version.id
     */
    public int insert(byte flag, String versionName, String description) {
        // 含参构造方法自动填充 createdAt
        Version version = new Version(flag, versionName, description);

        int rows = mapper.insert(version);
        if (rows > 0) {
            log.info("新版本插入成功，ID: {}", version.getId());
        } else {
            log.error("新版本插入失败");
        }
        return version.getId(); // 返回新插入的版本 ID
    }

    /**
     * 根据 id 获取版本
     */
    public Version getById(int id) {
        Version version = mapper.getById(id);
        if (version != null) {
            log.info("获取版本成功: {}", version);
        } else {
            log.warn("未找到 ID 为 {} 的版本", id);
        }
        return version;
    }

    /**
     * 根据版本名称查询
     */
    public Version getByVersionName(String versionName) {
        Version version = mapper.getByVersionName(versionName);
        if (version != null) {
            log.info("获取版本成功: {}", version);
        } else {
            log.warn("未找到名称为 {} 的版本", versionName);
        }
        return version;
    }

    /**
     * 根据 flag 查询版本列表（分页查询）
     */
    public PageInfo<Version> getByFlag(int page, int size, byte flag) {
        PageHelper.startPage(page, size);
        List<Version> list = mapper.getByFlag(flag);
        return new PageInfo<>(list);
    }

    /**
     * 查询所有版本（分页查询）
     */
    public PageInfo<Version> getAll(int page, int size) {
        PageHelper.startPage(page, size);
        List<Version> list = mapper.getAll();
        return new PageInfo<>(list);
    }

    /**
     * 根据 id 更新版本记录
     */
    public boolean updateById(int id, String versionName, String description) {
        Version version = mapper.getById(id);
        if ( versionName != null ) {
            version.setVersionName(versionName);
        }
        if ( description != null ) {
            version.setDescription(description);
        }
        int rows = mapper.updateById(version);
        if (rows > 0) {
            log.info("版本更新成功，ID: {}", id);
            return true;
        } else {
            log.error("版本更新失败，ID: {}", id);
            return false;
        }
    }

    /**
     * 根据 id 删除版本记录
     */
    public boolean deleteById(int id) {
        Version version = mapper.getById(id);
        int rows = mapper.deleteById(id);
        if (rows > 0) { // 删除版本时，如果是正式版本，还需要删除对应的材料
            if ( version.getFlag() == 1 ) {
                rows = methodMapper.deleteByVersionId(id);
            }
            else {
                rows = materialMapper.deleteByVersionId(id);
            }
            log.info("成功删除{}条记录，版本ID: {}", rows, id);
            return true;
        } else {
            log.info("版本删除失败，当前没有此版本，ID: {}", id);
            return false;
        }
    }



}
