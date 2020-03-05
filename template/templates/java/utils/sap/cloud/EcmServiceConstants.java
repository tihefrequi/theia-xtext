package {service.namespace}.utils.sap.cloud;

public class EcmServiceConstants {

	public static final String PERMISSION_ALL = "cmis:all";
	public static final String PERMISSION_WRITE = "cmis:write";
	public static final String PERMISSION_READ = "cmis:read";
	public static final String PERMISSION_FILE = "sap:file";
	public static final String PERMISSION_DELETE = "sap:delete";
	
	public static final String OBJECT_TYPE_FOLDER = "cmis:folder";
	public static final String OBJECT_TYPE_DOCUMENT = "cmis:document";
	
	
	public static final String USER_ADMIN = "{sap:builtin}admin";
	public static final String GROUP_EVERYONE = "{sap: builtin}everyone";
	
	public static final String PROP_OWNER = "sap:owner";

	public static enum PrincipalType {
		USER(""), ROLE("{role}"), GROUP("{group}");
		
		private String ecmServicePrefix;
		private PrincipalType(String ecmServicePrefix){
			this.ecmServicePrefix = ecmServicePrefix;
		}
		
		public String getEcmServicePrefix() {
			return this.ecmServicePrefix;
		}
	}
	
}
