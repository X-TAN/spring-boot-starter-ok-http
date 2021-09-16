package org.xtan.ok.http.config;

import org.springframework.core.env.Environment;

/**
 * 配置参数操作组件
 * (单例)
 *
 * @author: XOptional-TAN
 * @date: 2021-09-13
 */
public class EnvironmentComponent {

    /**
     * 必须是项目启动完成才能获取到该实例
     */
    public static EnvironmentComponent INSTANCE;

    private Environment environment;

    private EnvironmentComponent() {
    }

    private EnvironmentComponent(Environment environment) {
        this.environment = environment;
    }

    protected static void init(Environment environment) {
        INSTANCE = new EnvironmentComponent(environment);
    }

    /**
     * 获取配置
     *
     * @param key 配置key
     * @return
     */
    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    /**
     * 是否存在配置
     *
     * @param key 配置key
     * @return
     */
    public boolean containsProperty(String key) {
        return environment.containsProperty(key);
    }
}
