package org.xtan.ok.http.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * X-TAN
 *
 * @author: X-TAN
 * @date: 2021-08-05
 */
public class MD5 {
    /**
     * 获得MD5摘要算法的 MessageDigest 对象
     */
    private static MessageDigest mdInst = null;
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static MessageDigest getMdInst() {
        if (mdInst == null) {
            try {
                mdInst = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return mdInst;
    }

    public static String encode(String s) {
        try {
            byte[] btInput = s.getBytes();
            // 使用指定的字节更新摘要
            getMdInst().update(btInput);
            // 获得密文
            byte[] md = getMdInst().digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
