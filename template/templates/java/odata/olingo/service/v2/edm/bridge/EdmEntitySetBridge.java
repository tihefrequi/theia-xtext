package {service.namespace}.odata.edm.bridge;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

public class EdmEntitySetBridge implements EdmEntitySet {

	private org.apache.olingo.odata2.api.edm.EdmEntitySet edmEntitySet;

	public EdmEntitySetBridge(org.apache.olingo.odata2.api.edm.EdmEntitySet edmEntitySet) {
		this.edmEntitySet = edmEntitySet;
	}

	@Override
	public String getTitle() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmBindingTarget getRelatedBindingTarget(String paramString) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmNavigationPropertyBinding> getNavigationPropertyBindings() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmEntityContainer getEntityContainer() {
		try {
			return new EdmEntityContainerBridge(this.edmEntitySet.getEntityContainer());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmEntityType getEntityType() {
		try {
			return new EdmEntityTypeBridge(this.edmEntitySet.getEntityType());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public String getName() {
		try {
			return this.edmEntitySet.getName();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
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
	public EdmMapping getMapping() {
		try {
			return new EdmMappingBridge(this.edmEntitySet.getMapping());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public boolean isIncludeInServiceDocument() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

}
