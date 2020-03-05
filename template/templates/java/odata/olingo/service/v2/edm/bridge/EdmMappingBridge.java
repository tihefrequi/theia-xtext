package {service.namespace}.odata.edm.bridge;

import org.apache.olingo.commons.api.edm.EdmMapping;

public class EdmMappingBridge implements EdmMapping {

	private org.apache.olingo.odata2.api.edm.EdmMapping edmMapping;

	public EdmMappingBridge(org.apache.olingo.odata2.api.edm.EdmMapping edmMapping) {
		this.edmMapping = edmMapping;
	}

	@Override
	public String getInternalName() {
		return this.edmMapping.getInternalName();
	}

	@Override
	public Class<?> getMappedJavaClass() {
		// TODO: review this mapping
		return this.edmMapping.getObject().getClass();
	}

}
