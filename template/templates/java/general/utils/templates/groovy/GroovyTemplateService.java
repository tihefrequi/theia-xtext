package {service.namespace}.utils.templates.groovy;

import java.util.HashMap;

import groovy.lang.Binding;

public class GroovyTemplateService {
	
	public static HashMap<Integer, GroovyTemplate> GROOVY_TEMPLATES = new HashMap<>();
	
	public static GroovyTemplate getCompiledGroovyTemplate(String quellText, Binding binding) {
		 
		if(GROOVY_TEMPLATES.get(quellText.hashCode()) != null) {
			return GROOVY_TEMPLATES.get(quellText.hashCode());
		} else {
			// ...create new groovyScript
			GroovyTemplate groovyTemplate = GroovyTemplateProvider.createGroovyTemplate(quellText);
			GROOVY_TEMPLATES.put(quellText.hashCode(), groovyTemplate);
			return groovyTemplate;
		}
	}
}
