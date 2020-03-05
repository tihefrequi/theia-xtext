package {service.namespace}.odata.edm.bridge.v2;

import org.apache.olingo.odata2.api.edm.EdmTypeKind;

public class EdmTypeKindBridge {

	public static EdmTypeKind get(org.apache.olingo.commons.api.edm.constants.EdmTypeKind edmTypeKind) {

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
				return EdmTypeKind.NAVIGATION;
			case PRIMITIVE:
			case ENUM:
				return EdmTypeKind.SIMPLE;
			case FUNCTION:
			case ACTION:
			case DEFINITION:
				return EdmTypeKind.SYSTEM;
			default:
				return EdmTypeKind.UNDEFINED;
			}

		}
	}

}
