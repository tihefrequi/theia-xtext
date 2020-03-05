package {service.namespace}.auth;

import java.util.Set;

public interface IApplicationUserBase extends IPrincipal {

  public String getFirstName();

  public String getLastName();

  public String getMiddleName();

  public String getHonorificPrefix();

  public String getHonorificSuffix();

  public String getProfileUrl();

  public String getPhotoUrl();

  public String getUserType();

  public String getPreferredLanguage();

  public String getLocale();

  public String getTimeZone();

  public boolean getActive();

  public String getCompany();

  public String getCompanyRelationship();

  public String getDepartment();

  public String getDivision();

  public String getCostCenter();

  public String getManager();

  public String getEmail();

  public String getPhone();

  public String getInstantMessageAddress();

  public String getCountry();

  public String getPostalCode();

  public String getRegion();

  public String getLocality();

  public String getStreetAddress();

  public String getCustomAttribute1();

  public String getCustomAttribute2();

  public String getCustomAttribute3();

  public String getCustomAttribute4();

  public String getCustomAttribute5();

  public String getCustomAttribute6();

  public String getCustomAttribute7();

  public String getCustomAttribute8();

  public String getCustomAttribute9();

  public String getCustomAttribute10();

  public Set<String> getGroupUniqueNames();

  public Set<String> getRoleUniqueNames();

  public default String getCustomAttribute(String attributeName) {
    return null;
  }

  public default String getCustomAttribute(String namespace, String attributeName) {
    return null;
  }


}
