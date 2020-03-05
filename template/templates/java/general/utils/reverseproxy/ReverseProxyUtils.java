package {service.namespace}.utils.reverseproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import {service.namespace}.utils.http.HttpUtils;
import {service.namespace}.utils.http.HttpUtils.Header;
{customcode.import}

public class ReverseProxyUtils {
  
  public static final int COPY_CONTENT_BUFFER_SIZE = 1024;
  
  {customcode.start}
  
  public static List<Header> getRequestHeaders(HttpServletRequest request,boolean allowCookies) {
    // ...add request Headers
    List<Header> headers = new ArrayList<>();

    final Enumeration<String> reqHeaders = request.getHeaderNames();
    while (reqHeaders.hasMoreElements()) {
      final String header = reqHeaders.nextElement();
      if(!header.toLowerCase().equals("cookie") || allowCookies) {
        final Enumeration<String> values = request.getHeaders(header);
        while (values.hasMoreElements()) {
          final String value = values.nextElement();
          headers.add(HttpUtils.header(header, value));
        }
      }
    }

    return headers;
  }



  public static void proxyResponseHeaders(HttpServletRequest request, HttpServletResponse response,
      HttpURLConnection urlConnection) {
    Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
    for (Map.Entry<String, List<String>> headerField : headerFields.entrySet()) {
      if (StringUtils.equalsIgnoreCase(headerField.getKey(), "Set-Cookie")
          || StringUtils.equalsIgnoreCase(headerField.getKey(), "Set-Cookie2")) {
        proxyResponseCookie(request, response, StringUtils.join(headerField.getValue(), ", "));
      } else {
        if (!StringUtils.equalsIgnoreCase(headerField.getKey(), "Transfer-Encoding")) {
          response.addHeader(headerField.getKey(), StringUtils.join(headerField.getValue(), ", "));
        }
      }

    }
  }

  public static void proxyResponseCookie(HttpServletRequest request, HttpServletResponse response,
      String headerValue) {
    List<HttpCookie> cookies = HttpCookie.parse(headerValue);
    String path = request.getContextPath(); // path starts with / or is
                                            // empty string
    path += request.getServletPath(); // servlet path starts with / or is
                                      // empty string
    if (path.isEmpty()) {
      path = "/";
    }

    for (HttpCookie cookie : cookies) {
      // set cookie name prefixed w/ a proxy value so it won't collide w/
      // other cookies
      // don't set cookie domain

      Cookie servletCookie = new Cookie(cookie.getName(), cookie.getValue());
      servletCookie.setComment(cookie.getComment());
      servletCookie.setMaxAge((int) cookie.getMaxAge());
      servletCookie.setPath(path); // set to the path of the proxy servlet

      // ...set secure only if the request was http and corresponding
      // parameter is not false
      boolean proxySecureCookies = true;
      if (!request.isSecure() && !proxySecureCookies) {
        servletCookie.setSecure(false);
      } else {
        servletCookie.setSecure(cookie.getSecure());
      }

      servletCookie.setVersion(cookie.getVersion());
      response.addCookie(servletCookie);
    }
  }

  public static void copyStream(InputStream inStream, OutputStream outStream) throws IOException {
    byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
    int len;
    while ((len = inStream.read(buffer)) != -1) {
      outStream.write(buffer, 0, len);
    }
  }
  
  {customcode.end}
  
}
