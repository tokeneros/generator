package com.example.eros.common.utils;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 配置信息工具类
 */
public class ConfigUtils {

    /**
     * 通过配置信息名进行加载
     * @param fileName properties文件全名
     * @return
     */
    public static Configuration getConfig(String fileName) {
        PropertiesConfiguration p = null;
        try {
            p = new PropertiesConfiguration(fileName);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return p;
    }

}
