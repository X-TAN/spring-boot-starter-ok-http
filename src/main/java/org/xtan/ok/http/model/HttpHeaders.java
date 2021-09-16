package org.xtan.ok.http.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


/**
 * 请求头构造器
 *
 * @author: XOptional-TAN
 * @date: 2021-08-09
 */
public class HttpHeaders {

    /**
     * 请求头容器
     */
    private final Map<String, String> headers = new HashMap<>();

    /**
     * 私有构造
     */
    private HttpHeaders() {
    }

    /**
     * 构造器
     */
    public static HttpHeaders builder() {
        return new HttpHeaders();
    }

    /**
     * 添加请求头
     *
     * @param key   请求头key
     * @param value 请求头值
     */
    public HttpHeaders add(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpHeaders add(HttpHeaders httpHeaders) {
        if (null != httpHeaders) this.headers.putAll(httpHeaders.toMap());
        return this;
    }

    /**
     * 迭代器
     *
     * @param consumer
     */
    public void foreach(BiConsumer<String, String> consumer) {
        headers.forEach(consumer);
    }

    /**
     * 获取整个请求头容器
     *
     * @return
     */
    public Map<String, String> toMap() {
        return this.headers;
    }
}
