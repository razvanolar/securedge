package com.securedge.server.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringContextConfig implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    public static <T extends Object> T getBean(Class<T> beanClass) {
        return CONTEXT.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {

        // store ApplicationContext reference to access required beans later on
        SpringContextConfig.CONTEXT = context;
    }
}
