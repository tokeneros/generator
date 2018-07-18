package com.example.eros.common.service.impl;

import com.example.eros.common.dao.GeneratorMapper;
import com.example.eros.common.service.GeneratorService;
import com.example.eros.common.utils.GenUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成Service
 */
@Service
public class GeneratorServiceImpl implements GeneratorService{

    /**
     * 代码生成Mapper
     */
    @Autowired
    private GeneratorMapper generatorMapper;

    /**
     * 获取数据库列表
     * @return
     */
    @Override
    public List<Map<String, Object>> list() {
        List<Map<String,Object>> list = generatorMapper.list();
        return list;
    }

    /**
     * 获取每张表的 表信息 和 表中字段 ，来生成代码
     * @param tableNames 表名数组
     * @return
     */
    @Override
    public byte[] generatorCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for(String tableName : tableNames){
            //查询表信息
            Map<String,String> table = generatorMapper.get(tableName);
            //查询字段信息
            List<Map<String,String>> columns = generatorMapper.listColumns(tableName);
            GenUtils.generatorCode(table,columns,zip);
        }
        //只是关闭流
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

}
