package com.aether.common.utils.blowfish;


import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;

/**

 * BlowFish 算法用来加密 64Bit 长度的字符串

 * 用 BlowFish 算法加密信息,需要两个过程:

 * 1.密钥预处理

 * 2.信息加密

 * 加密数据要补齐8的倍数

 * by tester 2010-11-04

 */

public class BlowfishUtils {

    public static byte[] blowfishKey;
    public static byte[] blowfishIv;
    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            blowfishKey = generateKey();
        }catch (Exception e){
            blowfishKey = new byte[]{0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef, (byte)0xf0, (byte)0xe1, (byte)0xd2, (byte)0xc3, (byte)0xb4, (byte)0xa5, (byte)0x96, (byte)0x87};
        }
    }

    static {
        Random m_rndGen = new Random();
        long bfIv = m_rndGen.nextLong();
        blowfishIv = new byte[8];
        longToByteArray(bfIv,blowfishIv,0);
    }

    // 转换模式
    public static final String TRANSFORMATION = "Blowfish/CBC/NoPadding";

    // 密钥算法名称
    public static final String BLOWFISH = "Blowfish";

    //生成密钥
    public static byte[] generateKey() throws Exception{
        return generateKey(128);
    }

    public static byte[] generateKey(int keySize) throws Exception{
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(keySize,new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    private static void longToByteArray(long lValue, byte buffer[], int nStartIndex) {
        buffer[nStartIndex] = (byte) (int) (lValue >>> 56);
        buffer[nStartIndex + 1] = (byte) (int) (lValue >>> 48 & 255L);
        buffer[nStartIndex + 2] = (byte) (int) (lValue >>> 40 & 255L);
        buffer[nStartIndex + 3] = (byte) (int) (lValue >>> 32 & 255L);
        buffer[nStartIndex + 4] = (byte) (int) (lValue >>> 24 & 255L);
        buffer[nStartIndex + 5] = (byte) (int) (lValue >>> 16 & 255L);
        buffer[nStartIndex + 6] = (byte) (int) (lValue >>> 8 & 255L);
        buffer[nStartIndex + 7] = (byte) (int) lValue;
    }

    public static String encrypt(byte key[], byte text[], byte initializationVector[])
            throws Exception {
        text = padding(text);
        // 根据给定的字节数组构造一个密钥  Blowfish-与给定的密钥内容相关联的密钥算法的名称
        SecretKeySpec sksSpec = new SecretKeySpec(key, BLOWFISH);
        // 使用 initializationVector 中的字节作为 IV 来构造一个 IvParameterSpec 对象
        AlgorithmParameterSpec iv = new IvParameterSpec(initializationVector);
        // 返回实现指定转换的 Cipher 对象
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // 用密钥和随机源初始化此 Cipher
        cipher.init(Cipher.ENCRYPT_MODE, sksSpec, iv);
        // 加密文本
        byte[] encrypted = cipher.doFinal(text);
        return new String(Util.byteToHex(encrypted));
    }


    public static String decrypt(byte key[], byte text[], byte initializationVector[])
            throws Exception {
        SecretKeySpec skeSpect = new SecretKeySpec(key, BLOWFISH);
        AlgorithmParameterSpec iv = new IvParameterSpec(initializationVector);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, skeSpect, iv);
        byte[] decrypted = cipher.doFinal(text);
        return new String(decrypted);

    }

    //blowfish 要补齐8的倍数
    private static byte[] padding(byte[] input) {
        if (input == null) {
            return null;
        }
        byte[] ret;
        if (input.length % 8 != 0) {
            int p = 8 - input.length % 8;
            ret = new byte[input.length + p];
            System.arraycopy(input, 0, ret, 0, input.length);
            for (int i = 0; i < p; i++) {
                ret[input.length + i] = 0x20;
                //补0x20
            }
        } else {
            //8倍数 不用补0x20
            ret = input;
        }
        return ret;
    }


    public static String base64Decoder(String base64String) {
        if(StringUtils.isEmpty(base64String)){
            return base64String;
        } else {
            return new String(Base64.decode(base64String));
        }
    }


    public static String base64Encoder(String sourceString) {
        if(StringUtils.isEmpty(sourceString)) {
            return sourceString;
        } else {
            return Base64.toBase64String(sourceString.getBytes());
        }
    }

    /*public static void main(String[] args) {
        try {
            byte[] src = "abcdefgh1234".getBytes();
            String encryptEx = encrypt(blowfishKey, src, blowfishIv);
            System.out.println("加密后得到："+encryptEx);


            byte[] bytes = BytesUtils.hexToBytes(encryptEx);
//            String decryptStr1 = decrypt(blowfishKey, Util.hexToByte(encryptEx), blowfishIv);
            String decryptStr1 = decrypt(blowfishKey, bytes, blowfishIv);
            System.out.print("解密后得到："+decryptStr1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

}