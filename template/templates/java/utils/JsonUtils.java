package {service.namespace}.utils;

import java.util.Map.Entry;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class JsonUtils {

  public static JsonObject putKeyValueInObjectWithKey(JsonObject jsonObject, String topKey,
      String key, String value) {
    JsonObjectBuilder jsonBuilder;
    // Get json builder from existing or non-existing object
    if (jsonObject.containsKey(topKey)) {
      jsonBuilder = getCopy(jsonObject.getJsonObject(topKey));
    } else {
      jsonBuilder = Json.createObjectBuilder();
    }
    jsonBuilder.add(key, value);
    JsonObject jsonObjectAdded = jsonBuilder.build();
    return putKeyValueInObject(jsonObject, topKey, jsonObjectAdded);
  }

  public static JsonObject putKeyValueInObject(JsonObject jsonObject, String key, JsonArray value) {
    JsonObjectBuilder jsonBuilder = getCopy(jsonObject);
    jsonBuilder.add(key, value);
    return jsonBuilder.build();
  }

  public static JsonObject putKeyValueInObject(JsonObject jsonObject, String key,
      JsonObject value) {
    JsonObjectBuilder jsonBuilder = getCopy(jsonObject);
    jsonBuilder.add(key, value);
    return jsonBuilder.build();
  }

  public static JsonObject putKeyValueInObject(JsonObject jsonObject, String key, Boolean value) {
    JsonObjectBuilder jsonBuilder = getCopy(jsonObject);
    jsonBuilder.add(key, value);
    return jsonBuilder.build();
  }

  public static JsonObject putKeyValueInObject(JsonObject jsonObject, String key, String value) {
    JsonObjectBuilder jsonBuilder = getCopy(jsonObject);
    jsonBuilder.add(key, value);
    return jsonBuilder.build();
  }

  public static JsonObject addObjectInArrayOfKey(JsonObject jsonObject, String topKey,
      JsonObject addedObject) {
    JsonArrayBuilder jsonArrayBuilder = JsonUtils.getArrayBuilderFromKey(jsonObject, topKey);
    jsonArrayBuilder.add(addedObject);
    JsonArray newArray = jsonArrayBuilder.build();

    return JsonUtils.putKeyValueInObject(jsonObject, topKey, newArray);
  }

  public static JsonObject putValueInArrayWithKey(JsonObject jsonObject, String topKey,
      JsonValue value) {
    JsonArrayBuilder jsonArrayBuilder = JsonUtils.getArrayBuilderFromKey(jsonObject, topKey);
    jsonArrayBuilder.add(value);
    JsonArray newArray = jsonArrayBuilder.build();
    return JsonUtils.putKeyValueInObject(jsonObject, topKey, newArray);
  }

  public static JsonArrayBuilder getArrayBuilderFromKey(JsonObject jsonObject, String topKey) {
    JsonArrayBuilder jsonBuilder;
    // Get json builder from existing or non-existing object
    if (jsonObject.containsKey(topKey)) {
      jsonBuilder = getCopy(jsonObject.getJsonArray(topKey));
    } else {
      jsonBuilder = Json.createArrayBuilder();
    }
    return jsonBuilder;
  }

  public static JsonObjectBuilder getCopy(JsonObject jsonObject) {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    // If key exists already, copy all entries to a new copy
    for (Entry<String, JsonValue> entry : jsonObject.entrySet()) {
      jsonBuilder.add(entry.getKey(), entry.getValue());
    }
    return jsonBuilder;
  }

  public static JsonArrayBuilder getCopy(JsonArray jsonArray) {
    JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
    // If key exists already, copy all entries to a new copy
    jsonArray.forEach(jsonValue -> jsonBuilder.add(jsonValue));
    return jsonBuilder;
  }
	
   public static JsonObject putKeyValueAsObjectInArrayWithKey(JsonObject object, String arrayKey, String arrayObjectKeyName, String arrayObjectValueName, String attributeName, String attributeValue) {
    
    // Ensure Attributes Array is available
    if (object.getJsonArray(arrayKey)==null) {
      JsonArray emptyArray = Json.createArrayBuilder().build();
      object = JsonUtils.putKeyValueInObject(object, arrayKey, emptyArray);
    }
    
    // ReBuild Attributes Array without given attributeName
    JsonArray attributes = object.getJsonArray(arrayKey);
    JsonArrayBuilder attributesBuilder = Json.createArrayBuilder(); 
    attributes.stream().map((jv) -> (JsonObject) jv).filter(jo -> !jo.getString(arrayObjectKeyName).equals(attributeName)).forEach(jo->attributesBuilder.add(jo));
    object = JsonUtils.putKeyValueInObject(object, arrayKey,attributesBuilder.build());
    
    // New Custom Attribute
    JsonObjectBuilder arrayElementBuilder = Json.createObjectBuilder();
    arrayElementBuilder.add(arrayObjectKeyName,attributeName);
    arrayElementBuilder.add(arrayObjectValueName,attributeValue);
    JsonObject arrayElement = arrayElementBuilder.build();

    // Add new Attributes Object to Attributes Array
    object = JsonUtils.addObjectInArrayOfKey(object,arrayKey,arrayElement);
    
    return object;
  }
}
