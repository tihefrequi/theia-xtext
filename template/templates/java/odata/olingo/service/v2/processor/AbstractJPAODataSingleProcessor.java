package {service.namespace}.odata.processor;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmMapping;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeException;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.CommonExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionKind;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodOperator;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.UnaryExpression;
import org.apache.olingo.odata2.core.exception.ODataRuntimeException;

/**
 * Provides some convenience methods which can be useful for the specific
 * processor implementation in case of JPA persistence
 * 
 * @author storm
 *
 */
public abstract class AbstractJPAODataSingleProcessor extends AbstractODataSingleProcessor {
	// 1. substring method flag, 2. property flag
	private Object[] parseFlags = new Object[] { false, "" };

	/**
	 * Transforms the OData expression to a JPQL expression
	 * 
	 * @param whereExpression
	 * @param entityTypeVar
	 * @param flag
	 * @return
	 * @throws ODataException
	 */
	protected String processFilterExpression(Class<?> jpaEntityType, CommonExpression whereExpression,
			String entityTypeVar) throws ODataException {
		switch (whereExpression.getKind()) {

		// Unary operator expressions like "not" and "-"
		case UNARY: {
			UnaryExpression unaryExpression = (UnaryExpression) whereExpression;
			String operand = processFilterExpression(jpaEntityType, unaryExpression.getOperand(), entityTypeVar);

			switch (unaryExpression.getOperator()) {

			// "not"
			case NOT:
				return "NOT (" + operand + ")";

			case MINUS:
				if (operand.startsWith("-")) {
					return operand.substring(1);
				} else {
					return "-" + operand;
				}
			default:
				throw new ODataNotImplementedException();
			}
		}
		// Used to mark the root node of a filter expression tree
		case FILTER: {
			return processFilterExpression(jpaEntityType, ((FilterExpression) whereExpression).getExpression(),
					entityTypeVar);
		}
		// Binary operator expressions like "eq" and "or"
		case BINARY: {

			BinaryExpression binaryExpression = (BinaryExpression) whereExpression;
			MethodOperator operator = null;
			if (binaryExpression.getLeftOperand().getKind() == ExpressionKind.METHOD) {
				operator = ((MethodExpression) binaryExpression.getLeftOperand()).getMethod();
			}
			if (operator != null && ((binaryExpression.getOperator() == BinaryOperator.EQ)
					|| (binaryExpression.getOperator() == BinaryOperator.NE))) {
				if (operator == MethodOperator.SUBSTRINGOF) {
					parseFlags[0] = true;
				}
			}

			String left = processFilterExpression(jpaEntityType, binaryExpression.getLeftOperand(), entityTypeVar);
			String right = processFilterExpression(jpaEntityType, binaryExpression.getRightOperand(), entityTypeVar);

			// Special handling for STARTSWITH and ENDSWITH method expression
			if (operator != null && (operator == MethodOperator.STARTSWITH || operator == MethodOperator.ENDSWITH)) {
				if (!binaryExpression.getOperator().equals(BinaryOperator.EQ)) {
					throw new ODataApplicationException(
							"There is no equal/not equal operator given in the filter expression, but the compare method used ",
							Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST);
				} else if (right.equals("false")) {
					return "(" + left.replaceFirst("LIKE", "NOT LIKE") + " )";
				} else {
					return "(" + left + " )";
				}
			}

			switch (binaryExpression.getOperator()) {

			// ..."and"
			case AND: {
				return "(" + left + " AND " + right + ")";
			}
			// ..."or"
			case OR: {
				return "(" + left + " OR " + right + ")";
			}
			// ..."eq"
			case EQ: {
				return "(" + left + " " + (!"null".equals(right) ? "=" : "IS") + " " + right + ")";
			}
			// ..."ne"
			case NE: {
				return "(" + left + " " + (!"null".equals(right) ? "<>" : "IS NOT") + " " + right + ")";
			}
			// ..."lt"
			case LT: {
				return "(" + left + " < " + right + ")";
			}
			// ..."le"
			case LE: {
				return "(" + left + " <= " + right + ")";
			}
			// ..."gt"
			case GT: {
				return "(" + left + " > " + right + ")";
			}
			// ..."ge"
			case GE: {
				return "(" + left + " >= " + right + ")";
			}
			// ... "/"
			case PROPERTY_ACCESS: {
				throw new ODataNotImplementedException();
			}
			default:
				throw new ODataNotImplementedException();

			}
		}
		// Property expressions like "age"
		case PROPERTY: {

			String propertyName = getPropertyName(whereExpression);

			parseFlags[1] = propertyName;

			String returnStr = entityTypeVar + "." + propertyName;
			return returnStr;
		}
		// Member access expressions like "/" in "adress/street"
		case MEMBER: {
			String memberExpStr = "";
			int i = 0;
			MemberExpression member = null;
			CommonExpression tempExp = whereExpression;
			while (tempExp != null && tempExp.getKind() == ExpressionKind.MEMBER) {
				member = (MemberExpression) tempExp;
				if (i > 0) {
					memberExpStr = "." + memberExpStr;
				}
				i++;
				memberExpStr = getPropertyName(member.getProperty()) + memberExpStr;
				tempExp = member.getPath();
			}
			memberExpStr = getPropertyName(tempExp) + "." + memberExpStr;
			return entityTypeVar + "." + memberExpStr;
		}
		// Literal expressions like "1.1d" or "'This is a string'"
		case LITERAL: {

			LiteralExpression literal = (LiteralExpression) whereExpression;
			EdmSimpleType literalType = (EdmSimpleType) literal.getEdmType();
			EdmLiteral uriLiteral = EdmSimpleTypeKind.parseUriLiteral(literal.getUriLiteral());
			try {
				String evaluatedExpression = evaluateComparingExpression(jpaEntityType, (String) parseFlags[1],
						uriLiteral.getLiteral(), literalType);

				parseFlags[1] = "";

				return evaluatedExpression;
			} catch (NoSuchFieldException | SecurityException e) {
				throw new ODataApplicationException(
						"There an exception occured in the filter parsing: [" + e.getMessage() + "]", Locale.ENGLISH,
						HttpStatusCodes.BAD_REQUEST, e);
			}
		}
		// Method operator expressions like "substringof" and "concat"
		case METHOD: {
			final MethodExpression methodExpression = (MethodExpression) whereExpression;
			String first = processFilterExpression(jpaEntityType, methodExpression.getParameters().get(0),
					entityTypeVar);
			String second = methodExpression.getParameterCount() > 1
					? processFilterExpression(jpaEntityType, methodExpression.getParameters().get(1), entityTypeVar)
					: null;
			String third = methodExpression.getParameterCount() > 2
					? processFilterExpression(jpaEntityType, methodExpression.getParameters().get(2), entityTypeVar)
					: null;

			switch (methodExpression.getMethod()) {

			// ..."substring"
			case SUBSTRING: {
				third = third != null ? ", " + third : "";
				return String.format("SUBSTRING(%s, %s + 1 %s)", first, second, third);
			}
			// ..."substringof"
			case SUBSTRINGOF: {
				if ((boolean) parseFlags[0]) {
					parseFlags[0] = false;
					return String.format("(CASE WHEN (%s LIKE CONCAT('%%',CONCAT(%s,'%%'))) THEN TRUE ELSE FALSE END)",
							second, first);
				} else {
					return String.format(
							"(CASE WHEN (%s LIKE CONCAT('%%',CONCAT(%s,'%%'))) THEN TRUE ELSE FALSE END) = true",
							second, first);
				}
			}
			// ..."tolower"
			case TOLOWER: {
				return String.format("LOWER(%s)", first);
			}
			// ..."startswith"
			case STARTSWITH: {
				return String.format("%s LIKE CONCAT(%s,'%%')", first, second);
			}
			// ..."endswith"
			case ENDSWITH: {
				return String.format("%s LIKE CONCAT('%%',%s)", first, second);
			}
			// ..."indexof"
			case INDEXOF: {
				return String.format("%s LIKE CONCAT('%%',%s,'%%')", first, second);
			}
			// ...other methods are not supported
			default:
				throw new ODataNotImplementedException();
			}
		}
		default:
			throw new ODataNotImplementedException();
		}
	}

	/**
	 * Retrieves the name of the JPA entity property from filter expression
	 * 
	 * @param whereExpression
	 * @return
	 * @throws EdmException
	 */
	protected static String getPropertyName(CommonExpression whereExpression) throws EdmException {
		EdmTyped edmProperty = ((PropertyExpression) whereExpression).getEdmProperty();
		EdmMapping mapping;
		if (edmProperty instanceof EdmNavigationProperty) {
			EdmNavigationProperty edmNavigationProperty = (EdmNavigationProperty) edmProperty;
			mapping = edmNavigationProperty.getMapping();
		} else if (edmProperty instanceof EdmProperty) {
			EdmProperty property = (EdmProperty) edmProperty;
			mapping = property.getMapping();
		} else {
			throw new ODataRuntimeException();
		}

		return StringUtils.uncapitalize(mapping != null ? mapping.getInternalName() : edmProperty.getName());
	}

	/**
	 * This method evaluates the expression based on the type instance. Used for
	 * adding escape characters where necessary.
	 *
	 * @param uriLiteral
	 * @param edmSimpleType
	 * @return the evaluated expression
	 * @throws ODataApplicationException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws ODataJPARuntimeException
	 */
	protected static String evaluateComparingExpression(Class<?> jpaEntityType, String jpaPropertyName,
			String uriLiteral, final EdmSimpleType edmSimpleType)
			throws ODataApplicationException, NoSuchFieldException, SecurityException {

		// Special case : check for enum
		if (StringUtils.isNotEmpty(jpaPropertyName)) {
			Field jpaField = jpaEntityType.getDeclaredField(jpaPropertyName);
			if (jpaField.getType().isEnum()) {
				return jpaField.getType().getName() + "." + uriLiteral;
			}
		}

		// ...String or Guid
		if (EdmSimpleTypeKind.String.getEdmSimpleTypeInstance().isCompatible(edmSimpleType)
				|| EdmSimpleTypeKind.Guid.getEdmSimpleTypeInstance().isCompatible(edmSimpleType)) {
			uriLiteral = uriLiteral.replaceAll("'", "''");
			uriLiteral = "'" + uriLiteral + "'";
		}
		// ...DateTime or DateTimeOffset
		else if (EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance().isCompatible(edmSimpleType)
				|| EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance().isCompatible(edmSimpleType)) {
			try {
				Calendar datetime = (Calendar) edmSimpleType.valueOfString(uriLiteral, EdmLiteralKind.DEFAULT, null,
						edmSimpleType.getDefaultType());

				String year = String.format("%04d", datetime.get(Calendar.YEAR));
				String month = String.format("%02d", datetime.get(Calendar.MONTH) + 1);
				String day = String.format("%02d", datetime.get(Calendar.DAY_OF_MONTH));
				String hour = String.format("%02d", datetime.get(Calendar.HOUR_OF_DAY));
				String min = String.format("%02d", datetime.get(Calendar.MINUTE));
				String sec = String.format("%02d", datetime.get(Calendar.SECOND));

				uriLiteral = "{ts \'" + year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec + ".000\'}";

			} catch (EdmSimpleTypeException e) {
				throw new ODataApplicationException("Can't patse date [" + uriLiteral + "]", Locale.ENGLISH,
						HttpStatusCodes.BAD_REQUEST);
			}

		}
		// Time
		else if (EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance().isCompatible(edmSimpleType)) {
			try {
				Calendar time = (Calendar) edmSimpleType.valueOfString(uriLiteral, EdmLiteralKind.DEFAULT, null,
						edmSimpleType.getDefaultType());

				String hourValue = String.format("%02d", time.get(Calendar.HOUR_OF_DAY));
				String minValue = String.format("%02d", time.get(Calendar.MINUTE));
				String secValue = String.format("%02d", time.get(Calendar.SECOND));

				uriLiteral = "\'" + hourValue + ":" + minValue + ":" + secValue + "\'";
			} catch (EdmSimpleTypeException e) {
				throw new ODataApplicationException("Can't patse time [" + uriLiteral + "]", Locale.ENGLISH,
						HttpStatusCodes.BAD_REQUEST);
			}

		}
		// Long
		else if (Long.class.equals(edmSimpleType.getDefaultType())) {
			uriLiteral = uriLiteral + "L"; // $NON-NLS-1$
		}

		return uriLiteral;

	}

	protected boolean handleTransaction(UserTransaction utx) throws SystemException {
		if (utx.getStatus() == javax.transaction.Status.STATUS_ACTIVE) {
			return false;
		}
		return true;
	}
}
