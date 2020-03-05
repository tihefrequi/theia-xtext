package {service.namespace}.utils.sap.cloud;

/**
 * Based on SAP SCP documentation (https://help.sap.com/viewer/6d6d63354d1242d185ab4830fc04feb1/Cloud/en-US/7ae17a6da5314246a04d113f902d0fdf.html)
 * as well as SCIM RFC specification (https://tools.ietf.org/html/rfc7643) 
 * @author storm
 *
 */
public class SCIMConstants {

	//Well-known-schemas
	public static String SCHEMA_IETF_CORE_USER = "urn:ietf:params:scim:schemas:core:2.0:User"; 
	public static String SCHEMA_IETF_EXTENSION_ENTERPRISE_USER = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"; 
	public static String SCHEMA_SCP_EXTENSION_ENTERPRISE_USER = "urn:sap:cloud:scim:schemas:extension:custom:2.0:User"; 
	
	
	//Common
	public static String ID 				= "id"; 
	public static String META 				= "meta"; 
	public static String EXTERNAL_ID 		= "externalId"; 
	public static String SCHEMAS			= "schemas";
	
	//USER
	
	//...single
	public static String U_USER_NAME 		= "userName"; 
	public static String U_NAME 			= "name"; 
	public static String U_DISPLAY_NAME 	= "displayName"; 
	public static String U_NICK_NAME		= "nickName"; 
	public static String U_PROFILE_URL		= "profileUrl"; 
	public static String U_TITLE			= "title"; 
	public static String U_USER_TYPE		= "userType"; 
	public static String U_PREFERRED_LANG	= "preferredLanguage"; 
	public static String U_LOCALE			= "locale"; 
	public static String U_TIME_ZONE		= "timeZone"; 
	public static String U_ACTIVE			= "active"; 
	public static String U_PASSWORD			= "password"; 
		
	//...SAP SCP specific attributes
	public static String U_SCP_CONTACT_PREFERENCE_EMAIL 		= "contactPreferenceEmail";
	public static String U_SCP_CONTACT_PREFERENCE_TELEPHONE 	= "contactPreferenceTelephone";
	public static String U_SCP_INDUSTRY_CRM						= "industryCrm";
	public static String U_SCP_COMPANY							= "company";
	public static String U_SCP_COMPANY_RELATIONSHIP				= "companyRelationship";
	public static String U_SCP_DEPARTMENT						= "department";
	public static String U_SCP_CORPORATE_GROUPS					= "corporateGroups";
	public static String U_SCP_MAIL_VERIFIED					= "mailVerified";
	public static String U_SCP_TELEPHONE_VERIFIED				= "telephoneVerified";
	public static String U_SCP_TELEPHONE_VERIFICATION_ATTEMPTS	= "telephoneVerificationAttempts";
	public static String U_SCP_PASSWORD_POLICY					= "passwordPolicy";
	public static String U_SCP_PASSWORD_STATUS					= "passwordStatus";
	public static String U_SCP_PASSWORD_FAILED_LOGIN_ATTEMPTS	= "passwordFailedLoginAttempts";
	public static String U_SCP_OTP_FAILED_LOGIN_ATTEMPTS		= "otpFailedLoginAttempts";
	public static String U_SCP_TEMRS_OF_USE						= "termsOfUse";
	public static String U_SCP_TIME_OF_ACCEPTANCE				= "timeOfAcceptance";		
	public static String U_SCP_PRIVACY_POLICY					= "privacyPolicy";	
	public static String U_SCP_SOCIAL_IDENTITIES				= "socialIdentities";
	public static String U_SCP_PASSWORD_LOGIN_TIME				= "passwordLoginTime"; 
	public static String U_SCP_LOGIN_TIME						= "loginTime";
	public static String U_SCP_PASSWORD_SET_TIME				= "passwordSetTime";
	
	
	//...multi-valued
	public static String U_EMAILS				= "emails";
	public static String U_PHONE_NUMBERS		= "phoneNumbers"; 
	public static String U_IMS					= "ims"; 
	public static String U_PHOTOS				= "photos"; 
	public static String U_ADDRESSES			= "addresses"; 
	public static String U_GROUPS				= "groups"; 
	public static String U_ENTITLEMENTS			= "entitlements"; 
	public static String U_ROLES				= "roles"; 
	public static String U_X509_CERTIFICATES	= "x509Certificates";
	
	//...complex types
	
	//......address
	public static String U_ADDRESS_TYPE 			= "type";
	public static String U_ADDRESS_FORMATTED 		= "formatted";
	public static String U_ADDRESS_STREET_ADDRESS	= "streetAddress";
	public static String U_ADDRESS_LOCAITY			= "locality"; 
	public static String U_ADDRESS_REGION			= "region"; 
	public static String U_ADDRESS_POSTAL_CODE		= "postalCode"; 
	public static String U_ADDRESS_COUNTRY			= "country";

	//......name
	public static String U_NAME_FORMATTED			= "formatted"; 
	public static String U_NAME_FAMILY_NAME			= "familyName"; 
	public static String U_NAME_GIVEN_NAME			= "givenName"; 
	public static String U_NAME_MIDDLE_NAME			= "middleName"; 
	public static String U_NAME_HONORIC_PREFIX		= "honorificPrefix"; 
	public static String U_NAME_HONORIC_SUFFIX		= "honorificSuffix";
	
	//...terms of use (SAP SCP specific)
	public static String U_SCP_TEMRS_OF_USE_TIME_OF_ACCEPTANCE		= "timeOfAcceptance";
	public static String U_SCP_TEMRS_OF_USE_NAME					= "name";
	public static String U_SCP_TEMRS_OF_USE_ID						= "id";
	public static String U_SCP_TEMRS_OF_USE_LOCALE					= "locale";
	public static String U_SCP_TEMRS_OF_USE_VERSION					= "version";
	public static String U_SCP_PRIVACY_POLICY_TIME_OF_ACCEPTANCE	= "timeOfAcceptance";
	public static String U_SCP_PRIVACY_POLICY_NAME					= "name";
	public static String U_SCP_PRIVACY_POLICY_ID					= "id";
	public static String U_SCP_PRIVACY_POLICY_LOCALE				= "locale";
	public static String U_SCP_PRIVACY_POLICY_VERSION				= "version";

	//...extension attributes
	
	public static String SCH_IETF_EXT_ENTERP_U_EMPLOYEE_NUMBER 	= "employeeNumber";
	public static String SCH_IETF_EXT_ENTERP_U_COST_CENTER 		= "costCenter";
	public static String SCH_IETF_EXT_ENTERP_U_ORGANIZATION		= "organization";
	public static String SCH_IETF_EXT_ENTERP_U_DIVISION			= "division";
	public static String SCH_IETF_EXT_ENTERP_U_DEPARTMENT		= "department";
	public static String SCH_IETF_EXT_ENTERP_U_MANAGER			= "manager";

	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES = "attributes";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME = "name";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE = "value";

	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA1 = "customAttribute1";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA2 = "customAttribute2";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA3 = "customAttribute3";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA4 = "customAttribute4";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA5 = "customAttribute5";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA6 = "customAttribute6";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA7 = "customAttribute7";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA8 = "customAttribute8";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA9 = "customAttribute9";
	public static String SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA10 = "customAttribute10";

	
	//Multi-Value Attributes
	public static String MV_TYPE 		= "type";
	public static String MV_PRIMARY		= "primary";
	public static String MV_DISPLAY		= "display";
	public static String MV_VALUE		= "value";
	public static String MV_REF			= "$ref";
	
}
