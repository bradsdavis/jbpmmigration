package org.jbpm.migration;

import org.w3c.dom.Document;

public interface DomProcessor {
	public void process(Document bpmn);
}
