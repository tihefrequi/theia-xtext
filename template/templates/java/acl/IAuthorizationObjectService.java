/**
 * 
 */
package {service.namespace}.acl;

import java.util.Set;

import {service.namespace}.auth.Principals;

/**
 * @author storm
 *
 */
public interface IAuthorizationObjectService {

	public Set<String> getParentGroups();

	public Principals getPrincipals();

}
