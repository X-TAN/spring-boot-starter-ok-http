package org.xtan.ok.http.constants;

public interface HttpContentType {

    /**
     * 无请求类型
     */
    String NO = "";

    /**
     * 单文件上传
     */
    String FILE = "application/octet-stream";

    /**
     * 表单/多文件上传
     */
    String MULTIPART_FILE = "multipart/form-data";

    /**
     * JSON请求
     */
    String JSON = "application/json";

    /**
     * 表单请求
     */
    String FORM = "application/x-www-form-urlencoded";
}
