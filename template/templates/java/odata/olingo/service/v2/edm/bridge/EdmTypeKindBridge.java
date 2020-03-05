package {service.namespace}.odata.edm.bridge;

import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

public class EdmTypeKindBridge {

	public static EdmTypeKind get(org.apache.olingo.odata2.api.edm.EdmTypeKind edmTypeKind) {

		if (edmTypeKind == null) {
			return null;
		} else {

			// TODO: this mapping should be reviewed
			switch (edmTypeKind) {
			case COMPLEX:
				return EdmTypeKind.COMPLEX;
			case ENTITY:
				return EdmTypeKind.ENTITY;
			case NAVIGATION:
			case ASSOCIATION:
				return EdmTypeKind.NAVIGATION;
			case SYSTEM:
				return EdmTypeKind.FUNCTION;
			case UNDEFINED:
			case SIMPLE:
				return EdmTypeKind.PRIMITIVE;
			default:
				return null;
			}
		}
	}
}
