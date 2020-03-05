package {service.namespace}.utils.sap.cloud;

import java.util.HashSet;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.commons.lang3.StringUtils;

import com.sap.security.um.user.User;

public class SCIMUser extends IASUser {

  public SCIMUser(User cloudUser, JsonObject user) {
    super(cloudUser);
    initWithJson(user);
  }

  public SCIMUser(JsonObject user) {
    super(null);
    initWithJson(user);
  }

  private void initWithJson(JsonObject user) {

		if(user != null) {

			this.setUniqueName(user.getString(SCIMConstants.ID));

			//Simple
			
			if(user.containsKey(SCIMConstants.U_DISPLAY_NAME)) {
				this.setDisplayName(user.getString(SCIMConstants.U_DISPLAY_NAME));
			}
			if(user.containsKey(SCIMConstants.U_ACTIVE)) {
				this.setActive(user.getBoolean(SCIMConstants.U_ACTIVE));
			}
			if(user.containsKey(SCIMConstants.U_LOCALE)) {
				this.setLocale(user.getString(SCIMConstants.U_LOCALE));
			}
			if(user.containsKey(SCIMConstants.U_PREFERRED_LANG)) {
				this.setPreferredLanguage(user.getString(SCIMConstants.U_PREFERRED_LANG));
			}
			if(user.containsKey(SCIMConstants.U_PROFILE_URL)) {
				this.setProfileUrl(user.getString(SCIMConstants.U_PROFILE_URL));
			}
			if(user.containsKey(SCIMConstants.U_TIME_ZONE)) {
				this.setTimeZone(user.getString(SCIMConstants.U_TIME_ZONE));
			}
			if(user.containsKey(SCIMConstants.U_USER_TYPE)) {
				this.setUserType(user.getString(SCIMConstants.U_USER_TYPE));
			}

			//Complex
			
			//...Name
			if(user.containsKey(SCIMConstants.U_NAME)) {
				JsonObject names = user.getJsonObject(SCIMConstants.U_NAME);
				if(names.containsKey(SCIMConstants.U_NAME_GIVEN_NAME)) {					
					this.setFirstName(names.getString(SCIMConstants.U_NAME_GIVEN_NAME));
				}
				if(names.containsKey(SCIMConstants.U_NAME_MIDDLE_NAME)) {					
					this.setMiddleName(names.getString(SCIMConstants.U_NAME_MIDDLE_NAME));
				}
				if(names.containsKey(SCIMConstants.U_NAME_FAMILY_NAME)) {					
					this.setLastName(names.getString(SCIMConstants.U_NAME_FAMILY_NAME));
				}
				if(names.containsKey(SCIMConstants.U_NAME_HONORIC_PREFIX)) {					
					this.setHonorificPrefix(names.getString(SCIMConstants.U_NAME_HONORIC_PREFIX));
				}
				if(names.containsKey(SCIMConstants.U_NAME_HONORIC_SUFFIX)) {					
					this.setHonorificSuffix(names.getString(SCIMConstants.U_NAME_HONORIC_SUFFIX));
				}
			}
					
			//...Email
			if(user.containsKey(SCIMConstants.U_EMAILS)) {
				this.setEmail(getPrimaryValue(user.getJsonArray(SCIMConstants.U_EMAILS)));
			}

			//...Phone
			if(user.containsKey(SCIMConstants.U_PHONE_NUMBERS)) {
				this.setPhone(getPrimaryValue(user.getJsonArray(SCIMConstants.U_PHONE_NUMBERS)));
			}

			//...IMS
			if(user.containsKey(SCIMConstants.U_IMS)) {
				this.setInstantMessageAddress(getPrimaryValue(user.getJsonArray(SCIMConstants.U_IMS)));
			}

			//...Photo
			if(user.containsKey(SCIMConstants.U_PHOTOS)) {
				this.setPhotoUrl(getPrimaryValue(user.getJsonArray(SCIMConstants.U_PHOTOS)));
			}

			
			//...Address
			if(user.containsKey(SCIMConstants.U_ADDRESSES)) {
				JsonArray addresses = user.getJsonArray(SCIMConstants.U_ADDRESSES);
				int idx = getPrimaryIndex(addresses);
				
				JsonObject address = addresses.getJsonObject(idx);
				
				if(address.containsKey(SCIMConstants.U_ADDRESS_COUNTRY)) {
					this.setCountry(address.getString(SCIMConstants.U_ADDRESS_COUNTRY));
					
				}
				if(address.containsKey(SCIMConstants.U_ADDRESS_LOCAITY)) {
					this.setLocality(address.getString(SCIMConstants.U_ADDRESS_LOCAITY));
					
				}
				if(address.containsKey(SCIMConstants.U_ADDRESS_POSTAL_CODE)) {
					this.setPostalCode(address.getString(SCIMConstants.U_ADDRESS_POSTAL_CODE));
					
				}
				if(address.containsKey(SCIMConstants.U_ADDRESS_REGION)) {
					this.setRegion(address.getString(SCIMConstants.U_ADDRESS_REGION));					
				}
				if(address.containsKey(SCIMConstants.U_ADDRESS_STREET_ADDRESS)) {
					this.setStreetAddress(address.getString(SCIMConstants.U_ADDRESS_STREET_ADDRESS));					
				}
			}
			
			//SCP		
			if(user.containsKey(SCIMConstants.U_SCP_COMPANY)) {					
				this.setCompany(user.getString(SCIMConstants.U_SCP_COMPANY));
			}
			if(user.containsKey(SCIMConstants.U_SCP_COMPANY_RELATIONSHIP)) {					
				this.setCompanyRelationship(user.getString(SCIMConstants.U_SCP_COMPANY_RELATIONSHIP));
			}
			if(user.containsKey(SCIMConstants.U_SCP_DEPARTMENT)) {					
				this.setDepartment(user.getString(SCIMConstants.U_SCP_DEPARTMENT));
			}


			//Extension: Enterprise
			if(user.containsKey(SCIMConstants.SCHEMA_IETF_EXTENSION_ENTERPRISE_USER)) {
				JsonObject enterpriseUser = user.getJsonObject(SCIMConstants.SCHEMA_IETF_EXTENSION_ENTERPRISE_USER);

				if(enterpriseUser.containsKey(SCIMConstants.SCH_IETF_EXT_ENTERP_U_COST_CENTER)) {					
					this.setCostCenter(enterpriseUser.getString(SCIMConstants.SCH_IETF_EXT_ENTERP_U_COST_CENTER));
				}
				if(enterpriseUser.containsKey(SCIMConstants.SCH_IETF_EXT_ENTERP_U_DIVISION)) {					
					this.setDivision(enterpriseUser.getString(SCIMConstants.SCH_IETF_EXT_ENTERP_U_DIVISION));
				}
				if(enterpriseUser.containsKey(SCIMConstants.SCH_IETF_EXT_ENTERP_U_MANAGER)) {					
					JsonObject manager = enterpriseUser.getJsonObject(SCIMConstants.SCH_IETF_EXT_ENTERP_U_MANAGER);
					if(manager.containsKey(SCIMConstants.MV_VALUE)) {
						this.setManager(manager.getString(SCIMConstants.MV_VALUE));						
					}
				}
			}

			//Extension: SCP 
			if(user.containsKey(SCIMConstants.SCHEMA_SCP_EXTENSION_ENTERPRISE_USER)) {
				JsonObject scpEnterpriseUser = user.getJsonObject(SCIMConstants.SCHEMA_SCP_EXTENSION_ENTERPRISE_USER);

				if(scpEnterpriseUser.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES)) {					
					JsonArray scpCustomAttributes = scpEnterpriseUser.getJsonArray(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES);
					if(scpCustomAttributes != null) {
						for(JsonValue scpCustomAttribute : scpCustomAttributes) {
							JsonObject customAttribute = (JsonObject)scpCustomAttribute;
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA1)) {
								this.setCustomAttribute1(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA2)) {
								this.setCustomAttribute2(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA3)) {
								this.setCustomAttribute3(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA4)) {
								this.setCustomAttribute4(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA5)) {
								this.setCustomAttribute5(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA6)) {
								this.setCustomAttribute6(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA7)) {
								this.setCustomAttribute7(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA8)) {
								this.setCustomAttribute8(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA9)) {
								this.setCustomAttribute9(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
							if(customAttribute.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME) &&
									StringUtils.equals(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME), 
											SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA10)) {
								this.setCustomAttribute10(customAttribute.getString(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE));
							}
						}
					}
				}
			}

						
			//...Groups
			if(user.containsKey(SCIMConstants.U_GROUPS)) {
				JsonArray groups = user.getJsonArray(SCIMConstants.U_GROUPS);
				if(groups != null) {
					Set<String> userGroups = new HashSet<String>();
					for(JsonValue g: groups) {
						JsonObject group = (JsonObject) g;
						if(group.containsKey(SCIMConstants.MV_VALUE)) {
							userGroups.add(group.getString(SCIMConstants.MV_VALUE));
						}
					}
					this.setGroupUniqueNames(userGroups);
				}
			}
				
			//...Roles
			if(user.containsKey(SCIMConstants.U_ROLES)) {
				JsonArray roles = user.getJsonArray(SCIMConstants.U_ROLES);
				if(roles != null) {
					Set<String> userRoles = new HashSet<String>();
					for(JsonValue r: roles) {
						JsonObject role = (JsonObject) r;
						if(role.containsKey(SCIMConstants.MV_VALUE)) {
							userRoles.add(role.getString(SCIMConstants.MV_VALUE));
						}
					}
					this.setRoleUniqueNames(userRoles);
				}
			}			
		}
		
	}

	private String getPrimaryValue(JsonArray array) {
		int idx = getPrimaryIndex(array);	
		
		//...get email from index
		JsonObject returnValue = array.getJsonObject(idx);
		return returnValue.getString(SCIMConstants.MV_VALUE);
	}
	
	
	private int getPrimaryIndex(JsonArray array) {
		//..we take the first value by default
		int idx = 0;
		for(int i = 0; i < array.size(); i++) {
			JsonObject value = array.getJsonObject(i);
			//...if we have the value with primary = true > set corresponding index
			if(value.containsKey(SCIMConstants.MV_PRIMARY) && 
					value.getBoolean(SCIMConstants.MV_PRIMARY)) {
				idx = i;
			}
		}				
		
		return idx;
	}

}
