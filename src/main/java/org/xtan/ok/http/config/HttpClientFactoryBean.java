package org.xtan.ok.http.config;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.xtan.ok.http.proxy.HttpClientProxyHandler;

/**
 * 扫描HttpClient接口实例
 * 并将接口代理类注册成bean
 *
 * @author: XOptional-TAN
 * @date: 2021-08-11
 */
public class HttpClientFactoryBean implements FactoryBean<Object>, InitializingBean {

    private Class<?> type;

    /**
     * 代理类型实例
     */
    private HttpClientProxyHandler<?> handler;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public void afterPropertiesSet() {
        this.handler = HttpClientProxyHandler.proxy(type);
    }

    @Override
    public Object getObject() {
        return handler.getProxyInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}

