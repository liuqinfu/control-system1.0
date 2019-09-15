package com.aether.common.utils.SM2SM4;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SM4Utils {
    /* public static byte[] secretKey = {
             (byte)0xB1,0x22,(byte)0xAD,0x0A,0x7A,0x36,0x2E,(byte)0xC5,(byte)0xAB,(byte)0xA1,(byte)0xDE,(byte)0xEF,(byte)0xB3,(byte)0xBF,0x49,0x15
     };*/
    public static byte[] cbcIv = {
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00
    };

    public static byte[] sm4SecretKey;
    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            sm4SecretKey = generateKey();
        }catch (Exception e){
            sm4SecretKey = new byte[]{ (byte)0xB1,0x22,(byte)0xAD,0x0A,0x7A,0x36,0x2E,(byte)0xC5,(byte)0xAB,(byte)0xA1,(byte)0xDE,(byte)0xEF,(byte)0xB3,(byte)0xBF,0x49,0x15};
        }
    }

    public SM4Utils() {
    }

    public static byte[] getSm4SecretKey() {
        return sm4SecretKey;
    }

    public static void setSm4SecretKey(byte[] sm4SecretKey) {
        SM4Utils.sm4SecretKey = sm4SecretKey;
    }

    //生成SM4全局密钥密钥
    public static void generateSm4Key(){
        Security.addProvider(new BouncyCastleProvider());
        try {
            sm4SecretKey = generateKey();
        }catch (Exception e){
            sm4SecretKey = new byte[]{ (byte)0xB1,0x22,(byte)0xAD,0x0A,0x7A,0x36,0x2E,(byte)0xC5,(byte)0xAB,(byte)0xA1,(byte)0xDE,(byte)0xEF,(byte)0xB3,(byte)0xBF,0x49,0x15};
        }
    }

    /**
     * 加密
     * @param plainText
     * @return
     */
    public static String encryptData_ECB(String plainText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, sm4SecretKey);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes("UTF-8"));
            String cipherText = new BASE64Encoder().encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param cipherText
     * @return
     */
    public static String decryptData_ECB(String cipherText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, sm4SecretKey);
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, new BASE64Decoder().decodeBuffer(cipherText));
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 加密
     * @param plainText
     * @return
     */
    public static byte[] encryptByteData_ECB(byte[] plainText,byte[] sm4SecretKey) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, sm4SecretKey);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText);
            return encrypted;
          /*  String cipherText = new BASE64Encoder().encode(plainText);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;*/
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param decode
     * @return
     */
    public static byte[] decryptByteData_ECB(byte[] decode,byte[] sm4SecretKey) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] iv = new byte[16];
            System.arraycopy(cbcIv, 0, iv, 0, 16);
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, sm4SecretKey);
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, decode);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密
     * @param plainText
     * @return
     */
    public static byte[] encryptByteData_CBC(byte[] plainText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] iv = new byte[16];
            System.arraycopy(cbcIv, 0, iv, 0, 16);
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, sm4SecretKey);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, iv,plainText);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密
     * @param plainText
     * @return
     */
    public static byte[] encryptByteData_CBC(byte[] plainText,byte[] secureKey) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] iv = new byte[16];
            System.arraycopy(cbcIv, 0, iv, 0, 16);
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, secureKey);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, iv,plainText);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param decode
     * @return
     */
    public static byte[] decryptByteData_CBC(byte[] decode) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] iv = new byte[16];
            System.arraycopy(cbcIv, 0, iv, 0, 16);
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, sm4SecretKey);
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx,iv,decode);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param decode
     * @return
     */
    public static byte[] decryptByteData_CBC(byte[] decode, byte[] secretKey) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] iv = new byte[16];
            System.arraycopy(cbcIv, 0, iv, 0, 16);
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, secretKey);
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx,iv,decode);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

       /* SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        return key.getEncoded();*/
    }



    public static byte[] decryptByteData_CBC_tets(byte[] decode) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] iv = new byte[16];
            System.arraycopy(cbcIv, 0, iv, 0, 16);
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, Util.hexToByte("01028258B3F023D2D3103C97AAB37CA4"));
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx,iv,decode);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*public static void main(String[] args) {

      *//*  String plainText = "{\"cmdid\":\"login\",\"username\":\"15019285715\",\"password\":\"123456\"}";

        SM4Utils sm4 = new SM4UUtils();
//        sm4.secretKey = "JeF8U9wHFOMfs2Y8";
//        sm4.hexString = false;

        System.out.println("ECB模式");
        String cipherText = sm4.encryptData_ECB(plainText);
        System.out.println("密文: " + cipherText);
        System.out.println("");

        plainText = sm4.decryptData_ECB(cipherText);
        System.out.println("明文: " + plainText);
        System.out.println("");*//*

//        System.out.println("CBC模式");
//        sm4.iv = "UISwD9fW6cFh9SNS";
//        cipherText = sm4.encryptData_CBC(plainText);
//        System.out.println("密文: " + cipherText);
//        System.out.println("");
//
//        plainText = sm4.decryptData_CBC(cipherText);
//        System.out.println("明文: " + plainText);

        // 包头的长度
        *//*byte [] abc = SM4Utils.encryptByteData_CBC("aabbccdd".getBytes());
        byte [] def = SM4Utils.decryptByteData_CBC(abc);
        String jiemi = Util.byteToString(def);
        byte [] aaaaaaa = SM4Utils.decryptByteData_CBC_tets(Util.hexToByte("ec435e652f5f18867f71219d6012863dd91d887b154ecebc744bfd32f68ca3ad754f71f5bdbe61b2c71a58be73318bf7cddd78ef034096f5e733e95407a47c01303d3d2d43f60617cdedb9bae2e8798dc0523d6e2030d364c20a41110725eca77012ed14f337c209addce61d71c5ed32851413083b6ecf11cec997310c949c1608bdbe804d36291f03310cd7c9d13f34286e16a31993ce69bbcf8350d09f005f478fda40ffdd42343daffa2dca68d666accb74d13d81ccc8fca533ecb74494952cbab26b73a6c5a478b54729572d495bee38f2a168fdc95426bc08eda69501e08b0078fa40def41a4fb7446b322d89e08b4fe82d36194b63c7a3dd871e9161dd32bbcfebc076d085a0efe80d58b9e291b041c51d6dffcbc44b2e26a4a234d433"));

        String hext = Util.byteToHex(aaaaaaa);
        System.out.println(hext);
        int debug = 0;*//*
        String value="000400097472616465436f64650004383030350012726f7574657252657153657269616c4e756d00123030303030303030303030303030303031310011726f75746572526571526f75746572496400206378473153514331364d31552d3665323534642d3030304334333536303745320019726f75746572557365724571704d61634e6574537461747573008c2c33383a62633a31613a64643a34353a36382c4d65697a752d4d58342d50726f2e6c616e2c307c2c66343a65333a66623a65303a63303a62352c2c307c2c65303a63373a36373a34363a62303a61382c79616f64696e67777579616e7a752e6c616e2c327c2c66343a65333a66623a65303a63303a62352c4855415745495f4d6174655f372e6c616e2c327c20202020202020";
        String secureKey = RandomStringUtils.randomAlphanumeric(16);
//        String secureKey="01028258B3F023D2D3103C97AAB37CA4";
        //加密

        byte[] dataEncryptBySM4 = SM4Utils.encryptByteData_CBC(Util.hexToByte(value),secureKey.getBytes());
        //解密
//        String secureValue="ec435e652f5f18867f71219d6012863dd91d887b154ecebc744bfd32f68ca3ad754f71f5bdbe61b2c71a58be73318bf7cddd78ef034096f5e733e95407a47c01303d3d2d43f60617cdedb9bae2e8798dc0523d6e2030d364c20a41110725eca77012ed14f337c209addce61d71c5ed32851413083b6ecf11cec997310c949c1608bdbe804d36291f03310cd7c9d13f34286e16a31993ce69bbcf8350d09f005f478fda40ffdd42343daffa2dca68d666accb74d13d81ccc8fca533ecb74494952cbab26b73a6c5a478b54729572d495bee38f2a168fdc95426bc08eda69501e08b0078fa40def41a4fb7446b322d89e08b4fe82d36194b63c7a3dd871e9161dd32bbcfebc076d085a0efe80d58b9e291b041c51d6dffcbc44b2e26a4a234d433";
        String secureValue=Util.byteToHex(dataEncryptBySM4);
        byte[] dataBytes = SM4Utils.decryptByteData_CBC(Util.hexToByte(secureValue), Util.hexToByte(secureKey));
        String hext = Util.byteToHex(dataBytes);
        System.out.println(hext);
    }*/
}
