package org.jbpm.migration.xml;

import java.util.LinkedList;
import java.util.List;

import org.jbpm.migration.bpmn.ForceLayoutShapesProcessor;
import org.jbpm.migration.bpmn.GenerateBPMNEdgeProcessor;
import org.jbpm.migration.bpmn.GenerateProcessVariablesProcessor;
import org.jbpm.migration.bpmn.GenerateUniqueIoSpecificationIdProcessor;
import org.jbpm.migration.bpmn.MultiSourceToDivergingGatewayProcessor;
import org.jbpm.migration.bpmn.MultiTargetToConvergingGatewayProcessor;
import org.jbpm.migration.bpmn.SizeShapesProcessor;
import org.jbpm.migration.bpmn.SpaceBPMNShapeProcessor;
import org.jbpm.migration.bpmn.TransposeDiagramProcessor;
import org.w3c.dom.Document;

/**
 * @author bradsdavis@gmail.com
 *
 */
public class BpmnChainedProcessor implements DomProcessor {

	private final List<DomProcessor> domEnhancers;
	
	public BpmnChainedProcessor() {
		domEnhancers = new LinkedList<DomProcessor>();
		domEnhancers.add(new GenerateProcessVariablesProcessor());
		domEnhancers.add(new GenerateUniqueIoSpecificationIdProcessor());
		domEnhancers.add(new MultiTargetToConvergingGatewayProcessor());
		domEnhancers.add(new MultiSourceToDivergingGatewayProcessor());
		domEnhancers.add(new SpaceBPMNShapeProcessor());
		domEnhancers.add(new ForceLayoutShapesProcessor());
		domEnhancers.add(new SizeShapesProcessor());
		domEnhancers.add(new TransposeDiagramProcessor());
		domEnhancers.add(new GenerateBPMNEdgeProcessor());
	}

	public void preProcess(DomProcessor processor) {
		domEnhancers.add(0, processor);
	}
	public void postProcess(DomProcessor processor) {
		domEnhancers.add(processor);
	}
	
	@Override
	public void process(Document doc) {
		for(DomProcessor enhancer : domEnhancers) {
			enhancer.process(doc);
		}
	}
	
	
	
}
