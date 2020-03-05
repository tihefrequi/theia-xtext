package {service.namespace}.utils.sap.cloud;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;

import {service.namespace}.auth.IApplicationGroup;
import {service.namespace}.auth.IApplicationGroupProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;
import {service.namespace}.auth.simple.SimpleApplicationGroup;

import {service.namespace}.auth.IApplicationGroup;
import {service.namespace}.auth.simple.SimpleApplicationGroup;
import {service.namespace}.auth.IPrincipalSearchRequest;

public class IASApplicationGroupProvider implements IApplicationGroupProvider {

	// TEST GROUPS
	private static List<SimpleApplicationGroup> APP_GROUPS;

	static {
		APP_GROUPS = new ArrayList<SimpleApplicationGroup>();
		APP_GROUPS.add(new SimpleApplicationGroup("group1", "Test Group 1"));
		APP_GROUPS.add(new SimpleApplicationGroup("group2", "Test Group 2"));
		APP_GROUPS.add(new SimpleApplicationGroup("group3", "Test Group 3"));
		APP_GROUPS.add(new SimpleApplicationGroup("group4", "Test Group 4"));
		APP_GROUPS.add(new SimpleApplicationGroup("group5", "Test Group 5"));
	}

	@Override
	public int countGroups(IPrincipalSearchRequest searchRequest) {
		// TODO: Check fitler
		return APP_GROUPS.size();
	}

	@Override
	public IApplicationGroup[] getGroups(IPrincipalSearchRequest searchRequest) {
		// TODO: Check filter and search parameter
		return APP_GROUPS.toArray(new SimpleApplicationGroup[] {});
	}


	public IApplicationGroup getGroupByUniqueName(String groupUniqueName) {
		for (SimpleApplicationGroup appGroup : APP_GROUPS) {
			if (appGroup.getUniqueName().equals(groupUniqueName)) {
				return appGroup;
			}
		}
		return null;
	}
}
