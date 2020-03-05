package {service.namespace}.odata.edm.bridge;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

public class EdmKeyPropertyRefBridge implements EdmKeyPropertyRef {

	private org.apache.olingo.odata2.api.edm.EdmProperty edmProperty;

	public EdmKeyPropertyRefBridge(org.apache.olingo.odata2.api.edm.EdmProperty edmProperty) {
		this.edmProperty = edmProperty;
	}

	@Override
	public String getName() {
		try {
			return this.edmProperty.getName();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public String getAlias() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmProperty getProperty() {
		return new EdmPropertyBridge(this.edmProperty);
	}

}
