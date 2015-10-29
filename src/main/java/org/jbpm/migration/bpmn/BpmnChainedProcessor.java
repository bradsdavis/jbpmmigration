package org.jbpm.migration.bpmn;

import java.util.LinkedList;
import java.util.List;

import org.jbpm.migration.DomProcessor;
import org.w3c.dom.Document;

/**
 * @author bradsdavis
 *
 */
public class BpmnChainedProcessor implements DomProcessor {

	private final List<DomProcessor> domEnhancers;
	
	public BpmnChainedProcessor() {
		domEnhancers = new LinkedList<DomProcessor>();
		domEnhancers.add(new MultiTargetToConvergingGatewayProcessor());
		domEnhancers.add(new MultiSourceToDivergingGatewayProcessor());
	}

	public void preProcess(DomProcessor processor) {
		domEnhancers.add(0, processor);
	}
	public void postProcess(DomProcessor processor) {
		domEnhancers.add(processor);
	}
	
	@Override
	public void process(Document bpmn) {
		for(DomProcessor enhancer : domEnhancers) {
			enhancer.process(bpmn);
		}
	}
	
	
	
}
