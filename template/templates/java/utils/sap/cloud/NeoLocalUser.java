package {service.namespace}.utils.sap.cloud;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import {service.namespace}.auth.simple.SimpleApplicationUser;
import com.sap.security.um.user.UserAttribute;

public class NeoLocalUser extends SimpleApplicationUser {

   // Logger
  private Logger LOG = LoggerFactory.getLogger(NeoLocalUser.class);

  public NeoLocalUser(JsonObject jsonUser) {

    if (jsonUser != null) {

      this.setUniqueName(Optional.ofNullable(jsonUser.getString("UID"))
          .orElseThrow(() -> new RuntimeException("UID should never be null in " + jsonUser)));

      if (jsonUser.containsKey("Groups") ) {
        this.setGroupUniqueNames(new HashSet<String>(jsonUser.getJsonArray("Groups").stream()
            .map((j) -> ((JsonString) j).getString()).collect(Collectors.toList())));
      }
      if (jsonUser.containsKey("Roles") ) {
        this.setRoleUniqueNames(new HashSet<String>(jsonUser.getJsonArray("Roles").stream()
            .map((j) -> ((JsonString) j).getString()).collect(Collectors.toList())));
      }

      // we get the list of all provided attributes and try to map them
      for (JsonValue userAttributeJsonValue : jsonUser.getJsonArray("Attributes")) {
        JsonObject userAttributeJsonObject = (JsonObject) userAttributeJsonValue;
        String userAttribute = userAttributeJsonObject.getString("attributeName");

        String userAttributeValue = userAttributeJsonObject.getString("attributeValue");

        switch (userAttribute) {
          case UserAttribute.FIRST_NAME: {
            this.setFirstName(
                Optional.ofNullable(userAttributeValue).orElseThrow(
                    () -> new RuntimeException(userAttribute+" should never be null in " + jsonUser)));
            break;
          }
          case UserAttribute.LAST_NAME: {
            this.setLastName(
                Optional.ofNullable(userAttributeValue).orElseThrow(() -> new RuntimeException(
                    userAttribute + " should never be null in " + jsonUser)));
            break;
          }
          case UserAttribute.EMAIL_ADDRESS: {
            this.setEmail(
                Optional.ofNullable(userAttributeValue).orElseThrow(() -> new RuntimeException(
                    userAttribute + " should never be null in " + jsonUser)));
            break;
          }
          case IASUser.ATTR_COMPANY: {
            this.setCompany(userAttributeValue);
            break;
          }
          case IASUser.ATTR_COMPANY_REL: {
            this.setCompanyRelationship(userAttributeValue);
            break;
          }
          case IASUser.ATTR_COST_CENTER: {
            this.setCostCenter(userAttributeValue);
            break;
          }
          case IASUser.ATTR_COUNTRY: {
            this.setCountry(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_1: {
            this.setCustomAttribute1(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_2: {
            this.setCustomAttribute2(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_3: {
            this.setCustomAttribute3(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_4: {
            this.setCustomAttribute4(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_5: {
            this.setCustomAttribute5(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_6: {
            this.setCustomAttribute6(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_7: {
            this.setCustomAttribute7(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_8: {
            this.setCustomAttribute8(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_9: {
            this.setCustomAttribute9(userAttributeValue);
            break;
          }
          case IASUser.ATTR_CUST_10: {
            this.setCustomAttribute10(userAttributeValue);
            break;
          }
          case IASUser.ATTR_DEPARTMENT: {
            this.setDepartment(userAttributeValue);
            break;
          }
          case IASUser.ATTR_DISP_NAME: {
            this.setDisplayName(userAttributeValue);
            break;
          }
          case IASUser.ATTR_DIVISION: {
            this.setDivision(userAttributeValue);
            break;
          }
          case IASUser.ATTR_HONOR_PREF: {
            this.setHonorificPrefix(userAttributeValue);
            break;
          }
          case IASUser.ATTR_HONOR_SUF: {
            this.setHonorificSuffix(userAttributeValue);
            break;
          }
          case IASUser.ATTR_IMS: {
            this.setInstantMessageAddress(userAttributeValue);
            break;
          }
          case IASUser.ATTR_ACTIVE: {
            this.setActive(Boolean.parseBoolean(userAttributeValue));
            break;
          }
          case IASUser.ATTR_LOCALE: {
            this.setLocale(userAttributeValue);
            break;
          }
          case IASUser.ATTR_LOCALITY: {
            this.setLocality(userAttributeValue);
            break;
          }
          case IASUser.ATTR_MANAGER: {
            this.setManager(userAttributeValue);
            break;
          }
          case IASUser.ATTR_MIDDLE_NAME: {
            this.setMiddleName(userAttributeValue);
            break;
          }
          case IASUser.ATTR_PHONE: {
            this.setPhone(userAttributeValue);
            break;
          }
          case IASUser.ATTR_PHOTO_URL: {
            this.setPhotoUrl(userAttributeValue);
            break;
          }
          case IASUser.ATTR_POSTAL_CODE: {
            this.setPostalCode(userAttributeValue);
            break;
          }
          case IASUser.ATTR_PREF_LANG: {
            this.setPreferredLanguage(userAttributeValue);
            break;
          }
          case IASUser.ATTR_PROFILE_URL: {
            this.setProfileUrl(userAttributeValue);
            break;
          }
          case IASUser.ATTR_REGION: {
            this.setRegion(userAttributeValue);
            break;
          }
          case IASUser.ATTR_STREET_ADDR: {
            this.setStreetAddress(userAttributeValue);
            break;
          }
          case IASUser.ATTR_TIME_ZONE: {
            this.setTimeZone(userAttributeValue);
            break;
          }
          case IASUser.ATTR_USER_TYPE: {
            this.setUserType(userAttributeValue);
            break;
          }
          default: {
            LOG.warn("Can't map the attribute [" + userAttribute + "]");
          }
        }
      }

      // create display name
      if (StringUtils.isEmpty(this.getDisplayName())) {
        this.setDisplayName(
            (StringUtils.isNotBlank(this.getFirstName()) ? this.getFirstName() + " " : "")
            + (StringUtils.isNotBlank(this.getMiddleName()) ? this.getMiddleName() + " " : "")
            + (StringUtils.isNotBlank(this.getLastName()) ? this.getLastName() + " " : "")
           );
      }
    }
  }

}
