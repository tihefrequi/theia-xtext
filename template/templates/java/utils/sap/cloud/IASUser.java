package {service.namespace}.utils.sap.cloud;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserAttribute;
import {service.namespace}.auth.simple.SimpleApplicationUser;

public class IASUser extends SimpleApplicationUser {

	// Logger
	private Logger LOG = LoggerFactory.getLogger(IASUser.class);
	
	//Possible expected SAP SCP User Attribute which are mapped in the Trust configuration 
	//(https://help.sap.com/viewer/462e41a242984577acc28eae130855ad/Cloud/en-US/0df6abc18397483dbb34b87dcc0622c7.html)
	public final static String ATTR_COMPANY 			= "company";
	public final static String ATTR_COMPANY_REL		 	= "companyRel";
	public final static String ATTR_COST_CENTER 		= "costCenter";
	public final static String ATTR_COUNTRY 			= "country";
	public final static String ATTR_CUST_1				= "customAttribute1";
	public final static String ATTR_CUST_2				= "customAttribute2";
	public final static String ATTR_CUST_3				= "customAttribute3";
	public final static String ATTR_CUST_4				= "customAttribute4";
	public final static String ATTR_CUST_5				= "customAttribute5";
	public final static String ATTR_CUST_6				= "customAttribute6";
	public final static String ATTR_CUST_7				= "customAttribute7";
	public final static String ATTR_CUST_8				= "customAttribute8";
	public final static String ATTR_CUST_9				= "customAttribute9";
	public final static String ATTR_CUST_10				= "customAttribute10";
	public final static String ATTR_DEPARTMENT			= "department";
	public final static String ATTR_DISP_NAME			= "displayName";
	public final static String ATTR_DIVISION			= "division";
	public final static String ATTR_GROUPS				= "groups";
	public final static String ATTR_HONOR_PREF			= "honorificPrefix";
	public final static String ATTR_HONOR_SUF			= "honorificSuffix";
	public final static String ATTR_IMS 				= "imsAddress";
	public final static String ATTR_ACTIVE				= "active";
	public final static String ATTR_LOCALE				= "locale";
	public final static String ATTR_LOCALITY			= "locality";
	public final static String ATTR_MANAGER				= "manager";
	public final static String ATTR_MIDDLE_NAME			= "middleName";
	public final static String ATTR_PHONE				= "phone";
	public final static String ATTR_PHOTO_URL			= "photoUrl";
	public final static String ATTR_POSTAL_CODE			= "postalCode";
	public final static String ATTR_PREF_LANG			= "preferredLang";
	public final static String ATTR_PROFILE_URL			= "profileUrl";
	public final static String ATTR_REGION				= "region";
	public final static String ATTR_ROLES				= "roles";
	public final static String ATTR_STREET_ADDR			= "streetAddress";
	public final static String ATTR_TIME_ZONE			= "timeZone";
	public final static String ATTR_USER_TYPE			= "userType";
	
	public IASUser(User user) {
		
		if(user != null) {
			
			this.setUniqueName(user.getName());
			try {
				this.setFirstName(user.getAttribute(UserAttribute.FIRST_NAME));
				this.setLastName(user.getAttribute(UserAttribute.LAST_NAME));
				this.setEmail(user.getAttribute(UserAttribute.EMAIL_ADDRESS));
			} catch(UnsupportedUserAttributeException e) {
				LOG.trace("Can't find one of basic user attributes "
						+ "["+UserAttribute.FIRST_NAME+", "+UserAttribute.LAST_NAME+", "+ UserAttribute.EMAIL_ADDRESS
						+ "]. Please check the trust configuration!", e);
			}
			
			//we get the list of all provided attributes and try to map them
			Set<String> userAttributes = user.listAttributes();
			for(String userAttribute : userAttributes) {
				if(!(userAttribute.equals(UserAttribute.FIRST_NAME) || 
					userAttribute.equals(UserAttribute.LAST_NAME) ||
					userAttribute.equals(UserAttribute.NAME))) {
						
					String userAttributeValue = StringUtils.join(user.getAttributeValues(userAttribute), ",");
					
					switch(userAttribute) {
						case ATTR_COMPANY: { 
							this.setCompany(userAttributeValue);
							break;
						}
						case ATTR_COMPANY_REL: {
							this.setCompanyRelationship(userAttributeValue);							
							break;
						}
						case ATTR_COST_CENTER: {
							this.setCostCenter(userAttributeValue);
							break;
						}
						case ATTR_COUNTRY: {
							this.setCountry(userAttributeValue);
							break;
						}
						case ATTR_CUST_1: {
							this.setCustomAttribute1(userAttributeValue);
							break;
						}
						case ATTR_CUST_2: {
							this.setCustomAttribute2(userAttributeValue);
							break;
						}
						case ATTR_CUST_3: {
							this.setCustomAttribute3(userAttributeValue);
							break;
						}
						case ATTR_CUST_4: {
							this.setCustomAttribute4(userAttributeValue);
							break;
						}
						case ATTR_CUST_5: {
							this.setCustomAttribute5(userAttributeValue);
							break;
						}
						case ATTR_CUST_6: {
							this.setCustomAttribute6(userAttributeValue);
							break;
						}
						case ATTR_CUST_7: {
							this.setCustomAttribute7(userAttributeValue);
							break;
						}
						case ATTR_CUST_8: {
							this.setCustomAttribute8(userAttributeValue);
							break;
						}
						case ATTR_CUST_9: {
							this.setCustomAttribute9(userAttributeValue);
							break;
						}
						case ATTR_CUST_10: {
							this.setCustomAttribute10(userAttributeValue);
							break;
						}
						case ATTR_DEPARTMENT: {
							this.setDepartment(userAttributeValue);
							break;
						}
						case ATTR_DISP_NAME: {
							this.setDisplayName(userAttributeValue);
							break;
						}
						case ATTR_DIVISION: {
							this.setDivision(userAttributeValue);
							break;
						}
						case ATTR_GROUPS: {
							if(StringUtils.isNotBlank(userAttributeValue)) {
								this.setGroupUniqueNames(new HashSet<String>(Arrays.asList(user.getAttributeValues(userAttribute))));								
							}
							break;
						}
						case ATTR_HONOR_PREF: {
							this.setHonorificPrefix(userAttributeValue);
							break;
						}
						case ATTR_HONOR_SUF: {
							this.setHonorificSuffix(userAttributeValue);
							break;
						}
						case ATTR_IMS : {
							this.setInstantMessageAddress(userAttributeValue);
							break;
						}
						case ATTR_ACTIVE: {
							this.setActive(Boolean.parseBoolean(userAttributeValue));
							break;
						}
						case ATTR_LOCALE: {
							this.setLocale(userAttributeValue);
							break;
						}
						case ATTR_LOCALITY: {
							this.setLocality(userAttributeValue);
							break;
						}
						case ATTR_MANAGER: {
							this.setManager(userAttributeValue);
							break;
						}
						case ATTR_MIDDLE_NAME: {
							this.setMiddleName(userAttributeValue);
							break;
						}
						case ATTR_PHONE: {
							this.setPhone(userAttributeValue);
							break;
						}
						case ATTR_PHOTO_URL: {
							this.setPhotoUrl(userAttributeValue);
							break;
						}
						case ATTR_POSTAL_CODE: {
							this.setPostalCode(userAttributeValue);
							break;
						}
						case ATTR_PREF_LANG: {
							this.setPreferredLanguage(userAttributeValue);
							break;
						}
						case ATTR_PROFILE_URL: {
							this.setProfileUrl(userAttributeValue);
							break;
						}
						case ATTR_REGION: {
							this.setRegion(userAttributeValue);
							break;
						}
						case ATTR_ROLES: {
							if(StringUtils.isNotBlank(userAttributeValue)) {
								this.setRoleUniqueNames(new HashSet<String>(Arrays.asList(user.getAttributeValues(userAttribute))));								
							}
							break;
						}
						case ATTR_STREET_ADDR: {
							this.setStreetAddress(userAttributeValue);
							break;
						}
						case ATTR_TIME_ZONE: {
							this.setTimeZone(userAttributeValue);
							break;
						}
						case ATTR_USER_TYPE: {
							this.setUserType(userAttributeValue);
							break;
						} 
						default: {
							LOG.warn("Can't map the attribute ["+userAttribute+"]");
						}
					}
				}
			}
			
			//check some attributes
			if(StringUtils.isEmpty(this.getDisplayName())) {
				this.setDisplayName(this.getFirstName() + " " 
						+ (StringUtils.isNotBlank(this.getMiddleName()) ? this.getMiddleName()+ " " : "")
						+ this.getLastName()
				);
			}			
			if(CollectionUtils.isEmpty(this.getRoleUniqueNames()) && CollectionUtils.isNotEmpty(user.getRoles())) {
				this.setRoleUniqueNames(user.getRoles());
			}
			if(CollectionUtils.isEmpty(this.getGroupUniqueNames()) && CollectionUtils.isNotEmpty(user.getGroups())) {
				this.setGroupUniqueNames(user.getGroups());
			}
			
			
		}
	}

}
