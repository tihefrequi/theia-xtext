package {service.namespace}.utils.templates.groovy;

import groovy.lang.GroovyShell;

public class GroovyTemplateProvider {

	public static GroovyTemplate createGroovyTemplate(String quellText) {

		GroovyShell shell = new GroovyShell();
		return new GroovyTemplate(shell.parse(quellText)); 
	}
}
