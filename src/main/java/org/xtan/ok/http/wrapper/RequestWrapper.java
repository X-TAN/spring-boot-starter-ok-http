package org.xtan.ok.http.wrapper;

import okhttp3.Headers;
import okhttp3.Request;
import org.xtan.ok.http.model.HttpHeaders;
import org.xtan.ok.http.model.HttpRequest;


/**
 * @author cheng
 */
public interface RequestWrapper {

    /**
     * 默认的请求头构造器
     *
     * @param params
     * @return
     */
    default Headers initHeaders(HttpRequest params) {
        // 创建Headers
        HttpHeaders httpHeaders = params.getHeaders();
        Headers.Builder headerBuilder = new Headers.Builder();
        httpHeaders.foreach(headerBuilder::add);
        return headerBuilder.build();
    }

    /**
     * 请求构造
     *
     * @return
     */
    Request create();
}