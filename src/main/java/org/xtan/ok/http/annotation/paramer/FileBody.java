package org.xtan.ok.http.annotation.paramer;


import java.lang.annotation.*;

/**
 * 标识这是一个json实体对象
 *
 * @author: X-TAN
 * @date: 2021-08-10
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileBody {
}
