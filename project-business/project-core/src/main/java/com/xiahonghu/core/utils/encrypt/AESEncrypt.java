package com.xiahonghu.core.utils.encrypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;
import java.util.Random;

public class AESEncrypt {

    static {
        //如果是PKCS7Padding填充方式，则必须加上下面这行(为保证前后端一致性建议使用PKCS7Padding方式，也可以使用PKCS5Padding则可以不加)
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 创建Key
     * @return aesKey
     */
    public static String initKey(){
        String str="yzABcdCfgDqrEvwFGpsHIoxJKtuLhiMejNOklPQmnRSabTUX01VW23456YZ789";
        Random random=new Random();
        StringBuilder sb=new StringBuilder();
        while (sb.length()<16) sb.append(str.charAt(random.nextInt(str.length())));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(initKey());
    }

    /**
     * AES加密
     * @param plaintext 目标文字
     * @param aesKey 密码
     * @return 结果
     */
    public static String encrypt(String plaintext, String aesKey) {
        try {
            SecretKeySpec sKeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception ex) {
            return null;
        }
    }



    /**
     * AES解密
     * @param cipherText 密文
     * @param aesKey   密钥
     * @return 明文
     */
    public static String decrypt(String cipherText, String aesKey) {
        try {
            SecretKeySpec sKeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
            byte[] encrypted = Base64.getDecoder().decode(cipherText); //先用base64解密
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 进行MD5加密
     * @param text 要进行MD5转换的字符串
     * @return 该字符串的MD5值
     */
    public static String MD5(String text) throws NoSuchAlgorithmException {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = text.getBytes();
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;
        for (byte byte0 : md) {
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
