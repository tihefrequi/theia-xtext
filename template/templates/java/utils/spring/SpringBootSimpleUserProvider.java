package {service.namespace}.utils.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;

public class SpringBootSimpleUserProvider implements IApplicationUserProvider {

	private IApplicationUser simpleUser = new SpringBootSimpleUser();
	
	@Override
	public int countUsers(IPrincipalSearchRequest searchRequest) {

		return 1;
	}

	@Override
	public IApplicationUser[] getUsers(IPrincipalSearchRequest searchRequest) {
		
		return new IApplicationUser[] {simpleUser};
	}

	@Override
	public IApplicationUser getLoggedInUser(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

		return simpleUser;
	}

	@Override
	public IApplicationUser getLoggedInUser() {

		return simpleUser;
	}

	@Override
	public IApplicationUser getUserByUniqueName(String userUniqueName) {

		return simpleUser;
	}

}
