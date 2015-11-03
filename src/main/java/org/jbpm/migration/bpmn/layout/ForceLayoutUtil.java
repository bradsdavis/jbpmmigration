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
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2LayoutData;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.w3c.dom.Document;

public class ForceLayoutUtil {
	private static final int COEFFICIENT = 12;

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
        }
        
        List<Edge> edges = BpmnShapeUtil.getEdges(graphModel, bpmn, shapeIds);
        for(Edge e : edges) {
        	directedGraph.addEdge(e);
        }
        
        //Layout for 1 minute
        AutoLayout autoLayout = new AutoLayout(1, TimeUnit.SECONDS);
        autoLayout.setGraphModel(graphModel);
        
        ForceAtlas2 secondLayout = new ForceAtlas2(null);
        secondLayout.setBarnesHutOptimize(true);
        secondLayout.setAdjustSizes(true);
        secondLayout.setScalingRatio(100.0);
        secondLayout.setBarnesHutTheta(1000.0);
        autoLayout.addLayout(secondLayout, 1.0f);
        autoLayout.execute();
        
        autoLayout.cancel();

        Float minX = null;
        Float minY = null;
        for(Node n : graphModel.getGraph().getNodes()) {
        	
        	float dx = (float)((ForceAtlas2LayoutData)(n.getNodeData().getLayoutData())).dx;
        	float dy = (float)((ForceAtlas2LayoutData)(n.getNodeData().getLayoutData())).dy;
        	Float x = (-1)*n.getNodeData().x() + (-1)*dx;
        	Float y = (-1)*n.getNodeData().y() + (-1)*dy;
        	
        	if(minX == null || minX > x) {
        		minX = x;
        	}
        	if(minY == null || minY > y) {
        		minY = y;
        	}
        }
        for (Node n : graphModel.getGraph().getNodes()) {
        	Float x = (-1)*n.getNodeData().x();
        	Float y = (-1)*n.getNodeData().y();
        	float dx = (-1)*(float)((ForceAtlas2LayoutData)(n.getNodeData().getLayoutData())).dx;
        	float dy = (-1)*(float)((ForceAtlas2LayoutData)(n.getNodeData().getLayoutData())).dy;
        	
        	x = x+dx;
        	y = y+dy;
        	
        	x = x + + (-1)*minX;
        	y = y + ((-1)*minY);
        	
        	Integer xFinal = Math.round(x) * COEFFICIENT;
        	Integer yFinal = Math.round(y) * COEFFICIENT;
        	
        	n.getAttributes().setValue("XFinal", xFinal);
        	n.getAttributes().setValue("YFinal", yFinal);
        	
        	BpmnShapeUtil.updateXY(bpmn, n.getNodeData().getLabel(), xFinal, yFinal);
        }
        
        autoLayout = null;
	}
}
