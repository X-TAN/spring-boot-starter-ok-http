package org.xtan.ok.http.model;

import org.xtan.ok.http.annotation.method.Mapping;
import org.xtan.ok.http.constants.HttpPrefix;
import org.xtan.ok.http.handler.MappingHeadersPaddingHandler;
import org.xtan.ok.http.handler.MappingPathResolveHandler;

import java.lang.reflect.Method;

public class HttpMapping {

    /**
     * 请求前缀
     * http://
     * https://
     *
     * @see HttpPrefix
     */
    private String prefix;

    /**
     * host
     */
    private String host;

    /**
     * 请求资源路径
     */
    private String[] path;

    /**
     * 请求头
     */
    private HttpHeaders headers = HttpHeaders.builder();

    /**
     * 是否是动态资源路径
     */
    private boolean isDynamicPath;

    /**
     * 请求方式
     */
    private HttpMethod method;

    /**
     * contentType
     */
    private String contentType;

    /**
     * 请求地址
     *
     * @return
     */
    public String url() {
        return String.format("%s%s/%s", prefix, host, String.join("/", path));
    }

    /**
     * 构建
     *
     * @param host    host
     * @param headers 是否存在类全局请求头
     * @param mapping 请求映射
     * @return
     */
    public static HttpMapping builder(String host, HttpHeaders headers, Mapping mapping, Method method) {
        HttpMapping httpMapping = new HttpMapping();
        httpMapping.method(mapping.method());
        httpMapping.contentType(mapping.contentType());
        httpMapping.headers.add(headers);
        //解析地址
        MappingPathResolveHandler.staticPathResolve(host, mapping, httpMapping, method);
        //检查是否存在动态映射
        MappingPathResolveHandler.dynamicPathCheck(httpMapping);
        //解析静态请求头
        MappingHeadersPaddingHandler.headersOnMethod(httpMapping, method);
        //解析参数
        return httpMapping;
    }

    public String prefix() {
        return prefix;
    }

    public void prefix(String prefix) {
        this.prefix = prefix;
    }

    public String host() {
        return host;
    }

    public void host(String host) {
        this.host = host;
    }

    public String[] path() {
        return path;
    }

    public HttpMapping path(String[] path) {
        this.path = path;
        return this;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public HttpMapping headers(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public boolean isDynamicPath() {
        return isDynamicPath;
    }

    public HttpMapping isDynamicPath(boolean dynamicPath) {
        isDynamicPath = dynamicPath;
        return this;
    }

    public HttpMethod method() {
        return method;
    }

    public HttpMapping method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public String contentType() {
        return contentType;
    }

    public HttpMapping contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }
}
