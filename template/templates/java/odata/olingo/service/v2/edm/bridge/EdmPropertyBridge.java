package {service.namespace}.odata.edm.bridge;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmTyped;

public class EdmPropertyBridge implements EdmProperty {

	private org.apache.olingo.odata2.api.edm.EdmProperty edmProperty;

	private String name;
	private EdmType type;
	private boolean isCollection;

	public EdmPropertyBridge(org.apache.olingo.odata2.api.edm.EdmProperty edmProperty) {
		this.edmProperty = edmProperty;

		try {
			this.name = this.edmProperty.getName();
			this.type = EdmTypeBridge.getType(this.edmProperty.getType());
			EdmMultiplicity edmMultiplicity = this.edmProperty.getMultiplicity();
			if (edmMultiplicity != null && edmMultiplicity.compareTo(EdmMultiplicity.MANY) == 0) {
				this.isCollection = true;
			} else {
				this.isCollection = false;
			}
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}

	}

	public EdmPropertyBridge(EdmTyped edmTyped) {
		try {
			this.name = edmTyped.getName();
			this.type = EdmTypeBridge.getType(edmTyped.getType());
			EdmMultiplicity edmMultiplicity = edmTyped.getMultiplicity();
			if (edmMultiplicity != null && edmMultiplicity.compareTo(EdmMultiplicity.MANY) == 0) {
				this.isCollection = true;
			} else {
				this.isCollection = false;
			}
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public EdmType getType() {
		return this.type;
	}

	@Override
	public boolean isCollection() {
		return this.isCollection;
	}

	@Override
	public EdmMapping getMapping() {
		try {
			return new EdmMappingBridge(this.edmProperty.getMapping());
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
	public String getMimeType() {
		try {
			return this.edmProperty.getMimeType();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public boolean isPrimitive() {
		return this.edmProperty.isSimple();
	}

	@Override
	public boolean isNullable() {
		try {
			return (this.edmProperty.getFacets() != null && this.edmProperty.getFacets().isNullable());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public Integer getMaxLength() {
		try {
			return (this.edmProperty.getFacets() != null ? this.edmProperty.getFacets().getMaxLength() : 0);
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public Integer getPrecision() {
		try {
			return (this.edmProperty.getFacets() != null ? this.edmProperty.getFacets().getPrecision() : 0);
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public Integer getScale() {
		try {
			return (this.edmProperty.getFacets() != null ? this.edmProperty.getFacets().getScale() : 0);
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public SRID getSrid() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public boolean isUnicode() {
		try {
			return (this.edmProperty.getFacets() != null && this.edmProperty.getFacets().isUnicode());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public String getDefaultValue() {
		try {
			return (this.edmProperty.getFacets() != null ? this.edmProperty.getFacets().getDefaultValue() : null);
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

}
