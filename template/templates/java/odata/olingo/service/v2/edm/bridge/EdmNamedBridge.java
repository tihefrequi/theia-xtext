package {service.namespace}.odata.edm.bridge;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNamed;

public class EdmNamedBridge implements EdmNamed {

	private org.apache.olingo.odata2.api.edm.EdmNamed edmNamed;

	public EdmNamedBridge(org.apache.olingo.odata2.api.edm.EdmNamed edmNamed) {
		this.edmNamed = edmNamed;
	}

	@Override
	public String getName() {
		try {
			return this.edmNamed.getName();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

}
