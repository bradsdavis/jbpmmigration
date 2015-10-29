/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.migration.xsl;

/**
 * Tests for the jPDL process definition transformer with JAXP.
 * 
 * @author Eric D. Schabell
 * @author Maurice de Chateau
 */
public class Jpdl3ClaimFinanceProcessTest extends AbstractJpdl3Test {
    // Input jPDL file.
    private static final String INPUT_JPDL = "src/test/resources/jpdl3/claimFinanceProcess/processdefinition.xml";
    private static final String INPUT_GPD = "src/test/resources/jpdl3/claimFinanceProcess/gpd.xml";
    
    // Ignoring this test due to input process being non-bpmn2 construction. 
    // TODO: attempt to determine a way to handle this type of problem.
    //
    @Override
    protected String getJpdlFile() {
        return INPUT_JPDL;
    }
    
    @Override
    protected String getGpdFile() {
    	return INPUT_GPD;
    }
}
