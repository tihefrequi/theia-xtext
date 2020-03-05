package {service.namespace}.odata.edm.bridge;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

public class EdmComplexTypeBridge extends EdmStructuredTypeBridge implements EdmComplexType {

	private org.apache.olingo.odata2.api.edm.EdmComplexType edmComplexType;

	public EdmComplexTypeBridge(org.apache.olingo.odata2.api.edm.EdmComplexType edmComplexType) {
		super(edmComplexType);
		this.edmComplexType = edmComplexType;
	}

	@Override
	public EdmAnnotation getAnnotation(EdmTerm term, String qualifier) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmAnnotation> getAnnotations() {
				throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmComplexType getBaseType() {
		try {
			return new EdmComplexTypeBridge(this.edmComplexType.getBaseType());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

}
