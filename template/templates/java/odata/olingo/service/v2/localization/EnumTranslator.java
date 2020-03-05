package {service.namespace}.localization;

/**
 * Class to provide some methods using for translation of the Enums
 * 
 * @author storm
 *
 */
public class EnumTranslator {

	public static String getMessageKey(Enum<?> e) {
		return e.getClass().getSimpleName() + '.' + e.name();
	}

}
