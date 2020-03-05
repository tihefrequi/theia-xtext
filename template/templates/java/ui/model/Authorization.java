package {service.namespace}.ui.model;

import java.util.ArrayList;
import java.util.List;

public class Authorization {
	private List<String> uiAreas = new ArrayList<>();
	private List<String> uiActions = new ArrayList<>();
	private List<String> roles = new ArrayList<>();
	private List<String> groups = new ArrayList<>();

	public List<String> getUiAreas() {
		return uiAreas;
	}

	public void setUiAreas(List<String> uiAreas) {
		this.uiAreas = uiAreas;
	}

	public void addUiArea(String uiArea) {
		this.uiAreas.add(uiArea);
	}

	public List<String> getUiActions() {
		return uiActions;
	}

	public void setUiActions(List<String> uiActions) {
		this.uiActions = uiActions;
	}

	public void addUiAction(String uiAction) {
		this.uiActions.add(uiAction);
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void addRole(String role) {
		this.roles.add(role);
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public void addGroup(String group) {
		this.groups.add(group);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Authorization [uiAreas=").append(uiAreas).append(", uiActions=").append(uiActions).append(", roles=").append(roles).append("]");
		return builder.toString();
	}

}
