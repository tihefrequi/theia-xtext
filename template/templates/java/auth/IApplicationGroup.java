package {service.namespace}.auth;

import java.util.Set;

public interface IApplicationGroup extends IPrincipal {

    public Set<String> getDirectGroupUniqueNames();

    public Set<String> getRecursiveGroupUniqueNames();

    public Set<String> getDirectUserUniqueNames();

    public Set<String> getRecursiveUserUniqueNames();
}