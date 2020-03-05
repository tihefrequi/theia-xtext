package {service.namespace}.jpa.type;

import java.util.Locale;

/**
 * A interface for enum with text .
 * 
 * Contains all possible values.
 */
public interface EnumWithText<T extends Enum<T>> {

	public abstract String text(Locale locale);
}