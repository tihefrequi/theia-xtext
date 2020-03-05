package {service.namespace}.utils.templates.groovy;

import java.io.IOException;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import groovy.text.Template;

public class GroovyTemplateUtils {

	/**
	 * Compile Template and Render
	 * 
	 * @param templateName
	 * @param map
	 * @param templateContent
	 * @return
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws CompilationFailedException
	 */
	public static String compileAndRenderGString(String templateName, Map<String, Object> binding,
			String templateContent) throws CompilationFailedException, ClassNotFoundException, IOException {
		GStringTemplateEngine engine = new groovy.text.GStringTemplateEngine();
		Template template = engine.createTemplate(templateContent);
		Writable writable = template.make(binding);
		String result = writable.toString();
		return result;
	}

}
