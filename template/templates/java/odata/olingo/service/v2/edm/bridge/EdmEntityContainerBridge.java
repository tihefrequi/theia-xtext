package {service.namespace}.odata.edm.bridge;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

public class EdmEntityContainerBridge implements EdmEntityContainer {

	private org.apache.olingo.odata2.api.edm.EdmEntityContainer edmEntityContainer;

	public EdmEntityContainerBridge(org.apache.olingo.odata2.api.edm.EdmEntityContainer edmEntityContainer) {
		this.edmEntityContainer = edmEntityContainer;
	}

	@Override
	public String getName() {
		try {
			return this.edmEntityContainer.getName();
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmAnnotation getAnnotation(EdmTerm term, String qualifier) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmAnnotation> getAnnotations() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public String getNamespace() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public FullQualifiedName getFullQualifiedName() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmSingleton getSingleton(String name) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmEntitySet getEntitySet(String name) {
		try {
			return new EdmEntitySetBridge(this.edmEntityContainer.getEntitySet(name));
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public EdmActionImport getActionImport(String name) {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public EdmFunctionImport getFunctionImport(String name) {
		try {
			return new EdmFunctionImportBridge(this.edmEntityContainer.getFunctionImport(name));
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public List<EdmEntitySet> getEntitySets() {
		try {
			List<org.apache.olingo.odata2.api.edm.EdmEntitySet> entitySets = this.edmEntityContainer.getEntitySets();
			if (entitySets != null) {
				List<EdmEntitySet> resultEntitySets = new ArrayList<EdmEntitySet>();
				for (org.apache.olingo.odata2.api.edm.EdmEntitySet entitySet : entitySets) {
					resultEntitySets.add(new EdmEntitySetBridge(entitySet));
				}
				return resultEntitySets;
			} else {
				return null;
			}
		} catch (org.apache.olingo.odata2.api.edm.EdmException e) {
			throw new EdmException(e);
		}
	}

	@Override
	public List<EdmFunctionImport> getFunctionImports() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmSingleton> getSingletons() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public List<EdmActionImport> getActionImports() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

	@Override
	public FullQualifiedName getParentContainerName() {
		throw new ODataNotSupportedException("This method is not supported by this bridge");
	}

}
