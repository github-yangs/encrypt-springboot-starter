package com.yangjq.encrypt.config;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.yangjq.encrypt.utils.AESUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * @Author yangjq
 * @Since 2022/5/26
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
@RequiredArgsConstructor
public class RequestDecryptFilter implements Filter {

  private final CryptoConfigAdapter configAdapter;

  /**
   * 用于判断是否需要解密的HEADER参数
   */
  private static final String AES_HEADER = "encrypt";

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws ServletException, IOException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String encrypt = request.getHeader(AES_HEADER);
    String contentType = request.getContentType();
    String method = request.getMethod();
    String url = request.getRequestURL().toString();

    Map<String, String[]> parameterMap = request.getParameterMap();

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

    if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)){
      //如果是Json提交

      //包装请求对象
      JsonRequestWrapper encryptedRequestWrapper = new JsonRequestWrapper(request);
      //获得加密的请求体
      String encryptData = encryptedRequestWrapper.getRequestBody();
      //解密
      String decryptData = AESUtil.decrypt(configAdapter.getAesKey(), encryptData);
      //重新放入请求中
      encryptedRequestWrapper.setRequestBody(decryptData);
      filterChain.doFilter(encryptedRequestWrapper,response);

    }else if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType)){
      //如果是Form(表单提交)

      //包装请求对象
      FormRequestWrapper formRequestWrapper = new FormRequestWrapper(request);
      //获得加密的参数
      Map<String, String[]> encryptParamMap = formRequestWrapper.getParameterMap();
      //解密
      Map<String, String[]> decryptParamMap = decryptParams(encryptParamMap);
      //重新放入请求中
      formRequestWrapper.setParametrMap(decryptParamMap);
      filterChain.doFilter(formRequestWrapper, response);
    }else {
      log.error("请求：{} 传入了不支持的解密类型", url);
      throw new IllegalArgumentException("不支持的解密类型");
    }

  }

  private Map<String, String[]> decryptParams(Map<String, String[]> encryptParamMap){
    Map<String, String[]> params = new HashMap<>(encryptParamMap.size()*2);
    SymmetricCrypto instance = AESUtil.getInstance(configAdapter.getAesKey());
    encryptParamMap.forEach((key, value) -> {
      String[] decryptData = Arrays.stream(value).map(item -> AESUtil.decrypt(instance, item)).toArray(String[]::new);
      params.put(key, decryptData);
    });
    return params;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void destroy() {

  }
}
