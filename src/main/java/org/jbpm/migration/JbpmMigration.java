/**
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.jbpm.migration;

import static org.joox.JOOX.$;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.migration.Validator.ProcessLanguage;
import org.jbpm.migration.bpmn.BpmnChainedProcessor;
import org.jbpm.migration.bpmn.GpdToBpmnProcessor;
import org.jbpm.migration.bpmn.MultiSourceToDivergingGatewayProcessor;
import org.w3c.dom.Document;

/**
 * @author Eric D. Schabell
 * @author Maurice de Chateau
 * @author Brad Davis - bradsdavis@gmail.com
 */
public final class JbpmMigration {
    // Default XSLT sheet.
	private static final Logger LOG = Logger.getLogger(JbpmMigration.class);
    private static final String DEFAULT_XSLT_SHEET = "jpdl3-bpmn2.xsl";
    
    /** Private constructor to prevent instantiation. */
    private JbpmMigration() {
    }

    /**
     * Accept two or three command line arguments: - the name of an XML file (required) - the name of an XSLT stylesheet (optional, default is jpdl 3.2) - the
     * name of the file the result of the transformation is to be written to.
     */
    public static void main(final String[] args) throws TransformerException {
        if (args.length == 2) {
            // using default jpdl 3.2 as XSLT stylesheet.
            transform(args[0], null, args[1]);
        } else if (args.length == 3) {
            // using arg[1] as XSLT stylesheet.
            transform(args[0], args[1], args[2]);
        } else if (args.length == 4) {
        	
        } else {
            System.err.println("Usage:");
            System.err.println("  java " + JbpmMigration.class.getName() + " jpdlProcessDefinitionFileName xsltFileName outputFileName");
            System.err.println(" or you can use the default jpdl 3.2 transformation:");
            System.err.println("  java " + JbpmMigration.class.getName() + " jpdlProcessDefinitionFileName outputFileName");
            System.exit(1);
        }
    }

    /**
     * Perform the transformation called from the main method.
     * 
     * @param xmlFileName
     *            The name of an XML input file.
     * @param xsltFileName
     *            The name of an XSLT stylesheet.
     * @param outputFileName
     *            The name of the file the result of the transformation is to be written to.
     */
    private static void transform(final String xmlFileName, final String xsltFileName, final String outputFileName) {
        Source xsltSource = null;
        if (StringUtils.isNotBlank(xsltFileName)) {
            // Use the given stylesheet.
            xsltSource = new StreamSource(new File(xsltFileName));
        } else {
            // Use the default stylesheet.
            xsltSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_XSLT_SHEET));
        }

        // Transform the given input file and put the result in the given output file.
        final Source xmlSource = new StreamSource(new File(xmlFileName));
        final Result xmlResult = new StreamResult(new File(outputFileName));
        
        XmlUtils.transform(xmlSource, xsltSource, xmlResult);
    }
    

    /** 
     * Transforms jPDL to BPMN; supports GPD 
     * 
     * @param jpdlFile
     * @param gpdFile
     * @param bpmnFile
     */
    public static void transform(final File jpdlFile, final File gpdFile, final File bpmnFile) {
    	
    	transform(jpdlFile.getAbsolutePath(), null, bpmnFile.getAbsolutePath());
    	
    	
    	//processes the BPMN to resolve legacy jPDL patterns to BPMN compliant XML
        Document document;
		try {
			document = $(bpmnFile).document();
			
			BpmnChainedProcessor cp = new BpmnChainedProcessor();
			if(gpdFile != null) {
				cp.preProcess(new GpdToBpmnProcessor(gpdFile));
			}
	    	cp.process(document);
			
			$(document).write(new FileOutputStream(bpmnFile));
		} catch (Exception e) {
			LOG.error("Exception processing BPMN file.", e);
		}
		
    }

    
    public static void transform(final File jpdlFile, final File bpmnFile) {
    	transform(jpdlFile.getAbsolutePath(), null, bpmnFile.getAbsolutePath());
    }

    /**
     * API call to transform a JPDL definition (currently version 3.2) to a BPMN2 definition.
     * 
     * @param inputString
     *            The input JPDL definition (as an XML {@link String}).
     * @return The output BPMN2 definition (as an XML {@link String} as well).
     */
    @Deprecated
    public static String transform(final String inputString) {
        final StringWriter outputWriter = new StringWriter();

        // Transform using the default stylesheet.
        final Source xsltSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_XSLT_SHEET));
        XmlUtils.transform(new StreamSource(new StringReader(inputString)), xsltSource, new StreamResult(outputWriter));

        Document document = $(outputWriter.toString()).document();
        return $(document).toString();
    }
    

    public static boolean validateJpdl(final String inputString) {
        return Validator.validateDefinition(inputString, ProcessLanguage.JPDL);
    }

    public static boolean validateBpmn(final String inputString) {
        return Validator.validateDefinition(inputString, ProcessLanguage.BPMN);
    }
}
