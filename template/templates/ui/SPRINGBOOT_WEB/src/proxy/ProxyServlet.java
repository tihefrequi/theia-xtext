package {app.id};

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.HeaderParser;
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

//@MultipartConfig
public class ProxyServlet extends HttpServlet {
	@Resource
	//private TenantContext tenantContext; 

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

		String destinationQueryString = request.getQueryString();

		String pathInfo = request.getPathInfo();
		
		String targetUrl = getServletConfig().getInitParameter("targetUrl");
		
		// 2. Check the name
		if (StringUtils.isNotBlank(pathInfo)) {

			try {								
				URL url = new URL(targetUrl + pathInfo
						+ (StringUtils.isNotBlank(destinationQueryString) ? "?" + destinationQueryString : ""));

				// 5. Call url and write response
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

				// ... set request method
				urlConnection.setRequestMethod(method);

				// ... set request headers
				proxyRequestHeaders(request, urlConnection);

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
					String errorMessage = "Can't read destination [" + pathInfo + "]";
					LOGGER.error(errorMessage, e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);

				} finally {
					instream.close();
				}

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


	private void proxyRequestHeaders(HttpServletRequest request, HttpURLConnection urlConnection) {

		final Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			final String header = headers.nextElement();
			final Enumeration<String> values = request.getHeaders(header);
			while (values.hasMoreElements()) {
				final String value = values.nextElement();
				urlConnection.addRequestProperty(header, value);
			}
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

	private void copyStream(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
		int len;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
	}
}
