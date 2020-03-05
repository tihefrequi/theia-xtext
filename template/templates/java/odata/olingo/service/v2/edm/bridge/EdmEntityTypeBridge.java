package {service.namespace}.odata.edm.bridge;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;
import org.apache.olingo.odata2.api.edm.EdmTyped; 

public class EdmEntityTypeBridge implements EdmEntityType {

    private org.apache.olingo.odata2.api.edm.EdmEntityType edmEntityType;

    public EdmEntityTypeBridge(org.apache.olingo.odata2.api.edm.EdmEntityType edmEntityType) {
        this.edmEntityType = edmEntityType;
    }

    @Override
    public EdmElement getProperty(String name) {

        try {
            EdmTyped property = this.edmEntityType.getProperty(name);
            if (org.apache.olingo.odata2.api.edm.EdmNavigationProperty.class.isAssignableFrom(property.getClass())) {
                return new EdmNavigationPropertyBridge(
                    (org.apache.olingo.odata2.api.edm.EdmNavigationProperty) property);
            } else if (org.apache.olingo.odata2.api.edm.EdmProperty.class.isAssignableFrom(property.getClass())) {
                return new EdmPropertyBridge((org.apache.olingo.odata2.api.edm.EdmProperty) property);
            } else if (org.apache.olingo.odata2.api.edm.EdmElement.class.isAssignableFrom(property.getClass())) {
                return new EdmElementBridge(property);
            } else {
                throw new ODataNotSupportedException("This method is not supported by this bridge");
            }

        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public List<String> getPropertyNames() {
        try {
            return this.edmEntityType.getPropertyNames();
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
    public boolean isOpenType() {
        throw new ODataNotSupportedException("This method is not supported by this bridge");
    }

    @Override
    public boolean isAbstract() {
        throw new ODataNotSupportedException("This method is not supported by this bridge");
    }

    @Override
    public FullQualifiedName getFullQualifiedName() {
        try {
            return new FullQualifiedName(this.edmEntityType.getNamespace(), this.edmEntityType.getName());
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public String getNamespace() {
        try {
            return this.edmEntityType.getNamespace();
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public EdmTypeKind getKind() {
        return EdmTypeKindBridge.get(this.edmEntityType.getKind());
    }

    @Override
    public String getName() {
        try {
            return this.edmEntityType.getName();
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
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
    public List<String> getKeyPredicateNames() {
        try {
            List<org.apache.olingo.odata2.api.edm.EdmProperty> keyProperties = this.edmEntityType.getKeyProperties();
            if (keyProperties != null) {
                List<String> resultKeyPredicateNames = new ArrayList<String>();
                for (org.apache.olingo.odata2.api.edm.EdmProperty keyProperty : keyProperties) {
                    resultKeyPredicateNames.add(keyProperty.getName());
                }
                return resultKeyPredicateNames;
            } else {
                return null;
            }
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public List<EdmKeyPropertyRef> getKeyPropertyRefs() {
        try {
            List<org.apache.olingo.odata2.api.edm.EdmProperty> keyProperties = this.edmEntityType.getKeyProperties();
            if (keyProperties != null) {
                List<EdmKeyPropertyRef> resultKeyPropertyRefs = new ArrayList<EdmKeyPropertyRef>();
                for (org.apache.olingo.odata2.api.edm.EdmProperty keyProperty : keyProperties) {
                    resultKeyPropertyRefs.add(new EdmKeyPropertyRefBridge(keyProperty));
                }
                return resultKeyPropertyRefs;
            } else {
                return null;
            }
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public EdmKeyPropertyRef getKeyPropertyRef(String keyPredicateName) {
        try {
            List<org.apache.olingo.odata2.api.edm.EdmProperty> keyProperties = this.edmEntityType.getKeyProperties();
            if (keyProperties != null) {
                for (org.apache.olingo.odata2.api.edm.EdmProperty keyProperty : keyProperties) {
                    if (keyProperty.getName() != null && keyProperty.getName().equals(keyPredicateName)) {
                        return new EdmKeyPropertyRefBridge(keyProperty);
                    }
                }
            }
            return null;
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public boolean hasStream() {
        try {
            return this.edmEntityType.hasStream();
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public EdmEntityType getBaseType() {
        try {
            return new EdmEntityTypeBridge(this.edmEntityType.getBaseType());
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

}
