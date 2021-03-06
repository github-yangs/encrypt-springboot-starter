package com.yangjq.encrypt.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangjq
 * @since 2022/5/27
 *
 * From请求包装类
 */
@Slf4j
public class FormRequestWrapper extends HttpServletRequestWrapper {

  /**
   * 存储解密后的参数
   */
  private Map<String, String[]> params = new HashMap<>();

  /**
   * Constructs a request object wrapping the given request.
   * package-private：该方法一般只在请求Filter中使用
   * @param request The request to wrap
   * @throws IllegalArgumentException if the request is null
   */
  FormRequestWrapper(HttpServletRequest request) {
    super(request);
    this.params.putAll(request.getParameterMap());
  }


  @Override
  public String getParameter(String name) {
    String[] values = params.get(name);
    if (values == null || values.length == 0){
      return null;
    }
    return values[0];
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return this.params;
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return Collections.enumeration(params.keySet());
  }

  @Override
  public String[] getParameterValues(String name) {
    return params.get(name);
  }

  /**
   * 包私有：package-private
   * @param params
   */
  void setParameterMap(Map<String, String[]> params) {
    this.params = params;
  }
}
