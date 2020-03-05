package {service.namespace}.odata.processor.bridge;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

import {service.namespace}.odata.edm.bridge.EdmBridge;

public class ODataSingleProcessorBridge {

	public static Expression parseFilter(HttpServletRequest httpRequest, String v4Filter, ODataContext oDataContext,
			String targetEntitySet) throws ODataException {
		ODataService service = oDataContext.getService();
		EdmBridge edm = new EdmBridge(service.getEntityDataModel());

		OData oData = OData.newInstance();

		Parser parser = new Parser(edm, oData);

		ODataRequest oDataRequest = new ODataRequest();

		// siehe
		// https://github.com/apache/olingo-odata4/blob/master/lib/server-core/src/main/java/org/apache/olingo/server/core/ODataHttpHandlerImpl.java
		fillUriInformation(oDataRequest, httpRequest, 0);

		try {

			oDataRequest.setRawODataPath("/" + targetEntitySet);
			oDataRequest.setRawQueryPath("$filter=" + v4Filter);

			UriInfo uriInfo = parser.parseUri(oDataRequest.getRawODataPath(), oDataRequest.getRawQueryPath(), null);
			FilterOption filterOption = uriInfo.getFilterOption();

			return filterOption.getExpression();

		} catch (UriParserException | UriValidationException e) {
			throw new ODataException("Can't parse filter inside the version bridge", e);
		}
	}

	// ODataHttpHandlerImpl
	private static void fillUriInformation(ODataRequest odRequest, HttpServletRequest httpRequest, int split) {
		String rawRequestUri = httpRequest.getRequestURL().toString();
		String rawODataPath;
		if (!"".equals(httpRequest.getServletPath())) {
			int beginIndex = rawRequestUri.indexOf(httpRequest.getServletPath())
					+ httpRequest.getServletPath().length();
			rawODataPath = rawRequestUri.substring(beginIndex);
		} else {
			if (!"".equals(httpRequest.getContextPath())) {
				int beginIndex = rawRequestUri.indexOf(httpRequest.getContextPath())
						+ httpRequest.getContextPath().length();
				rawODataPath = rawRequestUri.substring(beginIndex);
			} else {
				rawODataPath = httpRequest.getRequestURI();
			}
		}
		String rawServiceResolutionUri = null;
		if (split > 0) {
			rawServiceResolutionUri = rawODataPath;
			for (int i = 0; i < split; i++) {
				int index = rawODataPath.indexOf('/', 1);
				if (-1 == index) {
					rawODataPath = "";
					break;
				}
				rawODataPath = rawODataPath.substring(index);
			}
			int end = rawServiceResolutionUri.length() - rawODataPath.length();
			rawServiceResolutionUri = rawServiceResolutionUri.substring(0, end);
		}
		String rawBaseUri = rawRequestUri.substring(0, rawRequestUri.length() - rawODataPath.length());

		odRequest.setRawQueryPath(httpRequest.getQueryString());
		odRequest.setRawRequestUri(rawRequestUri + (httpRequest.getQueryString() == null ? ""
				: new StringBuilder().append("?").append(httpRequest.getQueryString()).toString()));
		odRequest.setRawODataPath(rawODataPath);
		odRequest.setRawBaseUri(rawBaseUri);
		odRequest.setRawServiceResolutionUri(rawServiceResolutionUri);
	}

}
