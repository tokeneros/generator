package com.example.eros.common.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 代码生成dao
 */
@Mapper
public interface GeneratorMapper {

    /**
     * information_schema是mysql中的特殊表，可以查询一些数据库的额外信息
     * @return
     */
    @Select("SELECT table_name tableName,engine,create_time createTime,table_comment tableComment FROM information_schema.tables WHERE table_schema = (SELECT DATABASE());")
    List<Map<String,Object>> list();

    /**
     * 根据当前所选的数据库 和 表名 查询表信息
     * @param tableName 表名
     * @return
     */
    @Select("SELECT table_name tableName, engine, table_comment tableComment, create_time createTime " +
            "FROM information_schema.tables " +
            "WHERE table_schema = (SELECT DATABASE()) AND table_name = #{tableName}")
    Map<String,String> get(String tableName);

    /**
     * 根据当前所选的数据库 和 表名 查询字段信息
     * @param tableName
     * @return
     */
    @Select("SELECT column_name columnName,data_type dataType,column_comment columnComment,extra,column_key columnKey FROM information_schema.COLUMNS WHERE table_schema = (SELECT DATABASE()) AND table_name = #{tableName}")
    List<Map<String,String>> listColumns(String tableName);

    /**
     * 查询当前所选的数据库 的 表数量
     * @return
     */
    @Select("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = (SELECT DATABASE())")
    Integer count();
}
