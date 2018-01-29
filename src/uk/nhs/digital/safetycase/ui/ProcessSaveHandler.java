/*
 * 
 *   Copyright 2018  NHS Digital
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *  
 */
package uk.nhs.digital.safetycase.ui;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.Attribute;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.ui.processeditor.ProcessGraphEditor;

/**
 *
 * @author damian
 */
public class ProcessSaveHandler
        extends AbstractSaveHandler
{

    @Override
    public void handle(BasicGraphEditor ge) throws Exception {
       try {
           // TODO NEXT: Amend to handle "existing steps" from the editor, modelled on the 
           // way that the bowtie editor does it (but without the connections - see the
           // comment in ProcessEditor.setPersistableObject() about this).
           
            ProcessGraphEditor pge = (ProcessGraphEditor)ge;
            MetaFactory mf = MetaFactory.getInstance();
            PersistableFactory<uk.nhs.digital.safetycase.data.Process> pf = mf.getFactory("Process");
            PersistableFactory<ProcessStep> psf = mf.getFactory("ProcessStep");
            uk.nhs.digital.safetycase.data.Process process = pf.get(pge.getProcessId());
            if (process == null)
                return;            

            String xml = getXml(ge);
            parseSteps(xml, pge.getProcessId());
            process.setAttribute("GraphXml", new Attribute(xml));
            pf.put(process);
            SmartProject.getProject().editorEvent(Project.UPDATE, process);
            
       }
// Re-add this later
//       catch (BrokenConnectionException bce) {
//            System.err.println("TODO: Notify user that the diagram has a broken link and has not been saved: " + bce.getMessage());
//       }
       catch (Exception ex) {
            ex.printStackTrace();
       }    
       
    }
   
    private void parseSteps(String xml, int processid) 
            throws Exception
    {
        // Get current process steps for this process, and populate "steps" if there
        // are any. Then go through what we have and make sure the database version is in
        // sync with the graph
        //
        HashMap<Integer,ProcessStep> steps = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(xml);
        InputSource is = new InputSource(sr);
        Element d = db.parse(is).getDocumentElement();
        NodeList nl = d.getElementsByTagName("mxCell");
        MetaFactory mf = MetaFactory.getInstance();
        PersistableFactory<ProcessStep> psf = mf.getFactory("ProcessStep");
        ArrayList<Persistable> existingSteps = mf.getChildren("ProcessStep", "ProcessID", processid);
        if (existingSteps != null) {
            for (Persistable p : existingSteps) {
                steps.put(Integer.parseInt(p.getAttributeValue("GraphCellId")), (ProcessStep)p);

                // Whilst we're at it...
                // Anything deleted ? Things in the known list that aren't in the node list
                //
                boolean deleted = true;
                for (int i = 0; i < nl.getLength(); i++) {
                    Element cell = (Element)nl.item(i);
                    String id = cell.getAttribute("id");
                    if (id.contentEquals(p.getAttributeValue("GraphCellId")))
                        deleted = false;
                }
                if (deleted)
                    psf.delete((ProcessStep)p);
            }
        }
        
        // Do the process steps
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element)nl.item(i);
            if ((cell.getAttributeNode("style") == null) && (cell.getAttributeNode("value") == null))
                continue;
            if (cell.getAttributeNode("edge") == null) {
                // See if we know about this one and make sure it is up to date if we do,
                // if we don't, make a new ProcessStep
                String id = cell.getAttribute("id");
                ProcessStep ps = steps.get(Integer.parseInt(id));
                if (ps != null) {
                    String n = cell.getAttribute("value");
                    if (!ps.getAttributeValue("Name").equals(n)) {
                        ps.setAttribute("Name", n);
                    }
                } else {
                    String style = cell.getAttribute("style");
                    String n = cell.getAttribute("value");
                    ps = new ProcessStep();
                    int cellId = Integer.parseInt(id);
                    steps.put(cellId, ps);
                    ps.setAttribute("GraphCellId", cellId);
                    ps.setAttribute("Name", n);
                    ps.setAttribute("ProcessID", processid);
                    if (style == "") {
                        ps.setAttribute("Type", "Activity");
                    } else {
                        if (style.contentEquals("rhombus")) {
                            ps.setAttribute("Type", "Decision");
                        } else {
                            if (n.equals("Start"))
                                ps.setAttribute("Type", "Start");
                            else
                                ps.setAttribute("Type", "Stop");
                        }
                    }
                }
                psf.put(ps);
            }
        }
    }    
    
}
