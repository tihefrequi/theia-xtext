package {service.namespace}.utils.sap.netweaver;

import {service.namespace}.auth.IApplicationUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.UMRuntimeException;
import com.sapportals.wcm.repository.ICollection;
import com.sapportals.wcm.repository.IResource;
import com.sapportals.wcm.repository.IResourceContext;
import com.sapportals.wcm.repository.IResourceFactory;
import com.sapportals.wcm.repository.ResourceContext;
import com.sapportals.wcm.repository.ResourceException;
import com.sapportals.wcm.repository.ResourceFactory;
import com.sapportals.wcm.util.uri.RID;

/**
 * This class provides helper methods for acccessing the KM.
 * 
 * @author storm
 */
public final class KMAccessHelper {

	/** The Constant SERVICE_USER. */
	private static final String SERVICE_USER = "transport_service";

	/**
	 * Hidden: Instantiates a new KM access helper.
	 */
	private KMAccessHelper() {
	}

	/**
	 * Returns the given resource with an elevated resource context. The
	 * resource context is created by using the transport service user.
	 *
	 * @param resource the resource
	 * @return the elevated resource
	 * @throws ResourceException the resource exception
	 */
	public static IResource getElevatedResource(IResource resource) throws ResourceException {
		if (resource == null || SERVICE_USER.equals(resource.getContext().getUserUME().getUniqueName())) {
			return resource;
		}

		return getElevatedResource(resource.getRID());
	}

	/**
	 * Returns the given collection with an elevated resource context. The
	 * resource context is created by using the transport service user.
	 *
	 * @param collection the collection
	 * @return the elevated collection
	 * @throws ResourceException the resource exception
	 */
	public static ICollection getElevatedCollection(ICollection collection) throws ResourceException {
		IResource resource = getElevatedResource(collection);
		if (resource instanceof ICollection) {
			return (ICollection) resource;
		}
		return null;
	}

	/**
	 * Returns the specified resource with an elevated resource context. The
	 * resource context is created by using the transport service user.
	 *
	 * @param rid the rid
	 * @return the elevated resource
	 * @throws ResourceException the resource exception
	 */
	public static IResource getElevatedResource(RID rid) throws ResourceException {
		if (rid == null) {
			return null;
		}

		IResourceFactory factory = ResourceFactory.getInstance();
		return factory.getResource(rid, factory.getServiceContextUME(SERVICE_USER));
	}

    public static IResourceContext getResourceContext() throws ResourceException {
        return ResourceFactory.getInstance().getServiceContextUME(SERVICE_USER);
    }

	/**
     * 
     * @param currentUser
     * @return
     * @throws UMRuntimeException
     * @throws UMException
     */
    public static IResourceContext getResourceContext(IApplicationUser currentUser)
        throws UMRuntimeException, UMException {
        return ResourceContext
            .getInstance(UMFactory.getInstance().getUserFactory().getUserByUniqueName(currentUser.getUniqueName()));
    }

	/**
	 * Returns the specified resource with an elevated resource context. The
	 * resource context is created by using the transport service user.
	 *
	 * @param rid the rid
	 * @return the elevated resource
	 * @throws ResourceException the resource exception
	 */
	public static IResource getElevatedResource(String rid) throws ResourceException {
		return getElevatedResource(RID.getRID(rid));
	}

	/**
	 * Returns the specified collection with an elevated resource context. The
	 * resource context is created by using the transport service user.
	 *
	 * @param rid the rid
	 * @return the elevated collection
	 * @throws ResourceException the resource exception
	 */
	public static ICollection getElevatedCollection(RID rid) throws ResourceException {
		IResource resource = getElevatedResource(rid);
		if (resource instanceof ICollection) {
			return (ICollection) resource;
		}
		return null;
	}

	/**
	 * Returns the specified collection with an elevated resource context. The
	 * resource context is created by using the transport service user.
	 *
	 * @param rid the rid
	 * @return the elevated collection
	 * @throws ResourceException the resource exception
	 */
	public static ICollection getElevatedCollection(String rid) throws ResourceException {
		IResource resource = getElevatedResource(rid);
		if (resource instanceof ICollection) {
			return (ICollection) resource;
		}
		return null;
	}
}
