package {service.namespace}.utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthScheme;
import sun.net.www.protocol.http.AuthenticationInfo;
{customcode.import}

public class HttpUtils {

  private static Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

  {customcode.start}
	

  /**
   * @param request
   * @param cookieName
   * @return
   */
  public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals(cookieName)) {
          return Optional.ofNullable(cookie);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * /** Http Post Body to URL with Headers
   *
   * @param url
   * @param proxyConfig
   * @param body
   * @param headers
   * @return
   * @throws IOException
   */
  @SafeVarargs
  public static HttpURLConnection post(String url, ProxyConfig proxyConfig, String body,
      Header... headers) throws IOException {
    return buildHttpURLConnection(new URL(url), proxyConfig, "POST", body, headers);
  }

  /**
   * Http send Body to url (maybe via proxy) with Headers
   *
   * @param url
   * @param proxyConfig
   * @param requestMethod
   * @param body
   * @param headers
   * @return
   * @throws IOException
   */
  @SafeVarargs
  public static HttpURLConnection buildHttpURLConnection(URL url, ProxyConfig proxyConfig,
      String requestMethod, String body, Header... headers) throws IOException {

    final boolean hasOutBody = (requestMethod.equals("POST"));

    HttpURLConnection connection = null;
    if (proxyConfig != null && proxyConfig.type.equals(Type.HTTP)) {
      if (proxyConfig.username.isPresent() && proxyConfig.username.get() != null
          && proxyConfig.username.get().length() > 0) {
        if (proxyConfig.password.get() == null) {
          proxyConfig.password("");
        }

        /*
         * In Java 8u111 Basic authentication for HTTPS tunneling was disabled by default. From
         * http://www.oracle.com/technetwork/java/javase/8u111-relnotes-3124969.html
         *
         * In some environments, certain authentication schemes may be undesirable when proxying
         * HTTPS. Accordingly, the Basic authentication scheme has been deactivated, by default, in
         * the Oracle Java Runtime. Now, proxies requiring Basic authentication when setting up a
         * tunnel for HTTPS will no longer succeed by default. If required, this authentication
         * scheme can be reactivated by removing Basic from the
         * jdk.http.auth.tunneling.disabledSchemes networking property, or by setting a system
         * property of the same name to "" ( empty ) on the command line.
         */
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

        Authenticator.setDefault(new HttpUtils().new ProxyAuthenticator(proxyConfig.username.get(),
            proxyConfig.password.get()));

        // the Standard mechanism (Authenticator.setDefault()) did not
        // work (was not called) in jdk8u111 and jdk8u162.
        // So we preset the authentication cache with this map
        // as we need the AuthenticationInfo.getProxyAuth(...) to
        // deliver an Authentication Object, otherwise the standard
        // will fail (from our impression a jdk8 bug ...)
        AuthCacheImpl authCache = new AuthCacheImpl();
        authCache.put(AuthenticationInfo.PROXY_AUTHENTICATION + "::" + proxyConfig.host + ":"
            + proxyConfig.port, new HttpUtils().new ProxyBasicAuthentication(proxyConfig));
        AuthenticationInfo.setAuthCache(authCache);
      }
      connection = (HttpURLConnection) url.openConnection(proxyConfig.getProxy());

    } else {
      connection = (HttpURLConnection) url.openConnection();
    }

    connection.setRequestMethod(requestMethod);
    if (headers != null) {
      for (SimpleEntry<String, String> header : headers) {
        if (header != null) {
          connection.setRequestProperty(header.getKey(), header.getValue());
        }
      }
    }

    if (hasOutBody) {
      connection.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(body);
      writer.flush();
    }

    return connection;
  }

  /**
   * Build ProxyConfig
   *
   * @param key
   * @param value
   * @return
   */
  public static ProxyConfig proxyHTTP() {
    return new ProxyConfig().type(Proxy.Type.HTTP);
  }

  /**
   * Build ProxyConfig
   *
   * @param key
   * @param value
   * @return
   */
  public static ProxyConfig proxyNone() {
    return new ProxyConfig().type(Proxy.Type.DIRECT);
  }

  /** Proxy Configuration e.g. host/port/user/pass */
  public static class ProxyConfig {
    private Proxy.Type type = Type.DIRECT;
    private String host = null;
    private int port = 80;
    private Optional<String> username = Optional.empty();
    private Optional<String> password = Optional.empty();

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "ProxyConfig [type=" + type + ", host=" + host + ", port=" + port + ", username="
          + username + "]";
    }

    /** @return the type */
    public Type getType() {
      return type;
    }

    /** @param type the type to set */
    public ProxyConfig type(Type type) {
      this.type = type;
      return this;
    }

    /** @return the host */
    public String getHost() {
      return host;
    }

    /** @param host the host to set */
    public ProxyConfig host(String host) {
      this.host = host;
      return this;
    }

    /** @return */
    public Proxy getProxy() {
      return new Proxy(type, new InetSocketAddress(host, port));
    }

    /** @return the port */
    public int getPort() {
      return port;
    }

    /** @param port the port to set */
    public ProxyConfig port(int port) {
      this.port = port;
      return this;
    }

    /** @return the username */
    public Optional<String> getUsername() {
      return username;
    }

    /** @param username the username to set */
    public ProxyConfig username(String username) {
      this.username = Optional.ofNullable(username);
      return this;
    }

    /** @return the password */
    public Optional<String> getPassword() {
      return password;
    }

    /** @param password the password to set */
    public ProxyConfig password(String password) {
      this.password = Optional.ofNullable(password);
      return this;
    }
  }

  /** Proxy BASIC AUTH 1 */
  private class ProxyBasicAuthentication extends AuthenticationInfo {
    private static final long serialVersionUID = -8026989555971745867L;
    private transient String headerValue = null;

    public ProxyBasicAuthentication(ProxyConfig proxyConfig) {
      super(AuthenticationInfo.PROXY_AUTHENTICATION, AuthScheme.BASIC, proxyConfig.host,
          proxyConfig.port, null /* realm */);
      String proxyUserAndPassword =
          proxyConfig.getUsername().get() + ":" + proxyConfig.getPassword().get();
      headerValue = "Basic " + Base64.encodeBase64String(proxyUserAndPassword.getBytes());
    }

    @Override
    public boolean supportsPreemptiveAuthorization() {
      return true;
    }

    @Override
    public String getHeaderValue(URL paramURL, String method) {
      return headerValue;
    }

    @Override
    public boolean setHeaders(sun.net.www.protocol.http.HttpURLConnection paramHttpURLConnection,
        HeaderParser paramHeaderParser, String paramString) {
      return false;
    }

    @Override
    public boolean isAuthorizationStale(String paramString) {
      return false;
    }
  }

  /** PROXY BASIC AUTH 2 */
  private class ProxyAuthenticator extends Authenticator {

    private String user, password;

    public ProxyAuthenticator(String user, String password) {
      this.user = user;
      this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(user, password.toCharArray());
    }
  }

  /**
   * Use e.g. for Http Headers
   *
   * @param key
   * @param value
   * @return
   */
  public static Header header(String key, String value) {
    return new Header(key, value);
  }

  /** Use e.g. for Http Headers */
  public static class Header extends SimpleEntry<String, String> {

    private static final long serialVersionUID = 6381188054252063524L;

    public Header(String key, String value) {
      super(key, value);
    }
  }

  public enum AuthenticationType {
    Basic, Bearer, None
  }

  // TODO: Optional
  @SuppressWarnings("unused")
  public static Header getAuthenticationHeader(AuthenticationType authType, String token) {

    switch (authType) {
      case Basic:
        return HttpUtils.header("Authorization", "Basic " + token);
      case Bearer:
        return HttpUtils.header("Authorization", "Bearer " + token);
      default:
        return HttpUtils.header("", "");
    }
  }

  public static String getUrlConnectionResponseAsString(HttpURLConnection httpURLConnection) {
    InputStream in = null;
    String responseString = "";
    try {
      httpURLConnection.connect();

      int status = httpURLConnection.getResponseCode();

      if (status == 200 || status == 202) {
        in = httpURLConnection.getInputStream();
        responseString = IOUtils.toString(in, StandardCharsets.UTF_8);
      }

      LOG.debug("Response=" + responseString + " from urlConnection=" + httpURLConnection);

    } catch (IOException e) {
      LOG.error("Failed to get response from urlConnection=" + httpURLConnection, e);
    }

    return responseString;
  }

  public static String addQueryParams(String baseString, String... urlAddons) {
    for (String urlAddon : urlAddons) {
      if (!baseString.contains("?")) {
        baseString += "?" + urlAddon;
      } else {
        baseString += "&" + urlAddon;
      }
    }
    return baseString;
  }
  
  {customcode.end}

}
