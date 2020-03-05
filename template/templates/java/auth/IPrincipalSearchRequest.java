package {service.namespace}.auth;

import java.util.Set;

public interface IPrincipalSearchRequest {

	// Get Search Term containing Jokers
	public String getSearchTerm();

	// Restrict Search of Results to Principals that are contained in those
	// Groups
	public Set<String> getContainedInGroups();

	// Filter away all Results matching the given Set
	public Set<String> getHide();
}
