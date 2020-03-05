package {service.namespace}.auth;

public interface IApplicationUserChangeBuilderBase {

  Object getChangeData();
  
  public IApplicationUser getUser();

  IApplicationUserChangeBuilder firstName(String firstName);

  IApplicationUserChangeBuilder lastName(String lastName);

  IApplicationUserChangeBuilder middleName(String middleName);

  IApplicationUserChangeBuilder honorificPrefix(String honorificPrefix);

  IApplicationUserChangeBuilder honorificSuffix(String honorificSuffix);

  IApplicationUserChangeBuilder profileUrl(String profileUrl);

  IApplicationUserChangeBuilder photoUrl(String photoUrl);

  IApplicationUserChangeBuilder userType(String userType);

  IApplicationUserChangeBuilder preferredLanguage(String preferredLanguage);

  IApplicationUserChangeBuilder locale(String locale);

  IApplicationUserChangeBuilder timeZone(String timeZone);

  IApplicationUserChangeBuilder active(boolean active);

  IApplicationUserChangeBuilder company(String company);

  IApplicationUserChangeBuilder companyRelationship(String companyRelationship);

  IApplicationUserChangeBuilder department(String department);

  IApplicationUserChangeBuilder division(String division);

  IApplicationUserChangeBuilder costCenter(String costCenter);

  IApplicationUserChangeBuilder manager(String manager);

  IApplicationUserChangeBuilder email(String email);

  IApplicationUserChangeBuilder phoneWork(String phoneWork);

  IApplicationUserChangeBuilder phoneMobile(String phoneMobile);

  IApplicationUserChangeBuilder phoneFax(String phoneFax);

  IApplicationUserChangeBuilder instantMessageAddress(String instantMessageAddress);

  IApplicationUserChangeBuilder customAttribute1(String customAttribute1);

  IApplicationUserChangeBuilder customAttribute2(String customAttribute2);

  IApplicationUserChangeBuilder customAttribute3(String customAttribute3);

  IApplicationUserChangeBuilder customAttribute4(String customAttribute4);

  IApplicationUserChangeBuilder customAttribute5(String customAttribute5);

  IApplicationUserChangeBuilder customAttribute6(String customAttribute6);

  IApplicationUserChangeBuilder customAttribute7(String customAttribute7);

  IApplicationUserChangeBuilder customAttribute8(String customAttribute8);

  IApplicationUserChangeBuilder customAttribute9(String customAttribute9);

  IApplicationUserChangeBuilder customAttribute10(String customAttribute10);

  IApplicationUserChangeBuilder addressHome(String country, String region, String postalCode,
      String locality, String streetAddress);

  IApplicationUserChangeBuilder addressWork(String country, String region, String postalCode,
      String locality, String streetAddress);



}
