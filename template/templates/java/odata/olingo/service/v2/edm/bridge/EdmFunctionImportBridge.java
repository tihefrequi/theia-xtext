package {service.namespace}.odata.edm.bridge;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

public class EdmFunctionImportBridge implements EdmFunctionImport {

	private org.apache.olingo.odata2.api.edm.EdmFunctionImport edmFunctionImport;

	public EdmFunctionImportBridge(org.apache.olingo.odata2.api.edm.EdmFunctionImport edmFunctionImport) {
		this.edmFunctionImport = edmFunctionImport;
	}

	@Override
	public FullQualifiedName getFullQualifiedName() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmEntitySet getReturnedEntitySet() {
		try {
			return new EdmEntitySetBridge(this.edmFunctionImport.getEntitySet());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmEntityContainer getEntityContainer() {
		try {
			return new EdmEntityContainerBridge(this.edmFunctionImport.getEntityContainer());
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public String getName() {
		try {
			return this.edmFunctionImport.getName();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmAnnotation getAnnotation(EdmTerm paramEdmTerm, String paramString) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmAnnotation> getAnnotations() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmFunction> getUnboundFunctions() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmFunction getUnboundFunction(List<String> paramList) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public FullQualifiedName getFunctionFqn() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public String getTitle() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public boolean isIncludeInServiceDocument() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

}
