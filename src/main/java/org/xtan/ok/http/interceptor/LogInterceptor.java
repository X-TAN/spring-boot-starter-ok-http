package org.xtan.ok.http.interceptor;

import okhttp3.*;
import okio.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static com.alibaba.fastjson.util.IOUtils.UTF8;

/**
 * 日志拦截器
 *
 * @author: XOptional-TAN
 * @date: 2021-08-05
 */
public class LogInterceptor implements Interceptor {

    private final Pattern pattern = Pattern.compile("\\b(" + "text|html|xml|json|x-www-form-urlencoded" + ")\\b", Pattern.CASE_INSENSITIVE);

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        MediaType requestMediaType = null;
        //日志打印
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n");
        //如果请求内容不为空则尝试获取请求内容类型
        if (null != requestBody) requestMediaType = requestBody.contentType();
        //记录请求时间
        long startTime = System.currentTimeMillis();
        //请求
        Response response = chain.proceed(chain.request());
        //填充日志
        logBuilder.append(String.format("URL       --> %s\n", request.url()));
        logBuilder.append(String.format("TIME      --> %s.ms\n", System.currentTimeMillis() - startTime));
        logBuilder.append(String.format("METHOD    --> %s\n", request.method()));
        logBuilder.append(String.format("HEADERS   --> %s\n", headersLog(request, requestMediaType)));
        logBuilder.append(String.format("REQUEST   --> %s\n", requestBodyLog(requestBody, requestMediaType)));
        //获取返回信息
        ResponseBody responseBody = response.body();
        MediaType responseMediaType = null;
        byte[] resultBody = null;
        String resultStr = null;
        //获取返回内容，以及返回内容的类型
        if (null != responseBody) {
            responseMediaType = responseBody.contentType();
            resultBody = responseBody.bytes();
            //只打印特定类型的返回内容
            //检查是否有包含的关键字，忽略大小写
            if (null != responseMediaType
                    && pattern.matcher(responseMediaType.toString().toLowerCase()).find()) {
                resultStr = new String(resultBody);
            }
        }
        logBuilder.append(String.format("RESPONSE  --> %s\n", resultStr));
        response.close();
        log.info(logBuilder.toString());
        return response.newBuilder()
                .body(ResponseBody.create(responseMediaType, null != resultBody ? resultBody : new byte[0]))
                .build();
    }

    /**
     * 包装请求内容日志
     *
     * @param requestBody 请求内容
     * @param mediaType   请求内容类型
     * @return
     * @throws IOException
     */
    private String requestBodyLog(RequestBody requestBody, MediaType mediaType) throws IOException {
        String body = null;
        if (null != requestBody) {
            //如果是文件请求，固定日志打印
            if (null != mediaType && "form-data".equals(mediaType.subtype())) return "files";
            if (null != mediaType && "octet-stream".equals(mediaType.subtype())) return "file";
            //如果是字符串类型的请求
            if (null != mediaType && pattern.matcher(mediaType.toString().toLowerCase()).find()) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = mediaType.charset(UTF8);
                if (charset != null) {
                    body = buffer.readString(charset);
                    //请求内容超过2048长度的时候，忽略一部分内容
                    if (body.length() > 1024 * 2) body = body.substring(0, 1024 * 2);
                }
                // 如果是表单请求，则decode一下，规避中文乱码
                if (StringUtils.isNotBlank(body) && "x-www-form-urlencoded".equals(mediaType.subtype())) {
                    body = URLDecoder.decode(body, UTF8.name());
                    body = body.replaceAll("&", " | ");
                }
            }
        }
        return body;
    }

    /**
     * 格式化请求头日志打印
     *
     * @param request          请求
     * @param requestMediaType 请求内容类型
     * @return
     */
    private StringBuilder headersLog(Request request, MediaType requestMediaType) {
        StringBuilder headers = new StringBuilder();
        //格式化请求头打印
        if (null != requestMediaType) {
            headers.append("contentType:")
                    .append(requestMediaType.type()).append("/")
                    .append(requestMediaType.subtype()).append(" | ");
        }
        request.headers().toMultimap().forEach((k, v) -> headers.append(String.format("%s:%s | ", k, v.get(0))));
        return headers;
    }
}