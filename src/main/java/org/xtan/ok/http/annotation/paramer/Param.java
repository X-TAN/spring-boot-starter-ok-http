package org.xtan.ok.http.annotation.paramer;


import java.lang.annotation.*;

/**
 * 接口请求参数的参数名
 *
 * @author: X-TAN
 * @date: 2021-08-10
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    /**
     * 上传文件时的名称
     */
    String value();

}
