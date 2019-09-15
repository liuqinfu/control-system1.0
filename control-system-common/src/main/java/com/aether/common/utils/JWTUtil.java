package com.aether.common.utils;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.code.RspSuccessResult;
import com.aether.common.finals.CodeFinals;
import com.aether.common.finals.JWTError;
import com.aether.common.finals.PubFinals;
import com.auth0.jwt.Algorithm;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

/**
 * @author liuqinfu
 */
@Slf4j
public class JWTUtil {


    //验证
    public static RspResult check(String auth_str) {

        RspFailResult rsp = new RspFailResult();
        String authnumber = PubFinals.AUTH_RANDOMNUMBER;
        JWTVerifier jv = new JWTVerifier(authnumber);

        Map<String, Object> map = null;
        try {
            //如果验证成功，继续执行子类Controll的方法，Catch失败则直接返回失败码
            if (auth_str != null && auth_str != "") {
                map = jv.verify(auth_str);
                return new RspSuccessResult();
            } else {
                //获取不到头信息，直接返回无权限操作
                log.error("can`t get header");
                return new RspFailResult(CodeFinals.AUTH_ILLEAGAL_REQ);
            }

        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
            return new RspFailResult();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            return new RspFailResult();
        } catch (IllegalStateException e) {
            //返回非法信息；
            log.error(e.getMessage());
            return new RspFailResult(CodeFinals.AUTH_ILLEAGAL_REQ);
        } catch (SignatureException e) {
            log.error("signature verification failed");
            if (JWTError.SIGNATURE_VERIFICATION_FAILED.equals(e.getMessage())) {
                //签名验证失败,返回非法信息
                return new RspFailResult(CodeFinals.AUTH_ILLEAGAL_REQ);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return new RspFailResult();
        } catch (JWTVerifyException e) {
            log.error("jwt expired");
            if (JWTError.JWT_EXPIRED.equals(e.getMessage())) {
                //令牌已过期,返回非法信息
                return new RspFailResult(CodeFinals.AUTH_FAIL_CODE);
            }
        }
        log.error("no reason but failed");
        return rsp;
    }

    //获取令牌串里面的用户名
    public static String getUsername(String auth_str) {
        String authnumber = PubFinals.AUTH_RANDOMNUMBER;
        JWTVerifier jv = new JWTVerifier(authnumber);

        Map<String, Object> map = null;
        try {
            //如果验证成功，继续执行
            if (auth_str != null && auth_str != "") {
                map = jv.verify(auth_str);
                String username = map.get(PubFinals.AUTH_KEY_USER).toString();
                return username;
            } else {
                //获取不到头信息，直接返回null
                return null;
            }

        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            return null;
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return null;
        } catch (SignatureException e) {
            if (JWTError.SIGNATURE_VERIFICATION_FAILED.equals(e.getMessage())) {
                //签名验证失败,返回非法信息
                log.error(e.getMessage());
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        } catch (JWTVerifyException e) {
            if (JWTError.JWT_EXPIRED.equals(e.getMessage())) {
                //令牌已过期,返回非法信息
                log.error(e.getMessage());
                return null;
            }
        }

        return null;
    }


    /***
     * 获取令牌串里面的IIDD
     * @param auth_str
     * @return iidd
     */
    public static String getUserIIDD(String auth_str) {

        JWTVerifier jv = new JWTVerifier(PubFinals.AUTH_RANDOMNUMBER);

        Map<String, Object> map = null;
        try {
            //如果验证成功，继续执行
            if (auth_str != null && auth_str != "") {
                map = jv.verify(auth_str);
                String iidd = map.get(PubFinals.AUTH_KEY_USER_IIDD).toString();
                return iidd;
            } else {
                //获取不到头信息，直接返回null
                return null;
            }

        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            return null;
        } catch (IllegalStateException e) {
            //返回非法信息；
            log.error(e.getMessage());
            return null;
        } catch (SignatureException e) {
            if (JWTError.SIGNATURE_VERIFICATION_FAILED.equals(e.getMessage())) {
                //签名验证失败,返回非法信息
                log.error(e.getMessage());
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        } catch (JWTVerifyException e) {
            if (JWTError.JWT_EXPIRED.equals(e.getMessage())) {
                //令牌已过期,返回非法信息
                log.error(e.getMessage());
                return null;
            }
        }

        return null;
    }

    /***
     * 获取令牌串里面的用户类型
     * @param auth_str
     * @return iidd
     */
    public static String getUserRole(String auth_str) {
        log.warn("auth_code:"+auth_str);
        JWTVerifier jv = new JWTVerifier(PubFinals.AUTH_RANDOMNUMBER);

        Map<String, Object> map = null;
        try {
            //如果验证成功，继续执行
            if (auth_str != null && auth_str != "") {
                map = jv.verify(auth_str);
                String type = map.get(PubFinals.AUTH_KEY_USER_ROLE).toString();
                return type;
            } else {
                //获取不到头信息，直接返回null
                return null;
            }

        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            return null;
        } catch (IllegalStateException e) {
            //返回非法信息；
            log.error(e.getMessage());
            return null;
        } catch (SignatureException e) {
            if (JWTError.SIGNATURE_VERIFICATION_FAILED.equals(e.getMessage())) {
                //签名验证失败,返回非法信息
                log.error(e.getMessage());
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        } catch (JWTVerifyException e) {
            if (JWTError.JWT_EXPIRED.equals(e.getMessage())) {
                //令牌已过期,返回非法信息
                log.error(e.getMessage());
                return null;
            }
        }

        return null;
    }

    /**
     * @param map_auth 传入map进行加密
     * @return 默认令牌失效时间为 30*24*60*60秒
     */
    public static String sign(Map<String, Object> map_auth) {
        //获取系统字典表
        com.auth0.jwt.JWTSigner jwt = new com.auth0.jwt.JWTSigner(PubFinals.AUTH_RANDOMNUMBER);
        com.auth0.jwt.JWTSigner.Options opt = new com.auth0.jwt.JWTSigner.Options();
        opt.setAlgorithm(Algorithm.HS256);
        opt.setExpirySeconds(30 * 24 * 60 * 60);//超时时间，30天
//        opt.setExpirySeconds(10 * 60);//超时时间，10分钟

        //map_auth.put(SystemFinal.AUTH_KEY_USER,username);
        String auth_return_str = jwt.sign(map_auth, opt);
        return auth_return_str;

    }

    /**
     * @param map_auth      传入map进行加密
     * @param ExpirySeconds 几秒后令牌失效
     * @return
     */
    public static String sign(Map<String, Object> map_auth, int ExpirySeconds) {
        //获取系统字典表
        com.auth0.jwt.JWTSigner jwt = new com.auth0.jwt.JWTSigner(PubFinals.AUTH_RANDOMNUMBER);
        com.auth0.jwt.JWTSigner.Options opt = new com.auth0.jwt.JWTSigner.Options();
        opt.setAlgorithm(Algorithm.HS256);
        opt.setExpirySeconds(ExpirySeconds);//超时时间，120秒

        //map_auth.put(SystemFinal.AUTH_KEY_USER,username);
        String auth_return_str = jwt.sign(map_auth, opt);
        return auth_return_str;

    }

    public static String sign(Map<String, Object> map_auth, JWTSigner.Options opt) {
        //获取系统字典表
        com.auth0.jwt.JWTSigner jwt = new com.auth0.jwt.JWTSigner(PubFinals.AUTH_RANDOMNUMBER);

        String auth_return_str = jwt.sign(map_auth, opt);
        return auth_return_str;

    }

}
