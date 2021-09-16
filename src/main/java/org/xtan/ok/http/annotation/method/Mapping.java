package org.xtan.ok.http.annotation.method;

import org.xtan.ok.http.constants.HttpContentType;
import org.xtan.ok.http.model.HttpMethod;

import java.lang.annotation.*;

/**
 * DELETE请求
 *
 * @author: X-TAN
 * @date: 2021-08-06
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     * 请求地址 or 映射
     */
    String value() default "";

    /**
     * 请求方式
     */
    HttpMethod method();

    /**
     * 请求体类型
     */
    String contentType() default HttpContentType.NO;
}
