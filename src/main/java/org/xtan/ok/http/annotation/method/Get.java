package org.xtan.ok.http.annotation.method;

import org.springframework.core.annotation.AliasFor;
import org.xtan.ok.http.constants.HttpContentType;
import org.xtan.ok.http.model.HttpMethod;

import java.lang.annotation.*;

/**
 * GET请求
 *
 * @author: XOptional-TAN
 * @date: 2021-08-06
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping(method = HttpMethod.GET)
public @interface Get {

    /**
     * 请求映射名
     */
    @AliasFor(annotation = Mapping.class)
    String value() default "";

    /**
     * 请求体类型
     */
    @AliasFor(annotation = Mapping.class)
    String contentType() default HttpContentType.NO;
}
