package {service.namespace}.odata.edm.bridge;

import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;

public class EdmElementBridge implements EdmElement {

	private org.apache.olingo.odata2.api.edm.EdmTyped edmTyped;

	public EdmElementBridge(org.apache.olingo.odata2.api.edm.EdmTyped edmTyped) {
		this.edmTyped = edmTyped;
	}

	@Override
	public String getName() {
		try {
			return this.edmTyped.getName();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmType getType() {
		try {
			return EdmTypeBridge.getType(this.edmTyped.getType());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public boolean isCollection() {
		try {
			EdmMultiplicity edmMultiplicity = this.edmTyped.getMultiplicity();
			if (edmMultiplicity != null && edmMultiplicity.compareTo(EdmMultiplicity.MANY) == 0) {
				return true;
			} else {
				return false;
			}
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

}
