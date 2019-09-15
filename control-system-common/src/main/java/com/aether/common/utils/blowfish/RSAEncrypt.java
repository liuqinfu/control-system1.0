
package com.aether.common.utils.blowfish;


import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
    /*public static void main(String[] args) throws Exception {
        try {
            //生成公钥和私钥
            genKeyPair();
//            String pubKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1JI8pFcd9zeg2/kbB4cbgcMOleJf5cXAdY41inswRrGB+I146jrt606QaEhAk0s9BdkMJX4FhWiYqG4IXdmfdKJ75XfXNe3ldomxiSzlsv0gemwYUA+/TImwyB/momzLKO67+jBZRazXgVMuVLfUAsswq8pHHMRcRiVsNik0ieQIDAQAB";
            String priKey="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJr+x4mAIFVRrdYOE2ShOIGM3G41/+zmKDcn2IUccaiVo34ulkOdCekxJTPXl051qBPGjNuYPWNxnHY+UUhRkZrw9n6G8DqLPcPltKWAIRUTqNnSvyG9005/cZgwUJVDereQyIFV5xIidlnRs9DjmLEfY//W/ds+x3O21cHpVBDJAgMBAAECgYA5JxWg6DQEAfhd5BbWlnRloPFvWk4ipsPRgsneW5y1k2gUEU2rZO7QpCSZtze94Li3ghFSsBKC5tPQ7i2+wwUzTzms/72XS1rOmuEMfSIVdkrQjMBoxtQB6Ml0CFQ5pe8c0FHWSrmU/P9xJSwVNm4QjYXgQREodwYQq6xZL+auAQJBAN5Eam4vgu/b4RhMin0Z5kNHm+C8UEBb0KY+YUxFKCmSGmKwxM6bCrxf4HxvzlfPI0hwTSLUSxetCQaOuc7F6KkCQQCyhLGrRj8g/DYBl+jcWyk3eMryszQPI4qt4TvqFIP7M3AUUpupbiSGRBZtNwSzoEr/7DNGwo7Q6OKhOVtoVVshAkA+1Xp9AcNzSWad9u1jplG5DWJcqv/I4evfQ11AMYDEF8CLy8snWR8016nfo7/ihPVFgjHcOcWswMb/apPOh5ZZAkBcXPNf3nSDKHr40BQXDsOAg+rdL4Lb4lO6KzLIpdyzd6WhniwTuX0ESu4fi7vPBDxJtXfCQJ+LzzKSoA5/KMqBAkA/n9385fsFk4OaXmeBkfB96x1fv6r0WS3B2e0B1hjWmxIspGHO8z/zPmgTJhnyjlCv5aOxAOoqUr4yc+DkZv4B";
            //加密字符串
            *//*String message = "df723820";
            System.out.println("随机生成的公钥为:" + keyMap.get(0));
            System.out.println("随机生成的私钥为:" + keyMap.get(1));
            String messageEn = encrypt(message,keyMap.get(0));
//            String messageEn = encrypt(message,pubKey);
            System.out.println(message + "\t加密后的字符串为:" + messageEn);*//*
            String messageEn = "7F993B287F865C4331AE417B886A054D7D2D304C7970831FEFAB9039DD9A97255CF190E22578FD0B5BA8D6F78E6F0FFAF6DFC71A02B7B9188D4DE9D1E137D8C44C7739ED77DBB8B79F3F7CC96C3A24159653F9EE4F02523371C0A0CA294D8FF197B94C1B113ED83C297295C732249DBDC9E7EDEF6800C878A9B55D41C92A517C";
//            String messageDe = decrypt(messageEn,keyMap.get(1));
            byte[] decode = Base64.decode(priKey);
            byte[]  messageDebyteArr = decryptToByteArr(messageEn,priKey);
//            String messageDe = decrypt(messageEn,priKey);
            System.out.println("还原后的字符串为:" + BytesUtils.bcdToString(messageDebyteArr));

//            encrypt_test("111111","aaaaaa");
//
//            decrypt_test("","");
        }catch (Exception e){
            System.out.println("error:" + e.toString());
            System.out.println("error:" + e.toString());
        }

    }*/

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encode((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        //Cipher cipher = Cipher.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.toBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static byte[] encryptToByteArr( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        //Cipher cipher = Cipher.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(Util.hexToByte(str));
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
//        byte[] inputByte = Base64.decode(str.getBytes("UTF-8"));
        byte[] inputByte = Util.hexToByte(str);
        //base64编码的私钥
        byte[] decoded = Base64.decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static byte[] decryptToByteArr(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
//        byte[] inputByte = Base64.decode(str.getBytes("UTF-8"));
        byte[] inputByte = Util.hexToByte(str);
        //base64编码的私钥
        byte[] decoded = Base64.decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(inputByte);
    }

    public static String decrypt_test(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Util.hexToByte("B11EC817405C210545D873BCA9D9631CAAEE7C4357B9EB58FC7C9AE85951BCA7FE5E85711B4B17D79A13ADBAE0265CF2BED676C22BAB4512F74292102762F672475689BF2B0E8ED4899FF267A4818D6232E8C15D67425E6BAB9C0927ABAB9F764617C6BB3E96F25C1836B05CF7F80105E7D55D57AAE8C731449064517E835611");
        //base64编码的私钥
        byte[] decoded = Base64.decode("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALM+ZEmnZNvbkfalLYB6MYAJoRi71v0TMhXxM/ilWTemmcudtQrYqjKGkBzpeV99I3H5r+Ejlm2/4TH833Ha6xGZrPY96PlKXCwMyYA/uQYcPDoFexkKfLMWBMQ45yr/BHhriQD4I/C0dmGNpvj3dBXWG8T2Hja1f1k8U40rG+VjAgMBAAECgYBPGrrx0oFtkjKR0oYJTGQqKJXDL6sK13zkOCYClx6Ps+Pt+7TZi5NZZw+U4ukg3j5VX6diSe1qlCMTh5EpUz8oL5AyPocfcGGsxp8pQRVGGZTn1BfTBQ0QXpgJ9PWBhc7LPwADJLfAa/ZQmmE7r4bvyR3x+9iJqAyc2zjbWBggKQJBAPTeEH5NLpaJ50V0vA7Rn/Ow0RvWVaSoorc4lB1iT2UCikng6nZYZkk0J6AH/0MgAZxvz6OKpU+0bIR9q9BchLUCQQC7ZI2n8kRoxaC/Zq6cI5W3b7zY+KKbCBG3/ITSPt41uxOnLosj4s8Y237c+Tq3v5BX9mdlsK7/02VgqeTsAei3AkAX3h1Vckh8O+zjBEHgX+ZrZmy7feTTK+zQVx94zcBXZCv+j4HLD0rJD+Bc5OqorFH74ZonorgCUN/S+jzpDluRAkB8OKf+0QawPXHV74eIUb6JnzSc0BVal/rG8EpL5QkArUbV6HCsFUobvEVYzo4yCHbzGJBqRLl9NPFbJsRt/sX5AkEAxLVHfmfws0MyGNlpfArut/nNJJ2IuEiCdP2ZpT5yf71/JffzVbfOXigi2ibYj4H5wI9dk9Sp5P63ajdSQ7PuWg==");
        String priKeyString = Util.getHexString(decoded);
        String pubKeyString = Util.getHexString(Base64.decode("MIGJAoGBAPoqnmwYNyatn/CMTJQ88OSAkoXx+fTZpv8QkcdSWLoqIHtqVqDQFKem oEUGGdOUsP+2GeDWukZGE0tHF/9+VwkkQgsSdhJ8s5SVa6IOOf/xJ9v/9tss1fCH l+E+qnBBua2ULl3smEAjji7bqsHVJ3zHaZHmdGXLnAlS/8tkKzkhAgMBAAE="));

        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
        //Cipher cipher = Cipher.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }


    public static String encrypt_test( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClWxcBX6N34jkrGo4kHBd7B5Z+m6yFQc4UkVCek09YA6TMad+l16LuiNTMvVdFmKXscuCO6ZKM3G/snQFMOcNYPMjg4UtIZwUyRXpkWa0DU1X4C2Q5nmKcaD46GdG6iXfGvUkPPXp5SuSk4j0+688YLiu+p21cXP0eOSEqb9A5+QIDAQAB");
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        //Cipher cipher = Cipher.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] encrypt = cipher.doFinal(str.getBytes("UTF-8"));
        String hexString = Util.byteToHex(encrypt);
        String outStr = Base64.toBase64String(encrypt);
        return outStr;
    }


}