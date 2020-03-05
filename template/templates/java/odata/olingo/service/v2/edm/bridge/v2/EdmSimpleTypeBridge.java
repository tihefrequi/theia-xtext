package {service.namespace}.odata.edm.bridge.v2;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFacets;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeException;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.exception.MessageReference;

import {service.namespace}.odata.edm.bridge.EdmPrimitiveTypeBridge;

public class EdmSimpleTypeBridge implements EdmSimpleType {

	private EdmPrimitiveType edmPrimitiveType;

	public EdmSimpleTypeBridge(EdmPrimitiveType edmPrimitiveType) {
		this.edmPrimitiveType = edmPrimitiveType;
	}

	@Override
	public EdmTypeKind getKind() {
		return EdmTypeKindBridge.get(this.edmPrimitiveType.getKind());
	}

	@Override
	public String getNamespace() throws EdmException {
		return this.edmPrimitiveType.getNamespace();
	}

	@Override
	public String getName() throws EdmException {
		return this.edmPrimitiveType.getName();
	}

	@Override
	public Class<?> getDefaultType() {
		return this.edmPrimitiveType.getDefaultType();
	}

	@Override
	public boolean isCompatible(EdmSimpleType simpleType) {
		return this.edmPrimitiveType.isCompatible(new EdmPrimitiveTypeBridge(simpleType));
	}

	@Override
	public String toUriLiteral(String literal) throws EdmSimpleTypeException {
		return this.edmPrimitiveType.toUriLiteral(literal);
	}

	@Override
	public boolean validate(String value, EdmLiteralKind literalKind, EdmFacets facets) {
		return this.edmPrimitiveType.validate(value, facets.isNullable(), facets.getMaxLength(), facets.getPrecision(),
				facets.getScale(), facets.isUnicode());
	}

	@Override
	public <T> T valueOfString(String value, EdmLiteralKind literalKind, EdmFacets facets, Class<T> returnType)
			throws EdmSimpleTypeException {
		try {
			return this.edmPrimitiveType.valueOfString(value, facets.isNullable(), facets.getMaxLength(),
					facets.getPrecision(), facets.getScale(), facets.isUnicode(), returnType);
		} catch (EdmPrimitiveTypeException e) {
			throw new EdmSimpleTypeException(MessageReference.create(EdmSimpleTypeException.class, ""), e);
		}
	}

	@Override
	public String valueToString(Object value, EdmLiteralKind literalKind, EdmFacets facets)
			throws EdmSimpleTypeException {
		try {
			return this.edmPrimitiveType.valueToString(value, facets.isNullable(), facets.getMaxLength(),
					facets.getPrecision(), facets.getScale(), facets.isUnicode());
		} catch (EdmPrimitiveTypeException e) {
			throw new EdmSimpleTypeException(MessageReference.create(EdmSimpleTypeException.class, ""), e);
		}
	}

}
