package org.jbpm.migration.bpmn.layout;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class BpmnShapeUtil {

	public static void updateXY(Document bpmn, String shapeName, Integer x, Integer y) {
		for(Element shape : $(bpmn).find("BPMNShape").filter(attr("id", shapeName))) {
			Element bounds = $(shape).child("Bounds").first().get(0);
						
			$(bounds).attr("x", x.toString());
			$(bounds).attr("y", y.toString());
		}
	}
	
	public static Map<String, Node> getShapeIds(GraphModel graphModel, Document bpmn) {
		Map<String, Node> shapeIds = new HashMap<String, Node>();
		for(Element shape : $(bpmn).find("BPMNShape")) {
			String id = $(shape).attr("id");
			
			Node n0 = graphModel.factory().newNode(id);
            n0.getNodeData().setLabel(id);
			shapeIds.put(id, n0);
			n0.getNodeData().setSize(1000.0f);
		}
		
		return shapeIds;
	}
	
	public static List<Edge> getEdges(GraphModel graphModel, Document bpmn, Map<String, Node> nodes) {
		List<Edge> edges = new LinkedList<Edge>();
		for(Element sequenceFlow : $(bpmn).find("sequenceFlow").get()) {
			String sourceRef = $(sequenceFlow).attr("sourceRef");
			String sourceBpmnShapeName = findBpmnShapeName(bpmn, sourceRef);
			
			if(StringUtils.isBlank(sourceBpmnShapeName)) {
				sourceBpmnShapeName = addBpmnShape(graphModel, bpmn, sourceRef, nodes);
			}
			
			String targetRef = $(sequenceFlow).attr("targetRef");
			String targetBpmnShapeName = findBpmnShapeName(bpmn, targetRef);
		
			if(StringUtils.isBlank(targetBpmnShapeName)) {
				targetBpmnShapeName = addBpmnShape(graphModel, bpmn, targetRef, nodes);
			}
			
			Node source = nodes.get(sourceBpmnShapeName);
			Edge edge = graphModel.factory().newEdge(nodes.get(sourceBpmnShapeName), nodes.get(targetBpmnShapeName), 2f, true);
			edges.add(edge);
		}
		
		return edges;
	}
	
	private static String addBpmnShape(GraphModel graphModel, Document document, String bpmnElementId, Map<String, Node> nodes) {
		String elementId = "BPMNShape_"+bpmnElementId;
		//<dc:Bounds height="30.0" width="30.0" x="0.0" y="0.0"/>
		Match bounds = $("dc:Bounds").attr("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
		bounds.attr("x", "0").attr("y", "0");
		bounds.attr("width", "120").attr("height", "80");
		
		//<bpmn2:property id="processVar1" itemSubjectRef="ItemDefinition_1" name="processVar1"/>
		$(document).find("BPMNPlane").first().append(
				$("bpmndi:BPMNShape").attr("id", elementId)
					.attr("bpmnElement", bpmnElementId)
					.append(bounds));
		
		Node n0 = graphModel.factory().newNode(elementId);
        n0.getNodeData().setLabel(elementId);
		nodes.put(elementId, n0);
		n0.getNodeData().setSize(1000.0f);
		
		return elementId;
	}
	
	private static String findBpmnShapeName(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).first().attr("id");
		if(StringUtils.isNoneBlank(bpmnShapeId)) {
			return bpmnShapeId;
		}
		
		return null;
	}
	
}
