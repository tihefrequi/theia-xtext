package {service.namespace}.utils.sap.cloud;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import {service.namespace}.auth.IApplicationUserChangeBuilder;
import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.utils.JsonUtils;


public abstract class AbstractSCIMUserChangeBuilder implements IApplicationUserChangeBuilder {
  protected JsonObject user ;
  protected IApplicationUser applicationUser;

  protected AbstractSCIMUserChangeBuilder(IApplicationUser applicationUser) {
    super();
    this.applicationUser = applicationUser;
    this.user = Json.createObjectBuilder().build();
  }
  
  @Override
  public JsonObject getChangeData() {
    return user;
  }

  @Override
  public IApplicationUser getUser() {
    return applicationUser;
  }

  @Override
  public IApplicationUserChangeBuilder firstName(String firstName) {
    user = JsonUtils.putKeyValueInObjectWithKey(user, SCIMConstants.U_NAME,
        SCIMConstants.U_NAME_GIVEN_NAME, firstName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder lastName(String lastName) {
    user = JsonUtils.putKeyValueInObjectWithKey(user, SCIMConstants.U_NAME,
        SCIMConstants.U_NAME_FAMILY_NAME, lastName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder middleName(String middleName) {
    user = JsonUtils.putKeyValueInObjectWithKey(user, SCIMConstants.U_NAME,
        SCIMConstants.U_NAME_MIDDLE_NAME, middleName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder honorificPrefix(String honorificPrefix) {
    user = JsonUtils.putKeyValueInObjectWithKey(user, SCIMConstants.U_NAME,
        SCIMConstants.U_NAME_HONORIC_PREFIX, honorificPrefix);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder honorificSuffix(String honorificSuffix) {
    user = JsonUtils.putKeyValueInObjectWithKey(user, SCIMConstants.U_NAME,
        SCIMConstants.U_NAME_HONORIC_SUFFIX, honorificSuffix);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder locale(String locale) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_LOCALE, locale);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder timeZone(String timeZone) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_TIME_ZONE, timeZone);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder profileUrl(String profileUrl) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_PROFILE_URL, profileUrl);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder preferredLanguage(String preferredLanguage) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_PREFERRED_LANG, preferredLanguage);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder active(boolean active) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_ACTIVE, active);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder userType(String userType) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_USER_TYPE, userType);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder email(String email) {
    // Only one Email supported currently (as of SAP Cloud 2019-08-27 there is only one allowed)
    JsonObject emailEntry = Json.createObjectBuilder().add(SCIMConstants.MV_VALUE, email).build();
    JsonArray emailArray = Json.createArrayBuilder().add(emailEntry).build();
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_EMAILS, emailArray);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneWork(String phoneWork) {
    user = putTypeValueInArrayWithKey(user, SCIMConstants.U_PHONE_NUMBERS, "work", phoneWork);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneMobile(String phoneMobile) {
    user = putTypeValueInArrayWithKey(user, SCIMConstants.U_PHONE_NUMBERS, "mobile", phoneMobile);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneFax(String phoneFax) {
    user = putTypeValueInArrayWithKey(user, SCIMConstants.U_PHONE_NUMBERS, "phoneFax", phoneFax);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder instantMessageAddress(String instantMessageAddress) {
    // Only one IMS Address supported currently (as of 2019-08-27 )
    JsonObject object =
        Json.createObjectBuilder().add(SCIMConstants.MV_VALUE, instantMessageAddress).build();
    JsonArray array = Json.createArrayBuilder().add(object).build();
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_IMS, array);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder photoUrl(String photoUrl) {
    // Only one photo URL supported currently (as of 2019-08-27 )
    JsonObject object = Json.createObjectBuilder().add(SCIMConstants.MV_VALUE, photoUrl).build();
    JsonArray array = Json.createArrayBuilder().add(object).build();
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_IMS, array);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder addressHome(String country, String region, String postalCode,
      String locality, String streetAddress) {
    addressInternal("home", country, region, postalCode, locality, streetAddress);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder addressWork(String country, String region, String postalCode,
      String locality, String streetAddress) {
    addressInternal("work", country, region, postalCode, locality, streetAddress);
    return this;
  }

  protected void addressInternal(String type, String country, String region, String postalCode,
      String locality, String streetAddress) {
    JsonObject object = Json.createObjectBuilder().add(SCIMConstants.MV_TYPE, type)
        .add(SCIMConstants.U_ADDRESS_COUNTRY, country).add(SCIMConstants.U_ADDRESS_REGION, region)
        .add(SCIMConstants.U_ADDRESS_POSTAL_CODE, postalCode)
        .add(SCIMConstants.U_ADDRESS_LOCAITY, locality)
        .add(SCIMConstants.U_ADDRESS_STREET_ADDRESS, streetAddress).build();
    user = JsonUtils.addObjectInArrayOfKey(user, SCIMConstants.U_ADDRESSES, object);
  }

  public IApplicationUserChangeBuilder company(String company) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_SCP_COMPANY, company);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder companyRelationship(String companyRelationship) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_SCP_COMPANY_RELATIONSHIP,
        companyRelationship);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder department(String department) {
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.U_SCP_DEPARTMENT, department);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder division(String division) {
    user = JsonUtils.putKeyValueInObjectWithKey(user,
        SCIMConstants.SCHEMA_IETF_EXTENSION_ENTERPRISE_USER,
        SCIMConstants.SCH_IETF_EXT_ENTERP_U_DIVISION, division);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder costCenter(String costCenter) {
    user = JsonUtils.putKeyValueInObjectWithKey(user,
        SCIMConstants.SCHEMA_IETF_EXTENSION_ENTERPRISE_USER,
        SCIMConstants.SCH_IETF_EXT_ENTERP_U_COST_CENTER, costCenter);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder manager(String manager) {
    user = JsonUtils.putKeyValueInObjectWithKey(user,
        SCIMConstants.SCHEMA_IETF_EXTENSION_ENTERPRISE_USER,
        SCIMConstants.SCH_IETF_EXT_ENTERP_U_MANAGER, manager);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute1(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA1,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute2(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA2,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute3(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA3,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute4(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA4,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute5(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA5,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute6(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA6,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute7(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA7,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute8(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA8,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute9(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA9,
        customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute10(String customAttribute) {
    putCustomAttributeValueInArrayWithKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_CA10,
        customAttribute);
    return this;
  }

  //
  // ====== HELPER
  //

  protected void putCustomAttributeValueInArrayWithKey(String customAttributeName, String value) {
    // Ensure User has Enterprise Extension
    if (!user.containsKey(SCIMConstants.SCHEMA_SCP_EXTENSION_ENTERPRISE_USER)) {
      user = JsonUtils.putKeyValueInObject(user, SCIMConstants.SCHEMA_SCP_EXTENSION_ENTERPRISE_USER,
          Json.createObjectBuilder().build());
    }
    // Ensure User has Attributes Array in Enterprise Extension
    JsonObject scpExtensionEnterpriseUser =
        user.getJsonObject(SCIMConstants.SCHEMA_SCP_EXTENSION_ENTERPRISE_USER);
    if (!scpExtensionEnterpriseUser.containsKey(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES)) {
      JsonArray emptyArray = Json.createArrayBuilder().build();
      scpExtensionEnterpriseUser = JsonUtils.putKeyValueInObject(scpExtensionEnterpriseUser,
          SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES, emptyArray);
    }

    // New Custom Attribute
    JsonObjectBuilder arrayElementBuilder = Json.createObjectBuilder();
    arrayElementBuilder.add(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_NAME,
        customAttributeName);
    arrayElementBuilder.add(SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES_VALUE, value);
    JsonObject arrayElement = arrayElementBuilder.build();

    scpExtensionEnterpriseUser = JsonUtils.addObjectInArrayOfKey(scpExtensionEnterpriseUser,
        SCIMConstants.SCH_SCP_EXT_ENTERP_U_ATTRIBUTES, arrayElement);
    user = JsonUtils.putKeyValueInObject(user, SCIMConstants.SCHEMA_SCP_EXTENSION_ENTERPRISE_USER,
        scpExtensionEnterpriseUser);
  }

  protected JsonObject putTypeValueInArrayWithKey(JsonObject jsonObject, String topKey, String type,
      String value) {
    // Build Array Element
    JsonObjectBuilder arrayElementBuilder = Json.createObjectBuilder();
    arrayElementBuilder.add(SCIMConstants.MV_TYPE, type);
    arrayElementBuilder.add(SCIMConstants.MV_VALUE, value);
    JsonObject arrayElement = arrayElementBuilder.build();

    return JsonUtils.putValueInArrayWithKey(jsonObject, topKey, arrayElement);
  }
}
