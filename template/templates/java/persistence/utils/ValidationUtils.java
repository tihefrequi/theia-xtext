package {service.namespace}.utils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class ValidationUtils {
	
    /**
     * Explain given ConstraintValidationException as String.
     * We provide more details as the standard exception which just says, see embedded exceptions.
     * 
     * @param e ConstraintViolationException
     */
    public static String buildEnduserMessage(ConstraintViolationException e) {
        StringBuilder sb = new StringBuilder();

        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            if (constraintViolation.getPropertyPath() != null) {
                sb.append("Field '");
                sb.append(constraintViolation.getPropertyPath());
                sb.append("'");
            }
            if (constraintViolation.getRootBeanClass() != null) {
                sb.append(" in '");
                sb.append( constraintViolation.getRootBeanClass().getSimpleName());
                sb.append("'");
            }
            if (sb.length() > 0) {
                sb.append(" failed Validation");
            }
            if (constraintViolation.getMessage() != null) {
                sb.append(":");
                sb.append(constraintViolation.getMessage());
            }
            if (constraintViolation.getInvalidValue() != null) {
                if (sb.length() > 0) {
                    sb.append(".");
                }
                sb.append("Invalid Value='");
                sb.append(constraintViolation.getInvalidValue());
                sb.append("'");
            }
            sb.append(".\n");
        }
        if (sb.length() == 0) {
            sb.append("Validation failed.");
        }
        return sb.toString();
    }
    
    /**
     * Explain given ConstraintValidationException as JSON for use in UI functions, specifically ui5.
     * We provide more details as the standard exception which just says, see embedded exceptions.
     * 
     * @param e ConstraintViolationException
     */
	/*
    public static String buildEnduserJson(ConstraintViolationException e) {
		JsonObject json = new JsonObject();
		JsonArray violations = new JsonArray();
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			JsonObject violation = new JsonObject();
			violation.add("invalidValue", new JsonPrimitive(constraintViolation.getInvalidValue() != null
					? constraintViolation.getInvalidValue().toString() : ""));
			violation.add("propertyPath", new JsonPrimitive(constraintViolation.getPropertyPath() != null
					? constraintViolation.getPropertyPath().toString() : ""));
			violation.add("message", new JsonPrimitive(constraintViolation.getMessage() != null
					? constraintViolation.getMessage().toString() : ""));
			violations.add(violation);
		}
		json.add("violations", violations);
		return new ODataException(new Gson().toJson(json));
    }
		*/

}
