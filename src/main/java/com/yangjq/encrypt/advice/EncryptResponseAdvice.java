package com.yangjq.encrypt.advice;

import cn.hutool.json.JSONUtil;
import com.yangjq.encrypt.annotation.EncryptAnno;
import com.yangjq.encrypt.config.CryptoConfigAdapter;
import com.yangjq.encrypt.utils.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author yangjq
 * @Since 2022/5/26
 */
@ControllerAdvice
@RequiredArgsConstructor
public class EncryptResponseAdvice implements ResponseBodyAdvice {

  private final CryptoConfigAdapter configAdapter;

  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return returnType.hasMethodAnnotation(EncryptAnno.class);
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request,
      ServerHttpResponse response) {
    //获取AES密钥

    return AESUtil.encrypt(configAdapter.getAesKey(), JSONUtil.toJsonStr(body));
  }

}
