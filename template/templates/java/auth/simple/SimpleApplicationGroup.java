package {service.namespace}.auth.simple;

import java.util.Collections;
import java.util.Set;

import {service.namespace}.auth.IApplicationGroup;

public class SimpleApplicationGroup implements IApplicationGroup {

    private String uniqueName;
    private String displayName;

    public SimpleApplicationGroup(String uniqueName, String displayName) {
        super();
        this.uniqueName = uniqueName;
        this.displayName = displayName;
    }
    
    @Override
    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
    
	@Override
	public Set<String> getDirectGroupUniqueNames() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<String> getRecursiveGroupUniqueNames() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<String> getDirectUserUniqueNames() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<String> getRecursiveUserUniqueNames() {
		return Collections.EMPTY_SET;
	}

}
