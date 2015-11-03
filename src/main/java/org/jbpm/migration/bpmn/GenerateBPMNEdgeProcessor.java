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
				addBpmnEdge(bpmn, bpmnEdgeName, flowId, sourceBpmnShapeName, targetBpmnShapeName);
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

	private Integer findBpmnShapeX(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("x");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Integer.parseInt(bpmnShapeId);
		}
		
		return null;
	}
	
	private Integer findBpmnShapeY(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("y");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Integer.parseInt(bpmnShapeId);
		}
		
		return null;
	}
	
	private Integer findBpmnShapeWidth(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("width");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Integer.parseInt(bpmnShapeId);
		}
		
		return null;
	}
	
	private Integer findBpmnShapeHeight(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("height");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Integer.parseInt(bpmnShapeId);
		}
		
		return null;
	}
	
	
	private String findBpmnShapeCoordinate(Document bpmn, String ref, Coordinate coordinate) {
		return $(bpmn).find("BPMNShape").filter(attr("id", ref)).first().find("Bounds").first().attr(coordinate.coorinate);
	}

	private void addBpmnEdge(Document document, String bpmnEdgeId, String sequenceFlowId, String bpmnSourceShape, String bpmnTargetShape) {
		Integer sourceX = findBpmnShapeX(document, bpmnSourceShape);
		Integer sourceY = findBpmnShapeY(document, bpmnSourceShape);
		Integer sourceHeight = findBpmnShapeHeight(document, bpmnSourceShape);
		Integer sourceWidth = findBpmnShapeWidth(document, bpmnSourceShape);
		Integer sourceWaypointX = sourceX + Math.round(sourceWidth/2);
		Integer sourceWaypointY = sourceY - Math.round(sourceHeight/2);
		
		
		Integer targetX = findBpmnShapeX(document, bpmnTargetShape);
		Integer targetY = findBpmnShapeY(document, bpmnTargetShape);
		Integer targetHeight = findBpmnShapeHeight(document, bpmnTargetShape);
		Integer targetWidth = findBpmnShapeWidth(document, bpmnTargetShape);
		Integer targetWaypointX = targetX + Math.round(targetWidth/2);
		Integer targetWaypointY = targetY - Math.round(targetHeight/2);
		
		
		System.out.println("X: "+sourceWaypointX+" Y: "+sourceWaypointY);
		
		Match sourceWayPoint = $("di:waypoint").attr("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
		sourceWayPoint.attr("x", sourceWaypointX+".0").attr("y", sourceWaypointY+".0");
		sourceWayPoint.attr("xsi:type", "dc:Point");
		
		Match targetWayPoint = $("di:waypoint").attr("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
		targetWayPoint.attr("x", targetWaypointX+".0").attr("y", targetWaypointY+".0");
		targetWayPoint.attr("xsi:type", "dc:Point");
		
		//<bpmn2:property id="processVar1" itemSubjectRef="ItemDefinition_1" name="processVar1"/>
		$(document).find("BPMNPlane").first().append(
				$("bpmndi:BPMNEdge").attr("id", bpmnEdgeId)
					.attr("bpmnElement", sequenceFlowId)
					.attr("sourceElement", bpmnSourceShape)
					.attr("targetElement", bpmnTargetShape)
					.append(sourceWayPoint).append(targetWayPoint));
	}
	
	
	private enum Coordinate {
		X("x"), Y("y");
		
		final String coorinate;
		
		private Coordinate(String coordinate) {
			this.coorinate = coordinate;
		}
	}
	
}
