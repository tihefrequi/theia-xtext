package {service.namespace}.auth.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;
import {service.namespace}.auth.simple.SimpleApplicationUser;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;
import {service.namespace}.auth.simple.SimpleApplicationUser;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.simple.SimpleApplicationUser;
import {service.namespace}.auth.IPrincipalSearchRequest;

public class DemoApplicationUserProvider implements IApplicationUserProvider {

	// TEST USERS
	private static List<SimpleApplicationUser> APP_USERS;
	static {
		APP_USERS = new ArrayList<SimpleApplicationUser>();
		APP_USERS.add(new SimpleApplicationUser("user1", "Test User 1", "Test", "User 1", "R&S", "group1", "group2",
				"group3", "group4", "group5")
				.roles("role1"));
		APP_USERS.add(new SimpleApplicationUser("user2", "Test User 2", "Test", "User 2", "SAP", "group2", "group3",
				"group4", "group5")
				.roles("role2"));
		APP_USERS.add(new SimpleApplicationUser("user3", "Test User 3", "Test", "User 3", "Microsoft", "group3",
				"group4", "group5").roles("role1","role2"));
		APP_USERS.add(new SimpleApplicationUser("user4", "Test User 4", "Test", "User 4", "IBM", "group4", "group5"));
		APP_USERS.add(new SimpleApplicationUser("user5", "Test User 5", "Test", "User 5", "Apple", "group5"));
	}

	@Override
	public IApplicationUser getLoggedInUser(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		return APP_USERS.get(0);
	}

	@Override
	public IApplicationUser getLoggedInUser() {
		return APP_USERS.get(0);
	}

	@Override
	public int countUsers(IPrincipalSearchRequest searchRequest) {
		String search = searchRequest.getSearchTerm();
		if (search != null) {
			// TODO: search dummmy, replace with right search
			int i = 0;
			String searchTerm = search.toLowerCase();
			for (IApplicationUser appUser : APP_USERS) {

				if (!searchRequest.getHide().contains(appUser.getUniqueName())
						&& (searchRequest.getContainedInGroups().isEmpty() || CollectionUtils
								.containsAny(appUser.getGroupUniqueNames(), searchRequest.getContainedInGroups()))
						&& (appUser.getFirstName().toLowerCase().contains(searchTerm)
								|| appUser.getLastName().toLowerCase().contains(searchTerm)
								|| appUser.getDisplayName().toLowerCase().contains(searchTerm)
								|| appUser.getUniqueName().toLowerCase().contains(searchTerm))) {
					i++;
				}
			}

			return i++;
		} else {
			return APP_USERS.size();
		}

	}

	@Override
	public IApplicationUser[] getUsers(IPrincipalSearchRequest searchRequest) {
		String search = searchRequest.getSearchTerm();
		if (search != null) {
			List<SimpleApplicationUser> foundUsers = new ArrayList<SimpleApplicationUser>();
			String searchTerm = search.toLowerCase();
			for (SimpleApplicationUser appUser : APP_USERS) {
				if (!searchRequest.getHide().contains(appUser.getUniqueName())
						&& (searchRequest.getContainedInGroups().isEmpty() || CollectionUtils
								.containsAny(appUser.getGroupUniqueNames(), searchRequest.getContainedInGroups()))
						&& (appUser.getFirstName().toLowerCase().contains(searchTerm)
								|| appUser.getLastName().toLowerCase().contains(searchTerm)
								|| appUser.getDisplayName().toLowerCase().contains(searchTerm)
								|| appUser.getUniqueName().toLowerCase().contains(searchTerm))) {
					foundUsers.add(appUser);
				}
			}
			return foundUsers.toArray(new SimpleApplicationUser[] {});
		} else {
			return APP_USERS.toArray(new SimpleApplicationUser[] {});
		}
	}

	public IApplicationUser getUserByUniqueName(String userUniqueName) {
		for (SimpleApplicationUser appUser : APP_USERS) {
			if (appUser.getUniqueName().equals(userUniqueName)) {
				return appUser;
			}
		}
		return null;
	}

}