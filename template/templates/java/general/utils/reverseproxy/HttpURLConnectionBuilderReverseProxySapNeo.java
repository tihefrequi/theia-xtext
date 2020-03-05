package {service.namespace}.utils.reverseproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import {service.namespace}.utils.http.HttpURLConnectionBuilder;
import {service.namespace}.utils.http.HttpUtils;
import {service.namespace}.utils.http.HttpUtils.Header;
import {service.namespace}.utils.http.HttpUtils.ProxyConfig;
import {service.namespace}.utils.sap.cloud.http.HttpURLConnectionBuilderSapNeo;
import {service.namespace}.utils.reverseproxy.ReverseProxyUtils;
{customcode.import}

public class HttpURLConnectionBuilderReverseProxySapNeo {

  private static final Logger LOG =
      LoggerFactory.getLogger(HttpURLConnectionBuilderReverseProxySapNeo.class);

  {customcode.start}

  private String urlAddon;
  private String destination;
  private Optional<Header[]> customHeaders = Optional.empty();
  private String body = "";
  private String requestMethod = "";
  private ProxyConfig proxyConfig = HttpUtils.proxyNone();

  public ProxyConfig getProxyConfig() {
    return proxyConfig;
  }

  public HttpURLConnectionBuilderReverseProxySapNeo proxyConfig(ProxyConfig proxyConfig) {
    this.proxyConfig = proxyConfig;
    return this;
  }

  public String getDestination() {
    return destination;
  }

  public HttpURLConnectionBuilderReverseProxySapNeo destination(String destination) {
    this.destination = destination;
    return this;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public HttpURLConnectionBuilderReverseProxySapNeo requestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
    return this;
  }

  public String getUrlAddon() {
    return urlAddon;
  }

  public HttpURLConnectionBuilderReverseProxySapNeo urlAddon(String urlAddon) {
    this.urlAddon = urlAddon;
    return this;
  }

  public String getBody() {
    return this.body;
  }

  public HttpURLConnectionBuilderReverseProxySapNeo body(String body) {
    this.body = body;
    return this;
  }

  public Optional<Header[]> getHeaders() {
    if (customHeaders.isPresent()) {
      return customHeaders;
    }
    return Optional.empty();
  }

  public HttpURLConnectionBuilderReverseProxySapNeo headers(Header... customHeaders) {
    this.customHeaders = Optional.of(customHeaders);
    return this;
  }


  public void proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {

    try {
      List<Header> headers = new ArrayList<>();

      if (customHeaders.isPresent()) {
        for (Header header : customHeaders.get()) {
          headers.add(header);
        }
      }
      
      
      try {
        HttpURLConnectionBuilder connBuilder = new HttpURLConnectionBuilderSapNeo().method(request.getMethod())
            .destination(destination).urlAddon(urlAddon)
            .headers(headers.toArray(new Header[headers.size()])).create();

       if (connBuilder.getHeaders() != null && connBuilder.getHeaders().length > 0) {
          boolean allowCookies = true;
          for (Header header : connBuilder.getHeaders()) {
            if(header != null) {
              if (StringUtils.equals(header.getKey(), "Authorization")) {
                allowCookies = false;
              }
            }
          }
          headers.addAll(ReverseProxyUtils.getRequestHeaders(request, allowCookies));
        } else {
          headers.addAll(ReverseProxyUtils.getRequestHeaders(request, true));
        }

        HttpURLConnection conn = connBuilder.build();

        // ...Write request body in the connection
        if (request.getMethod().equals("POST")) {
          ReverseProxyUtils.copyStream(request.getInputStream(), conn.getOutputStream());
        }
        // ...Set response status and copy response headers
        response.setStatus(conn.getResponseCode());

        // ...set response headers
        ReverseProxyUtils.proxyResponseHeaders(request, response, conn);

        InputStream instream = conn.getInputStream();
        OutputStream outstream = response.getOutputStream();
        ReverseProxyUtils.copyStream(instream, outstream);
      } catch (Exception e) {
        String errorMessage =
            "Unable to forward to urlAddon=" + urlAddon + " and destination=" + destination;
        LOG.error(errorMessage, e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      }
    } catch (Exception e) {
      // Connectivity operation failed
      String errorMessage = "Connectivity operation failed with reason: " + e.getMessage()
          + ". See " + "logs for details. Hint: Make sure to have an HTTP proxy configured in your "
          + "local Eclipse environment in case your environment uses "
          + "an HTTP proxy for the outbound Internet " + "communication.";
      LOG.error("Connectivity operation failed", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
  }

  {customcode.end}

}
