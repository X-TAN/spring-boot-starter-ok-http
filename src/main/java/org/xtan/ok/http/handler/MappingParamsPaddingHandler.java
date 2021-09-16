package org.xtan.ok.http.handler;

import org.xtan.ok.http.OkBuilder;
import org.xtan.ok.http.annotation.Headers;
import org.xtan.ok.http.annotation.paramer.FileBody;
import org.xtan.ok.http.annotation.paramer.JSONBody;
import org.xtan.ok.http.annotation.paramer.Param;
import org.xtan.ok.http.annotation.paramer.PathParam;
import org.xtan.ok.http.exception.HttpClientException;
import org.xtan.ok.http.utils.XOptional;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

/**
 * 映射参数填充处理器
 *
 * @author: XOptional-TAN
 * @date: 2021-08-10
 */
public class MappingParamsPaddingHandler {

    /**
     * 参数填充
     *
     * @param okBuilder 请求构建器
     * @param method    请求方法
     * @param args      参数
     */
    public static void handler(OkBuilder okBuilder, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter it = parameters[i];
            if (it.isAnnotationPresent(PathParam.class) || it.isAnnotationPresent(Headers.class)) {
                continue;
            }
            if (it.isAnnotationPresent(JSONBody.class)) {
                okBuilder.isJson(args[i]);
                break;
            }
            if (it.isAnnotationPresent(FileBody.class)) {
                onlyFileParams(okBuilder, args[i], method);
                break;
            }
            if (!it.isAnnotationPresent(Param.class)) {
                throw new HttpClientException("parameter must have @Param annotation to declare field name!", method);
            }
            Param param = it.getAnnotation(Param.class);
            putParams(okBuilder, param.value(), args[i]);
        }
    }

    /**
     * 如果只是文件流请求
     *
     * @author: XOptional-TAN
     * @date: 2021-08-10
     */
    private static void onlyFileParams(OkBuilder okBuilder, Object arg, Method method) {
        if (arg instanceof File) {
            okBuilder.body((File) arg);
            return;
        }
        if (arg instanceof InputStream) {
            okBuilder.body((InputStream) arg);
            return;
        }
        if (arg instanceof byte[]) {
            okBuilder.body((byte[]) arg);
            return;
        }
        //被标识了onlyFile的请求必须只能有一个参数，而且参数必须是File or InputStream or byte[]
        throw new HttpClientException(
                "does not conform to the onlyFile setting, there must be only one parameter! And the parameter must be Param or InputStream or byte[]"
                , method);
    }

    /**
     * 常规参数解析设定
     *
     * @author: XOptional-TAN
     * @date: 2021-08-10
     */
    @SuppressWarnings("unchecked")
    private static void putParams(OkBuilder okBuilder, String paramName, Object arg) {
        if (arg instanceof File) {
            okBuilder.params(paramName, (File) arg);
            return;
        }
        if (arg instanceof InputStream) {
            okBuilder.params(paramName, (InputStream) arg);
            return;
        }
        if (arg instanceof byte[]) {
            okBuilder.params(paramName, (byte[]) arg);
            return;
        }
        if (arg instanceof String) {
            okBuilder.params(paramName, (String) arg);
            return;
        }
        if (arg instanceof Collection) {
            okBuilder.params(paramName, (Collection<? extends Serializable>) arg);
            return;
        }
        if (arg instanceof Integer) {
            okBuilder.params(paramName, (Integer) arg);
            return;
        }
        if (arg instanceof Boolean) {
            okBuilder.params(paramName, (Boolean) arg);
            return;
        }
        if (arg instanceof Double) {
            okBuilder.params(paramName, (Double) arg);
            return;
        }
        if (arg instanceof Float) {
            okBuilder.params(paramName, (Float) arg);
            return;
        }
        if (arg instanceof Character) {
            okBuilder.params(paramName, (Character) arg);
        }
    }
}
