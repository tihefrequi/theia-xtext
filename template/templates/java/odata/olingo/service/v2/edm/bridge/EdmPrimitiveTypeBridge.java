package {service.namespace}.odata.edm.bridge;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeException;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.Facets;
import org.apache.olingo.odata2.core.edm.EdmSimpleTypeFacadeImpl;

public class EdmPrimitiveTypeBridge extends EdmTypeBridge implements EdmPrimitiveType {

    private org.apache.olingo.odata2.api.edm.EdmSimpleType edmSimpleType;

    public EdmPrimitiveTypeBridge(org.apache.olingo.odata2.api.edm.EdmSimpleType edmSimpleType) {
        super(edmSimpleType);
        this.edmSimpleType = edmSimpleType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        EdmPrimitiveType primitiveType = (EdmPrimitiveType) obj;

        FullQualifiedName fqn = primitiveType.getFullQualifiedName();
        if (EdmPrimitiveTypeKind.Binary.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Binary));
        } else if (EdmPrimitiveTypeKind.Boolean.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Boolean));
        } else if (EdmPrimitiveTypeKind.Byte.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Byte));
        } else if (EdmPrimitiveTypeKind.Date.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.DateTime));
        } else if (EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType
                .equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.DateTimeOffset));
        } else if (EdmPrimitiveTypeKind.Decimal.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Decimal));
        } else if (EdmPrimitiveTypeKind.Double.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Double));
        } else if (EdmPrimitiveTypeKind.Guid.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Guid));
        } else if (EdmPrimitiveTypeKind.Int16.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Int16));
        } else if (EdmPrimitiveTypeKind.Int32.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Int32));
        } else if (EdmPrimitiveTypeKind.Int64.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Int64));
        } else if (EdmPrimitiveTypeKind.SByte.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.SByte));
        } else if (EdmPrimitiveTypeKind.Single.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Single));
        } else if (EdmPrimitiveTypeKind.String.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.String));
        } else if (EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.equals(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Time));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + primitiveType);
        }
    }

    @Override
    public boolean isCompatible(EdmPrimitiveType primitiveType) {

        FullQualifiedName fqn = primitiveType.getFullQualifiedName();
        if (EdmPrimitiveTypeKind.Binary.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Binary));
        } else if (EdmPrimitiveTypeKind.Boolean.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Boolean));
        } else if (EdmPrimitiveTypeKind.Byte.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Byte));
        } else if (EdmPrimitiveTypeKind.Date.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType
                .isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.DateTime));
        } else if (EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType
                .isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.DateTimeOffset));
        } else if (EdmPrimitiveTypeKind.Decimal.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Decimal));
        } else if (EdmPrimitiveTypeKind.Double.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Double));
        } else if (EdmPrimitiveTypeKind.Guid.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Guid));
        } else if (EdmPrimitiveTypeKind.Int16.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Int16));
        } else if (EdmPrimitiveTypeKind.Int32.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Int32));
        } else if (EdmPrimitiveTypeKind.Int64.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Int64));
        } else if (EdmPrimitiveTypeKind.SByte.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.SByte));
        } else if (EdmPrimitiveTypeKind.Single.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Single));
        } else if (EdmPrimitiveTypeKind.String.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.String));
        } else if (EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName().equals(fqn)) {
            return this.edmSimpleType.isCompatible(EdmSimpleTypeFacadeImpl.getEdmSimpleType(EdmSimpleTypeKind.Time));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + primitiveType);
        }
    }

    @Override
    public Class<?> getDefaultType() {
        return this.edmSimpleType.getDefaultType();
    }

    @Override
    public boolean validate(String value, Boolean isNullable, Integer maxLength, Integer precision, Integer scale,
        Boolean isUnicode) {

        return this.edmSimpleType.validate(value, EdmLiteralKind.DEFAULT,
            new Facets()
                .setNullable(isNullable).setMaxLength(maxLength).setPrecision(precision).setScale(scale)
                .setUnicode(isUnicode));

    }

    @Override
    public <T> T valueOfString(String value, Boolean isNullable, Integer maxLength, Integer precision, Integer scale,
        Boolean isUnicode, Class<T> returnType) throws EdmPrimitiveTypeException {
        try {
            return this.edmSimpleType.valueOfString(value, EdmLiteralKind.DEFAULT,
                new Facets()
                    .setNullable(isNullable).setMaxLength(maxLength).setPrecision(precision).setScale(scale)
                    .setUnicode(isUnicode),
                returnType);
        } catch (EdmSimpleTypeException e) {
            throw new EdmPrimitiveTypeException("Bridge exception", e);
        }
    }

    @Override
    public String valueToString(Object value, Boolean isNullable, Integer maxLength, Integer precision, Integer scale,
        Boolean isUnicode) throws EdmPrimitiveTypeException {
        try {
            return this.edmSimpleType.valueToString(value, EdmLiteralKind.DEFAULT,
                new Facets()
                    .setNullable(isNullable).setMaxLength(maxLength).setPrecision(precision).setScale(scale)
                    .setUnicode(isUnicode));
        } catch (EdmSimpleTypeException e) {
            throw new EdmPrimitiveTypeException("Bridge exception", e);
        }
    }

    @Override
    public String toUriLiteral(String literal) {
        try {
            return this.edmSimpleType.toUriLiteral(literal);
        } catch (org.apache.olingo.odata2.api.edm.EdmException e) {
            throw new EdmException(e);
        }
    }

    @Override
    public String fromUriLiteral(String literal) throws EdmPrimitiveTypeException {
        throw new ODataNotSupportedException("This method is not supported by this bridge");
    }

}
