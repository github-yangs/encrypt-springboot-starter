package com.yangjq.encrypt.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yangjq
 * @Since 2022/5/27
 */
public interface CryptoConfigAdapter {

  String AES_KEY = "aes_key";

  String getAesKey(HttpServletRequest request, HttpServletResponse response);

}
