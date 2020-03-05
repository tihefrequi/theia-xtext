package {service.namespace}.utils.templates.groovy;

import groovy.lang.Binding;
import groovy.lang.Script;

public class GroovyTemplate {

	private Script script;
	
	public GroovyTemplate(Script script) {
		this.script = script;
	}
	
	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

	public Object execute(Binding binding) {
		
		script.setBinding(binding);
		return script.run();
	}
}
