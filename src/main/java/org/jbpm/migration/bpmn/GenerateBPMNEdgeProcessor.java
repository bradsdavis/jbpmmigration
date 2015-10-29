package org.jbpm.migration.bpmn;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.migration.xml.DomProcessor;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Checks for multiple targetRef's pointing to BPMN nodes that aren't converging gateways.
 * The processor then introduces the converging gateway to the workflow to align with BPMN 2.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class GenerateBPMNEdgeProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(GenerateBPMNEdgeProcessor.class);
	
	@Override
	public void process(Document bpmn) {
		
		for(Element sequenceFlow : $(bpmn).find("sequenceFlow").get()) {
			//find the BPMNShape with the source and targets
			String flowId = $(sequenceFlow).attr("id");
			
			String sourceRef = $(sequenceFlow).attr("sourceRef");
			String sourceBpmnShapeName = findBpmnShapeName(bpmn, sourceRef);
			
			
			
			String targetRef = $(sequenceFlow).attr("targetRef");
			String targetBpmnShapeName = findBpmnShapeName(bpmn, targetRef);
			
			
			String bpmnEdgeName = "BPMNEdge_"+flowId;
			if(StringUtils.isNoneBlank(sourceBpmnShapeName) && StringUtils.isNotBlank(targetBpmnShapeName)) {
				String x = findBpmnShapeCoordinate(bpmn, targetBpmnShapeName, Coordinate.X);
				String y = findBpmnShapeCoordinate(bpmn, targetBpmnShapeName, Coordinate.Y);
				
				addBpmnEdge(bpmn, bpmnEdgeName, flowId, sourceBpmnShapeName, targetBpmnShapeName, x, y);
			}
			else {
				System.out.println("Didn't create BPMNEdge for: "+flowId+": source["+sourceRef+"] target["+targetBpmnShapeName+"]");
			}
				
		}
		
    	
	}

	private String findBpmnShapeName(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).first().attr("id");
		if(StringUtils.isNoneBlank(bpmnShapeId)) {
			return bpmnShapeId;
		}
		
		return null;
	}
	
	private String findBpmnShapeCoordinate(Document bpmn, String ref, Coordinate coordinate) {
		return $(bpmn).find("BPMNShape").filter(attr("bpmnElement", ref)).find("Bounds").first().attr(coordinate.coorinate);
	}

	private void addBpmnEdge(Document document, String bpmnEdgeId, String sequenceFlowId, String bpmnSourceShape, String bpmnTargetShape, String x, String y) {
		if(StringUtils.isBlank(x)) {
			x = "0";
		}
		if(StringUtils.isBlank(y)) {
			y = "0";
		}
		
		Match wayPoint = $("di:waypoint").namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance").namespace("di", "http://www.omg.org/spec/DD/20100524/DI");
		wayPoint.attr("x", x).attr("y", y);
		wayPoint.attr("xsi:type", "dc:Point");
		
		//<bpmn2:property id="processVar1" itemSubjectRef="ItemDefinition_1" name="processVar1"/>
		$(document).find("BPMNPlane").first().append(
				$("bpmndi:BPMNEdge").attr("id", bpmnEdgeId)
					.attr("bpmnElement", sequenceFlowId)
					.attr("sourceElement", bpmnSourceShape)
					.attr("targetElement", bpmnTargetShape)
					.append(wayPoint));
	}
	
	
	private enum Coordinate {
		X("x"), Y("y");
		
		final String coorinate;
		
		private Coordinate(String coordinate) {
			this.coorinate = coordinate;
		}
	}
	
}
