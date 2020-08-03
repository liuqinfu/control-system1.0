package utils;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
/**
 * liuqf
 */
public class SM2Utils {
    //生成随机秘钥对
    public static void generateKeyPair(){
        SM2 sm2 = SM2.Instance();
        AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
        BigInteger privateKey = ecpriv.getD();
        ECPoint publicKey = ecpub.getQ();

        System.out.println("公钥: " + Util.byteToHex(publicKey.getEncoded()));
        System.out.println("私钥: " + Util.byteToHex(privateKey.toByteArray()));
    }

    //数据加密
    public static String encrypt(byte[] publicKey, byte[] data) throws IOException
    {
        if (publicKey == null || publicKey.length == 0)
        {
            return null;
        }

        if (data == null || data.length == 0)
        {
            return null;
        }

        byte[] source = new byte[data.length];
        System.arraycopy(data, 0, source, 0, data.length);

        Cipher cipher = new Cipher();
        SM2 sm2 = SM2.Instance();
        ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

        ECPoint c1 = cipher.Init_enc(sm2, userKey);
        cipher.Encrypt(source);
        byte[] c3 = new byte[32];
        cipher.Dofinal(c3);

//      System.out.println("C1 " + Util.byteToHex(c1.getEncoded()));
//      System.out.println("C2 " + Util.byteToHex(source));
//      System.out.println("C3 " + Util.byteToHex(c3));
        //C1 C2 C3拼装成加密字串
        return Util.byteToHex(c1.getEncoded()) + Util.byteToHex(source) + Util.byteToHex(c3);

    }

    //数据解密
    public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) throws IOException
    {
        if (privateKey == null || privateKey.length == 0)
        {
            return null;
        }

        if (encryptedData == null || encryptedData.length == 0)
        {
            return null;
        }
        //加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
        String data = Util.byteToHex(encryptedData);
        /***分解加密字串
         * （C1 = C1标志位2位 + C1实体部分128位 = 130）
         * （C3 = C3实体部分64位  = 64）
         * （C2 = encryptedData.length * 2 - C1长度  - C2长度）
         */
        byte[] c1Bytes = Util.hexToByte(data.substring(0,130));
        int c2Len = encryptedData.length - 97;
        byte[] c2 = Util.hexToByte(data.substring(130,130 + 2 * c2Len));
        byte[] c3 = Util.hexToByte(data.substring(130 + 2 * c2Len,194 + 2 * c2Len));

        SM2 sm2 = SM2.Instance();
        BigInteger userD = new BigInteger(1, privateKey);

        //通过C1实体字节来生成ECPoint
        ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
        Cipher cipher = new Cipher();
        cipher.Init_dec(userD, c1);
        cipher.Decrypt(c2);
        cipher.Dofinal(c3);

        //返回解密结果
        return c2;
    }

    public static void main(String[] args) throws Exception
    {
        //生成密钥对
//        generateKeyPair();

        String plainText = "qwertyuiop123x234dsadasda";
        byte[] sourceData = plainText.getBytes();

        //下面的秘钥可以使用generateKeyPair()生成的秘钥内容
        // 国密规范正式私钥
        String prik = "03566D1A30E04EC04E24B0E86EFC30591E2A3F156C5B72727AD05F3366B61604";
        // 国密规范正式公钥
        String pubk = "0443E758D901971B23B8A7B2BC07A13CFF75991BD529821EB5BB7518ABB09C023C910FC5361EC3E9E5066222EE3659C77841C7BF042368656573D5220A0A092E86";

        System.out.println("加密: ");
        String cipherText = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);
        System.out.println(cipherText);
        System.out.println("解密: ");
        cipherText ="044D246A88DE0616DD9B22F5CCBDAE065171AFB72A2CDF189023C4EFD742E63F724049E1548E4E3624921496C7C7740F9BE271BFCD75B8F3FDFD7B524343D67F60B323060C845FB6CC114991C5FD8632010A4323AFFE4F542574A2674D6D1D43937CB86CBD035560E83F";
        plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText)));
        System.out.println(plainText);

    }

    /*public static void main(String[] args) throws Exception {
        // 国密规范正式私钥
//        String prik = "3690655E33D5EA3D9A4AE1A1ADD766FDEA045CDEAA43A9206FB8C430CEFE0D94";
        String prik = "0A41D3C7B54482973C42C11D7FA1459006CD2BC3ECDF1515DC5F0BF89DEDAF21";

        // 国密规范正式公钥
//        String pubk = "04F6E0C3345AE42B51E06BF50B98834988D54EBC7460FE135A48171BC0629EAE205EEDE253A530608178A98F1E19BB737302813BA39ED3FA3C51639D7A20C7391A";
        String pubk = "040DACF0142EFB8C4378F9D2A32CC93FEF752F8E68ADA57C8F6AA0768EE0B2ECD0C18E087350D0A2799B441BD5918080C11AC4C04ED6BA29B6340D6EA188813DB3";

        String plainText2 = "ererfeiisgod231231231";
        System.out.println("原文："+plainText2);
        byte[] sourceData2 = plainText2.getBytes();
        System.out.println("加密: ");
        String cipherText2 = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData2);
        System.out.println();
        System.out.println(cipherText2);
        System.out.println("解密: ");
        plainText2 = new String(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText2)));
        System.out.println(plainText2);

    }*/
}
