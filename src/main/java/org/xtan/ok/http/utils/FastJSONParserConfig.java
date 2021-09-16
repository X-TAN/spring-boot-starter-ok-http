package org.xtan.ok.http.utils;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;

/**
 * 针对Ok序列化的JSON工具的局部设置
 *
 * @author: X-TAN
 * @date: 2021-09-13
 */
public class FastJSONParserConfig {

    private static final ParserConfig parserConfig = new ParserConfig();

    static {
        //反序列化下划线转驼峰
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 获取配置
     */
    public static ParserConfig config() {
        return parserConfig;
    }


}
