package com.aether.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * AES加解密工具
 */
public class AESUtils {
    private static final String IV_STRING = "A-16-Byte-String";
    private static final String charset = "UTF-8";
    // 转换模式
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String TRANSFORMATION_NOPADDING = "AES/ECB/NoPadding";
    public static byte[] baseAesSecretKeyKey = {
            (byte)0xB1,0x22,(byte)0xAD,0x0A,0x7A,0x36,0x2E,(byte)0xC5,(byte)0xAB,(byte)0xA1,(byte)0xDE,(byte)0xEF,(byte)0xB3,(byte)0xBF,0x49,0x15
    };

    public static byte[] remoteAesSecretKeyKey;

    public static byte[] aesSecretKeyKey;    //加密内容密钥的密钥
    public static byte[] aesContentKey;      //内容密钥

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            aesSecretKeyKey = generateKey();
        }catch (Exception e){
            aesSecretKeyKey = new byte[]{ (byte)0xB1,0x22,(byte)0xAD,0x0A,0x7A,0x36,0x2E,(byte)0xC5,(byte)0xAB,(byte)0xA1,(byte)0xDE,(byte)0xEF,(byte)0xB3,(byte)0xBF,0x49,0x15};
        }
        generateContentKey();
    }

    //生成SM4全局密钥密钥
    public static void generateContentKey(){
        Security.addProvider(new BouncyCastleProvider());
        try {
            aesContentKey = generateKey();
        }catch (Exception e){
            e.printStackTrace();
            aesContentKey = new byte[]{ (byte)0xB1,0x22,(byte)0xAD,0x0A,0x7A,0x36,0x2E,(byte)0xC5,(byte)0xAB,(byte)0xA1,(byte)0xDE,(byte)0xEF,(byte)0xB3,(byte)0xBF,0x49,0x15};
        }
    }

    //生成密钥
    public static byte[] generateKey() throws Exception{
        return generateKey(128);
    }

    public static byte[] generateKey(int keySize) throws Exception{
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(keySize,new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    public static byte[] getAesSecretKeyKey() {
        return aesSecretKeyKey;
    }

    public static byte[] getAesContentKey() {
        //测试代码 CJJ
        return AESUtils.baseAesSecretKeyKey;
        //return aesContentKey;
    }

    public static byte[] getRemoteAesSecretKeyKey() {
        if(remoteAesSecretKeyKey != null) {
            return remoteAesSecretKeyKey;
        }else{
            return baseAesSecretKeyKey;
        }
    }

    public static void setRemoteAesSecretKeyKey(byte[] remoteAesSecretKeyKey) {
        AESUtils.remoteAesSecretKeyKey = remoteAesSecretKeyKey;
    }

    public static String aesEncryptString(String content, String key){
        String encryptString = null;
        try {
            byte[] contentBytes = content.getBytes(charset);
            byte[] keyBytes = key.getBytes(charset);
            byte[] encryptedBytes = aesEncryptBytes(contentBytes, keyBytes);
            encryptString = Base64.encodeBase64String(encryptedBytes);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return encryptString;
    }

    public static String aesDecryptString(String content, String key) {
        String decryptString = null;
        try {
            byte[] encryptedBytes = Base64.decodeBase64(content);
            byte[] keyBytes = key.getBytes(charset);
            byte[] decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes);
            decryptString =  new String(decryptedBytes, charset);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return decryptString;
    }


    public static byte[] aesEncrypt(byte[] content, byte[] key){
        byte[] encryptedBytes = null;
        try {
            encryptedBytes = aesEncryptBytes(content, key);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return encryptedBytes;
    }

    public static byte[] aesDecrypt(byte[] content, byte[] key) {
        byte[] decryptedBytes = null;
        try {
            decryptedBytes = aesDecryptBytes(content, key);
        }catch (Exception e){

        }
        return decryptedBytes;
    }

    public static byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] bytes = cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE);
        int len;
        if ((len=contentBytes.length) % 16 == 0){
            bytes = Arrays.copyOf(bytes,len - 16);
        }
        return bytes;
    }

    public static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return cipherOperationNoPadding(contentBytes, keyBytes, Cipher.DECRYPT_MODE);
    }

    private static byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] initParam = IV_STRING.getBytes(charset);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, secretKey);
        byte[] bytes = cipher.doFinal(contentBytes);
        return bytes;
    }
    private static byte[] cipherOperationNoPadding(byte[] contentBytes, byte[] keyBytes, int mode) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] initParam = IV_STRING.getBytes(charset);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_NOPADDING);
        cipher.init(mode, secretKey);
        byte[] bytes = cipher.doFinal(contentBytes);
        return bytes;
    }

    public static void main(String[] args){
        String string = AESUtils.aesEncryptString("55555", "16BytesLengthKey");
        System.out.println(string);
        String decrypt = AESUtils.aesDecryptString(string, "16BytesLengthKey");
        System.out.println(decrypt);
    }
}
