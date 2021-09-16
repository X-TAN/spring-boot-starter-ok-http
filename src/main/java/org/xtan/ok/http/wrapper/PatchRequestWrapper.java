package org.xtan.ok.http.wrapper;

import okhttp3.Request;
import org.xtan.ok.http.model.HttpRequest;

/**
 * patch请求
 *
 * @author: X-TAN
 * @date: 2021-08-05
 */
public class PatchRequestWrapper extends PostRequestWrapper {

    public PatchRequestWrapper(String url, HttpRequest params) {
        super(url, params);
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(super.params))
                .patch(super.initBody())
                .build();
    }
}
