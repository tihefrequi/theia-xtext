package {app.id};

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthScheme;
import sun.net.www.protocol.http.AuthenticationInfo;

/**
 * Servlet class making HTTP calls to specified HTTP destinations. Destinations
 * are used in the following exemplary connectivity scenarios:<br>
 * - Connecting to an outbound Internet resource using HTTP destinations<br>
 * - Connecting to an on-premise backend using on-premise HTTP destinations,<br>
 * where the destinations could have no authentication or basic
 * authentication.<br>
 * 
 * (@see
 * https://help.sap.com/doc/cca91383641e40ffbe03bdc78f00f681/Cloud/en-US/e592cf6cbb57101495d3c28507d20f1b.html)
 * and
 * neo-javaee7-wp-sdk-1.10.16\samples\connectivity\src\com\sap\cloud\sample\connectivity\ConnectivityServlet.java
 * 
 */
public class ProxyServlet extends HttpServlet {
	@Resource
	private TenantContext tenantContext;

	private static final String ON_PREMISE_PROXY = "OnPremise";
	private static final String INTERNET_PROXY = "Internet";

	private static final long serialVersionUID = 1L;
	private static final int COPY_CONTENT_BUFFER_SIZE = 1024;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServlet.class);

	private final String PARAM_PROXY_SECURE_COOKIES = "proxy-secure-cookies";

	/**
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		proxyRequest("POST", request, response);
	}

	/**
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		proxyRequest("GET", request, response);
	}

	/**
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private void proxyRequest(String method, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String errorMsg = "";

		// 0. Prepare
		final boolean hasOutBody = (method.equals("POST"));

		// 1. Get the destination name from the path
		String destinationName = "";
		String destinationPath = "";
		String destinationQueryString = request.getQueryString();

		String pathInfo = request.getPathInfo();
		if (!StringUtils.isBlank(pathInfo)) {
			destinationName = StringUtils.substringBetween(pathInfo, "/", "/");
			destinationPath = StringUtils.substringAfter(pathInfo, destinationName);
		}

		// 2. Check the name
		if (StringUtils.isNotBlank(destinationName)) {

			try {
				// 3. Look up the connectivity configuration API
				// "connectivityConfiguration"
				Context ctx = new InitialContext();
				ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
						.lookup("java:comp/env/connectivityConfiguration");

				// 4. Get destination configuration for destinationName
				DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);

				if (destConfiguration == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(
							"Destination %s is not found. Hint:" + " Make sure to have the destination configured.",
							destinationName));
					return;
				}

				String destUrl = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_URL);
				URL url = new URL(destUrl + destinationPath
						+ (StringUtils.isNotBlank(destinationQueryString) ? "?" + destinationQueryString : ""));

				// 5. Call url and write response
				HttpURLConnection urlConnection = getHttpConnection(url, destConfiguration);

				// ... set request method
				urlConnection.setRequestMethod(method);

				// ... set request headers
				proxyRequestHeaders(request, urlConnection, destConfiguration);

				urlConnection.setUseCaches(false);
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(hasOutBody);
				urlConnection.connect();

				// 6. Write request body in the connection
				if (hasOutBody) {
					copyStream(request.getInputStream(), urlConnection.getOutputStream());
				}

				// 7. Set response status and copy response headers
				response.setStatus(urlConnection.getResponseCode());

				// ...set response headers
				proxyResponseHeaders(request, response, urlConnection);

				// 8. Copy content from the incoming response to the outgoing
				// response
				InputStream instream = urlConnection.getInputStream();
				try {
					OutputStream outstream = response.getOutputStream();
					copyStream(instream, outstream);
				} catch (Exception e) {
					// Can't read destination
					String errorMessage = "Can't read destination [" + destinationName + "]";
					LOGGER.error(errorMessage, e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);

				} finally {
					instream.close();
				}
			} catch (NamingException e) {
				// Lookup of destination failed
				String errorMessage = "Lookup of destination failed with reason: " + e.getMessage() + ". See "
						+ "logs for details. Hint: Make sure to have the destination " + destinationName
						+ " configured.";
				LOGGER.error("Lookup of destination failed", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
			} catch (Exception e) {
				// Connectivity operation failed
				String errorMessage = "Connectivity operation failed with reason: " + e.getMessage() + ". See "
						+ "logs for details. Hint: Make sure to have an HTTP proxy configured in your "
						+ "local Eclipse environment in case your environment uses "
						+ "an HTTP proxy for the outbound Internet " + "communication.";
				LOGGER.error("Connectivity operation failed", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
			}
		} else {
			errorMsg = "Lookup of destination failed: No destination name found in the path";
			LOGGER.error(errorMsg);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
		}

	}

	private void proxyRequestHeaders(HttpServletRequest request, HttpURLConnection urlConnection,
			DestinationConfiguration destConfiguration) {

		String proxyType = destConfiguration.getProperty("ProxyType");

		final Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			final String header = headers.nextElement();
			final Enumeration<String> values = request.getHeaders(header);
			while (values.hasMoreElements()) {
				final String value = values.nextElement();
				urlConnection.addRequestProperty(header, value);
			}
		}

		// ...add custom headers
		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Insert header for on-premise connectivity with the consumer
			// account name
			urlConnection.setRequestProperty("SAP-Connectivity-ConsumerAccount",
					tenantContext.getTenant().getAccount().getId());
		}

		// ... check authentication and set corresponding attributes
		String destAuthType = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_AUTHENTICATION_TYPE);
		if (StringUtils.equals(destAuthType, "BasicAuthentication")) {
			String user = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_USER);
			String password = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_PASSWORD);
			String userAndPassword = user + ":" + password;
			urlConnection.setRequestProperty("Authorization",
					"Basic " + Base64.encodeBase64String(userAndPassword.getBytes()));
		} else if (StringUtils.equals(destConfiguration.getProperty("UseODataBearerTokenFromPassword"), "true")) {
			String token = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_PASSWORD);
			urlConnection.setRequestProperty("Authorization", "Bearer " + token);
		}

	}

	private void proxyResponseHeaders(HttpServletRequest request, HttpServletResponse response,
			HttpURLConnection urlConnection) {
		Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
		for (Map.Entry<String, List<String>> headerField : headerFields.entrySet()) {
			if (StringUtils.equalsIgnoreCase(headerField.getKey(), "Set-Cookie")
					|| StringUtils.equalsIgnoreCase(headerField.getKey(), "Set-Cookie2")) {
				proxyResponseCookie(request, response, StringUtils.join(headerField.getValue(), ", "));
			} else {
				response.addHeader(headerField.getKey(), StringUtils.join(headerField.getValue(), ", "));
			}

		}
	}

	private void proxyResponseCookie(HttpServletRequest request, HttpServletResponse response, String headerValue) {
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
			boolean proxySecureCookies = Boolean.parseBoolean(this.getInitParameter(PARAM_PROXY_SECURE_COOKIES));
			if (!request.isSecure() && !proxySecureCookies) {
				servletCookie.setSecure(false);
			} else {
				servletCookie.setSecure(cookie.getSecure());
			}

			servletCookie.setVersion(cookie.getVersion());
			response.addCookie(servletCookie);
		}
	}

	/**
	 * Gets the http connection, if needed, inserts an authenticating proxy
	 *
	 * @param url
	 *            the url
	 * @param logonData
	 *            the logon data
	 * @param destConfiguration
	 *            the DestinationConfiguration
	 * @return the http connection
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public HttpURLConnection getHttpConnection(URL url, DestinationConfiguration destConfiguration) throws IOException {
		HttpURLConnection connection;
		Optional<ProxyDefinition> proxyDefinition = getProxy(destConfiguration);
		if (proxyDefinition.isPresent()) {
			/* In Java 8u111 Basic authentication for HTTPS tunneling was disabled by default.  
			   From http://www.oracle.com/technetwork/java/javase/8u111-relnotes-3124969.html

			   In some environments, certain authentication schemes may be undesirable when proxying HTTPS. 
			   Accordingly, the Basic authentication scheme has been deactivated, by default, in the Oracle Java Runtime.
			   Now, proxies requiring Basic authentication when setting up a tunnel for HTTPS will no longer succeed by default. 
			   If required, this authentication scheme can be reactivated by removing Basic 
			   from the jdk.http.auth.tunneling.disabledSchemes networking property, 
			   or by setting a system property of the same name to "" ( empty ) on the command line.
			*/
			System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
			//System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(proxyDefinition.get().host, proxyDefinition.get().port));
			String proxyUser = destConfiguration.getProperty("StormProxyUser");
			String proxyPassword = destConfiguration.getProperty("StormProxyPassword");
			connection = (HttpURLConnection) url.openConnection(proxy);
			if (proxyUser != null && proxyUser.length() > 0) {
				if (proxyPassword == null) {
					proxyPassword = "";
				}
				char[] proxyPasswordAsChars = proxyPassword.toCharArray();

				// the Standard mechanism (Authenticator.setDefault()) did not
				// work (was not called) in jdk8u111 and jdk8u162.
				// So we preset the authentication cache with this map
				// as we need the AuthenticationInfo.getProxyAuth(...) to
				// deliver an Authentication Object, otherwise the standard
				// will fail (from our impression a jdk8 bug ...)
				AuthCacheImpl authCache = new AuthCacheImpl();
				authCache.put(
						AuthenticationInfo.PROXY_AUTHENTICATION + "::" + proxyDefinition.get().host + ":"
								+ proxyDefinition.get().port,
						new ProxyBasicAuthentication(proxyDefinition.get(), proxyUser, proxyPassword));
				AuthenticationInfo.setAuthCache(authCache);

			}
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}
		return connection;
	}

	private class ProxyDefinition {
		String host;
		int port;
	}

	private class ProxyBasicAuthentication extends AuthenticationInfo {
		private static final long serialVersionUID = -8026989555971745867L;
		private transient String headerValue = null;

		public ProxyBasicAuthentication(ProxyDefinition proxyDefinition, String proxyUser, String proxyPassword) {
			super(AuthenticationInfo.PROXY_AUTHENTICATION, AuthScheme.BASIC, proxyDefinition.host, proxyDefinition.port,
					null /* realm */);
			String proxyUserAndPassword = proxyUser + ":" + proxyPassword;
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

	private Optional<ProxyDefinition> getProxy(DestinationConfiguration destConfiguration) {
		String proxyHost = null;
		String proxyPort = null;
		String proxyType = destConfiguration.getProperty("ProxyType");

		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Get proxy for on-premise destinations
			proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
			proxyPort = System.getenv("HC_OP_HTTP_PROXY_PORT");
		} else if (INTERNET_PROXY.equals(proxyType)) {
			// Internet
			// if Proxy is set by Storm (httpProxy statement in
			// system/servciegroup definition)
			proxyHost = destConfiguration.getProperty("StormProxyHost");
			proxyPort = destConfiguration.getProperty("StormProxyPort");
			if (proxyHost == null || proxyHost.length() == 0) {
				// Get proxy for internet destinations from tomcat java system
				// properties in launchconfiguration
				proxyHost = System.getProperty("https.proxyHost");
				proxyPort = System.getProperty("https.proxyPort");
			}
		}

		if (proxyPort != null && proxyHost != null) {
			ProxyDefinition proxy = new ProxyDefinition();
			proxy.host = proxyHost;
			proxy.port = Integer.parseInt(proxyPort);
			return Optional.of(proxy);
		} else {
			return Optional.empty();
		}
	}

	private void copyStream(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
		int len;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
	}
}
