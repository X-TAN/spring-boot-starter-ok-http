package org.xtan.ok.http.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.xtan.ok.http.config.HttpClientsRegistrar;

import java.lang.annotation.*;

/**
 * 开启httpClient自动装配
 *
 * @author: X-TAN
 * @date: 2021-08-11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpClientsRegistrar.class)
public @interface EnableHttpClients {

    /**
     * 包地址路径集合
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * 包地址路径集合
     */
    @AliasFor("value")
    String[] basePackages() default {};

}
