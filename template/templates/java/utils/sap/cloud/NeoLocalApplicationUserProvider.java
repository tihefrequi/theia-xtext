package {service.namespace}.utils.sap.cloud;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserProvider;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserChangeBuilder;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;
import {service.namespace}.utils.JsonUtils;

public class NeoLocalApplicationUserProvider extends IASApplicationUserProvider
    implements IApplicationUserProvider {

  protected UserProvider userProvider;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(NeoLocalApplicationUserProvider.class);

  public NeoLocalApplicationUserProvider() {
    super();
  }

  @Override
  public int countUsers(IPrincipalSearchRequest searchRequest) {

    String searchTerm = searchRequest.getSearchTerm();
    if (searchTerm != null) {
       return getUserObjects().filter((ju) -> ju.getString("UID").contains(searchTerm)).mapToInt(e->1).sum();
    } else {
      LOGGER.error("This count without search term is not supported!");
      return -1;
    }

  }

  @Override
  public IApplicationUser[] getUsers(IPrincipalSearchRequest searchRequest) {
    List<IApplicationUser> applicationUsers = new ArrayList<IApplicationUser>();
    String searchTerm = searchRequest.getSearchTerm();
    if (searchTerm != null && !"".equals(searchTerm)) {
      getUserObjects().filter((ju) -> ju.getString("UID").contains(searchTerm)).forEach((jo) -> applicationUsers.add(toApplicationUser(jo)));
      if (applicationUsers.isEmpty()) {
        LOGGER.debug("Can't find a user with name [" + searchTerm + "]");
      }
    } else {
      LOGGER.error(
          "Retrieving of users without search term is not supported! searchTerm=" + searchTerm);
    }
    return applicationUsers.toArray(new IApplicationUser[] {});
  }

  public IApplicationUser getUserByUniqueName(String userUniqueName) {
    try {
      Optional<JsonObject> userJson = getUserObjectByUniqueName(userUniqueName);
      if (userJson.isPresent()) {
        return toApplicationUser(userJson.get());
      }
    } catch (Exception e) {
      LOGGER.error("Can't get user with name [" + userUniqueName + "]", e);
    }
    return null;
  }

  public void updateUser(IApplicationUserChangeBuilder changeBuilder) throws Exception {
    try {
      IApplicationUser user = changeBuilder.getUser();
      Map<String, String> attributeChanges = (Map<String, String>) changeBuilder.getChangeData();
      Optional<JsonObject> userJsonOptional = getUserObjectByUniqueName(user.getUniqueName());
      if (userJsonOptional.isPresent()) {
        JsonObject userJson = userJsonOptional.get();
        for (Map.Entry<String, String> attributeChange : attributeChanges.entrySet()) {
          userJson =
              JsonUtils.putKeyValueAsObjectInArrayWithKey(userJson, "Attributes", "attributeName",
                  "attributeValue", attributeChange.getKey(), attributeChange.getValue());
        }
        updateUserInPrincipalFile(user, userJson);
      } else {
        LOGGER.error("Can't update user data for [" + changeBuilder.getUser()
            + "] because user is not found in neousers.json");
      }
    } catch (Exception e) {
      String text = "Can't update user data for [" + changeBuilder.getUser() + "] in neousers.json";
      LOGGER.trace(text, e);
      throw new RuntimeException(text, e);
    }

  }

  protected IApplicationUser toApplicationUser(JsonObject jsonUser) {
    NeoLocalUser applicationUser = new NeoLocalUser(jsonUser);
    return applicationUser;
  }

  protected IApplicationUser toApplicationUser(User user)
      throws UnsupportedUserAttributeException, IOException {

    Optional<JsonObject> userJson = getUserObjectByUniqueName(user.getName());
    if (userJson.isPresent()) {
      return toApplicationUser(userJson.get());
    }

    IASUser applicationUser = new IASUser(user);

    return toApplicationUserCustom(user, applicationUser);
  }

  private void updateUserInPrincipalFile(IApplicationUser user, JsonObject userJson) {
    JsonObject object = loadPrincipalUserFile();

    // ReBuild Attributes Array without given attributeName
    JsonArray attributes = object.getJsonArray("Users");
    JsonArrayBuilder attributesBuilder = Json.createArrayBuilder();
    attributes.stream().map((jv) -> (JsonObject) jv)
        .filter(jo -> !jo.getString("UID").equals(user.getUniqueName()))
        .forEach(jo -> attributesBuilder.add(jo));
    object = JsonUtils.putKeyValueInObject(object, "Users", attributesBuilder.build());

    object = JsonUtils.putValueInArrayWithKey(object, "Users", userJson);
    writePrincipalUserFile(object);

  }

  private Optional<JsonObject> getUserObjectByUniqueName(String userUniqueName) {
    return getUserObjects().filter((ju) -> ju.getString("UID").equals(userUniqueName)).findFirst();
  }

  private Stream<JsonObject> getUserObjects() {
    JsonObject jsonObject = loadPrincipalUserFile();
    JsonArray jsonUsers = jsonObject.getJsonArray("Users");
    return jsonUsers.stream().map((jv) -> (JsonObject) jv);
  }

  private JsonObject loadPrincipalUserFile() {
    return loadPrincipalFile("neousers.json");
  }

  private void writePrincipalUserFile(JsonObject jsonObject) {
    writePrincipalFile("neousers.json", jsonObject);
  }

  private JsonObject loadPrincipalFile(String fileName) {
    JsonReader jsonReader;
    try {
      jsonReader = Json.createReader(new FileReader(getNeoPrincipalFile(fileName)));
      JsonObject jsonObject = jsonReader.readObject();
      return jsonObject;
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Could not read principal file [" + fileName + "] fullPath=["
          + getNeoPrincipalFile(fileName) + "]", e);
    }
  }

  private void writePrincipalFile(String fileName, JsonObject jsonObject) {
    try {
      FileWriter fileWriter = new FileWriter(getNeoPrincipalFile(fileName));
      JsonWriter jsonWriter = Json.createWriter(fileWriter);
      jsonWriter.writeObject(jsonObject);
      jsonWriter.close();
    } catch (IOException e) {
      throw new RuntimeException("Could not write to principal file [" + fileName + "] fullPath=["
          + getNeoPrincipalFile(fileName) + "] jsonObject=[" + jsonObject + "]", e);
    }
  }

  private String getNeoPrincipalFile(String fileName) {
    return System.getProperty("path.to.runtime.installation.folder")
        + "/config_master/com.sap.security.um.provider.neo.local/" + fileName;
  }

}
