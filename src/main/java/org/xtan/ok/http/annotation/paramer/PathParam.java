package org.xtan.ok.http.annotation.paramer;

import org.springframework.core.annotation.AliasFor;
import org.xtan.ok.http.annotation.method.*;

import java.lang.annotation.*;

/**
 * 动态映射填充参数注解
 * <p>
 * 如果需要使用当前类，则必须在
 * {@link Get} {@link Delete} {@link Post} {@link Put} {@link Patch}
 * value or path 中声明动态映射 以{value}表示
 * </p>
 * 示例：
 * <pre>
 * /user/{id}/{detailId}
 * </pre>
 *
 * @author: XOptional-TAN
 * @date: 2021-08-09
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the path variable to bind to.
     */
    @AliasFor("value")
    String name() default "";

    /**
     * 必须为true
     *
     * @return
     */
    boolean required() default true;

}
