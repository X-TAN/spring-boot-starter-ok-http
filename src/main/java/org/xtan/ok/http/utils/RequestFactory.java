package org.xtan.ok.http.utils;


import okhttp3.Request;
import org.xtan.ok.http.exception.HttpClientException;
import org.xtan.ok.http.model.HttpMethod;
import org.xtan.ok.http.model.HttpRequest;
import org.xtan.ok.http.wrapper.*;

/**
 * 请求构造的工厂
 *
 * @author: X-TAN
 * @date: 2021-08-05
 */
public class RequestFactory {

    private final HttpMethod method;
    private final HttpRequest params;
    private final String url;

    /**
     * 根据这个工厂生成对应okhttp的请求
     *
     * @param method http请求方法
     * @param params http请求参数
     * @param url    http请求url
     */
    public RequestFactory(HttpMethod method, HttpRequest params, String url) {
        this.method = method;
        this.params = params;
        this.url = url;
    }

    /**
     * 根据请求内容型实现请求体生成
     *
     * @return
     */
    public Request initRequest() {
        switch (method) {
            case PATCH:
                return new PatchRequestWrapper(url, params).create();
            case DELETE:
                return new DeleteRequestWrapper(url, params).create();
            case GET:
                return new GetRequestWrapper(url, params).create();
            case WS:
                return new WsRequestWrapper(url, params).create();
            case PUT:
                return new PutRequestWrapper(url, params).create();
            case POST:
                return new PostRequestWrapper(url, params).create();
            default: {
                throw new HttpClientException(String.format("不支持的请求方式: [%s]", method));
            }
        }
    }

}

