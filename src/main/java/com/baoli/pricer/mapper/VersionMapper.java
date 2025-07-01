package com.baoli.pricer.mapper;

import com.baoli.pricer.pojo.Version;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VersionMapper {
    /**
     * 插入一条新版本，插入后会自动回填到 version.id
     */
    int insert(Version version);

    /**
     * 根据 id 获取版本
     */
    Version getById(@Param("id") int id);

    /**
     * 根据版本名称查询
     */
    Version getByVersionName(@Param("versionName") String versionName);

    /**
     * 根据 flag 查询版本列表
     */
    List<Version> getByFlag(@Param("flag") byte flag);

    /**
     * 查询所有版本
     */
    List<Version> getAll();

    /**
     * 根据 id 更新版本记录
     */
    int updateById(Version version);

    /**
     * 根据 id 删除版本记录
     */
    int deleteById(@Param("id") int id);
}