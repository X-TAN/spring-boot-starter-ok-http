package org.xtan.ok.http.wrapper;


import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.xtan.ok.http.model.HttpRequest;

/**
 * delete请求
 *
 * @author: X-TAN
 * @date: 2021-08-05
 */
public class DeleteRequestWrapper implements RequestWrapper {
    private final String url;
    private final HttpRequest params;

    public DeleteRequestWrapper(String url, HttpRequest params) {
        StringBuilder urlParams = params.getParams();
        if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(urlParams.toString())) {
            if (url.contains("?")) {
                url = url + "&" + urlParams;
            } else {
                url = url + "?" + urlParams;
            }
        }

        this.url = url;
        this.params = params;
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(params))
                .delete()
                .build();
    }
}