package {service.namespace}.odata.edm.bridge;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

/**
 * 
 * Implementation of the bridge between Edm V4 and Edm V2 classes
 *
 */
public class EdmBridge implements Edm {

	private org.apache.olingo.odata2.api.edm.Edm edm;

	public EdmBridge(org.apache.olingo.odata2.api.edm.Edm edm) {
		this.edm = edm;
	}

	@Override
	public EdmAnnotations getAnnotationGroup(FullQualifiedName targetName, String qualifier) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmAction getBoundAction(FullQualifiedName actionName, FullQualifiedName bindingParameterTypeName,
			Boolean isBindingParameterCollection) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmAction getBoundActionWithBindingType(FullQualifiedName bindingParameterTypeName,
			Boolean isBindingParameterCollection) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmFunction getBoundFunction(FullQualifiedName functionName, FullQualifiedName bindingParameterTypeName,
			Boolean isBindingParameterCollection, List<String> parameterNames) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmFunction> getBoundFunctionsWithBindingType(FullQualifiedName bindingParameterTypeName,
			Boolean isBindingParameterCollection) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmComplexType getComplexType(FullQualifiedName name) {

		try {
			return new EdmComplexTypeBridge(this.edm.getComplexType(name.getNamespace(), name.getName()));
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmEntityContainer getEntityContainer() {
		try {
			return new EdmEntityContainerBridge(this.edm.getEntityContainer(null));
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmEntityContainer getEntityContainer(FullQualifiedName name) {
		try {
			return new EdmEntityContainerBridge(this.edm.getEntityContainer(name.getFullQualifiedNameAsString()));
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmEntityType getEntityType(FullQualifiedName name) {
		try {
			return new EdmEntityTypeBridge(this.edm.getEntityType(name.getNamespace(), name.getName()));
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmEnumType getEnumType(FullQualifiedName name) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmSchema getSchema(String namespace) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmSchema> getSchemas() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmTerm getTerm(FullQualifiedName termName) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmTypeDefinition getTypeDefinition(FullQualifiedName name) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmAction getUnboundAction(FullQualifiedName actionName) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmFunction getUnboundFunction(FullQualifiedName functionName, List<String> parameterNames) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmFunction> getUnboundFunctions(FullQualifiedName functionName) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}
}
