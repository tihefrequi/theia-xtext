package {service.namespace}.ui.model;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import {service.namespace}.auth.IApplicationUser;

public class AuthorizationFactory {

	public static final String AUTHORIZATION_ALLOWED_ALL = "*";

	public static Authorization getAuthorization(IApplicationUser user, HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		Authorization authorization = new Authorization();
		authorization.addUiArea(AUTHORIZATION_ALLOWED_ALL);
		authorization.addUiAction(AUTHORIZATION_ALLOWED_ALL);
		// Todo should only show the defined role of this app for the user
		authorization.setRoles(new LinkedList(user.getRoleUniqueNames()));
		// Todo should only show the defined role of this app for the user
		authorization.setGroups(new LinkedList(user.getGroupUniqueNames()));
		return authorization;
	}

}
