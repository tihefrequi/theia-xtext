package {service.namespace}.odata.processor.bridge.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;
import javax.persistence.ElementCollection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.joda.time.DateTime;

import {service.namespace}.odata.edm.{service.odata.edmprovider.classname};
import {service.namespace}.acl.AclQueryAddon;
import {service.namespace}.jpa.entity.I18NPersist;

//Please do not forget to replace {service.odata.edmprovider.classname} when reneweing 


/**
 * Visitor implementation for OData V4 expressions
 *
 */
public class ApplicationExpressionVisitor implements ExpressionVisitor<String> {

	private final String LAMBDA_ANY = "ANY";
	private final String LAMBDA_ALL = "ALL";
	
	private final String LITERAL_NULL = "null";

	private Map<String, String> joins;
	private String currentMember;
	private Stack<Pair<String, Class<?>>> entityStack;

	private String lambdaVariable = null;
	private String lambdaFunction = LAMBDA_ANY;
	private String lambdaFrom = null;

	public ApplicationExpressionVisitor(Class<?> jpaEntity) {
		this.joins = new HashMap<String, String>();

		this.currentMember = null;

		this.entityStack = new Stack<Pair<String, Class<?>>>();
		this.entityStack.push(new ImmutablePair<String, Class<?>>(getEntityVar(jpaEntity), jpaEntity));
	}

	public ApplicationExpressionVisitor(Class<?> jpaEntity, String entityVar) {
		this.joins = new HashMap<String, String>();

		this.currentMember = null;

		this.entityStack = new Stack<Pair<String, Class<?>>>();
		this.entityStack.push(new ImmutablePair<String, Class<?>>(entityVar, jpaEntity));
	}

	public ApplicationExpressionVisitor setLambdaVariable(String lambdaVariable) {
		this.lambdaVariable = lambdaVariable;
		return this;
	}

	public ApplicationExpressionVisitor setLambdaFunction(String lambdaFunction) {
		this.lambdaFunction = lambdaFunction;
		return this;
	}

	public ApplicationExpressionVisitor setLambdaFrom(String lambdaFrom) {
		this.lambdaFrom = lambdaFrom;
		return this;
	}

	@Override
	public String visitBinaryOperator(BinaryOperatorKind operator, String left, String right)
			throws ExpressionVisitException, ODataApplicationException {

		StringBuilder sb = new StringBuilder();

		// if we have the ALL lambda variable (= select sub query with ALL operator) -> we should swap the parts
		if (lambdaVariable != null && StringUtils.equals(this.lambdaFunction, LAMBDA_ALL)) {
			String newRight = new String(left);
			String newLeft = new String(right);

			right = newRight;
			left = newLeft;
		}

		switch (operator) {
			case HAS :
				throw new ExpressionVisitException(
						"Can't parse filter. Unsupported operation exception [" + operator + "]");
			case MUL :
				sb.append(left).append(" * ").append(right);
				break;
			case DIV :
				sb.append(left).append(" / ").append(right);
				break;
			case MOD :
				sb.append("MOD(").append(left).append(",").append(right).append(")");
				break;
			case ADD :
				sb.append(left).append(" + ").append(right);
				break;
			case SUB :
				sb.append(left).append(" - ").append(right);
				break;
			case GT :
				sb.append(left).append(" > ").append(right);
				break;
			case GE :
				sb.append(left).append(" >= ").append(right);
				break;
			case LT :
				sb.append(left).append(" < ").append(right);
				break;
			case LE :
				sb.append(left).append(" <= ").append(right);
				break;
			case EQ :
				if (StringUtils.equalsIgnoreCase(StringUtils.trim(right), "NULL")) {
					sb.append(left).append(" IS ").append(right);
				} else {
					sb.append(left).append(" = ").append(right);
				}
				break;
			case NE :
				if (StringUtils.equalsIgnoreCase(StringUtils.trim(right), "NULL")) {
					sb.append(left).append(" IS NOT ").append(right);
				} else {
					sb.append(left).append(" != ").append(right);
				}
				break;
			case AND :
				sb.append("(").append(left).append(") AND (").append(right).append(")");
				break;
			case OR :
				sb.append("(").append(left).append(") OR (").append(right).append(")");
				break;
			default :
				throw new ExpressionVisitException(
						"Can't parse filter. Unsupported operation exception [" + operator + "]");

		}

		// we remove the last added entity since we leave the expression context
		if (this.entityStack.size() > 1) {
			this.entityStack.pop();
		}

		return sb.toString();
	}

	@Override
	public String visitUnaryOperator(UnaryOperatorKind operator, String operand)
			throws ExpressionVisitException, ODataApplicationException {

		StringBuilder sb = new StringBuilder();

		switch (operator) {
			case MINUS :
				sb.append(operand).append(" * (-1)");
				break;
			case NOT :
				sb.append("NOT (").append(operand).append(")");
				break;
			default :
				throw new ExpressionVisitException(
						"Can't parse filter. Unsupported operation exception [" + operator + "]");
		}

		// we remove the last added entity since we leave the expression context
		if (this.entityStack.size() > 1) {
			this.entityStack.pop();
		}

		return sb.toString();
	}

	@Override
	public String visitMethodCall(MethodKind methodCall, List<String> parameters)
			throws ExpressionVisitException, ODataApplicationException {

		StringBuilder sb = new StringBuilder();

		// NOTE: sometimes we add/substract 1 since the OData is 0-based and JPQL 1-based indexing

		String firstParameter = parameters.get(0);
		boolean isLiteral = StringUtils.startsWith(firstParameter, "'");

		// TODO: check for literal vs member
		switch (methodCall) {
			case CONTAINS :
				sb.append("LOCATE(").append(parameters.get(1)).append(",").append(parameters.get(0)).append(",1) >= 1");
				// TODO: alternative, check which is more performant
				// return sb.append(parameters.get(0)).append(" LIKE '%").append(parameters.get(1)).append("%'").toString();
				break;
			case STARTSWITH :
				sb.append("LOCATE(").append(parameters.get(1)).append(",").append(parameters.get(0)).append(",1) = 1");
				// TODO: alternative, check which is more performant
				// return sb.append(parameters.get(0)).append(" LIKE '").append(parameters.get(1)).append("%'").toString();
				break;
			case ENDSWITH :

				sb.append(parameters.get(0)).append(" LIKE '%").append(parameters.get(1)).append("'");
				// TODO: alternative, check which is more performant
				// return sb.append("LOCATE('").append(parameters.get(1)).append("','").append(parameters.get(0))
				// .append("',LENGTH('").append(parameters.get(1)).append("')-LENGTH('").append(parameters.get(0))
				// .append("')) = (LENGTH('").append(parameters.get(1)).append("')-LENGTH('").append(parameters.get(0))
				// .append("'))").toString();

				break;
			case LENGTH :

				sb.append("LENGTH('").append(parameters.get(0)).append("')");
				break;

			case INDEXOF :

				sb.append("LOCATE('").append(parameters.get(1)).append("','").append(parameters.get(0))
						.append("',1) - 1");
				break;
			case SUBSTRING :
				sb = sb.append("SUBSTRING('").append(parameters.get(0)).append("',").append(parameters.get(1))
						.append("+1");

				if (parameters.get(2) != null) {
					sb = sb.append(",").append(parameters.get(2));
				}
				sb.append(")");
				break;

			case TOLOWER :
				sb.append("LOWER(").append(parameters.get(0)).append(")");
				break;
			case TOUPPER :
				sb.append("UPPER(").append(parameters.get(0)).append(")");
				break;
			case TRIM :
				sb.append("TRIM(BOTH FROM '").append(parameters.get(0)).append("')");
				break;
			case CONCAT :
				sb.append("CONCAT('").append(parameters.get(0)).append("','").append(parameters.get(1)).append("')");
				break;
			case YEAR :
				sb.append("YEAR({d '").append(parameters.get(0)).append("'})");
				break;
			case MONTH :
				sb.append("MONTH({d '").append(parameters.get(0)).append("'})");
				break;
			case DAY :
				sb.append("DAY({d '").append(parameters.get(0)).append("'})");
				break;
			case HOUR :
				sb.append("HOUR({t '").append(parameters.get(0)).append("'})");
				break;
			case MINUTE :
				sb.append("MINUTE({t '").append(parameters.get(0)).append("'})");
				break;
			case SECOND :
				sb.append("SECOND({t '").append(parameters.get(0)).append("'})");
				break;
			case NOW :
				sb.append("CURRENT_TIMESTAMP");
				break;
			case DATE :
				sb.append("'" + new DateTime(parameters.get(0)).toString("yyyy-MM-dd") + "'");
				break;
			case ROUND :
			case FLOOR :
			case CEILING :
			case CAST :
			case TIME :
			case FRACTIONALSECONDS :
			case TOTALSECONDS :
			case TOTALOFFSETMINUTES :
			case MINDATETIME :
			case MAXDATETIME :
			case GEODISTANCE :
			case GEOLENGTH :
			case GEOINTERSECTS :
			case ISOF :
			default :
				throw new ExpressionVisitException(
						"Can't parse filter. Unsupported method exception [" + methodCall + "]");

		}

		// we remove the last added entity since we leave the expression context
		if (this.entityStack.size() > 1) {
			this.entityStack.pop();
		}

		return sb.toString();

	}

	@Override
	public String visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression)
			throws ExpressionVisitException, ODataApplicationException {

		// NOTE: This is currently not used
		return null;

	}

	public String visitLambdaExpressionInternal(String lambdaFunction, String lambdaVariable, Expression expression,
			String subSelectFromClause) throws ExpressionVisitException, ODataApplicationException {

		StringBuilder sb = new StringBuilder();

		if (StringUtils.equals(lambdaFunction, LAMBDA_ALL) || StringUtils.equals(lambdaFunction, LAMBDA_ANY)) {
			Pair<String, Class<?>> jpaEntityPair = this.entityStack.peek();
			Class<?> jpaEntity = jpaEntityPair.getRight();
			Class<?> jpaPropertyType = null;
			try {
				jpaPropertyType = getJpaPropertyType(jpaEntity, this.currentMember);

				Class<?> innerEntity;
				if (Collection.class.isAssignableFrom(jpaPropertyType)) {
					Type jpaPropertyGenericType = getJpaPropertyGenericType(jpaEntity, this.currentMember);
					ParameterizedType innerEntityType = (ParameterizedType) jpaPropertyGenericType;
					Type[] innerEntityGenerics = innerEntityType.getActualTypeArguments();
					innerEntity = (Class<?>) innerEntityGenerics[innerEntityGenerics.length - 1];
				} else {
					innerEntity = jpaPropertyType;
				}
				return sb
						.append(expression
								.accept(new ApplicationExpressionVisitor(innerEntity).setLambdaVariable(lambdaVariable)
										.setLambdaFunction(lambdaFunction).setLambdaFrom(subSelectFromClause)))
						.toString();
			} catch (NoSuchFieldException | SecurityException e) {
				throw new ExpressionVisitException(
						"Can't parse filter. Can't retrieve the inner jpa entity for member [" + this.currentMember
								+ "] for lambda function [" + lambdaFunction + "], lambda variable [" + lambdaVariable
								+ "] and expression [" + expression.toString() + "]. Jpa Property type is ["
								+ jpaPropertyType + "] of Entity [" + jpaEntity + "]",
						e);
			}

		} else {
			throw new ExpressionVisitException(
					"Can't parse filter. Unsupported lambda expression [" + lambdaFunction + "]");
		}
	}

	@Override
	public String visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {

	    String literalText = literal.getText();

		if (this.currentMember != null) {
			Class<?> jpaPropertyType = null;
			Pair<String, Class<?>> jpaEntityPair = this.entityStack.peek();
			Class<?> jpaEntity = jpaEntityPair.getRight();
			try {			 			   
			    
			    // if the current entity is enum itself (e.g. the case if we have the member handling such
  				// MarketSegment/any{d:d/MarketSegment eq ...}) where MarketSegment is Enumerated Collection of type
  				// Enum
    			    
    				if (jpaEntity.isEnum()) {
              			//SDEV-214: check for "empty" literal and then it should be replaced with null in enum case 
                        if(StringUtils.isEmpty(literalText)) {
                          return LITERAL_NULL;
                        } else {
        				    return jpaEntity.getName() + "."
        							+ StringUtils.removeStart(StringUtils.removeEnd(literalText, "'"), "'");
                        }
    				} else {
    					jpaPropertyType = getJpaPropertyType(jpaEntity, this.currentMember);
    					if (jpaPropertyType != null && jpaPropertyType.isEnum()) {
                            //SDEV-214: check for "empty" literal and then it should be replaced with null in enum case 
                            if(StringUtils.isEmpty(literalText)) {
                              return LITERAL_NULL;
                            } else {
        					    return jpaPropertyType.getName() + "."
        								+ StringUtils.removeStart(StringUtils.removeEnd(literalText, "'"), "'");
                            }
    					}
    				}
			} catch (SecurityException e) {
				throw new ExpressionVisitException("Can't parse filter. Can't check for enum the member ["
						+ this.currentMember + "] for literal [" + literalText + "]. Jpa Property type is ["
						+ jpaPropertyType + "] of Entity [" + jpaEntity + "]", e);
			}
		}

		return literalText;
	}

	  /**
	   *
	   * Get name of the languages map collection dynamically
	   *
	   */
	  private String getLangMapPropertyName(Class<?> jpaPropertyType) {
		Optional<Field> langMapField = Stream.of(jpaPropertyType.getDeclaredFields())
			.filter(field -> field.isAnnotationPresent(ElementCollection.class)).findFirst();
		if (langMapField.isPresent()) {
		  return langMapField.get().getName();
		} else {
		  return null;
		}

	  }

	@Override
	public String visitMember(Member member) throws ExpressionVisitException, ODataApplicationException {
		StringBuilder sb = new StringBuilder();

		List<UriResource> items = member.getResourcePath().getUriResourceParts();

		if (items != null) {

			// size = 1 > we have only member itself so we add it as property of the current jpa entity in the query

			if (items.size() == 1) {
			  			  
				String jpaPropertyName = StringUtils.uncapitalize(((UriResourcePartTyped) items.get(0)).toString(true));
				Pair<String, Class<?>> jpaEntityPair = this.entityStack.peek();
                Class<?> jpaEntity = jpaEntityPair.getRight();
                String jpaEntityName = jpaEntityPair.getLeft();
                Class<?> jpaPropertyType = getJpaPropertyType(jpaEntity, jpaPropertyName);
                
                this.currentMember = jpaPropertyName;
                
                //check for the translatable field
                if(I18NPersist.class.isAssignableFrom(jpaPropertyType)) {
                  //...in this case we add the join to the corresponding map and surround the corresponding variable name with VALUE(...)
                               
                  String i18nMapPropertyName = getLangMapPropertyName(jpaPropertyType);
                  
                  String joinMember = jpaPropertyName+"."+i18nMapPropertyName;
                  
                  //... e.g. myentity.title => JoinName: myentity.title.map, JoinVar: myentityTitleMap
                  String joinName = new StringBuilder().append(jpaEntityName).append(".")
                          .append(joinMember).toString();
                  String joinVar = new StringBuilder().append(jpaEntityName).append(StringUtils.capitalize(jpaPropertyName)).append(StringUtils.capitalize(i18nMapPropertyName)).toString();
                  
                  StringBuilder joinSb = new StringBuilder(joinName).append(" ").append(joinVar);
                  if (!joins.containsKey(joinName)) {
                    this.joins.put(joinName, joinSb.toString());
                  }
                  
                  sb.append("VALUE(").append(joinVar).append(")");
                } 
                //...otherwise just add the member as the property of the entity to the query
                else {
                  
                  sb.append(jpaEntityName).append(".").append(this.currentMember);
                }
			}
			// size = 2 > we have something like "A/B" or "A/any(d:d/B eq 1)
			else if (items.size() == 2) {
				// ...check the parts
				UriResource right = items.get(1);
				UriResource left = items.get(0);

				// ......lambda?
				if (right instanceof UriResourceLambdaAll || right instanceof UriResourceLambdaAny) {

					this.currentMember = StringUtils.uncapitalize(((UriResourcePartTyped) left).toString(true));

					if (left instanceof UriResourcePartTyped) {

						// in ALL case we handle the lambda with subselect
						if (right instanceof UriResourceLambdaAll) {

							// ...we save the FROM clause of Sub select
							String jpaEntityProperty = StringUtils
									.uncapitalize(((UriResourcePartTyped) left).toString(true));
							StringBuilder subSelectFromClause = new StringBuilder()
									.append(this.entityStack.peek().getLeft()).append(".").append(jpaEntityProperty)
									.append(" ");

							UriResourceLambdaAll all = (UriResourceLambdaAll) right;

							subSelectFromClause.append(all.getLambdaVariable());
							// ...check for special case acls -> in this case we should add the acl addon for sub select
							// as well
							if (StringUtils.equals(jpaEntityProperty, "principalsAclElements")) {
								subSelectFromClause.append(" WHERE ").append(AclQueryAddon.QUERY_PLACEHOLDER);
							}

							sb.append(visitLambdaExpressionInternal(LAMBDA_ALL, all.getLambdaVariable(),
									all.getExpression(), subSelectFromClause.toString()));
						}
						// in ANY case we handle the lambda with join
						else {

							String joinName = new StringBuilder().append(this.entityStack.peek().getLeft()).append(".")
									.append(StringUtils.uncapitalize(((UriResourcePartTyped) left).toString(true)))
									.toString();
							StringBuilder joinSb = new StringBuilder(joinName).append(" ");

							UriResourceLambdaAny any = (UriResourceLambdaAny) right;

							sb.append(visitLambdaExpressionInternal(LAMBDA_ANY, any.getLambdaVariable(),
									any.getExpression(), null));
							joinSb.append(any.getLambdaVariable());

							if (!joins.containsKey(joinName)) {
								joins.put(joinName, joinSb.toString());
							}

						}
					}
				}
				// ......otherwise
				else {

					// ...since we have navigation to single cardinality (e.g. A/B) we should set corresponding jpa
					// entity (=A) and member (=B)

					this.currentMember = StringUtils.uncapitalize(((UriResourcePartTyped) right).toString(true));
					String jpaPropertyName = StringUtils.uncapitalize(((UriResourcePartTyped) left).toString(true));

					Pair<String, Class<?>> jpaEntityPair = this.entityStack.peek();
					Class<?> jpaEntity = jpaEntityPair.getRight();
					Class<?> jpaPropertyType = null;
					try {
                      UriResourcePartTyped leftTyped = (UriResourcePartTyped) left;
                      EdmType leftType = leftTyped.getType();
                      FullQualifiedName leftFQN = leftType.getFullQualifiedName();

                      // ...check whether the left side is the lambda variable
                      if (lambdaVariable != null && StringUtils.equals(jpaPropertyName, this.lambdaVariable)) {

                          // --- generate the subquery prefix (in case of ALL lambda)
                          if (StringUtils.equals(this.lambdaFunction, LAMBDA_ALL)) {
                              sb.append(this.lambdaFunction).append("(SELECT ");
                          }

                          // we should check whether the left side is system object. In this case the type of the
                          // field is String but we need the system entity
                          if (BackendServiceEdmProvider.isApplicationTypeEntity(leftFQN)) {
                              // in case of user type we have navigation property on the Odata layer (e.g.
                              // OfficeInCharge/UniqueName) but only simple property
                              // on JPA (e.g. officeInCharge of type string).
                              // ...so because of that we set the current member to left side and ignore the right
                              // side at
                              // all
                              this.currentMember = jpaPropertyName;
                              sb.append(this.currentMember);

                          } else {
                          
                              //if we have the lambda variable we should additionally check whether the current entity is enum -> 
                              //in this case we ignore the current member since the entity itself should be compared with the value
                              if (Enum.class.isAssignableFrom(jpaEntity)) {
                                  // ...in this case we just add the lambda variable as property name
                                  sb.append(jpaPropertyName);
                              } else {
                                  // ...otherwise we use the lambda variable in the combination with the requested member 
                                  sb.append(jpaPropertyName).append(".").append(this.currentMember);
                              }
  
                          }
                          
                          // --- generate the sub query suffix
                          if (StringUtils.equals(this.lambdaFunction, LAMBDA_ALL)) {
                              sb.append(" FROM ").append(this.lambdaFrom).append(")");
                          }
                      } else {

                          //...otherwise handle the normal A/B membership
                          jpaPropertyType = getJpaPropertyType(jpaEntity, jpaPropertyName);

                          // we should check whether the left side is system object. In this case the type of the
                          // field is String but we need the system entity
                          if (BackendServiceEdmProvider.isApplicationTypeEntity(leftFQN)) {
                              // in case of user type we have navigation property on the Odata layer (e.g.
                              // OfficeInCharge/UniqueName) but only simple property
                              // on JPA (e.g. officeInCharge of type string).
                              // ...so because of that we set the current member to left side and ignore the right
                              // side at
                              // all
                              this.currentMember = jpaPropertyName;
                              sb.append(this.entityStack.peek().getLeft()).append(".").append(this.currentMember);

                          }
                          // ... else ...
                          // in all other cases we assume, that this is jpa association and retrieve the corresponding
                          // type from the field
                          else {
                              Class<?> innerEntity;
                              if (Collection.class.isAssignableFrom(jpaPropertyType)) {
                                  Type jpaPropertyGenericType = getJpaPropertyGenericType(jpaEntity, jpaPropertyName);
                                  ParameterizedType innerEntityType = (ParameterizedType) jpaPropertyGenericType;
                                  Type[] innerEntityGenerics = innerEntityType.getActualTypeArguments();
                                  innerEntity = (Class<?>) innerEntityGenerics[innerEntityGenerics.length - 1];
                              } else {
                                  innerEntity = jpaPropertyType;
                              }

                              this.entityStack.push(
                                      new ImmutablePair<String, Class<?>>(getEntityVar(innerEntity), innerEntity));

                              // TODO: check it later... wrong, we should use fieldnames instaed classnames
                              for (int i = 0; i < this.entityStack.size() - 1; i++) {
                                  sb.append(this.entityStack.get(i).getLeft()).append(".");
                              }

                              sb.append(jpaPropertyName).append(".").append(this.currentMember);

                          }
                      }
					} catch (NoSuchFieldException | SecurityException e) {
						throw new ExpressionVisitException(
								"Can't parse filter. Can't retrieve the inner jpa entity for member ["
										+ this.currentMember + "] and expression [" + member.toString()
										+ "]. Jpa Property Type is [" + jpaPropertyType + "] of Entity [" + jpaEntity
										+ "]",
								e);
					}
				}
			}
			// size > 2 : we have something like A/B/C/... - in this case we only support simple deep navigation or
			// lambda expression at the end (e.a. A/B/any)
			else {

				// ...since we have deep navigation to single cardinality (e.g. A/B/C/...) we should set corresponding
				// jpa
				// entity (=A) and connect members (=B,C,...) via '.'
				UriResource left = items.get(0);
				UriResource last = items.get(items.size() - 1);

				// ......lambda?
				if (last instanceof UriResourceLambdaAll || last instanceof UriResourceLambdaAny) {

					List<UriResource> middleItems = items.subList(1, items.size() - 1);
					UriResource secondToLast = middleItems.get(middleItems.size() - 1);

					this.currentMember = StringUtils.uncapitalize(((UriResourcePartTyped) left).toString(true));

					if (secondToLast instanceof UriResourcePartTyped) {

						if (last instanceof UriResourceLambdaAll) {

							String jpaEntityProperty = StringUtils
									.uncapitalize(((UriResourcePartTyped) secondToLast).toString(true));
							// ...we save the FROM clause of Sub select
							StringBuilder subSelectFromClause = new StringBuilder()
									.append(this.entityStack.peek().getLeft());
							subSelectFromClause.append(".")
									.append(StringUtils.uncapitalize(((UriResourcePartTyped) left).toString(true)));
							for (UriResource middleItem : middleItems) {
								subSelectFromClause.append(".").append(
										StringUtils.uncapitalize(((UriResourcePartTyped) middleItem).toString(true)));
							}
							subSelectFromClause.append(" ");

							UriResourceLambdaAll all = (UriResourceLambdaAll) last;

							subSelectFromClause.append(all.getLambdaVariable());
							// ...check for special case acls -> in this case we should add the acl addon as well
							if (StringUtils.equals(jpaEntityProperty, "principalsAclElements")) {
								subSelectFromClause.append(" WHERE ").append(AclQueryAddon.QUERY_PLACEHOLDER);
							}

							sb.append(visitLambdaExpressionInternal(LAMBDA_ALL, all.getLambdaVariable(),
									all.getExpression(), subSelectFromClause.toString()));
						} else {

							String joinMember = StringUtils.uncapitalize(((UriResourcePartTyped) left).toString(true));
							String joinName = new StringBuilder().append(this.entityStack.peek().getLeft()).append(".")
									.append(joinMember).toString();
							StringBuilder joinSb = new StringBuilder(joinName).append(" ");

							String joinVar = joinMember;
							for (UriResource middleItem : middleItems) {

								// ...add middleItem as variable and corresponding additional join
								joinSb.append(joinVar);

								if (!joins.containsKey(joinName)) {
									joins.put(joinName, joinSb.toString());
								}

								joinMember = StringUtils
										.uncapitalize(((UriResourcePartTyped) middleItem).toString(true));
								joinName = new StringBuilder().append(joinVar).append(".").append(joinMember)
										.toString();

								joinSb = new StringBuilder(joinName).append(" ");

								// ...save the joinMemeber as joinVar for the next iteration
								joinVar = joinMember;

							}

							UriResourceLambdaAny any = (UriResourceLambdaAny) last;

							sb.append(visitLambdaExpressionInternal(LAMBDA_ANY, any.getLambdaVariable(),
									any.getExpression(), null));
							joinSb.append(any.getLambdaVariable());

							if (!joins.containsKey(joinName)) {
								joins.put(joinName, joinSb.toString());
							}

						}
					}
				} else {
					String jpaPropertyName = StringUtils.uncapitalize(((UriResourcePartTyped) left).toString(true));

					List<UriResource> memberItems = items.subList(1, items.size());

					Pair<String, Class<?>> jpaEntityPair = this.entityStack.peek();
					Class<?> jpaEntity = jpaEntityPair.getRight();
					Class<?> jpaPropertyType = null;
					try {

						// ...check whether the left side is the lambda variable
						if (lambdaVariable != null && StringUtils.equals(jpaPropertyName, this.lambdaVariable)) {

							// --- generate the subquery prefix
							if (StringUtils.equals(this.lambdaFunction, LAMBDA_ALL)) {
								sb.append(this.lambdaFunction).append("(SELECT ");
							}

							// ...in this case we use the lambda variable in the combination with the requested member
							sb.append(jpaPropertyName);
							for (UriResource memberItem : memberItems) {
								sb.append(".").append(StringUtils
										.uncapitalize(((UriResourcePartTyped) memberItem).toString(true).toString()));
							}

							// --- generate the sub query suffix
							if (StringUtils.equals(this.lambdaFunction, LAMBDA_ALL)) {
								sb.append(" FROM ").append(this.lambdaFrom).append(")");
							}
						} else {

							// ...otherwise handle the normal A/B membership
							jpaPropertyType = getJpaPropertyType(jpaEntity, jpaPropertyName);

							// we assume, that this is jpa association and retrieve the corresponding
							// type from the field
							Class<?> innerEntity;
							if (Collection.class.isAssignableFrom(jpaPropertyType)) {
								Type jpaPropertyGenericType = getJpaPropertyGenericType(jpaEntity, jpaPropertyName);
								ParameterizedType innerEntityType = (ParameterizedType) jpaPropertyGenericType;
								Type[] innerEntityGenerics = innerEntityType.getActualTypeArguments();
								innerEntity = (Class<?>) innerEntityGenerics[innerEntityGenerics.length - 1];
							} else {
								innerEntity = jpaPropertyType;
							}

							this.entityStack
									.push(new ImmutablePair<String, Class<?>>(getEntityVar(innerEntity), innerEntity));

							// TODO: check it later... wrong, we should use field names instead class names
							for (int i = 0; i < this.entityStack.size() - 1; i++) {
								sb.append(this.entityStack.get(i).getLeft()).append(".");
							}

							sb.append(jpaPropertyName);
							for (UriResource memberItem : memberItems) {
								sb.append(".").append(StringUtils
										.uncapitalize(((UriResourcePartTyped) memberItem).toString(true).toString()));
							}
						}
					} catch (NoSuchFieldException | SecurityException e) {
						throw new ExpressionVisitException(
								"Can't parse filter. Can't retrieve the inner jpa entity for members ["
										+ StringUtils.join(memberItems, ",") + "] and expression [" + member.toString()
										+ "]. Jpa Property Type is [" + jpaPropertyType + "] of Entity [" + jpaEntity
										+ "]",
								e);
					}
				}
			}

			return sb.toString();
		} else {
			throw new ExpressionVisitException("Can't parse filter. The member expression has no items.");
		}
	}

	private Type getJpaPropertyGenericType(Class<?> jpaEntity, String propertyName)
			throws NoSuchFieldException, SecurityException {

		// ...check field
		Field jpaField = jpaEntity.getDeclaredField(propertyName);
		if (jpaField != null) {
			return jpaField.getGenericType();
		} else {
			// ...check getter
			Method jpaPropertyGetter = getJpaPropertyGetter(jpaEntity, propertyName);
			if (jpaPropertyGetter != null) {
				return jpaPropertyGetter.getGenericReturnType();
			}
		}
		return null;
	}

	private Class<?> getJpaPropertyType(Class<?> jpaEntity, String propertyName) throws SecurityException {
		try {
			Field jpaField = jpaEntity.getDeclaredField(propertyName);
			return jpaField.getType();
		} catch (NoSuchFieldException e) {
			// ...check getter
			Method jpaPropertyGetter = getJpaPropertyGetter(jpaEntity, propertyName);
			if (jpaPropertyGetter != null) {
				return jpaPropertyGetter.getReturnType();
			}
		}
		return null;
	}

	private Method getJpaPropertyGetter(Class<?> jpaEntity, String propertyName) {
		for (Method method : jpaEntity.getMethods()) {
			if ((method.getName().startsWith("get")) && (method.getName().length() == (propertyName.length() + 3))) {
				if (method.getName().toLowerCase().endsWith(propertyName.toLowerCase())) {
					// ...found getter run it
					return method;
				}
			}
		}
		return null;
	}

	private String getEntityVar(Class<?> jpaEntity) {
		return StringUtils.lowerCase(jpaEntity.getSimpleName());
	}

	@Override
	public String visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException {
		throw new ExpressionVisitException("Can't parse filter. Alias are currently not supported.");
	}

	@Override
	public String visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException {
		// TODO: correct?
		return new StringBuilder().append(type.getName()).toString();
	}

	@Override
	public String visitLambdaReference(String variableName) throws ExpressionVisitException, ODataApplicationException {
		throw new ExpressionVisitException("Can't parse filter. Kambd are currently not supported.");
	}

	@Override
	public String visitEnum(EdmEnumType type, List<String> enumValues)
			throws ExpressionVisitException, ODataApplicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getJoins() {
		return this.joins;
	}

}
