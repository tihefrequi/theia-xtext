package {service.namespace}.auth;

public interface IPrincipal {
	
	public enum Type { User, Group, Role }

    public String getUniqueName();

    public String getDisplayName();
    
}