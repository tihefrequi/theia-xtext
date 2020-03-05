package {service.namespace}.odata.edm.bridge;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

public class EdmTypeBridge extends EdmNamedBridge implements EdmType {

	private org.apache.olingo.odata2.api.edm.EdmType edmType;

	public EdmTypeBridge(org.apache.olingo.odata2.api.edm.EdmType edmType) {
		super(edmType);

		this.edmType = edmType;
	}

	@Override
	public FullQualifiedName getFullQualifiedName() {
		try {
			return new FullQualifiedName(this.edmType.getNamespace(), this.edmType.getName());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public String getNamespace() {
		try {
			return this.edmType.getNamespace();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmTypeKind getKind() {
		return EdmTypeKindBridge.get(this.edmType.getKind());
	}

	public static EdmType getType(org.apache.olingo.odata2.api.edm.EdmType edmType) { 
        if (org.apache.olingo.odata2.api.edm.EdmEntityType.class.isAssignableFrom(edmType.getClass())) {
            return new EdmEntityTypeBridge((org.apache.olingo.odata2.api.edm.EdmEntityType) edmType);
        } else if (org.apache.olingo.odata2.api.edm.EdmComplexType.class.isAssignableFrom(edmType.getClass())) {
          return new EdmComplexTypeBridge((org.apache.olingo.odata2.api.edm.EdmComplexType) edmType);
        } else if (org.apache.olingo.odata2.api.edm.EdmStructuralType.class.isAssignableFrom(edmType.getClass())) {
            return new EdmStructuredTypeBridge((org.apache.olingo.odata2.api.edm.EdmStructuralType) edmType);
        } else if (org.apache.olingo.odata2.api.edm.EdmSimpleType.class.isAssignableFrom(edmType.getClass())) {
            return new EdmPrimitiveTypeBridge((org.apache.olingo.odata2.api.edm.EdmSimpleType) edmType);
        } else {
            return new EdmTypeBridge(edmType);
        }
	}

}
