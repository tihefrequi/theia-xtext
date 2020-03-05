package {service.namespace}.utils.sap.cloud.http;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import java.rmi.ServerException;
import javax.naming.ConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {service.namespace}.utils.http.HttpURLConnectionBuilder;
import {service.namespace}.utils.http.HttpUtils;
import {service.namespace}.utils.http.HttpUtils.AuthenticationType;
import {service.namespace}.utils.http.HttpUtils.Header;
import {service.namespace}.utils.http.HttpUtils.ProxyConfig;
import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

{customcode.import}

public class HttpURLConnectionBuilderSapNeo {

  private static Logger LOG = LoggerFactory.getLogger(HttpURLConnectionBuilderSapNeo.class);

  {customcode.start}

  private static final String ON_PREMISE_PROXY = "OnPremise";

  private static final String INTERNET_PROXY = "Internet";

  private String destination;
  private String urlAddon = "";
  private String body = "";
  private Optional<Header[]> customHeaders = Optional.empty();
  private String requestMethod = "";

  public String getDestination() {
    return destination;
  }

  public HttpURLConnectionBuilderSapNeo destination(String destination) {
    this.destination = destination;
    return this;
  }

  public HttpURLConnectionBuilderSapNeo get() {
    this.requestMethod = "GET";
    return this;
  }

  public HttpURLConnectionBuilderSapNeo post() {
    this.requestMethod = "POST";
    return this;
  }

  public String getUrlAddon() {
    return urlAddon;
  }

  public HttpURLConnectionBuilderSapNeo urlAddon(String urlAddon) {
    this.urlAddon = urlAddon;
    return this;
  }

  public String getBody() {
    return this.body;
  }

  public HttpURLConnectionBuilderSapNeo body(String body) {
    this.body = body;
    return this;
  }

  public Optional<Header[]> getHeaders() {
    if (customHeaders.isPresent()) {
      return customHeaders;
    }
    return Optional.empty();
  }

  public HttpURLConnectionBuilderSapNeo headers(Header... customHeaders) {
    this.customHeaders = Optional.of(customHeaders);
    return this;
  }

  public String getMethod() {
    return requestMethod;
  }

  public HttpURLConnectionBuilderSapNeo method(String requestMethod) {
    this.requestMethod = requestMethod;
    return this;
  }

  public DestinationConfiguration getDestinationConfiguration() throws NamingException {
    try {
      Context ctx = new InitialContext();
      ConnectivityConfiguration configuration =
          (ConnectivityConfiguration) ctx.lookup("java:comp/env/connectivityConfiguration");
      DestinationConfiguration destConfiguration = configuration.getConfiguration(getDestination()); // myBackend
      return destConfiguration;
    } catch (NamingException e) {
      LOG.error(
          "Failed to get destination configuration from JNDI: destination=" + destination + ".", e);
      throw e;
    }
  }

  /**
   *
   * @param destination
   * @param urlAddon
   *
   * @return
   */

	public HttpURLConnectionBuilder create() throws ServerException {

    try {
      DestinationConfiguration destConfiguration = getDestinationConfiguration();

      String proxyType =
          destConfiguration.getProperty(DestinationConfiguration.DESTINATION_PROXY_TYPE);

      String type = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_TYPE);
      
      if(type.toLowerCase().equals("http")) {
        String destUrl = StringUtils.defaultIfBlank(destConfiguration.getProperty(DestinationConfiguration.DESTINATION_URL), "");
        String concatUrl = destUrl;
        if(!destUrl.isEmpty()) {
          if(StringUtils.endsWith(destUrl, "/") && urlAddon.startsWith("/")) {
            
            concatUrl = StringUtils.substring(concatUrl, 0,concatUrl.length()-1)+urlAddon; 
          } else if(!StringUtils.endsWith(destUrl, "/") && !urlAddon.startsWith("/")){
            concatUrl += "/"+urlAddon;
          } else {
            concatUrl+= urlAddon;
          }
        }else {
          throw new ConfigurationException("When using a destination of type HTTP we except the property 'url' to be set with an URL.");
        }
        
        URL url = new URL(concatUrl);
        LOG.debug("url=" + url);

        if (ON_PREMISE_PROXY.equals(proxyType)) {
          if (StringUtils.equals("https", url.getProtocol())) {

            LOG.warn(
                "HTTPS is not allowed in Cloud Connector. Rewriting to HTTP (BUT keeping original port !!!). "//
                    + "You SHOULD change your destination settings to HTTP. " //
                    + "Destination name: >" + getDestination() + "<");

            url = new URL("http", url.getHost(),
                url.getPort() > 0 ? url.getPort() : url.getDefaultPort(), url.getFile());
            LOG.debug("url after rewriting Protocol=" + url);
          }
        }

        Header injectedHeader = injectHeader(proxyType);
        LOG.debug("Using ProxyType=" + proxyType);

        Header authHeader = null;
        if (StringUtils.equals(destConfiguration.getProperty("UseODataBearerTokenFromPassword"),
            "true")) {
          authHeader = HttpUtils.getAuthenticationHeader(AuthenticationType.Bearer,
              destConfiguration.getProperty(DestinationConfiguration.DESTINATION_PASSWORD));

        } else if (StringUtils.equals(destConfiguration.getProperty("Authentication"),
            "BasicAuthentication")) {

          String user = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_USER);
          String password =
              destConfiguration.getProperty(DestinationConfiguration.DESTINATION_PASSWORD);
          String encodedUser =
              Base64.getEncoder().encodeToString(String.format("%s:%s", user, password).getBytes());
          authHeader = HttpUtils.getAuthenticationHeader(AuthenticationType.Basic, encodedUser);
        }

        // ...reverse Proxy Tenant
        Header accountHeader = null;

        ProxyConfig proxyConfig = HttpUtils.proxyNone();
        if (ON_PREMISE_PROXY.equals(proxyType)) {
          proxyConfig = getProxy(proxyType, destConfiguration);
        }
        LOG.debug("Using ProxyConfig=" + proxyConfig);
        if (authHeader != null) {
          LOG.debug("Auth (value cut after 10 chars)=" //
              + authHeader.getKey() + "=" + StringUtils.substring(authHeader.getValue(), 0, 10)
              + "...");
        }
        List<Header> allHeaders = new ArrayList<>();
        if (customHeaders.isPresent()) {
          for (Header header : customHeaders.get()) {
            allHeaders.add(header);
          }
        }

        allHeaders.add(authHeader);
        allHeaders.add(injectedHeader);
        allHeaders.add(accountHeader);
        allHeaders.add(HttpUtils.header("Accept", "application/json"));
        // ...add custom headers

        HttpURLConnectionBuilder connection =
            new HttpURLConnectionBuilder().requestMethod(requestMethod).url(url)
                .proxyConfig(proxyConfig).headers(allHeaders.toArray(new Header[allHeaders.size()]));

        if (StringUtils.isNotBlank(this.body)) {
          connection.body(this.body);
        }

        return connection;
      }else {
        throw new ConfigurationException("The destination type="+type+" is not supported. Please use a destination of type 'HTTP'");
      } 
     

    } catch (NamingException | IOException e) {
      LOG.error("Failed to create a URl Connection for urlAddon=" + urlAddon + " destination="
          + destination + ".", e);
    }
    throw new ServerException("We could not establish a proxy connection using the destination+"+destination+" an the urlAddon="+urlAddon);
  }

  private static Header injectHeader(String proxyType) {
    if (ON_PREMISE_PROXY.equals(proxyType)) {
      // Insert header for on-premise connectivity with the consumer subaccount name
      try {
        return HttpUtils.header("SAP-Connectivity-ConsumerAccount", getCurrentAccountId());
      } catch (ServletException e) {
        LOG.error("Unable to get current accountID.", e);
      }
    }
    return null;
  }

  public static String getCurrentAccountId() throws ServletException {
    String accountId = null;
    try {
      Context ctx = new InitialContext();
      TenantContext tenantContext = (TenantContext) ctx.lookup("java:comp/env/tenantContext");
      accountId = tenantContext.getTenant().getAccount().getId();
    } catch (NamingException e) {
      LOG.trace(".getCurrentAccountId()", e);
    }
    if (StringUtils.isBlank(accountId)) {
      accountId = System.getenv("HC_ACCOUNT");
    }
    LOG.debug("SAP Account ID: " + accountId);
    return accountId;
  }

  private static ProxyConfig getProxy(String proxyType,
      DestinationConfiguration destConfiguration) {
    String proxyHost = null;
    int proxyPort = 0;
    if (ON_PREMISE_PROXY.equals(proxyType)) {
      // Get proxy for on-premise destinations
      proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
      proxyPort = Integer.parseInt(System.getenv("HC_OP_HTTP_PROXY_PORT"));
    } else if (INTERNET_PROXY.equals(proxyType)) {
      // Internet
      // if Proxy is set by Storm (httpProxy statement in
      // system/servciegroup definition)
      proxyHost = destConfiguration.getProperty("StormProxyHost");
      proxyPort = Integer.parseInt(destConfiguration.getProperty("StormProxyPort"));
      if (proxyHost == null || proxyHost.length() == 0) {
        // Get proxy for internet destinations from tomcat java system
        // properties in launchconfiguration
        proxyHost = System.getProperty("https.proxyHost");
        proxyPort = Integer.parseInt(System.getProperty("https.proxyPort"));
      }
    }
    return HttpUtils.proxyHTTP().host(proxyHost).port(proxyPort);
  }
  
  {customcode.end}
  
}
