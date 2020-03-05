package {service.namespace}.utils.sap.netweaver;

import java.util.Locale;
import java.util.TimeZone;
import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserChangeBuilder;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserMaint;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;

public abstract class AbstractUMEUserChangeBuilder implements IApplicationUserChangeBuilder {
  protected IUserMaint user;
  protected IApplicationUser applicationUser;

  protected AbstractUMEUserChangeBuilder(IApplicationUser applicationUser) {
    this.applicationUser = applicationUser;

    try {
      final IUserFactory userFactory = UMFactory.getUserFactory();
      // Retrieve User by Unique Name
      IUser user = userFactory.getUserByUniqueName(applicationUser.getUniqueName());
      // Get the unique ID.
      String uniqueID = user.getUniqueID();
      this.user = userFactory.getMutableUser(uniqueID);
    } catch (UMException e) {
      throw new RuntimeException("Could not build changes for applicationUser=" + applicationUser,
          e);
    }
  }

  @Override
  public IUserMaint getChangeData() {
    return user;
  }

  @Override
  public IApplicationUser getUser() {
    return applicationUser;
  }

  @Override
  public IApplicationUserChangeBuilder firstName(String firstName) {
    user.setFirstName(firstName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder lastName(String lastName) {
    user.setLastName(lastName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder middleName(String middleName) {
    customAttribute("middleName", middleName);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder honorificPrefix(String honorificPrefix) {
    user.setTitle(honorificPrefix);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder honorificSuffix(String honorificSuffix) {
    customAttribute("honorificSuffix", honorificSuffix);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder locale(String locale) {
    user.setLocale(Locale.forLanguageTag(locale));
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder timeZone(String timeZoneId) {
    user.setTimeZone(TimeZone.getTimeZone(timeZoneId));
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder profileUrl(String profileUrl) {
    customAttribute("profileUrl", profileUrl);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder preferredLanguage(String preferredLanguage) {
    customAttribute("preferredLanguage", preferredLanguage);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder active(boolean active) {
    customAttribute("active", Boolean.toString(active));
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder userType(String userType) {
    customAttribute("userType", userType);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder email(String email) {
    user.setEmail(email);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneWork(String phoneWork) {
    user.setTelephone(phoneWork);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneMobile(String phoneMobile) {
    user.setCellPhone(phoneMobile);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder phoneFax(String phoneFax) {
    customAttribute("phoneFax", phoneFax);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder instantMessageAddress(String instantMessageAddress) {
    customAttribute("instantMessageAddress", instantMessageAddress);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder photoUrl(String photoUrl) {
    customAttribute("photoUrl", photoUrl);
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
    if ("work".equals(type)) {
      user.setCountry(country);
      customAttribute("region", region);
      user.setZip(postalCode);
      user.setCity(locality);
      user.setStreet(streetAddress);
    } else {
      customAttribute(type + ".country", country);
      customAttribute(type + ".region", region);
      customAttribute(type + ".postalCode", postalCode);
      customAttribute(type + ".region", region);
      customAttribute(type + ".locality", locality);
      customAttribute(type + ".streetAddress", streetAddress);
    }
  }

  public IApplicationUserChangeBuilder company(String company) {
    user.setCompany(company);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder companyRelationship(String companyRelationship) {
    customAttribute(companyRelationship, companyRelationship);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder department(String department) {
    user.setDepartment(department);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder division(String division) {
    customAttribute("division", division);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder costCenter(String costCenter) {
    customAttribute("costCenter", costCenter);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder manager(String manager) {
    customAttribute("manager", manager);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute1(String customAttribute) {
    customAttribute("customAttribute1", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute2(String customAttribute) {
    customAttribute("customAttribute2", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute3(String customAttribute) {
    customAttribute("customAttribute3", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute4(String customAttribute) {
    customAttribute("customAttribute4", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute5(String customAttribute) {
    customAttribute("customAttribute5", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute6(String customAttribute) {
    customAttribute("customAttribute6", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute7(String customAttribute) {
    customAttribute("customAttribute7", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute8(String customAttribute) {
    customAttribute("customAttribute8", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute9(String customAttribute) {
    customAttribute("customAttribute9", customAttribute);
    return this;
  }

  @Override
  public IApplicationUserChangeBuilder customAttribute10(String customAttribute) {
    customAttribute("customAttribute10", customAttribute);
    return this;
  }

  //
  // ====== HELPER
  //

  /**
   * 
   * Set a customAttribute for this user in the default storm namespace, which is identical for all
   * any application in any package
   * 
   * @param name
   * @param value
   */
  protected void customAttribute(String name, String value) {
    user.setAttribute(IApplicationUser.customNamespace(), name, new String[] {value});
  }

  /**
   * Set a customAttribute for this user
   * 
   * @param namespace
   * @param name
   * @param value
   */
  protected void customAttribute(String namespace, String name, String value) {
    user.setAttribute(namespace, name, new String[] {value});
  }

}

