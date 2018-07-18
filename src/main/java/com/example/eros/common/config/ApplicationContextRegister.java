package com.example.eros.common.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 上下文配置
 */
@Component
public class ApplicationContextRegister implements ApplicationContextAware{

    private static ApplicationContext APPLICATION_CONTEXT;

    /**
     * 获取容器对象
     * @param type
     * @param <T> 对象类型
     * @return
     */
    public static <T> T getBean(Class<T> type) {
        return APPLICATION_CONTEXT.getBean(type);
    }

    /**
     * 返回spring上下文
     * @return
     */
    public static ApplicationContext getApplicationContext(){
        return APPLICATION_CONTEXT;
    }

    /**
     * 设置spring上下文
     * @param applicationContext spring上下文
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }
}
