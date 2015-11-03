package org.jbpm.migration.bpmn;

import static org.joox.JOOX.$;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
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
public class GenerateUniqueIoSpecificationIdProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(GenerateUniqueIoSpecificationIdProcessor.class);
	
	@Override
	public void process(Document bpmn) {
    	for(Element ioSpecification : $(bpmn).find("ioSpecification").get()) {
    		Map<String, String> uniqueInputIdMap = new HashMap<String, String>();
    		Map<String, String> uniqueOutputIdMap = new HashMap<String, String>();
    		
    		generateInputSetId(ioSpecification);
    		mapInputOutput(ioSpecification, uniqueInputIdMap, uniqueOutputIdMap);
    		processInputs(ioSpecification, uniqueInputIdMap);
    		processOutputs(ioSpecification, uniqueOutputIdMap);
    	}
	}

	private void generateInputSetId(Element ioSpecification) {
		for(Element input : $(ioSpecification).find("inputSet")) {
			String uuid = "InputSet" + "_"+UUID.randomUUID().toString();
			$(input).attr("id", uuid);
		}
		
		for(Element output : $(ioSpecification).find("outputSet")) {
			String uuid = "OutputSet" + "_"+UUID.randomUUID().toString();
			$(output).attr("id", uuid);
		}
	}

	private void mapInputOutput(Element ioSpecification, Map<String, String> uniqueInputIdMap, Map<String, String> uniqueOutputIdMap) {
		//get all data inputs
		for(Element input : $(ioSpecification).find("dataInput")) {
			String id = $(input).attr("id");
			String uuid = id + "_"+UUID.randomUUID().toString();
			uniqueInputIdMap.put(id, uuid);
			
			$(input).attr("id", uuid);
		}
		
		//get all data output
		for(Element input : $(ioSpecification).find("dataOutput")) {
			String id = $(input).attr("id");
			String uuid = id + "_"+UUID.randomUUID().toString();
			uniqueOutputIdMap.put(id, uuid);
			
			$(input).attr("id", uuid);
		}
	}

	private void processInputs(Element ioSpecification, Map<String, String> uniqueInputIdMap) {
		
		for(Element inputRef : $(ioSpecification).find("dataInputRefs")) {
			String id = $(inputRef).text();
			String uuid = uniqueInputIdMap.get(id);
			
			if(StringUtils.isNotBlank(uuid)) {
				$(inputRef).text(uuid);
			}
		}
		
		for(Element inputRef : $(ioSpecification).parent().find("dataInputAssociation")) {
			String target = $(inputRef).child("targetRef").text();
			String targetUUID = uniqueInputIdMap.get(target);
			
			if(StringUtils.isNotBlank(targetUUID)) {
				$(inputRef).child("targetRef").text(targetUUID);
			}
		}
	}

	private void processOutputs(Element ioSpecification, Map<String, String> uniqueOutputIdMap) {
		for(Element outputRef : $(ioSpecification).find("dataOutputRefs")) {
			String id = $(outputRef).text();
			String uuid = uniqueOutputIdMap.get(id);
			
			if(StringUtils.isNotBlank(uuid)) {
				$(outputRef).text(uuid);
			}
		}

		for(Element outputRef : $(ioSpecification).parent().find("dataOutputAssociation")) {
			String source = $(outputRef).child("sourceRef").text();
			String sourceUUID = uniqueOutputIdMap.get(source);
			
			if(StringUtils.isNotBlank(sourceUUID)) {
				$(outputRef).child("sourceRef").text(sourceUUID);
			}
		}
	}
	

}
