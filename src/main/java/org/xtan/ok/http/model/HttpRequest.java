package org.xtan.ok.http.model;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Http请求的参数集合
 *
 * @author: XOptional-TAN
 * @date: 2021-08-05
 */
public class HttpRequest {
    /**
     * 参数容器
     */
    private final Map<String, Object> paramsEntries = new HashMap<>(8);

    /**
     * 请求头容器
     */
    private final HttpHeaders mHttpHeaders = HttpHeaders.builder();

    /**
     * 文件流
     */
    private byte[] bytes = null;

    /**
     * 是否是文件请求
     */
    private boolean hasFile;

    /**
     * 请求内容类型
     */
    private String contentType = null;

    /**
     * 是否是json请求内容
     */
    private boolean isJson;

    /**
     * json请求实体
     */
    private Object bodyObj = null;

    /**
     * hasFile 为true时使用才会生效
     * 是否分片（当请求内容为文件的时候才会生效）
     */
    private Boolean chunked = Boolean.FALSE;

    public boolean isHasFile() {
        return hasFile;
    }

    public byte[] getBytes() {
        return null == this.bytes ? getParams().toString().getBytes() : this.bytes;
    }

    public void putHeaders(HttpHeaders header) {
        mHttpHeaders.add(header);
    }

    public void putHeaders(final String key, final String value) {
        mHttpHeaders.add(key, value);
    }

    public void put(final String key, final int value) {
        this.put(key, value + "");
    }

    /**
     * 添加文本参数
     */
    public void put(final String key, final String value) {
        paramsEntries.put(key, value);
    }


    /**
     * 添加文件参数,可以实现文件上传功能
     */
    public void put(final String key, final File file) {
        hasFile = true;
        paramsEntries.put(key, file);
    }

    public void put(final String key, final byte[] file) {
        hasFile = true;
        paramsEntries.put(key, file);
    }

    public void put(final String key, final InputStream file) {
        hasFile = true;
        paramsEntries.put(key, file);
    }

    public void put(byte[] bytes) {
        try {
            hasFile = true;
            this.bytes = bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getParams() {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;

        for (Map.Entry<String, Object> entry : paramsEntries.entrySet()) {
            if (entry.getValue() instanceof File) {
                continue;
            }
            if (entry.getValue() instanceof byte[]) {
                continue;
            }
            if (!isFirst) {
                result.append("&");
            } else {
                isFirst = false;
            }
            try {
                result.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), "utf8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public Map<String, Object> getParamsEntries() {
        return paramsEntries;
    }

    public HttpHeaders getHeaders() {
        return mHttpHeaders;
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean json) {
        isJson = json;
    }

    /**
     * 直接返回请求的内容的json对象
     *
     * @return String
     */
    public String getJsonBody() {
        if (isJson) {
            return null == bodyObj ? JSON.toJSONString(paramsEntries) : JSON.toJSONString(bodyObj);
        } else {
            return null;
        }
    }

    public void setJsonBody(Object obj) {
        this.bodyObj = obj;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setChunked() {
        this.chunked = Boolean.TRUE;
    }

    public boolean isChunked() {
        return chunked;
    }
}
