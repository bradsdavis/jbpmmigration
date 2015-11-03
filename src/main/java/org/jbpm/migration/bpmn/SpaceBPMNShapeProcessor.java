package org.jbpm.migration.bpmn;

import static org.joox.JOOX.$;

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
public class SpaceBPMNShapeProcessor implements DomProcessor {

	private final double X_SPACE_FACTOR = 1.5;
	private final double Y_SPACE_FACTOR = 1.5;
	private static final Logger LOG = Logger.getLogger(SpaceBPMNShapeProcessor.class);
	
	@Override
	public void process(Document bpmn) {
		int minX = -1;
		int minY = -1;
		
		
		for(Element shape : $(bpmn).find("BPMNShape")) {
			Element bounds = $(shape).child("Bounds").first().get(0);
			String x = $(bounds).attr("x");
			String y = $(bounds).attr("y");
			
			if(StringUtils.isNotBlank(x)&&StringUtils.isNotBlank(y)) {
				Integer xVal = Integer.parseInt(x);
				Integer yVal = Integer.parseInt(y);

				xVal = (int)Math.round(xVal * X_SPACE_FACTOR);
				yVal = (int)Math.round(yVal * Y_SPACE_FACTOR);

				if(minX == -1 || xVal < minX) {
					minX = xVal;
				}
				if(minY == -1 || yVal < minY) {
					minY = yVal;
				}
				
				$(bounds).attr("x", xVal.toString());
				$(bounds).attr("y", yVal.toString());
			}
		}
		
		if(minX>0&&minY>0) {
			System.out.println("MinX: "+minX+", MinY: "+minY);
			
			for(Element shape : $(bpmn).find("BPMNShape")) {
				Element bounds = $(shape).child("Bounds").first().get(0);
				String x = $(bounds).attr("x");
				String y = $(bounds).attr("y");
				
				if(StringUtils.isNotBlank(x)&&StringUtils.isNotBlank(y)) {
					Integer xVal = Integer.parseInt(x);
					Integer yVal = Integer.parseInt(y);
	
					xVal = xVal - minX + 80;
					yVal = yVal - minY + 80;
					
					$(bounds).attr("x", xVal.toString());
					$(bounds).attr("y", yVal.toString());
				}
			}
		}
	}


	
}
