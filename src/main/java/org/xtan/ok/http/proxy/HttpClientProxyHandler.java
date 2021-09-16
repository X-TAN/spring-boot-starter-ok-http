package org.xtan.ok.http.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.xtan.ok.http.OkBuilder;
import org.xtan.ok.http.annotation.Headers;
import org.xtan.ok.http.annotation.HttpClient;
import org.xtan.ok.http.annotation.method.Mapping;
import org.xtan.ok.http.exception.HttpClientException;
import org.xtan.ok.http.handler.MappingHeadersPaddingHandler;
import org.xtan.ok.http.handler.MappingParamsPaddingHandler;
import org.xtan.ok.http.handler.MappingPathResolveHandler;
import org.xtan.ok.http.model.HttpHeaders;
import org.xtan.ok.http.model.HttpMapping;
import org.xtan.ok.http.utils.FastJSONParserConfig;
import org.xtan.ok.http.utils.MD5;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 代理处理器
 *
 * @author: X-TAN
 * @date: 2021-08-06
 */
public class HttpClientProxyHandler<T> implements InvocationHandler {

    /**
     * 代理类型实例
     */
    private final Class<T> proxyInterface;

    /**
     * 映射缓存
     */
    private final Map<String, HttpMapping> mappingMap = new HashMap<>(1 >> 4);

    public Class<T> getProxyInterface() {
        return proxyInterface;
    }

    private HttpClientProxyHandler(Class<T> proxyInterface) {
        this.proxyInterface = proxyInterface;
        //注册Mapping
        registerMappings(
                proxyInterface.getAnnotation(HttpClient.class).value(),
                MappingHeadersPaddingHandler.loadAnnotationHeaders(proxyInterface.getAnnotation(Headers.class))
        );
    }

    /**
     * 初始化代理容器
     * 传入参数必须是声明了注解：@{@link HttpClient}
     * 的接口类
     *
     * @param proxyInterface 需要实现代理的接口.class
     */
    public static <T> HttpClientProxyHandler<T> proxy(Class<T> proxyInterface) {
        //创建代理容器
        return new HttpClientProxyHandler<>(proxyInterface);
    }

    /**
     * 获取代理实现类
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public T getProxyInstance() {
        return (T) Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[]{proxyInterface}, this);
    }

    /**
     * 代理类方法执行的实现
     *
     * @param proxy  代理
     * @param method 方法
     * @param args   参数
     * @return object 对应方法的返回参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        HttpMapping httpMapping = mappingMap.get(MD5.encode(method.toGenericString()));
        //检查动态映射,构建请求器
        OkBuilder okBuilder = OkBuilder.url(
                        //是否存在动态映射地址
                        httpMapping.isDynamicPath()
                                ? MappingPathResolveHandler.dynamicPathResolve(httpMapping, method, args)
                                : httpMapping.url()
                )
                //填充请求方式
                .method(httpMapping.method())
                //填充请求内容类型
                .contentType(httpMapping.contentType())
                //填充静态请求头
                .headers(httpMapping.headers());
        //填充参数请求头
        MappingHeadersPaddingHandler.headsOnMethodParams(okBuilder, method, args);
        //填充请求参数
        MappingParamsPaddingHandler.handler(okBuilder, method, args);
        //获取请求结果
        String result = okBuilder.execute();
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        //获取方法返回类型，如果不是String则通过JSON反序列化
        return method.getReturnType().equals(String.class) ? result : JSON.parseObject(result, method.getReturnType(), FastJSONParserConfig.config());
    }

    /**
     * 注册client的所有方法
     */
    private void registerMappings(String host, HttpHeaders headers) {
        Arrays.stream(this.proxyInterface.getMethods()).forEach(method -> {
            Mapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, Mapping.class);
            //不存在任何映射注解，抛出异常
            if (null == mapping) {
                throw new HttpClientException("no mapping annotation exists!", method);
            }
            //如果没有定义地址，则抛出地址未定义的异常
            if (StringUtils.isBlank(mapping.value())) {
                throw new HttpClientException("the request url is undefined!", method);
            }
            //缓存mapping
            mappingMap.put(MD5.encode(method.toGenericString()), HttpMapping.builder(host, headers, mapping, method));
        });
    }
}
