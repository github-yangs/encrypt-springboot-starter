package com.yangjq.encrypt.config;

import cn.hutool.core.io.IoUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

/**
 * @Author yangjq
 * @Since 2022/5/25
 *
 * JSON请求包装类
 *
 * 1、通的参数可以从request的getParameterMap中获取，而@RequestBody的参数需要从request的InputStream中获取。
 * 2、但是InputStream只能读取一次，如果过滤器读取了参数，后面拦截器和controler层就读取不到参数了。可以把request封装一下，copy一份requet，一个用于在拦截器（过滤器）中读取参数，一个放行给controller使用
 * 3、修改request的body 的重点在于重写request的 getInputStream 这样在controller中@RequestBody时获取就是我们自定义的body值了
 */
@Slf4j
public class JsonRequestWrapper extends HttpServletRequestWrapper {

  /**
   * 请求体
   */
  private String requestBody;

  public JsonRequestWrapper(HttpServletRequest request) {
    super(request);
    try {
      ByteArrayOutputStream cachedBytes = new ByteArrayOutputStream();
      IoUtil.copy(super.getInputStream(), cachedBytes);
      requestBody = StreamUtils.copyToString(cachedBytes, Charset.forName("UTF-8"));
    } catch (IOException e) {
      log.error("请求体复制失败，原因：{}", e.getMessage(), e);
    }
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new CachedServletInputStream(requestBody.getBytes());
  }

  /**
   * 读取缓存的请求请求正文的输入流
   */
  public static class CachedServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream input;

    public CachedServletInputStream(byte[] buf) {
      // 从缓存的请求正文创建一个新的输入流
      input = new ByteArrayInputStream(buf);
    }

    @Override
    public boolean isFinished() {
      return false;
    }

    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
      return input.read();
    }
  }

  public String getRequestBody() {
    return requestBody;
  }

  public void setRequestBody(String requestBody) {
    this.requestBody = requestBody;
  }


}
