package {service.namespace}.auth;

import com.sap.security.um.user.User;

public abstract class AbstractApplicationUserProvider implements IApplicationUserProvider{
	
	
	protected IApplicationUser toApplicationUserCustom(User user, IApplicationUser applicationUser) {
		//extension Point to override the application user
		return applicationUser;
	}
}
