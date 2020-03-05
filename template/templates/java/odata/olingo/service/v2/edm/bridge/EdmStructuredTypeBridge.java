package {service.namespace}.odata.edm.bridge;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

public class EdmStructuredTypeBridge extends EdmTypeBridge implements EdmStructuredType {

	private org.apache.olingo.odata2.api.edm.EdmStructuralType edmStructuredType;

	public EdmStructuredTypeBridge(org.apache.olingo.odata2.api.edm.EdmStructuralType edmStructuredType) {
		super(edmStructuredType);
		this.edmStructuredType = edmStructuredType;
	}

	@Override
	public EdmAnnotation getAnnotation(EdmTerm paramEdmTerm, String paramString) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmAnnotation> getAnnotations() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmElement getProperty(String name) {

		try {
			return new EdmPropertyBridge(this.edmStructuredType.getProperty(name));
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public List<String> getPropertyNames() {
		try {
			return this.edmStructuredType.getPropertyNames();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmProperty getStructuralProperty(String name) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmNavigationProperty getNavigationProperty(String name) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<String> getNavigationPropertyNames() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public boolean compatibleTo(EdmType targetType) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmStructuredType getBaseType() {
		try {
			return new EdmStructuredTypeBridge(this.edmStructuredType.getBaseType());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public boolean isOpenType() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public boolean isAbstract() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

}
