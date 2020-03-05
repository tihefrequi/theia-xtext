package {service.namespace}.auth;

import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;

import {service.namespace}.auth.IApplicationGroup;
import {service.namespace}.auth.IPrincipalSearchRequest;

public interface IApplicationGroupProvider {
	public int countGroups(IPrincipalSearchRequest searchRequest);

	public IApplicationGroup[] getGroups(IPrincipalSearchRequest searchRequest);
	
	public IApplicationGroup getGroupByUniqueName(String groupUniqueName);
}