package com.yangjq.encrypt.advice;

import cn.hutool.json.JSONUtil;
import com.yangjq.encrypt.annotation.EncryptAnno;
import com.yangjq.encrypt.utils.AESUtil;
import com.yangjq.encrypt.utils.RedisUtil;
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
public class EncryptResponse implements ResponseBodyAdvice {

  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return returnType.hasMethodAnnotation(EncryptAnno.class);
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request,
      ServerHttpResponse response) {

    return AESUtil.encrypt(RedisUtil.getKey("1"), JSONUtil.toJsonStr(body));
  }
}
