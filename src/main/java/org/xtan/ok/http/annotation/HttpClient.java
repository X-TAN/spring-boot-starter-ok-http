package org.xtan.ok.http.annotation;

import java.lang.annotation.*;

/**
 * 申明为OkHttp接口请求类
 *
 * @author: X-TAN
 * @date: 2021-08-06
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpClient {

    /**
     * host
     */
    String value() default "";

    /**
     * bean名称
     * 默认取ClassName作为bean名称
     */
    String name() default "";
}
