package org.xtan.ok.http;


import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.xtan.ok.http.exception.HttpClientException;
import org.xtan.ok.http.interceptor.GzipRequestInterceptor;
import org.xtan.ok.http.interceptor.LogInterceptor;
import org.xtan.ok.http.model.HttpHeaders;
import org.xtan.ok.http.model.HttpMethod;
import org.xtan.ok.http.model.HttpRequest;
import org.xtan.ok.http.utils.FileUtil;
import org.xtan.ok.http.utils.RequestFactory;
import org.xtan.ok.http.utils.SSLUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * okHttp的工具类，建造者模式
 *
 * @author: XOptional-TAN
 * @date: 2021-08-05
 */
public final class OkBuilder {

    /**
     * 请求地址
     */
    private final String url;

    /**
     * 请求参数
     */
    private final HttpRequest request = new HttpRequest();

    /**
     * 是否开启gzip
     */
    private boolean isGzip = false;

    /**
     * 请求方式
     */
    private HttpMethod method = HttpMethod.GET;

    private OkBuilder(String url) {
        this.url = url;
    }

    /**
     * 构建实例
     *
     * @param url
     * @return
     */
    public static OkBuilder url(String url) {
        return new OkBuilder(url);
    }

    /**
     * 填充请求方式
     *
     * @param method
     * @return
     */
    public OkBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * 是否开启gzip
     *
     * @param isGzip
     * @return
     */
    public OkBuilder gzip(boolean isGzip) {
        this.isGzip = isGzip;
        return this;
    }

    /**
     * 填充请求头
     *
     * @param key   key
     * @param value value
     * @return
     */
    public OkBuilder header(String key, String value) {
        request.putHeaders(key, value);
        return this;
    }

    /**
     * 添加请求头 headers
     *
     * @param headers
     * @return
     */
    public OkBuilder headers(HttpHeaders headers) {
        request.putHeaders(headers);
        return this;
    }

    /**
     * 定义内容传输类型
     *
     * @param contentType
     * @return
     */
    public OkBuilder contentType(String contentType) {
        request.setContentType(contentType);
        return this;
    }

    public OkBuilder params(String key, String value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, Collection<? extends Serializable> value) {
        request.put(key, value.toString());
        return this;
    }

    public OkBuilder params(String key, int value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, long value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, boolean value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, float value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, double value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, char value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, File value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, byte[] value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, InputStream value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder body(File file) {
        return body(FileUtil.fileToByteArray(file));
    }

    public OkBuilder body(InputStream stream) {
        return body(FileUtil.streamToByteArray(stream));
    }

    public OkBuilder body(byte[] file) {
        request.put(file);
        return this;
    }

    public OkBuilder transferEncoding() {
        if (HttpMethod.GET.equals(method)) {
            throw new HttpClientException("http method can not be get!");
        }
        request.setChunked();
        request.putHeaders("Transfer-Encoding", "chunked");
        return this;
    }

    /**
     * 使用map集合填充参数
     *
     * @param map
     * @return
     */
    public OkBuilder request(Map<String, Object> map) {
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                request.put(stringObjectEntry.getKey(), stringObjectEntry.getValue() + "");
            }
        }
        return this;
    }

    /**
     * 定义请求为JSON请求
     *
     * @return
     */
    public OkBuilder isJson(Object obj) {
        request.setJson(Boolean.TRUE);
        request.setJsonBody(obj);
        return this;
    }

    /**
     * 开启jsonPost请求
     *
     * @return
     */
    public OkBuilder isJson() {
        return this.isJson(null);
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    public String execute() {
        try (Response response = beforeExecute()) {
            if (null != response) {
                ResponseBody body = response.body();
                if (null != body) return body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构造ws的请求
     *
     * @return
     */
    public WebSocket initWs(WebSocketListener listener) {
        if (StringUtils.isBlank(url) || !(url.contains("ws://") || url.contains("wss://"))) {
            throw new HttpClientException("Url can not be supported !");
        }
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                //设置读取超时时间
                .readTimeout(3, TimeUnit.SECONDS)
                //设置写的超时时间
                .writeTimeout(3, TimeUnit.SECONDS)
                //设置连接超时时间
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();

        //构造请求
        return mOkHttpClient.newWebSocket(initRequest(), listener);
    }


    /**
     * 构造带有ping-pong的ws请求
     *
     * @return
     */
    public WebSocket initPingWs(WebSocketListener listener) {
        if (StringUtils.isBlank(url) || !(url.contains("ws://") || url.contains("wss://"))) {
            throw new HttpClientException("Url can not be supported !");
        }
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                //设置读取超时时间
                .readTimeout(3, TimeUnit.SECONDS)
                //设置写的超时时间
                .writeTimeout(3, TimeUnit.SECONDS)
                //设置连接超时时间
                .connectTimeout(3, TimeUnit.SECONDS)
                .pingInterval(40, TimeUnit.SECONDS)
                .build();

        //构造请求
        return mOkHttpClient.newWebSocket(initRequest(), listener);
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    public InputStream executeStream() {
        try (Response response = beforeExecute()) {
            if (null != response) {
                ResponseBody body = response.body();
                if (null != body) return body.byteStream();
            }
        }
        return null;
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    public byte[] executeBytes() {
        try (Response response = beforeExecute()) {
            if (null != response) {
                ResponseBody body = response.body();
                if (null != body) return body.bytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步执行，并返回请求响应内容
     *
     * @return
     */
    private Response response() {
        try (Response response = beforeExecute()) {
            return response;
        }
    }


    private Response beforeExecute() {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("Url can not be null!");
        }
        //构造请求
        Request request = initRequest();
        Response response;
        try {
            if (isGzip) {
                response = new OkHttpClient.Builder()
                        .addInterceptor(new GzipRequestInterceptor())
                        .sslSocketFactory(SSLUtil.createSSLSocketFactory(), new SSLUtil.TrustAllManager())
                        .hostnameVerifier(new SSLUtil.TrustAllHostnameVerifier())
                        .addInterceptor(new LogInterceptor())
                        .build()
                        .newCall(request)
                        .execute();
            } else {
                response = new OkHttpClient.Builder()
                        .sslSocketFactory(SSLUtil.createSSLSocketFactory(), new SSLUtil.TrustAllManager())
                        .hostnameVerifier(new SSLUtil.TrustAllHostnameVerifier())
                        .addInterceptor(new LogInterceptor())
                        .build()
                        .newCall(request)
                        .execute();
            }
            return response;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 构造请求，根据请求类型不同，此处应该采用工厂模式
     *
     * @return
     */
    private Request initRequest() {
        return new RequestFactory(method, request, url).initRequest();
    }
}
