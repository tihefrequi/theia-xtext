package {service.namespace}.odata.edm;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.AnnotationElement;

/**
 * Helper class to build annotations elements with method chains
 * 
 * @author storm
 *
 */
public class AnnotationBuilder {

	/**
	 * OData V4 annotations related elements
	 * 
	 * @author storm
	 *
	 */
	public static enum Element {
		TERM("Term"), ANNOTATIONS("Annotations"), ANNOTATION("Annotation"), PROPERTY_VALUE("PropertyValue");
		
		private String text;
		private Element(String text) { this.text = text; }
		public String text() { return this.text; } 
	}
	
	/**
	 * OData V4 annotations related attributes
	 * 
	 * @author storm
	 *
	 */
	public static enum Attribute {
		NAME("Name"), TYPE("Type"), BASE_TERM("BaseTerm"), DEFAULT_VALUE("DefaultValue"), APPLIES_TO("AppliesTo"),
		TARGET("Target"), QUALIFIER("Qualifier"), TERM("Term"), FUNCTION("Function"), PROPERTY("Property");
		
		private String text;
		private Attribute(String text) { this.text = text; }
		public String text() { return this.text; } 
	}

	
	/**
	 * OData V4 annotations related expressions
	 * 
	 * @author storm
	 *
	 */
	public static enum Expression {
		BINARY("Binary"), BOOL("Bool"), DATE("Date"), DATE_TIME_OFFSET("DateTimeOffset"), DECIMAL("Decimal"),
		DURATION("Duration"), ENUM_MEMBER("EnumMember"), FLOAT("Float"), GUID("Guid"), INT("Int"), STRING("String"),
		TIME_OF_DAY("TimeOfDay"), AND("And"), OR("Or"), NOT("Not"), EQ("Eq"), NE("Ne"), GT("Gt"), GE("Ge"), LT("Lt"), LE("Le"),
		ANNOTATION_PATH("AnnotationPath"), APPLY("Apply"), CAST("Cast"), COLLECTION("Collection"), IF("If"), IS_OF("IsOf"),
		LABELED_ELEMENT("LabeledElement"), LABELED_ELEMENT_REFERENCE("LabeledElementReference"), NULL("Null"),
		NAVIGATION_PROPERTY_PATH("NavigationPropertyPath"), PATH("Path"), PROPERTY_PATH("PropertyPath"), RECORD("Record"),
		URL_REF("UrlRef");
		
		private String text;
		private Expression(String text) { this.text = text; }
		public String text() { return this.text; } 
	}

	/**
	 * OData V4 annotations related function
	 * 
	 * @author storm
	 *
	 */
	public static enum Function {
		ODATA_CONCAT("odata.concat"),  ODATA_FILL_URI_TEMPLATE("odata.fillUriTemplate"), ODATA_URI_ENCODE("odata.uriEncode"); 
		
		private String text;
		private Function(String text) { this.text = text; }
		public String text() { return this.text; } 
	}
	
	//Current annotation element to be build
	private AnnotationElement curElement; 
	
	//Private constructors to build a new elment
	private AnnotationBuilder(Element el) {
		this.curElement = new AnnotationElement();
		curElement.setName(el.text());
	}
	private AnnotationBuilder(Expression expr) {
		this.curElement = new AnnotationElement();
		curElement.setName(expr.text());
	}
	
	/**
	 * Build a new annotation element from OData element
	 * @return
	 */
	public static AnnotationBuilder element(Element el) {
		AnnotationBuilder elBuilder = new AnnotationBuilder(el);
		return elBuilder;
		
	}

	/**
	 * Build a new annotation element from OData expression
	 * @return
	 */
	public static AnnotationBuilder element(Expression expr) {
		AnnotationBuilder elBuilder = new AnnotationBuilder(expr);
		return elBuilder;
	}
	
	/**
	 * Build a new annotation attribute from OData attribute
	 * @return
	 */
	public static AnnotationAttribute attr(Attribute attr, String value) {
		AnnotationAttribute attribute = new AnnotationAttribute();
		
		attribute.setName(attr.text());
		attribute.setText(value);
		return attribute;
	}

	/**
	 * Build a new annotation attribute from OData attribute
	 * @return
	 */
	public static AnnotationAttribute attr(Expression expr, String value) {
		AnnotationAttribute attribute = new AnnotationAttribute();
		
		attribute.setName(expr.text());
		attribute.setText(value);
		return attribute;
	}


	/**
	 * Build a new annotation attribute from OData attribute
	 * @return
	 */
	public static AnnotationAttribute attr(Function func) {
		AnnotationAttribute attribute = new AnnotationAttribute();
		
		attribute.setName(Attribute.FUNCTION.text());
		attribute.setText(func.text());
		return attribute;
	}

	
	/**
	 * Set namespace to the current element
	 * @param namespace
	 * @return
	 */
	public AnnotationBuilder ns(String namespace) {
		this.curElement.setNamespace(namespace);
		return this;
	}
	
	/**
	 * Set given attributes to current annotation element
	 * @param annotationAttribute
	 * @return
	 */
	public AnnotationBuilder attributes(AnnotationAttribute... annotationAttributes) {
		
		List<AnnotationAttribute> attributes = new ArrayList<AnnotationAttribute>(); 
		for(AnnotationAttribute annotationAttribute: annotationAttributes) {
			attributes.add(annotationAttribute);
		}
		this.curElement.setAttributes(attributes);
		
		return this;
	}

	
	/**
	 * Set given children to annotation element
	 * @param annotationAttribute
	 * @return
	 */
	public AnnotationBuilder children(AnnotationElement... annotationElements) {
		
		List<AnnotationElement> elements = new ArrayList<AnnotationElement>(); 
		for(AnnotationElement annotationElement: annotationElements) {
			elements.add(annotationElement);
		}
		this.curElement.setChildElements(elements);
		
		return this;
	}
	
	/**
	 * Build the annotation element
	 * @param
	 * @return
	 */
	public AnnotationElement build() {
		return this.curElement;
	}
	
}
