package {service.namespace}.auth;

import java.util.HashSet;
import java.util.Set;

public class PrincipalSearchRequest implements IPrincipalSearchRequest {
	private String searchTerm;
	private Set<String> containedInGroups = new HashSet<String>();
	private Set<String> hide = new HashSet<String>();

	@Override
	public Set<String> getContainedInGroups() {
		return containedInGroups;
	}

	public void setContainedInGroups(Set<String> containedInGroups) {
		this.containedInGroups = containedInGroups;
	}

	@Override
	public Set<String> getHide() {
		return hide;
	}

	public void setHide(Set<String> hide) {
		this.hide = hide;
	}

	@Override
	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PrincipalSearchRequest [searchTerm=" + searchTerm + ", containedInGroups=" + containedInGroups
            + ", hide=" + hide + "]";
    }
}
