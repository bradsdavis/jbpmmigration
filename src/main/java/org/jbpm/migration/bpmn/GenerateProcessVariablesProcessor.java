package org.jbpm.migration.bpmn;

import static org.joox.JOOX.$;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jbpm.migration.xml.DomProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Checks for multiple targetRef's pointing to BPMN nodes that aren't converging gateways.
 * The processor then introduces the converging gateway to the workflow to align with BPMN 2.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class GenerateProcessVariablesProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(GenerateProcessVariablesProcessor.class);
	
	@Override
	public void process(Document bpmn) {
		
		Set<String> variableNames = new HashSet<String>(); 
    	for(Element dataInput : $(bpmn).find("dataInput").get()) {
    		variableNames.add($(dataInput).attr("name"));
    	}
    	for(Element dataInput : $(bpmn).find("dataOutput").get()) {
    		variableNames.add($(dataInput).attr("name"));
    	}
    	
    	addVariableType(bpmn, "String");
    	for(String variableName : variableNames) {
    		addProcessVariable(bpmn, variableName, "String");
    	}
    	
	}

	private void addVariableType(Document document, String variableType) {
		String itemTypeId = generateVariableTypeId(variableType);
		
		//add item type
		//<bpmn2:itemDefinition id="ItemDefinition_3" isCollection="false" structureRef="Example1"/>
		$(document).find("process").first().parent().prepend(
				$("itemDefinition").attr("id", itemTypeId)
					.attr("isCollection", "false")
					.attr("structureRef", itemTypeId));
	}
	private void addProcessVariable(Document document, String variableName, String variableType) {
		LOG.debug("Must Introduce: Process Variable ["+variableName+"] :: of type: ["+variableType+"]");
		
		//<bpmn2:property id="processVar1" itemSubjectRef="ItemDefinition_1" name="processVar1"/>
		$(document).find("process").first().prepend(
				$("property").attr("id", variableName)
					.attr("name", variableName)
					.attr("itemSubjectRef", generateVariableTypeId(variableType)));
	}
	
	private String generateVariableTypeId(String variableType) {
		String typeId = "ItemType_"+variableType;
		return typeId;
	}
	
}
