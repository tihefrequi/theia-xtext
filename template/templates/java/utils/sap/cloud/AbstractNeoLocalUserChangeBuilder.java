package {service.namespace}.utils.sap.cloud;

import java.util.HashMap;
import java.util.Map;
import com.sap.security.um.user.UserAttribute;

import {service.namespace}.auth.IApplicationUserChangeBuilder;
import {service.namespace}.auth.IApplicationUser;

public abstract class AbstractNeoLocalUserChangeBuilder implements IApplicationUserChangeBuilder {
  protected Map<String, String> user;
  protected IApplicationUser applicationUser;

  protected AbstractNeoLocalUserChangeBuilder(IApplicationUser applicationUser) {
    super();
    this.applicationUser = applicationUser;
    this.user = new HashMap<>();
  }

  @Override
  public Map<String, String> getChangeData() {
    return user;
  }

  @Override
  public IApplicationUser getUser() {
    return applicationUser;
  }

  @Override
  public IApplicationUserChangeBuilder firstName(String firstName) {
    user.put(UserAttribute.FIRST_NAME, firstName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder lastName(String lastName) {
    user.put(UserAttribute.LAST_NAME, lastName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder middleName(String middleName) {
    user.put(IASUser.ATTR_MIDDLE_NAME, middleName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder honorificPrefix(String honorificPrefix) {
    user.put(IASUser.ATTR_HONOR_PREF, honorificPrefix);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder honorificSuffix(String honorificSuffix) {
    user.put(IASUser.ATTR_HONOR_SUF, honorificSuffix);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder locale(String locale) {
    user.put(IASUser.ATTR_LOCALE, locale);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder timeZone(String timeZone) {
    user.put(IASUser.ATTR_TIME_ZONE, timeZone);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder profileUrl(String profileUrl) {
    user.put(IASUser.ATTR_PROFILE_URL, profileUrl);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder preferredLanguage(String preferredLanguage) {
    user.put(IASUser.ATTR_PREF_LANG, preferredLanguage);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder active(boolean active) {
    user.put(IASUser.ATTR_ACTIVE, Boolean.toString(active));
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder userType(String userType) {
    user.put(IASUser.ATTR_USER_TYPE, userType);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder email(String email) {
    user.put(UserAttribute.EMAIL_ADDRESS, email);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneWork(String phoneWork) {
    user.put(IASUser.ATTR_PHONE, phoneWork);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneMobile(String phoneMobile) {
    user.put("phoneMobile", phoneMobile);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneFax(String phoneFax) {
    user.put("phoneFax", phoneFax);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder instantMessageAddress(String instantMessageAddress) {
    user.put(IASUser.ATTR_IMS, instantMessageAddress);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder photoUrl(String photoUrl) {
    user.put(IASUser.ATTR_PHOTO_URL, photoUrl);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder addressHome(String country, String region, String postalCode,
      String locality, String streetAddress) {
    user.put("home.country", country);
    user.put("home.region", region);
    user.put("home.postalCode", postalCode);
    user.put("home.locality", locality);
    user.put("home.streetAddress", streetAddress);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder addressWork(String country, String region, String postalCode,
      String locality, String streetAddress) {
    user.put("work.country", country);
    user.put("work.region", region);
    user.put("work.postalCode", postalCode);
    user.put("work.locality", locality);
    user.put("work.streetAddress", streetAddress);
    return this;
  }

  public IApplicationUserChangeBuilder company(String company) {
    user.put(IASUser.ATTR_COMPANY, company);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder companyRelationship(String companyRelationship) {
    user.put(IASUser.ATTR_COMPANY_REL, companyRelationship);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder department(String department) {
    user.put(IASUser.ATTR_DEPARTMENT, department);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder division(String division) {
    user.put(IASUser.ATTR_DIVISION, division);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder costCenter(String costCenter) {
    user.put(IASUser.ATTR_COST_CENTER, costCenter);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder manager(String manager) {
    user.put(IASUser.ATTR_MANAGER, manager);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute1(String customAttribute) {
    user.put(IASUser.ATTR_CUST_1, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute2(String customAttribute) {
    user.put(IASUser.ATTR_CUST_2, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute3(String customAttribute) {
    user.put(IASUser.ATTR_CUST_3, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute4(String customAttribute) {
    user.put(IASUser.ATTR_CUST_4, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute5(String customAttribute) {
    user.put(IASUser.ATTR_CUST_5, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute6(String customAttribute) {
    user.put(IASUser.ATTR_CUST_6, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute7(String customAttribute) {
    user.put(IASUser.ATTR_CUST_7, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute8(String customAttribute) {
    user.put(IASUser.ATTR_CUST_8, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute9(String customAttribute) {
    user.put(IASUser.ATTR_CUST_9, customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute10(String customAttribute) {
    user.put(IASUser.ATTR_CUST_10, customAttribute);
    return this;
  }

}
