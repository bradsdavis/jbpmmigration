package org.jbpm.migration.bpmn;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

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
public class TransposeDiagramProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(TransposeDiagramProcessor.class);
	
	@Override
	public void process(Document bpmn) {
		boolean transposeLeft = false;
		boolean transposeUp = false;
		
		//check where the start is
		Integer startX = findStartX(bpmn);
		Integer startY = findStartY(bpmn);
		
		
		Integer midX = findMidX(bpmn);
		Integer midY = findMidY(bpmn);
		
		if(startX > midX) {
			transposeLeft = true;
		}
		if(startY > midY) {
			transposeUp = true;
		}
		
		System.out.println("Mid: "+midX+midY);
		repositionToMid(bpmn, midX, midY);
		
		if(transposeLeft) {
			transposeHorizantal(bpmn);
		}
		
		if(transposeUp) {
			transposeVertical(bpmn);
		}
	}
	
	public void transposeHorizantal(Document bpmn) {
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Integer x = Integer.parseInt($(shape).child("Bounds").attr("x"));
			x = x * (-1);
			$(shape).child("Bounds").attr("x", x.toString());
		}
	}

	public void transposeVertical(Document bpmn) {
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Integer y = Integer.parseInt($(shape).child("Bounds").attr("y"));
			y = y * (-1);
			$(shape).child("Bounds").attr("y", y.toString());
		}
	}
	
	public void repositionToMid(Document bpmn, Integer midX, Integer midY) {
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Integer x = Integer.parseInt($(shape).child("Bounds").attr("x"));
			Integer y = Integer.parseInt($(shape).child("Bounds").attr("y"));
			
			x = x - midX;
			y = y - midY;
			$(shape).child("Bounds").attr("x", x.toString());
			$(shape).child("Bounds").attr("y", y.toString());
		}
	}
	
	private Integer findStartY(Document bpmn) {
		String startEventName = $(bpmn).find("startEvent").first().attr("id");
		for(Element shape : $(bpmn).find("BPMNShape").filter(attr("bpmnElement", startEventName))) {
			Integer val = Integer.parseInt($(shape).child("Bounds").attr("y"));
			return val;
		}
		
		return null;
	}
	private Integer findStartX(Document bpmn) {
		String startEventName = $(bpmn).find("startEvent").first().attr("id");
		for(Element shape : $(bpmn).find("BPMNShape").filter(attr("bpmnElement", startEventName))) {
			Integer val = Integer.parseInt($(shape).child("Bounds").attr("x"));
			return val;
		}
		
		return null;
	}
	
	private Integer findMinX(Document bpmn) {
		Integer val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Integer currentVal = Integer.parseInt($(shape).child("Bounds").attr("x"));
			
			if(val == null || val < currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	private Integer findMinY(Document bpmn) {
		Integer val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Integer currentVal = Integer.parseInt($(shape).child("Bounds").attr("y"));
			
			if(val == null || val > currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	private Integer findMaxX(Document bpmn) {
		Integer val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Integer currentVal = Integer.parseInt($(shape).child("Bounds").attr("x"));
			
			if(val == null || val > currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	private Integer findMaxY(Document bpmn) {
		Integer val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Integer currentVal = Integer.parseInt($(shape).child("Bounds").attr("y"));
			
			if(val == null || val < currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	
	
	private Integer findMidX(Document bpmn) {
		Integer max = findMaxX(bpmn);
		Integer min = findMinX(bpmn);
		
		Integer mid = (min+max)/2;
		return mid;
	}
	private Integer findMidY(Document bpmn) {
		Integer max = findMaxY(bpmn);
		Integer min = findMinY(bpmn);
		
		Integer mid = (min+max)/2;
		return mid;
	}
	
	
	private String findBpmnShapeName(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).first().attr("id");
		if(StringUtils.isNoneBlank(bpmnShapeId)) {
			return bpmnShapeId;
		}
		
		return null;
	}

	private Integer getBpmnShapeX(Document bpmn, String sourceRef) {
		String val = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("x");
		return Integer.parseInt(val);
	}
	private Integer getBpmnShapeY(Document bpmn, String sourceRef) {
		String val = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("y");
		return Integer.parseInt(val);
	}
	private void setBpmnShapeX(Document bpmn, String sourceRef, Integer x) {
		$(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("x", x.toString());
	}
	private void setBpmnShapeY(Document bpmn, String sourceRef, Integer y) {
		$(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("y", y.toString());
	}
}
