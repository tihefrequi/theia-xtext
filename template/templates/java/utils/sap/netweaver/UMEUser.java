package {service.namespace}.utils.sap.netweaver;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import {service.namespace}.auth.IApplicationUser;
import com.sap.security.api.IRole;
import com.sap.security.api.IRoleFactory;
import com.sap.security.api.IGroup;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.IUser;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;

public class UMEUser implements IApplicationUser {
  private static Location LOC = Location.getLocation(UMEUser.class);
  private IUser user;

  private String customNamespace() {
    return "com.storm.custom.ume";
  }

  public UMEUser(IUser user) {
    super();
    this.user = user;
  }

  @Override
  public String getUniqueName() {
    return user.getUniqueName();
  }

  @Override
  public String getDisplayName() {
    return user.getDisplayName() + " (" + getUniqueName() + ")";
  }

  @Override
  public String getFirstName() {
    return user.getFirstName();
  }

  @Override
  public String getLastName() {
    return user.getLastName();
  }

  @Override
  public String getCompany() {
    return user.getCompany();
  }

  @Override
  public String getEmail() {
    return user.getEmail();
  }

  @Override
  public Set<String> getGroupUniqueNames() {
    HashSet<String> set = new HashSet<String>();
    IGroupFactory groupFactory = UMFactory.getGroupFactory();
    Iterator it = user.getParentGroups(true);
    IGroup group;
    String groupUniqueId;
    String groupUniqueName;
    while ((it != null) && (it.hasNext())) {
      groupUniqueId = (String) it.next();
      if (groupUniqueId != null) {
        try {
          group = groupFactory.getGroup(groupUniqueId);
          if (group != null) {
            groupUniqueName = group.getUniqueName();
            if (groupUniqueName != null) {
              set.add(groupUniqueName);
            }
          }
        } catch (Throwable t) {
          LOC.catching("Unable to access groupUniqueId [" + groupUniqueId + "] for user ["
              + user.getUniqueName() + "]", t);
        }
      }
    }
    return set;
  }

  @Override
  public Set<String> getRoleUniqueNames() {
    HashSet<String> set = new HashSet<String>();
    IRoleFactory roleFactory = UMFactory.getRoleFactory();
    Iterator it = user.getRoles(true);
    IRole role;
    String roleUniqueId;
    String roleUniqueName;
    while ((it != null) && (it.hasNext())) {
      roleUniqueId = (String) it.next();
      if (roleUniqueId != null) {
        try {
          role = roleFactory.getRole(roleUniqueId);
          if (role != null) {
            roleUniqueName = role.getUniqueName();
            if (roleUniqueName != null) {
              set.add(roleUniqueName);
            }
          }
        } catch (Throwable t) {
          LOC.catching("Unable to access roleUniqueId [" + roleUniqueId + "] for user ["
              + user.getUniqueName() + "]", t);
        }
      }
    }
    return set;
  }


  @Override
  public String getMiddleName() {
    return getCustomAttribute("middleName");
  }

  @Override
  public String getHonorificPrefix() {
    return user.getTitle();
  }

  @Override
  public String getHonorificSuffix() {
    return getCustomAttribute("honorificSuffix");
  }

  @Override
  public String getProfileUrl() {
    String customValue = getCustomAttribute("profileUrl");
    if (customValue == null) {
      customValue = "/irj/go/km/docs/ume/users/" + user.getUniqueName();
    }
    return customValue;
  }

  @Override
  public String getPhotoUrl() {
    String customValue = getCustomAttribute("photoUrl");
    if (customValue == null) {
      customValue = "/irj/go/km/docs/ume/users/photos" + user.getUniqueName();
    }
    return customValue;
  }

  @Override
  public String getUserType() {
    return getCustomAttribute("userType");
  }

  @Override
  public String getPreferredLanguage() {
    String customValue = getCustomAttribute("preferredLanguage");
    if (customValue == null) {
      if (user.getLocale() != null) {
        return user.getLocale().toLanguageTag();
      }
    }
    return customValue;
  }

  @Override
  public String getLocale() {
    if (user.getLocale() != null) {
      return user.getLocale().toLanguageTag();
    }
    return null;
  }

  @Override
  public String getTimeZone() {
    if (user.getTimeZone() != null) {
      return user.getTimeZone().getID();
    }
    return null;
  }

  @Override
  public boolean getActive() {
    String customValue = getCustomAttribute("active");
    if (customValue == null) {
      customValue = "true";
    }
    return Boolean.valueOf(customValue);
  }

  @Override
  public String getCompanyRelationship() {
    return getCustomAttribute("companyRelationship");
  }

  @Override
  public String getDepartment() {
    return user.getDepartment();
  }

  @Override
  public String getDivision() {
    return getCustomAttribute("division");
  }

  @Override
  public String getCostCenter() {
    return getCustomAttribute("costCenter");
  }

  @Override
  public String getManager() {
    return getCustomAttribute("manager");
  }

  @Override
  public String getPhone() {
    return user.getCellPhone();
  }

  @Override
  public String getInstantMessageAddress() {
    return getCustomAttribute("instantMessageAddress");
  }

  @Override
  public String getCountry() {
    return user.getCountry();
  }

  @Override
  public String getPostalCode() {
    return user.getZip();
  }

  @Override
  public String getRegion() {
    return getCustomAttribute("region");
  }

  @Override
  public String getLocality() {
    return user.getCity();
  }

  @Override
  public String getStreetAddress() {
    return user.getStreet();
  }

  @Override
  public String getCustomAttribute1() {
    return getCustomAttribute("customAttribute1");
  }

  @Override
  public String getCustomAttribute2() {
    return getCustomAttribute("customAttribute2");
  }

  @Override
  public String getCustomAttribute3() {
    return getCustomAttribute("customAttribute3");
  }

  @Override
  public String getCustomAttribute4() {
    return getCustomAttribute("customAttribute4");
  }

  @Override
  public String getCustomAttribute5() {
    return getCustomAttribute("customAttribute5");
  }

  @Override
  public String getCustomAttribute6() {
    return getCustomAttribute("customAttribute6");
  }

  @Override
  public String getCustomAttribute7() {
    return getCustomAttribute("customAttribute7");
  }

  @Override
  public String getCustomAttribute8() {
    return getCustomAttribute("customAttribute8");
  }

  @Override
  public String getCustomAttribute9() {
    return getCustomAttribute("customAttribute9");
  }

  @Override
  public String getCustomAttribute10() {
    return getCustomAttribute("customAttribute10");
  }

  /**
   * Primitive Storage of a custom User Attribute, ignoring multiple values
   * 
   * @param attributeName
   * @return
   */
  @Override
  public String getCustomAttribute(String attributeName) {
    return getCustomAttribute(customNamespace(),attributeName);
  }
  /**
   * Primitive Storage of a custom User Attribute, ignoring multiple values
   * 
   * @param namespace
   * @param attributeName
   * @return
   */
  @Override
  public String getCustomAttribute(String namespace, String attributeName) {
    String[] values = user.getAttribute(namespace, attributeName);
    if (values == null) {
      return null;
    } else if (values.length > 0) {
      return values[0];
    } else { // values.length == 0
      return null;
    }
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "UMEUser [user=" + user + ", getUniqueName()=" + getUniqueName() + ", getEmail()="
        + getEmail() + "]";
  }

}

