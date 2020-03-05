/**
 * 
 */
package {service.namespace}.odata.edm;

import org.apache.olingo.odata2.api.edm.provider.EdmProvider;

/**
 * @author storm
 *
 */
public class BaseEdmProvider extends EdmProvider  {
	// ----------------------------------------
	// ANNOTATIONS
	// ----------------------------------------

	protected static final String AN_VOC_NS_ODATA_CAPABILITIES = "Org.OData.Capabilities.V1";
	protected static final String AN_VOC_NS_ODATA_CORE = "Org.OData.Core.V1";
	protected static final String AN_VOC_NS_ODATA_MEASURES = "Org.OData.Measures.V1";
	protected static final String AN_VOC_NS_SAP_COMMON = "com.sap.vocabularies.Common.v1";
	protected static final String AN_VOC_NS_SAP_COMMUNICATION = "com.sap.vocabularies.Communication.v1";
	protected static final String AN_VOC_NS_SAP_UI = "com.sap.vocabularies.UI.v1";

	protected static final String AN_TERM_COMPUTED = "Computed";
	protected static final String AN_TERM_LABEL = "Label";
	protected static final String AN_TERM_VALUE_LIST = "ValueList";
	protected static final String AN_TERM_TEXT = "Text";
	protected static final String AN_TERM_INSERT_RESTRICTIONS = "InsertRestrictions";
	protected static final String AN_TERM_DELETE_RESTRICTIONS = "DeleteRestrictions";
	protected static final String AN_TERM_UPDATE_RESTRICTIONS = "UpdateRestrictions";
	protected static final String AN_TERM_COUNT_RESTRICTIONS = "CountRestrictions";
	protected static final String AN_TERM_LINE_ITEM = "LineItem";

	protected static final String AN_PROP_LABEL = "Label";
	protected static final String AN_PROP_COLLECTION_PATH = "CollectionPath";
	protected static final String AN_PROP_SEARCH_SUPPORTED = "SearchSupported";
	protected static final String AN_PROP_PARAMETERS = "Parameters";
	protected static final String AN_PROP_LOCAL_DATA_PROP = "LocalDataProperty";
	protected static final String AN_PROP_VALUE_LIST_PROP = "ValueListProperty";
	protected static final String AN_PROP_INSERTABLE = "Insertable";
	protected static final String AN_PROP_DELETABLE = "Deletable";
	protected static final String AN_PROP_UPDATABLE = "Updatable";
	protected static final String AN_PROP_COUNTABLE = "Countable";
	protected static final String AN_PROP_VALUE = "Value";

	protected static final String AN_TYPE_VALUE_LIST_PARAM_IN_OUT = "ValueListParameterInOut";
	protected static final String AN_TYPE_VALUE_LIST_PARAM_DISPLAY_ONLY = "ValueListParameterDisplayOnly";
	protected static final String AN_TYPE_VALUE_LIST_DATA_FIELD = "DataField";

	protected static final String AN_XML_NAMESPACE = "http://docs.oasis-open.org/odata/ns/edm";


}
