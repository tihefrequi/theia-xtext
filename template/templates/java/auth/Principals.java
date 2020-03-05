package {service.namespace}.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import {service.namespace}.auth.IPrincipal;
import {service.namespace}.auth.Principals;
import {service.namespace}.auth.IPrincipal.Type;

import {service.namespace}.auth.IPrincipal.Type;

public class Principals extends HashMap<IPrincipal.Type, Set<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1837449443827824733L;

	public Principals() {
		super();
		this.put(Type.Role, new HashSet<String>());
		this.put(Type.User, new HashSet<String>());
		this.put(Type.Group, new HashSet<String>());
	}

    public boolean somePrincipalsAvailable() {
        return !this.get(Type.User).isEmpty() || !this.get(Type.Group).isEmpty() || !this.get(Type.Role).isEmpty();
    }

    public Principals add(Principals principals) {
        if (principals != null) {
            this.get(Type.User).addAll(principals.get(Type.User));
            this.get(Type.Group).addAll(principals.get(Type.Group));
            this.get(Type.Role).addAll(principals.get(Type.Role));
        }
        return this;
    }

	public Principals add(Type type, String uniqueName) {
		if (uniqueName != null) {
			this.get(type).add(uniqueName);
		}
		return this;
	}

	public Principals add(Type type, Collection<String> uniqueNames) {
		addAll(type, uniqueNames);
		return this;
	}

	public Principals addAll(Type type, Collection<String> uniqueNames) {
		if (uniqueNames != null) {
			this.get(type).addAll(uniqueNames);
		}
		return this;
	}

	public Principals addAll(Principals principals) {
		if (principals != null) {
			for (Type key : principals.keySet()) {
				if (!principals.get(key).isEmpty()) {
					this.get(key).addAll(principals.get(key));
				}
			}
		}
		return this;
	}
}
