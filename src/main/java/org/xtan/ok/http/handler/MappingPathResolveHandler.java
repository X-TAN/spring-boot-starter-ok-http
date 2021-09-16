package org.xtan.ok.http.handler;

import org.apache.commons.lang3.StringUtils;
import org.xtan.ok.http.annotation.method.Mapping;
import org.xtan.ok.http.annotation.paramer.PathParam;
import org.xtan.ok.http.config.EnvironmentComponent;
import org.xtan.ok.http.constants.HttpPrefix;
import org.xtan.ok.http.exception.HttpClientException;
import org.xtan.ok.http.model.HttpMapping;
import org.xtan.ok.http.model.HttpMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 地址解析处理器
 *
 * @author: X-TAN
 * @date: 2021-08-09
 */

/**
 * 地址解析处理器
 *
 * @author: X-TAN
 * @date: 2021-08-09
 */
public class MappingPathResolveHandler {

    /**
     * 动态地址检查
     *
     * @param entity mapping对象
     */
    public static void dynamicPathCheck(HttpMapping entity) {
        Arrays.stream(entity.path())
                .filter(it -> it.contains("{") && it.lastIndexOf("}") > -1)
                .findAny()
                .ifPresent(it -> entity.isDynamicPath(true));
    }


    /**
     * 返回动态地址
     *
     * @param entity mapping对象
     * @param method 请求方法
     * @param args   请求方法参数
     */
    public static String dynamicPathResolve(HttpMapping entity, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
            if (null != pathParam) map.put(pathParam.value(), args[i].toString());
        }
        //生成一个新的path
        StringBuilder sb = new StringBuilder();
        for (String it : entity.path()) {
            sb.append("/");
            if (it.contains("{") && it.lastIndexOf("}") > -1) {
                String paramsName = it.substring(1, it.length() - 1);
                sb.append(map.get(paramsName));
                continue;
            }
            sb.append(it);
        }
        return String.format("%s%s%s", entity.prefix(), entity.host(), sb);
    }

    /**
     * 地址解析
     *
     * @param host    host
     * @param mapping 请求映射
     * @param entity  mapping对象
     */
    public static void staticPathResolve(String host, Mapping mapping, HttpMapping entity, Method method) {
        //如果host为spring参数注入的host，进行转换
        String propertyHost = propertyHost(host, method);
        host = StringUtils.isNotBlank(propertyHost) ? propertyHost : host;
        //mapping.value
        String mappingUrl = propertyHost(mapping.value(), method);
        mappingUrl = StringUtils.isNotBlank(mappingUrl) ? mappingUrl : mapping.value();

        //如果是携带了http://等前缀的完整请求地址，尝试解析，如果成功不继续进行下列的解析操作
        if (mappingHasHttpPrefixResolve(mappingUrl, entity)) {
            return;
        }
        // 如果是没有携带http://等前缀的完整请求地址，尝试解析，如果成功不继续进行下列的解析操作
        if (mappingNotHttpPrefixResolve(mappingUrl, entity)) {
            return;
        }
        // 如果mappingUrl是一个映射，并不包含请求域名或者host
        hostAndMapping(host, mappingUrl, entity, method);
    }

    /**
     * 如果是携带了http://等前缀的完整请求地址
     *
     * @param mappingUrl 方法请映射地址
     * @param entity     映射实体
     */
    private static boolean mappingHasHttpPrefixResolve(String mappingUrl, HttpMapping entity) {
        String httpPrefix = HttpPrefix.Prefix.getByUrl(mappingUrl);
        if (StringUtils.isBlank(httpPrefix)) {
            return false;
        }
        String value = mappingUrl.substring(httpPrefix.length());
        String[] split = value.split("/");
        List<String> collect = Arrays.stream(split).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        collect.remove(0);
        entity.prefix(httpPrefix);
        entity.host(split[0]);
        entity.path(collect.toArray(new String[0]));
        return true;
    }


    /**
     * 如果是没有携带http://等前缀的完整请求地址
     *
     * @param mappingUrl 方法请映射地址
     * @param entity     映射实体
     */
    private static boolean mappingNotHttpPrefixResolve(String mappingUrl, HttpMapping entity) {
        //是否是没有http://前缀的完整请求地址
        List<String> paths = Arrays.stream(mappingUrl.split("/"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (paths.get(0).contains(".") || paths.get(0).contains(":")) {
            entity.host(paths.get(0));
            paths.remove(0);
            entity.path(paths.toArray(new String[0]));
            entity.prefix(HttpMethod.WS.equals(entity.method()) ? HttpPrefix.WS : HttpPrefix.HTTP);
            return true;
        }
        return false;
    }

    /**
     * 如果mappingUrl是一个映射，并不包含请求域名或者host
     *
     * @param host       域名或者host
     * @param mappingUrl 请求映射
     * @param entity     映射实体
     * @param method     调用方法
     */
    private static void hostAndMapping(String host, String mappingUrl, HttpMapping entity, Method method) {
        //如果只是映射，则检查host是否存在
        if (StringUtils.isBlank(host)) {
            throw new HttpClientException("if the host of httpclient is empty, the URL should be a complete request address!", method);
        }
        String httpPrefix = HttpPrefix.Prefix.getByUrl(host);
        entity.prefix(StringUtils.isNotBlank(httpPrefix) ? httpPrefix : (HttpMethod.WS.equals(entity.method()) ? HttpPrefix.WS : HttpPrefix.HTTP));
        entity.host(StringUtils.isNotBlank(httpPrefix) ? host.substring(httpPrefix.length()) : host);
        entity.path(Arrays.stream(mappingUrl.split("/"))
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new)
        );
    }


    /**
     * 获取 Spring 参数注入的 host
     *
     * @param propertyKey 注入参数的 key
     * @param method      请求方法
     * @return
     */
    private static String propertyHost(String propertyKey, Method method) {
        if (null == EnvironmentComponent.INSTANCE) {
            return propertyKey;
        }
        //如果是spring的环境参数注入的host
        if ((propertyKey.startsWith("${")) && propertyKey.endsWith("}")) {
            String property = EnvironmentComponent.INSTANCE.getProperty(propertyKey.substring(2, propertyKey.length() - 1));
            if (StringUtils.isBlank(property)) {
                throw new HttpClientException("Property cannot be found by " + propertyKey, method);
            }
            return property;
        }
        return propertyKey;
    }
}
