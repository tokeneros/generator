package com.example.eros.common.controller;

import com.alibaba.fastjson.JSON;
import com.example.eros.common.service.GeneratorService;
import com.example.eros.common.utils.ConfigUtils;
import com.example.eros.common.utils.R;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成Controller
 */
@RequestMapping("/common/generator")
@Controller
public class GeneratorController {

    /**
     * 返回路径前缀
     */
    private String prefix = "common/generator";

    /**
     * 代码生成Service
     */
    @Autowired
    private GeneratorService generatorService;

    /**
     * 当访问根路径时访问
     * @return
     */
    @GetMapping()
    String generator(){
        return prefix + "/list";
    }

    /**
     * 查询数据库列表
     * @return
     */
    @GetMapping("/list")
    @ResponseBody
    List<Map<String,Object>> list(){
        List<Map<String,Object>> list = generatorService.list();
        return list;
    }

    /**
     * 根据表名称查询表所有的字段，然后进行生成代码
     * @param request
     * @param response
     * @param tableName 表名称
     */
    @RequestMapping("/code/{tableName}")
    public void code(HttpServletRequest request, HttpServletResponse response,
                     @PathVariable("tableName")String tableName) throws IOException {
        String[] tableNames = new String[]{tableName};
        byte[] data = generatorService.generatorCode(tableNames);
        //设置调用网页下载
        response.reset();
        response.setHeader("Content-Disposition","attachment; filename=\"generator.zip\"");
        response.addHeader("Content-Length",""+data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());

//        FileOutputStream file = new FileOutputStream(new File("D:\\generator.zip"));
//        IOUtils.write(data,file);
//        file.close();
    }

    /**
     * 跳转到edit页面
     * @param model 模型存放配置参数
     * @return
     */
    @GetMapping("/edit")
    public String edit(Model model){
        Configuration conf = ConfigUtils.getConfig("generator.properties");
        Map<String, Object> property = new HashMap<>(16);
        property.put("author",conf.getString("author"));
        property.put("email",conf.getString("email"));
        property.put("package",conf.getString("package"));
        property.put("autoRemovePre",conf.getString("autoRemovePre"));
        property.put("tablePrefix",conf.getString("tablePrefix"));
        model.addAttribute("property",property);
        return prefix + "/edit";
    }

    /**
     * 修改全文配置文件
     * @param map
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public R update(@RequestParam Map<String, Object> map){
        try {
            PropertiesConfiguration conf = new PropertiesConfiguration("generator.properties");
            conf.setProperty("author",map.get("author"));
            conf.setProperty("email",map.get("email"));
            conf.setProperty("package",map.get("package"));
            conf.setProperty("autoRemovePre",map.get("autoRemovePre"));
            conf.setProperty("tablePrefix",map.get("tablePrefix"));
            conf.save();
        } catch (ConfigurationException e) {
            return R.error("保存配置文件出错");
        }
        return R.ok();
    }

    /**
     * 批量生成代码
     * @param tables 表名集合
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/batchCode")
    public void batchCode(HttpServletRequest request,HttpServletResponse response,String[] tables) throws IOException {
        String[] tableNames = new String[]{};
//        tableNames = JSON.parseArray(tables).toArray(tableNames);
        byte[] data = generatorService.generatorCode(tables);
        response.reset();
        response.setHeader("Content-Disposition","attachment; filename=\"generator.zip\"");
        response.addHeader("Content-Length",data.length+"");
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data,response.getOutputStream());
    }

}
