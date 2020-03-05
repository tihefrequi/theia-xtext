package {service.namespace}.odata.processor;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.ep.callback.OnWriteEntryContent;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.ep.callback.WriteCallbackContext;

public abstract class ODataWriteCallback implements OnWriteEntryContent, OnWriteFeedContent {

	/**
	 * Checks the navigation from - to
	 * 
	 * @param context
	 * @param entitySetName
	 * @param navigationPropertyName
	 * @return
	 * @throws EdmException
	 */
	protected boolean isNavigationFromTo(WriteCallbackContext context, String entitySetName,
			String navigationPropertyName) throws EdmException {
		if (entitySetName == null || navigationPropertyName == null) {
			return false;
		}
		EdmEntitySet sourceEntitySet = context.getSourceEntitySet();
		EdmNavigationProperty navigationProperty = context.getNavigationProperty();
		return entitySetName.equals(sourceEntitySet.getName())
				&& navigationPropertyName.equals(navigationProperty.getName());
	}
	
	/**
	 * Get Debug Infos from Context (exception safe)
	 * 
	 * @param context
	 * @return e.g. entitySetName and navProperty from Context
	 */
    protected CharSequence debugInfo(WriteCallbackContext context) {
        StringBuffer sb = new StringBuffer();
        if (context != null) {
            sb.append(" sourceEntitySetName=[");
            try {
                sb.append(context.getSourceEntitySet() != null ? context.getSourceEntitySet().getName() : null);
            } catch (EdmException e) {
                sb.append(" got exception while reading: " + e.getMessage());
            }
            sb.append("] navigationPropertyName=[");
            try {
                sb.append(context.getNavigationProperty() != null ? context.getNavigationProperty().getName() : null);
            } catch (EdmException e) {
                sb.append(" got exception while reading: " + e.getMessage());
            }
            sb.append("]");
        } else {
            sb.append(" context=[null]");
        }
        return sb;
    }

}
