package {service.namespace}.utils.sap.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.json.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.NotImplementedException;

import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserAttribute;
import com.sap.security.um.user.UserProvider;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;

public class IASApplicationUserProvider implements IApplicationUserProvider {

	protected UserProvider userProvider;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IASApplicationUserProvider.class);
	
	public IASApplicationUserProvider(){
		try {
			InitialContext ctx = new InitialContext();
			userProvider = (UserProvider) ctx.lookup("java:comp/env/user/Provider");
		} catch(NamingException e) {
			LOGGER.error("Couldn't find the user provider", e);
		}
		
	}
	
	@Override
	public IApplicationUser getLoggedInUser(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		if (userProvider != null) {
			try {
				User user = userProvider.getUser(servletRequest.getUserPrincipal().getName());
				if (user == null) {
					throw new RuntimeException(
							"User Provider cannot retrieve logged-in user. the user in servletrequest was: "
									+ servletRequest.getUserPrincipal().getName());
				}
				return toApplicationUser(user);
			} catch (Exception e) {
				throw new RuntimeException("Can't get logged-in user given in request.", e);
			}
		}
		throw new RuntimeException("User Provider cannot be retrieved. So we can't get the logged-in-user");

	}

	@Override
	public IApplicationUser getLoggedInUser() {
		if (userProvider != null) {
			try {
				User user = userProvider.getCurrentUser();
				if (user == null) {
					throw new RuntimeException("User Provider cannot retrieve logged-in user. user=" + user);
				}
				return toApplicationUser(user);
			} catch (Exception e) {
				throw new RuntimeException("Can't get logged-in user from userprovider", e);
			}
		}
		throw new RuntimeException("User Provider cannot be retrieved. So we can't get the logged-in-user");
	}

	@Override
	public int countUsers(IPrincipalSearchRequest searchRequest) {
		
		String searchTerm = searchRequest.getSearchTerm();
		if (searchTerm != null) {
			Set<String> userNames = userProvider.searchUser(UserAttribute.NAME, searchTerm, UserProvider.SearchOperator.EQUALS, UserProvider.CaseSensitive.NO);
			if (userNames != null) {
				return userNames.size();			
			} else {
				return 0;
			}
		} else {
			LOGGER.error("This count without search term is not supported!");
			return -1;
		}

	}

	@Override
	public IApplicationUser[] getUsers(IPrincipalSearchRequest searchRequest) {
		List<IApplicationUser> applicationUsers = new ArrayList<IApplicationUser>();
		if (userProvider != null) {
		  String searchTerm = searchRequest.getSearchTerm();
		  if (searchTerm != null && !"".equals(searchTerm)) {
			Set<String> userNames =
				userProvider.searchUser(
					UserAttribute.NAME,
					searchTerm,
					UserProvider.SearchOperator.EQUALS,
					UserProvider.CaseSensitive.NO);
			if (userNames != null) {
			  for (String userName : userNames) {
				try {
				  applicationUsers.add(toApplicationUser(userProvider.getUser(userName)));
				} catch (Exception e) {
				  LOGGER.error("Can't get user with name [" + userName + "]", e);
				}
			  }
			} else {
			  try {
				User user = userProvider.getUser(searchTerm);
				if (user != null) {
				  applicationUsers.add(toApplicationUser(user));
				}
			  } catch (Exception e) {
				LOGGER.debug("Can't find a user with name [" + searchTerm + "]", e);
			  }
			}
		  } else {
			LOGGER.error("Retrieving of users without search term is not supported! searchTerm="+searchTerm);
		  }
		}
		return applicationUsers.toArray(new IApplicationUser[] {});
	}

	public IApplicationUser getUserByUniqueName(String userUniqueName) {
		if(userProvider != null) {
			try {
				User user = userProvider.getUser(userUniqueName);
				return toApplicationUser(user);
			} catch (Exception e) {
				LOGGER.error("Can't get user with name ["+userUniqueName+"]" , e);
			}
		}
		return null;
	}
	
	protected IApplicationUser toApplicationUser(User user) throws UnsupportedUserAttributeException, IOException {
		IASUser applicationUser = new IASUser(user);
		
		return toApplicationUserCustom(user, applicationUser);
		
		
	}
	
	protected IApplicationUser toApplicationUserCustom(User user, IApplicationUser applicationUser) {
		//extension Point to override the application user
		return applicationUser;
	}

}