package org.xtan.ok.http.constants;

/**
 * http请求前缀
 *
 * @author: X-TAN
 * @date: 2021-08-09
 */
public interface HttpPrefix {

    /**
     * http
     */
    String HTTP = "http://";

    /**
     * https
     */
    String HTTPS = "https://";

    /**
     * ws
     */
    String WS = "ws://";

    /**
     * wss
     */
    String WSS = "wss://";

    /**
     * 前缀
     */
    class Prefix {
        /**
         * 获取前缀
         *
         * @param url 地址
         * @return
         */
        public static String getByUrl(String url) {
            if (url.startsWith(HTTP)) return HTTP;
            if (url.startsWith(HTTPS)) return HTTPS;
            if (url.startsWith(WS)) return WS;
            if (url.startsWith(WSS)) return WSS;
            return null;
        }
    }

}
