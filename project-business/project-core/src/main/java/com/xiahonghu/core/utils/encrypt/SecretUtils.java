package com.xiahonghu.core.utils.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Created by Tony on 2017/9/20.
 */
public class SecretUtils {
    private static final byte[] codes = new byte[256];
    static {
        for (int i = 0; i < 256; i++)
            codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++)
            codes[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; i++)
            codes[i] = (byte) (26 + i - 'a');
        for (int i = '0'; i <= '9'; i++)
            codes[i] = (byte) (52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }
    private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    /**
     * 字符串加密以及解密函数
     *
     * @param  $string(string)	原文或者密文
     * @param  $operation（string）	操作(ENCODE | DECODE), 默认为 DECODE
     * @param  $key	(string)	密钥
     * @param  $expiry（int）		密文有效期, 加密时候有效， 单位 秒，0 为永久有效
     * @return string		处理后的 原文或者 经过 base64_encode 处理后的密文
     * @example
     * 	$a = authCode('abc', 'ENCODE', 'key');
     * 	$b = authCode($a, 'DECODE', 'key');//$b(abc)
     * 	$a = authCode('abc', 'ENCODE', 'key', 3600);
     * 	$b = authCode('abc', 'DECODE', 'key');//在一个小时内，$b(abc)，否则 $b 为空
     */
    private static String authCode(String $string, String $operation, String $key,int $expiry ) {

        if($string != null){
            if($operation.equals("DECODE")){
                try{
                    $string = $string.replaceAll("\\.0\\.", " ");
                    $string = $string.replaceAll("\\.1\\.", "=");
                    $string = $string.replaceAll("\\.2\\.", "+");
                    $string = $string.replaceAll("\\.3\\.", "/");
                }catch(Exception ignored){}
            }
        }
        int $ckey_length = 4;	//note 随机密钥长度 取值 0-32;
        //note 加入随机密钥，可以令密文无任何规律，即便是原文和密钥完全相同，加密结果也会每次不同，增大破解难度。
        //note 取值越大，密文变动规律越大，密文变化 = 16 的 $ckey_length 次方
        //note 当此值为 0 时，则不产生随机密钥

        $key = md5( $key!=null ? $key : "123456");
        assert $key != null;
        String $keya = md5(substr($key, 0, 16));
        String $keyb = md5(substr($key, 16, 16));
        String $keyc;
        if ($operation.equals("DECODE")) {
            assert $string != null;
            $keyc = substr($string, 0, $ckey_length);
        } else {
            $keyc = substr(md5(microtime()), -$ckey_length);
        }

        String $cryptkey = $keya + md5( $keya + $keyc);
        int $key_length = $cryptkey.length();

        $string = $operation.equals("DECODE") ? base64_decode(substr($string, $ckey_length)) : sprintf("%010d", $expiry>0 ? $expiry + time() : 0)+substr(Objects.requireNonNull(md5($string + $keyb)), 0, 16)+$string;
        int $string_length = $string.length();

        StringBuilder $result1 = new StringBuilder();

        int[] $box = new int[256];
        for(int i=0;i<256;i++){
            $box[i] = i;
        }
        int[] $rndkey = new int[256];
        for(int $i = 0; $i <= 255; $i++) {
            $rndkey[$i] = (int)$cryptkey.charAt($i % $key_length);
        }

        int $j=0;
        for(int $i = 0; $i < 256; $i++) {
            $j = ($j + $box[$i] + $rndkey[$i]) % 256;
            int $tmp = $box[$i];
            $box[$i] = $box[$j];
            $box[$j] = $tmp;
        }

        $j=0;
        int $a=0;
        for(int $i = 0; $i < $string_length; $i++) {
            $a = ($a + 1) % 256;
            $j = ($j + $box[$a]) % 256;
            int $tmp = $box[$a];
            $box[$a] = $box[$j];
            $box[$j] = $tmp;

            $result1.append((char)( ((int)$string.charAt($i)) ^ ($box[($box[$a] + $box[$j]) % 256])));

        }

        if($operation.equals("DECODE")) {
            String $result = $result1.substring(0, $result1.length());
            if((Integer.parseInt(substr($result, 0, 10)) == 0 || Long.parseLong(substr($result, 0, 10)) - time() > 0) && substr($result, 10, 16).equals(substr(Objects.requireNonNull(md5(substr($result, 26) + $keyb)), 0, 16))) {
                return substr($result, 26);
            } else {
                return "";
            }
        } else {
            String str = $keyc+base64_encode($result1.toString());
            try{
                str = str.replaceAll(" ",".0.");
                str = str.replaceAll("=",".1.");
                str = str.replaceAll("\\+",".2.");
                str = str.replaceAll("/",".3.");
            }catch(Exception ignored){}
            return str;
        }
    }
    private static String md5(String input){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return byte2hex(md.digest(input.getBytes()));
    }
    private static String md5(long input){
        return md5(String.valueOf(input));
    }
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (byte value : b) {
            stmp = (Integer.toHexString(value & 0XFF));
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString();
    }
    private static String substr(String input,int begin, int length){
        return input.substring(begin, begin+length);
    }
    private static String substr(String input,int begin){
        if(begin>0){
            return input.substring(begin);
        }else{
            return input.substring(input.length()+ begin);
        }
    }
    private static long microtime(){
        return System.currentTimeMillis();
    }
    private static long time(){
        return System.currentTimeMillis()/1000;
    }
    private static String sprintf(String format, long input){
        String temp = "0000000000"+input;
        return temp.substring(temp.length()-10);
    }
    private static String base64_decode(String input){
        try {
            return new String(decode(input.toCharArray()), StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public static byte[] decode(char[] data) {
        int tempLen = data.length;
        for (char datum : data) {
            if ((datum > 255) || codes[datum] < 0)
                --tempLen;
        }
        int len = (tempLen / 4) * 3;
        if ((tempLen % 4) == 3)
            len += 2;
        if ((tempLen % 4) == 2)
            len += 1;
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (char datum : data) {
            int value = (datum > 255) ? -1 : codes[datum];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ((accum >> shift) & 0xff);
                }
            }
        }
        if (index != out.length) {
            throw new Error("Miscalculated data length (wrote " +
                    index + " instead of " + out.length + ")");
        }
        return out;
    }
    private static String base64_encode(String input){
        try {
            return new String(encode(input.getBytes(StandardCharsets.ISO_8859_1)));
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public static char[] encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;

            int val = (0xFF & data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index] = alphabet[val & 0x3F];
        }
        return out;
    }
    private static final String ENCODE = "ENCODE";
    private static final String DECODE = "DECODE";
    public static String expiryEncrypt(String $text,String $salt,Integer $expiry){
        return authCode($text,ENCODE,$salt,$expiry);
    }
    public static String encrypt(String $text,String $salt){
        return expiryEncrypt($text, $salt,0);
    }
    public static String decrypt(String $encData,String $salt){
        return authCode($encData,DECODE,$salt,0);
    }
}



