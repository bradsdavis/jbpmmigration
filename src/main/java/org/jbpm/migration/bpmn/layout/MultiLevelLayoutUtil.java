package org.jbpm.migration.bpmn.layout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.w3c.dom.Document;

public class MultiLevelLayoutUtil {
	private static final double COEFFICIENT = 2.5;

	public static void layout(Document bpmn) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();

        //See if graph is well imported
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        Map<String, Node> shapeIds = BpmnShapeUtil.getShapeIds(graphModel, bpmn);
		
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        
        //Create three nodes
        for(Node n : shapeIds.values()) {
        	directedGraph.addNode(n);
        	
        	System.out.println("Node: "+n.getNodeData().getId());
        }
        
        List<Edge> edges = BpmnShapeUtil.getEdges(graphModel, bpmn, shapeIds);
        for(Edge e : edges) {
        	directedGraph.addEdge(e);
        }
        
        
        System.out.println("Nodes: " + directedGraph.getNodeCount());
        System.out.println("Edges: " + directedGraph.getEdgeCount());
        
        //Layout for 1 minute
        AutoLayout autoLayout = new AutoLayout(5, TimeUnit.SECONDS);
        autoLayout.setGraphModel(graphModel);
        
        YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(2f));
        firstLayout.setOptimalDistance(1000f);
        firstLayout.setBarnesHutTheta(0f);
        firstLayout.setStepRatio(2f);
        autoLayout.addLayout(firstLayout, 1.0f);
        autoLayout.execute();
        
        autoLayout.cancel();

        Float minX = null;
        Float minY = null;
        for(Node n : graphModel.getGraph().getNodes()) {
        	
        	float dx = 0;
        	float dy = 0;
        	Float x = (-1)*n.getNodeData().x() + (-1)*dx;
        	Float y = (-1)*n.getNodeData().y() + (-1)*dy;
        	
        	if(minX == null || minX > x) {
        		minX = x;
        	}
        	if(minY == null || minY > y) {
        		minY = y;
        	}
        }
        System.out.println("Min: X: "+minX+", Y: "+minY);
       
        for (Node n : graphModel.getGraph().getNodes()) {
        	Float x = (-1)*n.getNodeData().x();
        	Float y = (-1)*n.getNodeData().y();
        	float dx = 0;
        	float dy = 0;
        	
        	x = x+dx;
        	y = y+dy;
        	
        	System.out.println("Was X: "+x+" Y: "+y);
        	x = x + + (-1)*minX;
        	y = y + ((-1)*minY);
        	
        	Integer xFinal = (int)Math.round(x * COEFFICIENT);
        	Integer yFinal = (int)Math.round(y * COEFFICIENT);
        	
        	n.getAttributes().setValue("XFinal", xFinal);
        	n.getAttributes().setValue("YFinal", yFinal);
        	System.out.println("Is X: "+xFinal+" Y: "+yFinal);
        	
        	BpmnShapeUtil.updateXY(bpmn, n.getNodeData().getLabel(), xFinal, yFinal);
        }
        
        autoLayout = null;
	}
}
