package {service.namespace}.utils.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {service.namespace}.utils.http.HttpUtils.Header;
import {service.namespace}.utils.http.HttpUtils.ProxyConfig;
{customcode.import}

public class HttpURLConnectionBuilder {
  
  {customcode.start}
  
  private static Logger LOG = LoggerFactory.getLogger(HttpURLConnectionBuilder.class);

  private URL url;
  private String requestMethod = "";
  private ProxyConfig proxyConfig;
  private String body = "";
  private Header[] headers;

  public String getRequestMethod() {
    return requestMethod;
  }

  public HttpURLConnectionBuilder requestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
    return this;
  }

  public HttpURLConnectionBuilder get() {
    this.requestMethod = "GET";
    return this;
  }

  public HttpURLConnectionBuilder post() {
    this.requestMethod = "POST";
    return this;
  }

  public URL getUrl() {
    return url;
  }

  public HttpURLConnectionBuilder url(URL url) {
    this.url = url;
    return this;
  }

  public ProxyConfig getProxyConfig() {
    return proxyConfig;
  }

  public HttpURLConnectionBuilder proxyConfig(ProxyConfig proxyConfig) {
    this.proxyConfig = proxyConfig;
    return this;
  }

  public String getBody() {
    return this.body;
  }

  public HttpURLConnectionBuilder body(String body) {
    this.body = body;
    return this;
  }

  public Header[] getHeaders() {
    return headers;
  }

  public HttpURLConnectionBuilder headers(Header... headers) {
    this.headers = headers;
    return this;
  }

  public HttpURLConnection build() {

    try {
      return HttpUtils.buildHttpURLConnection(getUrl(), getProxyConfig(), getRequestMethod(),
          getBody(), getHeaders());
    } catch (IOException e) {
      LOG.error("Failed while building HttpURLConnectiuon for url=" + getUrl() + " and proxyConfig="
          + proxyConfig + " and requestMethod=" + requestMethod + " and body=" + body
          + " and Headers=" + headers);
    }
    return null;

  }

  public HttpURLConnectionBuilder url(String urlString) {
    try {
      return url(new URL(urlString));
    } catch (MalformedURLException e) {
      LOG.error("MalformedURLException " + e.getMessage() + " in url >" + urlString + "<");
      throw new RuntimeException("MalformedURLException " + e.getMessage() + " in url '" + urlString + "'",e);
    }
  }
  
  {customcode.end}
}
