package org.xtan.ok.http.annotation;

import java.lang.annotation.*;

/**
 * 请求头设置
 * <p>
 * 设置请求头的方式有两种
 * 1.给定请求头
 * 2.动态设置请求头
 * </p>
 * 示例
 * <pre>
 *
 * 1.@Headers({"key:value","key1:value1"})
 *    method()
 * 2. method(@Headers Headers headers)
 * </pre>
 *
 * @author: X-TAN
 * @date: 2021-08-06
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Headers {

    /**
     * 多个header填充
     * 格式 - key:value
     */
    String[] value() default "";
}
