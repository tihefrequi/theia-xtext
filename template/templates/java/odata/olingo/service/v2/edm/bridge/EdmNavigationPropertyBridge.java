package {service.namespace}.odata.edm.bridge;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmReferentialConstraint;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;
import org.apache.olingo.odata2.api.edm.EdmTyped;

public class EdmNavigationPropertyBridge extends EdmPropertyBridge implements EdmType, EdmNavigationProperty {

	private org.apache.olingo.odata2.api.edm.EdmNavigationProperty navigationProperty;

	public EdmNavigationPropertyBridge(org.apache.olingo.odata2.api.edm.EdmNavigationProperty edmProperty) {
		super(edmProperty);
		this.navigationProperty = edmProperty;
	}

	public EdmNavigationPropertyBridge(EdmTyped edmTyped) {
		super(edmTyped);
	}

	@Override
	public EdmEntityType getType() {
		try {
			return new EdmEntityTypeBridge(navigationProperty.getRelationship().getEnd2().getEntityType());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmTypeKind getKind() {
		try {
			return EdmTypeKindBridge.get(navigationProperty.getType().getKind());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public boolean containsTarget() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmNavigationProperty getPartner() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public String getReferencingPropertyName(String paramString) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmReferentialConstraint> getReferentialConstraints() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public FullQualifiedName getFullQualifiedName() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public String getNamespace() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

}
