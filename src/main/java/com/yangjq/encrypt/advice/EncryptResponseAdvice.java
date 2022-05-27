package com.yangjq.encrypt.advice;

import cn.hutool.json.JSONUtil;
import com.yangjq.encrypt.annotation.EncryptAnno;
import com.yangjq.encrypt.config.CryptoConfigAdapter;
import com.yangjq.encrypt.utils.AesUtil;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author yangjq
 * @Since 2022/5/26
 */
@ControllerAdvice
public class EncryptResponseAdvice implements ResponseBodyAdvice {

  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return returnType.hasMethodAnnotation(EncryptAnno.class);
  }

  @SneakyThrows
  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request,
      ServerHttpResponse response) {
    return AesUtil.encrypt(JSONUtil.toJsonStr(body), getAesKey());
  }

  private String getAesKey(){
    HttpServletRequest request = ((ServletRequestAttributes) Objects
        .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    return (String) request.getAttribute(CryptoConfigAdapter.AES_KEY);
  }

}
