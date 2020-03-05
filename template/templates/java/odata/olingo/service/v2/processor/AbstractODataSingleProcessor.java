package {service.namespace}.odata.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import {service.namespace}.odata.processor.AbstractODataSingleProcessor;
import {service.namespace}.auth.IPrincipalSearchRequest;
import {service.namespace}.auth.PrincipalSearchRequest;

import {service.namespace}.odata.processor.AbstractODataSingleProcessor;
import {service.namespace}.auth.IPrincipalSearchRequest;
import {service.namespace}.auth.PrincipalSearchRequest;

/**
 * Provides some convenience methods which can be useful for the specific
 * processor implementation
 * 
 * @author storm
 *
 */
public abstract class AbstractODataSingleProcessor extends ODataSingleProcessor {

	public static final String HEADER_PARAM_SLUG = "slug";
	public static final String HEADER_PARAM_ENTITY_ID = "entityId"; // needed as workaround for document uploads (we add the document entity is as well)
	public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

	public static final String REQUEST_PARAM_SEARCH = "search";
	public static final String REQUEST_PARAM_V4_FILTER = "v4Filter";
	public static final String REQUEST_PARAM_DISTINCT = "distinct";	
    public static final String REQUEST_PARAM_FILTER_BY_ELEMENT_KEYS_TO_IGNORE = "FilterByElementKeysToIgnore";
	public static final String REQUEST_PARAM_FILTER_BY_AUTH_OBJECT = "FilterByAuthorizationObjectElement";
	
    protected Map<String, Object> ID_MAP = new HashMap<String, Object>();
    protected ObjectMap CREATE_MAP = new ObjectMap();
    protected ObjectMap UPDATE_MAP = new ObjectMap();
    protected List<DeferredDocumentDelete> DEFERRED_DOCUMENT_DELETES = new ArrayList<DeferredDocumentDelete>();

    protected static final String ID_TEMP_PREFIX = "id-";
    protected static final Pattern ID_TEMP_PATTERN = Pattern.compile("\\('(.*?)'\\)");
	
	/**
     * 
	 */
	protected static class ObjectMap {
		private Map<String, Set<Object>> MAP = new HashMap<String, Set<Object>>();

		public void add(String key, Object object) {
			Set<Object> idsContractUpdated = MAP.get(key);
			if (idsContractUpdated == null) {
				idsContractUpdated = new LinkedHashSet<Object>();
			}
			idsContractUpdated.add(object);
			MAP.put(key, idsContractUpdated);
		}

		public Set<Object> objectsForKey(String key) {
			return MAP.get(key);
		}
	}

    /**
     * Bean for deferred Document Deletes
     */
	public static class DeferredDocumentDelete {
        private ODataContext context;
        private DeleteUriInfo uriInfo;
        private String mediaName;

		public DeferredDocumentDelete(ODataContext context, DeleteUriInfo uriInfo, String mediaName) {
			super();
			this.context = context;
			this.uriInfo = uriInfo;
			this.mediaName = mediaName;
		}

        public ODataContext getContext() {
            return context;
        }

        public DeleteUriInfo getUriInfo() {
            return uriInfo;
        }

        public String getMediaName() {
            return mediaName;
        }

	}

	/**
	 * Maps the list of provided key predicates to the map of property names and
	 * values
	 * 
	 * @param keys
	 * @return
	 * @throws EdmException
	 */
	public Map<String, Object> mapKeys(final List<KeyPredicate> keys) throws EdmException {

		Map<String, Object> keyMap = new HashMap<String, Object>();

		for (final KeyPredicate key : keys) {
			final EdmProperty property = key.getProperty();
			final EdmSimpleType type = (EdmSimpleType) property.getType();

			keyMap.put(property.getName(), type.valueOfString(key.getLiteral(), EdmLiteralKind.DEFAULT,
					property.getFacets(), type.getDefaultType()));
		}
		return keyMap;
	}

	/**
	 * Maps the function parameters from literal to the values
	 * 
	 * @param functionImportParameters
	 * @return
	 * @throws EdmSimpleTypeException
	 */
	public Map<String, Object> mapFunctionParameters(final Map<String, EdmLiteral> functionImportParameters)
			throws EdmSimpleTypeException {
		if (functionImportParameters == null) {
			return Collections.emptyMap();
		} else {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			for (final String parameterName : functionImportParameters.keySet()) {
				final EdmLiteral literal = functionImportParameters.get(parameterName);
				final EdmSimpleType type = literal.getType();
				parameterMap.put(parameterName,
						type.valueOfString(literal.getLiteral(), EdmLiteralKind.DEFAULT, null, type.getDefaultType()));
			}
			return parameterMap;
		}
	}

	/**
	 * Checks whether the HTTP status code is an error code
	 * 
	 * @return
	 */
	public boolean isErrorCode(HttpStatusCodes httpStatusCode) {
		// please refer to https://de.wikipedia.org/wiki/HTTP-Statuscode
		return httpStatusCode.getStatusCode() >= 400;
	}

	/**
	 * Retrieves the callbacks for the entity
	 * 
	 * @param data
	 * @param entityType
	 * @return
	 * @throws EdmException
	 */
	protected <T> Map<String, ODataCallback> getCallbacks(final EdmEntityType entityType, ODataWriteCallback callback)
			throws EdmException {
		final List<String> navigationPropertyNames = entityType.getNavigationPropertyNames();
		if (navigationPropertyNames.isEmpty()) {
			return null;
		} else {
			Map<String, ODataCallback> callbacks = new HashMap<String, ODataCallback>();
			for (final String name : navigationPropertyNames) {
				callbacks.put(name, callback);
			}
			return callbacks;
		}
	}

	/**
	 * 
	 * @param uriInfo
	 * @return
	 */
	protected IPrincipalSearchRequest getPrincipalSearchRequest(GetEntitySetUriInfo uriInfo) {
		return getPrincipalSearchRequest(uriInfo.getCustomQueryOptions());
	}

	/**
	 * 
	 * @param uriInfo
	 * @return
	 */
	protected IPrincipalSearchRequest getPrincipalSearchRequest(GetEntitySetCountUriInfo uriInfo) {
		return getPrincipalSearchRequest(uriInfo.getCustomQueryOptions());
	}

	/**
	 * 
	 * @param queryOptions
	 * @return
	 */
	private IPrincipalSearchRequest getPrincipalSearchRequest(Map<String, String> queryOptions) {
		PrincipalSearchRequest searchRequest = new PrincipalSearchRequest();

		// Search Term
		String search = queryOptions.get(AbstractODataSingleProcessor.REQUEST_PARAM_SEARCH);
		if (search == null || search.length() == 0) {
			search = null;
		}
		searchRequest.setSearchTerm(search == null ? "" : search);

		//
		// We receive e.g.:
		// group eq 'mygroupname' and UniqueName ne 'XX'
		String v4filter = queryOptions.get(AbstractODataSingleProcessor.REQUEST_PARAM_V4_FILTER);
		// very very simple Visitor 8)
		String[] expressions = StringUtils.splitByWholeSeparator(v4filter, "and");
		if (expressions != null) {
			for (String expression : expressions) { 
				String[] exprParts = StringUtils.split(StringUtils.trim(expression), " \t");
				if (exprParts == null || exprParts.length != 3) {
					throw new RuntimeException(
							"The Principal Query Expression Part [" + expression + "] of the whole Expression ["
									+ v4filter + "] does not have 3 parts e.g. [group eq 'name']");
				}
				if (exprParts[0].startsWith("group")) {
					if (!"eq".equalsIgnoreCase(exprParts[1])) {
						throw new RuntimeException(
								"The Principal Query Expression Part [" + expression + "] of the whole Expression ["
										+ v4filter + "] does not allow for group any other Operator than eq");
					}
					searchRequest.getContainedInGroups()
							.add(StringUtils.removeStart(StringUtils.removeEnd(exprParts[2], "'"), "'"));
				} else if (exprParts[0].startsWith("UniqueName")) {
					if (!"ne".equalsIgnoreCase(exprParts[1])) {
						throw new RuntimeException(
								"The Principal Query Expression Part [" + expression + "] of the whole Expression ["
										+ v4filter + "] does not allow for UniqueName any other Operator than ne");
					}
					searchRequest.getHide().add(StringUtils.removeStart(StringUtils.removeEnd(exprParts[2], "'"), "'"));
				}
			}
		}

		return searchRequest;
	}

}
