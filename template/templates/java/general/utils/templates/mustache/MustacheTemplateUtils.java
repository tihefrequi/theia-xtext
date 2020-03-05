package {service.namespace}.utils.templates.mustache;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class MustacheTemplateUtils {

  
  
  /**
   * Compile Template and Render (JsonArrays)
   * @param templateName
   * @param List
   * @param templateContent
   * @return
   */
  public static String compileAndRender(String templateName, List<Map<String, Object>> map,
      String templateContent) {
    return compileAndRenderObject(templateName, map, templateContent);
  }
  
  /**
   * Compile Template and Render (JsonObject)
   * @param templateName
   * @param map
   * @param templateContent
   * @return
   */
  public static String compileAndRender(String templateName, Map<String, Object> map,
      String templateContent) {
    return compileAndRenderObject(templateName, map, templateContent);
  }
  
  
  /**
   * Compile Template and Render     
   */
  public static String compileAndRenderObject(String templateName, Object map,
      String templateContent) {
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache musTemplate = mf.compile(new StringReader(templateContent), templateName);
    StringWriter stringWriter = new StringWriter();
    musTemplate.execute(stringWriter, map);
    stringWriter.flush();
    String result = stringWriter.toString();
    return result;
  }
}
