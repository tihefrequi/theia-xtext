package {service.namespace}.odata.processor;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.PathSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Implements the error callback for the OData v2 service for {service.name}. 
 * Logs the error in the corresponding server trace.
 * 
 * @author storm
 *
 */
public class {Service.name}ServiceErrorCallback implements ODataErrorCallback{
	
	  private static final Logger LOG = LoggerFactory.getLogger({Service.name}ServiceErrorCallback.class);

	    /**
	     * @see ODataErrorCallback#handleError(ODataErrorContext)
	     */
	    @Override
	    public ODataResponse handleError(final ODataErrorContext context) throws ODataApplicationException {
	        if (context.getHttpStatus() == HttpStatusCodes.INTERNAL_SERVER_ERROR) {
	            LOG.error("Internal Server Error due to Exception "
	                + (context.getException() != null ? context.getException().getMessage() : " no exception.")
	                + "\nmessage=" + context.getMessage() + " innerError=" + context.getInnerError() + "\nhttpStatus="
	                + context.getHttpStatus() + "\nrequestUri=" + context.getRequestUri() + "\nlocale="
	                + context.getLocale() + "\ncontentType=" + "\ncontentType=" + context.getContentType() + "\nerrorCode="
	                + context.getErrorCode()  + "\nrequestHeaders=" + context.getRequestHeaders()
	                + (context.getPathInfo() != null ? "\nPathInfo.serviceRoot=" + context.getPathInfo().getServiceRoot()
	                    + "\nPathInfo.requestUri=" + context.getPathInfo().getRequestUri() + "\nPathInfo.precedingSegments="
	                    + debugInfo(context.getPathInfo().getPrecedingSegments()) + "\noPathInfo.DataSegments="
	                    + debugInfo(context.getPathInfo().getODataSegments()) : "null"),
	                context.getException());
	        }

	        return EntityProvider.writeErrorDocument(context);
	    }

	    private String debugInfo(List<PathSegment> pathSegments) {
	        String debugInfo = "";
	        if (pathSegments != null) {
	            int i = 0;
	            for (PathSegment pathSegment : pathSegments) {
	                debugInfo += "\n  pathSegment[" + i + "]: path=" + pathSegment.getPath() + " parameters="
	                    + pathSegment.getMatrixParameters();
	            }
	        }
	        return debugInfo;

	    }

}
