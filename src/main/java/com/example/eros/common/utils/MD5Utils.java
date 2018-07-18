package com.example.eros.common.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * MD5生成工具类
 * 利用shiro自带的生成,也是使用MessageDigest加密
 */
public class MD5Utils {

    public static final String SALT = "2altgltv3";

    public static final String ALGORITH_NAME = "md5";

    public static final int HASH_ITERATION = 2;

    public static String encrypt(String password) {
        String newPassword = new SimpleHash(ALGORITH_NAME,password, ByteSource.Util.bytes(SALT),HASH_ITERATION).toHex();
        return newPassword;
    }


    public static String encrypt(String username, String password) {
        String newPassword = new SimpleHash(ALGORITH_NAME,password, ByteSource.Util.bytes(username + SALT),HASH_ITERATION).toHex();
        return newPassword;
    }

}
