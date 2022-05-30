package com.yangjq.encrypt.utils;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author huanzi
 * RSA加、解密算法工具类
 * modifier: yangjq
 */
@Component
public class RsaUtil {

  /**
   * 加密算法AES
   */
  private static final String KEY_ALGORITHM = "RSA";

  /**
   * 算法名称/加密模式/数据填充方式
   * 默认：RSA/ECB/PKCS1Padding
   */
  private static final String ALGORITHMS = "RSA/ECB/PKCS1Padding";

  /**
   * 公钥的key
   */
  private static String PUBLIC_KEY_VALUE;

  /**
   * 私钥的key
   */
  private static String PRIVATE_KEY_VALUE;

  /**
   * RSA最大加密明文大小
   */
  private static final int MAX_ENCRYPT_BLOCK = 117;

  /**
   * RSA最大解密密文大小
   */
  private static final int MAX_DECRYPT_BLOCK = 128;

  /**
   * RSA 位数 如果采用2048 上面最大加密和最大解密则须填写:  245 256
   */
  private static final int INITIALIZE_LENGTH = 1024;


  /**
   * 生成密钥对(公钥和私钥)打印在控制台
   */
  public static void genKeyPair() throws Exception {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    keyPairGen.initialize(INITIALIZE_LENGTH);
    KeyPair keyPair = keyPairGen.generateKeyPair();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    System.out.println("公钥：" + Base64.encodeBase64String(publicKey.getEncoded()));
    System.out.println("私钥：" + Base64.encodeBase64String(privateKey.getEncoded()));
  }

  /**
   * 私钥解密
   *
   * @param encryptedData 已加密数据
   * @param privateKey    私钥(BASE64编码)
   */
  public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
    //base64格式的key字符串转Key对象
    byte[] keyBytes = Base64.decodeBase64(privateKey);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);

    Cipher cipher = Cipher.getInstance(ALGORITHMS);
    cipher.init(Cipher.DECRYPT_MODE, privateK);

    //分段进行解密操作
    return encryptAndDecryptOfSubsection(encryptedData, cipher, MAX_DECRYPT_BLOCK);
  }

  /**
   * 公钥加密
   *
   * @param data      源数据
   * @param publicKey 公钥(BASE64编码)
   */
  public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
    //base64格式的key字符串转Key对象
    byte[] keyBytes = Base64.decodeBase64(publicKey);
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    Key publicK = keyFactory.generatePublic(x509KeySpec);

    Cipher cipher = Cipher.getInstance(ALGORITHMS);
    cipher.init(Cipher.ENCRYPT_MODE, publicK);

    //分段进行加密操作
    return encryptAndDecryptOfSubsection(data, cipher, MAX_ENCRYPT_BLOCK);
  }

  /**
   * 注意这个方法是没有static关键字的！
   *
   * Value注解：注解需要Spring容器启动并扫描后才会注入，因此：
   * 1、需要在类上加@Component注解，这样才能被spring扫描到
   * 2、无法在main（）方法中测试，因为此时spring容器没有启动
   * @param publicKeyValue 公钥
   */
  @Value("${rsa.public_key}")
  public void setPublicKeyValue(String publicKeyValue) {
    PUBLIC_KEY_VALUE = publicKeyValue;
  }

  @Value("${rsa.private_key}")
  public void setPrivateKeyValue(String privateKeyValue) {
    PRIVATE_KEY_VALUE = privateKeyValue;
  }

  public static String getPublicKeyValue() {
    return PUBLIC_KEY_VALUE;
  }

  public static String getPrivateKeyValue() {
    return PRIVATE_KEY_VALUE;
  }

  /**
   * 分段进行加密、解密操作
   */
  private static byte[] encryptAndDecryptOfSubsection(byte[] data, Cipher cipher, int encryptBlock) throws Exception {
    int inputLen = data.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    byte[] cache;
    int i = 0;
    // 对数据分段加密
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > encryptBlock) {
        cache = cipher.doFinal(data, offSet, encryptBlock);
      } else {
        cache = cipher.doFinal(data, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      i++;
      offSet = i * encryptBlock;
    }
    byte[] toByteArray = out.toByteArray();
    out.close();
    return toByteArray;
  }

}
