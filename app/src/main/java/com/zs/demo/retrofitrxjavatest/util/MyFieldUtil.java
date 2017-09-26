package com.zs.demo.retrofitrxjavatest.util;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by zs
 * Date：2017年 09月 22日
 * Time：11:24
 * —————————————————————————————————————
 * About:
 * —————————————————————————————————————
 */

public class MyFieldUtil {

    // 服务号
    public static String token = "QIBAIDUSSBDAPP";
    // 加密随机码
    public static String NONCE = "sdfsafdsa";
    // IM加密密钥
    public static String IMEncodingAESKey = "$IbaoDian$@SsBd@";

    /**
     * 校验签名
     *
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return token
     */
    public static String getSignature(String timestamp, String nonce) {
        String ciphertext = null;
        String result = null;
        try {
            // 对token、timestamp和nonce按字典排序
            String[] paramArr = new String[]{token, timestamp, nonce};
            Arrays.sort(paramArr);
            // 将排序后的结果拼接成一个字符串
            String content = paramArr[0].concat(paramArr[1])
                    .concat(paramArr[2]);
            // 对接后的字符串进行sha1加密
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());
            ciphertext = byteToStr(digest);
            return ciphertext;

        } catch (Exception e) {
            // JetSystem.log.error("接收客户端信息checkSignature出错",
            // e.getMessage());
        }
        return result;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    public static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    public static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

}
