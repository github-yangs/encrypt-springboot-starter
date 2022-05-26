package com.yangjq.encrypt.config;

import com.yangjq.encrypt.utils.AESUtil;
import com.yangjq.encrypt.utils.RedisUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @Author yangjq
 * @Since 2022/5/26
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class RequestDecryptFilter extends OncePerRequestFilter {

  /**
   * 用于判断是否需要解密的HEADER参数
   */
  private static final String AES_HEADER = "encrypt";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String encrypt = request.getHeader(AES_HEADER);
    String contentType = request.getContentType();
    String method = request.getMethod();
    String url = request.getRequestURL().toString();

    //判断是否需要解密，不需要直接返回
    if (!"true".equalsIgnoreCase(encrypt)){
      filterChain.doFilter(request, response);
      return;
    }

    if (!HttpMethod.POST.toString().equalsIgnoreCase(method)){
      log.error("URL: {} 解密失败! 暂不支持除了POST方法以外的解密", url);
      filterChain.doFilter(request,response);
      return;
    }

    if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)){
      log.error("URL: {} 解密失败! 暂不支持除了application/json以外的解密", url);
      filterChain.doFilter(request,response);
      return;
    }

    //开始解密
    //包装请求对象
    EncryptedRequestWrapper encryptedRequestWrapper = new EncryptedRequestWrapper(request);
    //获得加密的请求体
    String encryptData = encryptedRequestWrapper.getRequestBody();
    String decryptData = AESUtil.decrypt(RedisUtil.getKey("1"), encryptData);
    //重新放入请求中
    encryptedRequestWrapper.setRequestBody(decryptData);


    filterChain.doFilter(encryptedRequestWrapper,response);

  }
}
