package com.xiahonghu.core.utils.encrypt;


import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {

    /**
     * 生成公钥与私钥
     *
     * @return privateKey publicKey
     */
    public static Map<String, String> genKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
            Base64.Encoder base64Encoder = Base64.getEncoder();
            Map<String, String> json = new HashMap<>();
            json.put("publicKey", new String(base64Encoder.encode(publicKey.getEncoded())));
            json.put("privateKey", new String(base64Encoder.encode((privateKey.getEncoded()))));
            return json;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * RSA 公钥加密
     *
     * @param str       带价密字符串
     * @param publicKey 公钥
     * @return 解密结果
     */
    public static String encrypt(String str, String publicKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return new String(Base64.getEncoder().encode(cipher.doFinal(str.getBytes())));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * RSA 私钥解密
     *
     * @param str        带解密字符串
     * @param privateKey 私钥
     * @return 解密参数
     */
    public static String decrypt(String str, String privateKey) {
        try {
            byte[] inputByte = Base64.getDecoder().decode(str.getBytes());
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            return null;
        }
    }
}
