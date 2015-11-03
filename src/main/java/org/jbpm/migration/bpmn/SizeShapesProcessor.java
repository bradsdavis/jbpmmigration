package org.jbpm.migration.bpmn;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.migration.xml.DomProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sets the size of the standard elements; pads certain elements from the GPD to 
 * the BPMN2.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class SizeShapesProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(SizeShapesProcessor.class);
	
	@Override
	public void process(Document bpmn) {
		
		for(Element sequenceFlow : $(bpmn).find("startEvent").get()) {
			//find the BPMNShape with the source and targets
			String id = $(sequenceFlow).attr("id");
			setBpmnShapeHeight(bpmn, id, 30);
			setBpmnShapeWidth(bpmn, id, 30);
		}
		for(Element sequenceFlow : $(bpmn).find("endEvent").get()) {
			//find the BPMNShape with the source and targets
			String id = $(sequenceFlow).attr("id");
			setBpmnShapeHeight(bpmn, id, 28);
			setBpmnShapeWidth(bpmn, id, 28);
		}
		
		for(Element sequenceFlow : $(bpmn).find("exclusiveGateway").get()) {
			//find the BPMNShape with the source and targets
			String id = $(sequenceFlow).attr("id");
			setBpmnShapeHeight(bpmn, id, 40);
			setBpmnShapeWidth(bpmn, id, 40);
		}
		
		for(Element sequenceFlow : $(bpmn).find("parallelGateway").get()) {
			//find the BPMNShape with the source and targets
			String id = $(sequenceFlow).attr("id");
			setBpmnShapeHeight(bpmn, id, 40);
			setBpmnShapeWidth(bpmn, id, 40);
		}
		
		for(Element sequenceFlow : $(bpmn).find("scriptTask").get()) {
			//find the BPMNShape with the source and targets
			String id = $(sequenceFlow).attr("id");
			Integer width = getBpmnShapeWidth(bpmn, id);
			Integer height = getBpmnShapeHeight(bpmn, id);
			
			setBpmnShapeHeight(bpmn, id, height+4);
			setBpmnShapeWidth(bpmn, id, width+4);
		}
		for(Element sequenceFlow : $(bpmn).find("callActivity").get()) {
			//find the BPMNShape with the source and targets
			String id = $(sequenceFlow).attr("id");
			Integer width = getBpmnShapeWidth(bpmn, id);
			Integer height = getBpmnShapeHeight(bpmn, id);
			
			setBpmnShapeHeight(bpmn, id, height+8);
			setBpmnShapeWidth(bpmn, id, width+4);
		}
	}

	private String findBpmnShapeName(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).first().attr("id");
		if(StringUtils.isNoneBlank(bpmnShapeId)) {
			return bpmnShapeId;
		}
		
		return null;
	}

	private Integer getBpmnShapeHeight(Document bpmn, String sourceRef) {
		String val = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("height");
		return Integer.parseInt(val);
	}

	
	private Integer getBpmnShapeWidth(Document bpmn, String sourceRef) {
		String width = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("width");
		return Integer.parseInt(width);
	}
	
	private void setBpmnShapeWidth(Document bpmn, String sourceRef, Integer width) {
		$(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("width", width.toString());
	}
	private void setBpmnShapeHeight(Document bpmn, String sourceRef, Integer height) {
		$(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("height", height.toString());
	}
	
}
