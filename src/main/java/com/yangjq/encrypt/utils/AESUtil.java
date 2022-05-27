package com.yangjq.encrypt.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * @Author yangjq
 * @Since 2022/5/24
 *
 * AES 对称算法工具类
 */
public class AESUtil {

  /**
   * 生成Base64加密的AES的key
   * @return
   */
  public static String generateKey(){
    byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
    return Base64.encode(key);
  }

  /**
   * 获得加密对象实例
   * @param key
   * @return
   */
  public static SymmetricCrypto getInstance(String key){
    return new SymmetricCrypto(SymmetricAlgorithm.AES, Base64.decode(key));
  }

  /**
   * AES 加密
   * @param key 密钥
   * @param content 待加密的数据
   * @return 加密后的数据
   */
  public static String encrypt(String key, String content){
    return getInstance(key).encryptBase64(content);
  }

  /**
   * AES 解密
   * @param key 密钥
   * @param encodeData 待解密的数据
   * @return 解密后的数据
   */
  public static String decrypt(String key, String encodeData){
    return getInstance(key).decryptStr(encodeData, CharsetUtil.CHARSET_UTF_8);
  }

  /**
   * AES 解密
   * @param aes
   * @param encodeData
   * @return
   */
  public static String decrypt(SymmetricCrypto aes, String encodeData){
    return aes.decryptStr(encodeData, CharsetUtil.CHARSET_UTF_8);
  }


}
