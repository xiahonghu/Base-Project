package com.xiahonghu.core.utils.encrypt;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * Author: sir.li
 * email:  lesli2@qq.com
 * Date:   2020/9/22
 */
public class DESEncrypt {

    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813，
     * 与public static byte[]hexStr2ByteArr(String strIn) 互为可逆的转换过程
     *
     * @param arrB 需要转换的byte数组
     * @return 转换后的字符串
     */
    private static String byteArr2HexStr(byte[] arrB) {
        int iLen = arrB.length;
        // 每个byte用2个字符才能表示，所以字符串的长度是数组长度的2倍
        StringBuilder sb = new StringBuilder(iLen * 2);
        for (int b : arrB) {
            int intTmp = b;
            while (intTmp < 0) intTmp = intTmp + 256;// 把负数转换为正数
            if (intTmp < 16) sb.append("0");// 小于0F的数需要在前面补0
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组
     * 与public static String byteArr2HexStr(byte[] arrB) 互为可逆的转换过程
     *
     * @param strIn 需要转换的字符串
     * @return 转换后的byte数组
     */
    private static byte[] hexStr2ByteArr(String strIn) {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }


    /**
     * 加密字符串
     *
     * @param strIn 需加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String strIn, String strKey) {
        try {
            Key key = getKey(strKey.getBytes());
            Cipher encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            return byteArr2HexStr(encryptCipher.doFinal(strIn.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解密字符串
     *
     * @param strIn 需解密的字符串
     * @return 解密后的字符串
     */
    public static String decrypt(String strIn, String strKey) {
        try {
            Key key = getKey(strKey.getBytes());
            Cipher decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            return new String(decryptCipher.doFinal(hexStr2ByteArr(strIn)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp 构成该字符串的字节数组
     * @return 生成的密钥
     */
    private static Key getKey(byte[] arrBTmp) {
        byte[] arrB = new byte[8];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) arrB[i] = arrBTmp[i];
        return new javax.crypto.spec.SecretKeySpec(arrB, "DES");
    }
}
