package {service.namespace}.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IApplicationUserProvider {
  public int countUsers(IPrincipalSearchRequest searchRequest);

  public IApplicationUser[] getUsers(IPrincipalSearchRequest searchRequest);

  public IApplicationUser getLoggedInUser(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse);

  public IApplicationUser getLoggedInUser();

  public IApplicationUser getUserByUniqueName(String userUniqueName);

  public default void updateUser(IApplicationUserChangeBuilder changeBuilder) throws Exception {
    throw new RuntimeException(
        "The update of a User is not possible here. User =" + changeBuilder.getUser() + " changes=" + changeBuilder.getChangeData());
  }
  
}
