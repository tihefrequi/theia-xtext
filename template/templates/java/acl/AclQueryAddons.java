package {service.namespace}.acl;

import java.util.ArrayList;
import java.util.Set;

import {service.namespace}.acl.AclQueryAddon;
import {service.namespace}.acl.AclQueryAddon.AUTHORIZATION;

@SuppressWarnings("serial")
public class AclQueryAddons extends ArrayList<AclQueryAddon> {

	/**
	 * Has at least one Authorizations for one of the AclElements 
	 * 
	 * @return
	 */
	public boolean hasAuthorizations() {
		return !hasNoAuthorizations();
	}

	/**
	 * Has no Authorizations for any AclElement 
	 * 
	 * @return
	 */
	public boolean hasNoAuthorizations() {
		boolean hasNoAuthorizations = true;
		for (AclQueryAddon aclQueryAddon : this) {
			if (!AUTHORIZATION.NONE.equals(aclQueryAddon.getAuthorization())) {
				hasNoAuthorizations = false;
				break;
			}
		}
		return hasNoAuthorizations;
	}
	
	/**
	 * Has Authorizations of Type NONE for any AclElement 
	 * (these are typcially separately used in a acl query, as they produce AND and not OR statements unlike ALL or BYUSERSANDGROUPS)
	 * 
	 * @return
	 */
	public boolean hasAuthorizationsOfTypeNONE() {
		boolean hasAuthorizationsOfTypeNONE = false;
		for (AclQueryAddon aclQueryAddon : this) {
			if (AUTHORIZATION.NONE.equals(aclQueryAddon.getAuthorization())) {
				hasAuthorizationsOfTypeNONE = true;
				break;
			}
		}
		return hasAuthorizationsOfTypeNONE;
	}

	/**
	 * Has at least one Authorization of Type ALL or BYUSERSANDGROUPS for a
	 * AclElement (these are typcially separately used in a acl query, as they
	 * produce different statements unlike NONE)
	 * 
	 * @return
	 */
	public boolean hasAuthorizationsOfTypeALLorBYUSERSANDGROUP() {
		boolean hasAuthorizationsOfTypeALLorBYUSERSANDGROUP = false;
		for (AclQueryAddon aclQueryAddon : this) {
			if (AUTHORIZATION.ALL.equals(aclQueryAddon.getAuthorization())
					|| AUTHORIZATION.BYUSERANDGROUPS.equals(aclQueryAddon.getAuthorization())) {
				hasAuthorizationsOfTypeALLorBYUSERSANDGROUP = true;
				break;
			}
		}
		return hasAuthorizationsOfTypeALLorBYUSERSANDGROUP;
	}

	/**
	 * Has at least one Authorization of Type ALL for a AclElement
	 * 
	 * @return
	 */
	public boolean hasAuthorizationsOfTypeALL() {
		boolean hasAuthorizationsOfTypeALL = false;
		for (AclQueryAddon aclQueryAddon : this) {
			if (AUTHORIZATION.ALL.equals(aclQueryAddon.getAuthorization())) {
				hasAuthorizationsOfTypeALL = true;
				break;
			}
		}
		return hasAuthorizationsOfTypeALL;
	}

	/**
	 * Checks wether the List of AclQueryAddons contains at least one
	 * BYUSERANDGROUPS AclQuery where the user or groupnames match
	 * 
	 * @param uniqueName
	 * @param groupUniqueNames
	 * @return
	 */
	public boolean isAllowed(String uniqueName, Set<String> groupUniqueNames) {
		boolean isAllowed = false;
		for (AclQueryAddon aclQueryAddon : this) {
			if (AUTHORIZATION.BYUSERANDGROUPS.equals(aclQueryAddon.getAuthorization())) {
				if (aclQueryAddon.getUserUniqueNames().contains(uniqueName)) {
					isAllowed = true;
					break;
				}
				for (String groupUniqueName : groupUniqueNames) {
					if (aclQueryAddon.getGroupUniqueNames().contains(groupUniqueName)) {
						isAllowed = true;
						break;
					}
				}
			}
			if (isAllowed) {
				break;
			}
		}
		return isAllowed;
	}

}
