package {service.namespace}.acl;

import java.util.Collection;
import java.util.Collections;

import {service.namespace}.acl.AclQueryAddon.AUTHORIZATION;

/**
 * Acl Query Addon
 */
public class AclQueryAddon {
	
    public static final String QUERY_PLACEHOLDER = "::ACLADDON::";
    
	public enum AUTHORIZATION {
		NONE, ALL, BYUSERANDGROUPS
	}

	private AUTHORIZATION authorization;
	private String aclElementName;
	private Collection<String> roleUniqueNames;
	private Collection<String> groupUniqueNames;
	private Collection<String> userUniqueNames;

	public AclQueryAddon(String aclElementName, AUTHORIZATION authorization) {
		this(aclElementName, authorization, Collections.<String>emptySet(), Collections.<String>emptySet(), Collections.<String>emptySet());
		if (AUTHORIZATION.BYUSERANDGROUPS.equals(authorization)) {
			throw new IllegalArgumentException("It is not allowed to use authorization "
					+ AUTHORIZATION.BYUSERANDGROUPS.name() + " Use other constructors for this usecase.");
		}
	}

	public AclQueryAddon(String aclElementName, Collection<String> roleUniqueNames, Collection<String> groupUniqueNames,
			Collection<String> userUniqueNames) {
		this(aclElementName, AUTHORIZATION.BYUSERANDGROUPS, roleUniqueNames, groupUniqueNames, userUniqueNames);
	}

	private AclQueryAddon(String aclElementName, AUTHORIZATION authorization, Collection<String> roleUniqueNames, Collection<String> groupUniqueNames,
			Collection<String> userUniqueNames) {
		super();
		this.aclElementName = aclElementName;
		this.authorization = authorization;
		this.roleUniqueNames = roleUniqueNames;
		this.groupUniqueNames = groupUniqueNames;
		this.userUniqueNames = userUniqueNames;
	}

	public boolean hasUsers() {
		return !userUniqueNames.isEmpty();
	}

	public boolean hasGroups() {
		return !groupUniqueNames.isEmpty();
	}

	public boolean hasRoles() {
		return !roleUniqueNames.isEmpty();
	}

	public String getAclElementName() {
		return aclElementName;
	}

	public Collection<String> getRoleUniqueNames() {
		return roleUniqueNames;
	}
	
	public Collection<String> getGroupUniqueNames() {
		return groupUniqueNames;
	}

	public Collection<String> getUserUniqueNames() {
		return userUniqueNames;
	}

	public AUTHORIZATION getAuthorization() {
		return authorization;
	}
	
	public boolean hasAuthorizationBYUSERANDGROUPS() {
		return AUTHORIZATION.BYUSERANDGROUPS.equals(authorization);
	}

	public boolean hasAuthorizationALL() {
		return AUTHORIZATION.ALL.equals(authorization);
	}

	public boolean hasAuthorizationNONE() {
		return AUTHORIZATION.NONE.equals(authorization);
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AclQueryAddon [aclElementName=").append(aclElementName)
				.append(", authorization=" + authorization.name());

		if (!roleUniqueNames.isEmpty()) {
			builder.append(", roleUniqueNames=").append(roleUniqueNames);
		}
		if (!groupUniqueNames.isEmpty()) {
			builder.append(", groupUniqueNames=").append(groupUniqueNames);
		}
		if (!userUniqueNames.isEmpty()) {
			builder.append(", userUniqueNames=").append(userUniqueNames);
		}
		builder.append("]");
		return builder.toString();
	}

}
