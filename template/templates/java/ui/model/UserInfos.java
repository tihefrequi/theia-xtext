package {service.namespace}.ui.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UserInfos {
	private String uniqueName;
	private Authorization authorization;
	private Map<String, String> attributes = new HashMap<>();

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setAttribute(String attributeName, String attributeValue) {
		attributes.put(attributeName, attributeValue);
	}

	public void addAttributes(Map<String, String> attributes) {
		if (attributes != null) {
			for (Entry<String, String> attribute : attributes.entrySet()) {
				setAttribute(attribute.getKey(), attribute.getValue());
			}
		}
	}

}
