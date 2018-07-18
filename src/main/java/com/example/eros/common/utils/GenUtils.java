package com.example.eros.common.utils;

import com.example.eros.common.config.Constant;
import com.example.eros.common.domain.ColumnDO;
import com.example.eros.common.domain.TableDO;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成工具类
 */
public class GenUtils {

    /**
     * 生成代码
     * @param table 表信息
     * @param columns 字段信息
     * @param zip ZipOutputStream 压缩流
     */
    public static void generatorCode(Map<String, String> table, List<Map<String, String>> columns, ZipOutputStream zip) {
        //配置信息
        Configuration config = ConfigUtils.getConfig("generator.properties");
        //表信息
        TableDO tableDO = new TableDO();
        tableDO.setTableName(table.get("tableName"));
        tableDO.setComments(table.get("tableComment"));
        //表名转换成java类名
        String className = tableToJava(tableDO.getTableName(), config.getString("tablePrefix"), config.getString("autoRemovePre"));
        tableDO.setClassName(className);
        //uncapitalize首字母小写
        tableDO.setClassname(StringUtils.uncapitalize(className));

        //每列信息
        List<ColumnDO> columnDOList = new ArrayList<>();
        for(Map<String,String> column : columns){
            ColumnDO columnDO = new ColumnDO();
            columnDO.setColumnName(column.get("columnName"));
            columnDO.setComments(column.get("columnComment"));
            columnDO.setDataType(column.get("dataType"));
            columnDO.setExtra(column.get("extra"));

            //列名转换成java属性名
            String attrName = columnToJava(columnDO.getColumnName());
            columnDO.setAttrName(attrName);
            columnDO.setAttrname(StringUtils.uncapitalize(attrName));
            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnDO.getDataType());
            columnDO.setAttrType(attrType);

            //是否主键
            if("PRI".equalsIgnoreCase(column.get("columnKey")) && tableDO.getPk() == null){
                tableDO.setPk(columnDO);
            }

            columnDOList.add(columnDO);
        }
        tableDO.setColumns(columnDOList);

        //没主键，则第一个字段为主键
        if(tableDO.getPk() == null){
            tableDO.setPk(tableDO.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        //封装Velocity模板数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName",tableDO.getTableName());
        map.put("comments",tableDO.getComments());
        map.put("pk",tableDO.getPk());
        map.put("className",tableDO.getClassName());
        map.put("classname",tableDO.getClassname());
        map.put("columns",tableDO.getColumns());
        map.put("pathName",config.getString("package").substring(config.getString("package").lastIndexOf(".") + 1));
        map.put("author",config.getString("author"));
        map.put("package",config.getString("package"));
        map.put("email",config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        List<String> templates = getTemplates();
        for(String template : templates){
            //获取模板文件的流
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template,"UTF-8");
            tpl.merge(context,sw);

            try {
                zip.putNextEntry(new ZipEntry(getFileName(template, tableDO.getClassname(), tableDO.getClassName(), config.getString("package").substring(config.getString("package").lastIndexOf(".") + 1))));
                IOUtils.write(sw.toString(),zip,"UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否去掉表前缀  或者  指定前缀
     * @param tableName 表名
     * @param tablePrefix 前缀
     * @param autoRemovePre 自动去前缀
     * @return
     */
    private static String tableToJava(String tableName, String tablePrefix, String autoRemovePre) {
        if(Constant.AUTO_REMOVE_PRE.equals(autoRemovePre)){
            tableName = tableName.substring(tableName.indexOf("_")+1);
        }
        if (StringUtils.isNotBlank(tablePrefix)){
            tableName = tableName.replace(tablePrefix,"");
        }
        return columnToJava(tableName);
    }

    /**
     * 将数据表名或字段名 转换为 常见java类名
     * @param columnName 表名或字段名
     * @return java类名
     */
    private static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName,new char[]{'_'}).replaceAll("_","");
    }

    /**
     * 模板列表
     * @return
     */
    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("templates/common/generator/domain.java.vm");
        templates.add("templates/common/generator/Dao.java.vm");
        templates.add("templates/common/generator/Mapper.xml.vm");
        templates.add("templates/common/generator/Service.java.vm");
        templates.add("templates/common/generator/ServiceImpl.java.vm");
        templates.add("templates/common/generator/Controller.java.vm");
        templates.add("templates/common/generator/list.html.vm");
        templates.add("templates/common/generator/add.html.vm");
        templates.add("templates/common/generator/edit.html.vm");
        templates.add("templates/common/generator/list.js.vm");
        templates.add("templates/common/generator/add.js.vm");
        templates.add("templates/common/generator/edit.js.vm");
//        templates.add("templates/common/generator/menu.sql.vm");
        return templates;
    }

    /**
     * 获取文件名
     * @param template 模板路径
     * @param classname 类名 - 首字母小写
     * @param className 类名 - 首字母大写
     * @param pathName 路径
     * @return
     */
    private static String getFileName(String template, String classname, String className, String pathName) {
        String packagePath = "main" + File.separator + "java" +File.separator;
        if(StringUtils.isNotBlank(pathName)){
            packagePath += pathName.replace(".", File.separator) + File.separator;
        }

        if(template.contains("domain.java.vm")){
            return packagePath + "domain" + File.separator + className + "DO.java";
        }

        if(template.contains("Dao.java.vm")){
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }

        if(template.contains("Service.java.vm")){
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if(template.contains("ServiceImpl.java.vm")){
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if(template.contains("Controller.java.vm")){
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if(template.contains("Mapper.xml.vm")){
            return "main" + File.separator + "resources" + File.separator + "mybatis" + File.separator + pathName + File.separator + className + "Mapper.xml";
        }

        if (template.contains("list.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator
                    + packagePath + File.separator + classname + File.separator + classname + ".html";
            //				+ "modules" + File.separator + "generator" + File.separator + className.toLowerCase() + ".html";
        }
        if (template.contains("add.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator
                    + packagePath + File.separator + classname + File.separator + "add.html";
        }
        if (template.contains("edit.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator
                    + packagePath + File.separator + classname + File.separator + "edit.html";
        }

        if (template.contains("list.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js" + File.separator
                    + "appjs" + File.separator + packagePath + File.separator + classname + File.separator + classname + ".js";
            //		+ "modules" + File.separator + "generator" + File.separator + className.toLowerCase() + ".js";
        }
        if (template.contains("add.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js" + File.separator
                    + "appjs" + File.separator + packagePath + File.separator + classname + File.separator + "add.js";
        }
        if (template.contains("edit.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js" + File.separator
                    + "appjs" + File.separator + packagePath + File.separator + classname + File.separator + "edit.js";
        }

//        if(template.contains("menu.sql.vm")){
//            return packagePath + "controller" + File.separator + className + "Controller.java";
//        }

        return "";
    }

    public static void main(String[] args){

    }
}
