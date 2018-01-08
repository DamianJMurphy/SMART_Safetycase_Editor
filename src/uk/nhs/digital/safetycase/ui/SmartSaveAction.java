/*
 * 
 *   Copyright 2017  NHS Digital
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

//import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import static com.mxgraph.examples.swing.editor.EditorActions.getEditor;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.safetycase.data.Attribute;
import uk.nhs.digital.safetycase.data.Cause;
import uk.nhs.digital.safetycase.data.Control;
import uk.nhs.digital.safetycase.data.Effect;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.Process;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor;
import uk.nhs.digital.safetycase.ui.processeditor.ProcessGraphEditor;
/**
 *
 * @author damian
 */
public class SmartSaveAction 
    extends AbstractAction
{    
    public SmartSaveAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        int processStepId = -1;
        try {
            BasicGraphEditor editor = getEditor(e);
            String geclass = editor.getClass().toString();
            ProcessGraphEditor pge = null;
            BowtieGraphEditor bge = null;
            MetaFactory mf = MetaFactory.getInstance();
            PersistableFactory<Process> pf = mf.getFactory("Process");
            PersistableFactory<ProcessStep> psf = mf.getFactory("ProcessStep");
            PersistableFactory<Hazard> hf = mf.getFactory("Hazard");
            Process process = null;
            Hazard hazard = null;
            ProcessStep ps = null;
            if (geclass.contains("Process")) {
                    pge = (ProcessGraphEditor)editor;
                    process = pf.get(pge.getProcessId());
                    if (process == null)
                        return;
            } else if (geclass.contains("Bowtie")) {
                    bge = (BowtieGraphEditor)editor;
                    if (bge.getHazardId() !=  -1) {
                        hazard = hf.get(bge.getHazardId());
                    }
                    ps = bge.getProcessStep();
                    if (ps == null)
                        return;
                    processStepId = ps.getId();
            } else {
                // Can add more here if we do system/function or project overview editors
                return;
            }
            
            mxGraphComponent graphComponent = editor.getGraphComponent();
            mxGraph graph = graphComponent.getGraph();            
            mxCodec codec = new mxCodec();
            String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));
            if (process != null) {
                parseSteps(xml, pge.getProcessId());
                process.setAttribute("GraphXml", new Attribute(xml));
                pf.put(process);
            } else {
                HashMap<String,BowtieElement> existingBowtie = null; 
                int projectid = -1;
                if (hazard == null) {
                    hazard = new Hazard();
                    process = pf.get(ps.getAttribute("ProcessID").getIntValue());
                    projectid = process.getAttribute("ProjectID").getIntValue();
                    hazard.setAttribute("ProjectID", projectid);
                    hazard.setAttribute("ConditionID", 0);
                    hazard.setAttribute("Status", "New");
                    hf.put(hazard);
                } else {
                    projectid = hazard.getAttribute("ProjectID").getIntValue();
                    existingBowtie = bge.getExistingBowtie();
                }
                NodeList cells = parseHazard(xml, hazard);
                HashMap<String,BowtieElement> bowtieElements = parseBowtie(cells, hazard, projectid);
                
                                
                if (existingBowtie != null) {
        
                    for (String cid : existingBowtie.keySet()) {
                        BowtieElement bt = existingBowtie.get(cid);
                        bt.object.deleteAutomaticRelationships();
                        MetaFactory.getInstance().getFactory(bt.object.getDatabaseObjectName()).put(bt.object);
                        MetaFactory.getInstance().getFactory(bt.object.getDatabaseObjectName()).refresh(bt.object.getId());
                        BowtieElement updated = bowtieElements.get(cid);
                        if (updated != null) {
                            updated.object = bt.object;
                            updated.object.setAttribute("Name", updated.name);
                        }
                    }
                } else {
                    createProcessStepHazardRelationship(hazard, processStepId);
                }
                saveBowtie(hazard, bowtieElements, projectid);
            }
//            String exml = URLEncoder.encode(xml, "UTF-8");
            System.out.println(xml);
            
        } 
        catch (BrokenConnectionException bce) {
            System.err.println("TODO: Notify user that the diagram has a broken link and has not been saved: " + bce.getMessage());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
   
    
    private void createProcessStepHazardRelationship(Hazard h, int psid) 
            throws Exception
    {
        PersistableFactory<ProcessStep> psf = MetaFactory.getInstance().getFactory("ProcessStep"); 
        ProcessStep ps = psf.get(psid);
        ArrayList<Relationship> rels = ps.getRelationships("Hazard");
        if (rels != null) {
            // See if this already exists
            for (Relationship r : rels) {
                if (r.getTarget() == h.getId())
                    return;
            }
        }
        Relationship pr = new Relationship(psid, h.getId(), "Hazard");
        ps.addRelationship(pr);
        psf.put(ps);
    }
    
    private HashMap<String,BowtieElement> parseBowtie(NodeList nl, Hazard h, int projectid)
            throws Exception
    {
        // Get the list of causes, controls and effects in the bowtie...
        
        HashMap<String,BowtieElement> bowtieElements = new HashMap<>();
        
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element)nl.item(i);
            if (!cell.hasAttribute("edge")) { // Vertices only
                if (cell.hasAttribute("style")) { // Ignore the couple of "virtual" cells at the start of the model
                    BowtieElement bt = new BowtieElement(cell.getAttribute("style"), cell.getAttribute("value"), cell.getAttribute("id"));
                    bowtieElements.put(cell.getAttribute("id"), bt);
                }
            }
        }
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element)nl.item(i);
            if (cell.hasAttribute("edge")) {
                String s = cell.getAttribute("source");
                String t = cell.getAttribute("target");
                if (s.length() == 0) {
                    if (t.length() == 0) {
                        throw new BrokenConnectionException("Unconnected bowtie link");
                    } else {
                        String c = getCellName(nl, t);
                        throw new BrokenConnectionException("Link to " + c + " has no source bowtie element");
                    }
                }
                if (t.length() == 0) {
                    String c = getCellName(nl, t);
                    throw new BrokenConnectionException("Link from " + c + " has no target bowtie element");
                }
                BowtieElement bt = bowtieElements.get(s);
                bt.fromCell = Integer.parseInt(s);
                bt.toCell = Integer.parseInt(t);
            }
        }
        return bowtieElements;
    }
    
    private String getCellName(NodeList nl, String id) 
    {
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element)nl.item(i);
            if (cell.getAttribute("id").contentEquals(id)) {
                return cell.getAttribute("value");
            }
        }
        return null;
    }
    
    private void saveBowtie(Hazard h, HashMap<String,BowtieElement> bowtieElements, int projectid) 
            throws Exception
    {
        
        // For each... except the Hazard itself
        // 1. See if we have a Relationship for an entity of that name and cell id to the hazard
        // 2. If not, create an appropriate Persistable, and set the Name and GraphCellId
        // 3. if we do, get it out of the factory
        
        for (BowtieElement bt : bowtieElements.values()) {
            if (bt.type.contentEquals("Hazard")) {
                bt.object = h;
                h.setAttribute("Name", bt.name);
            } else {
                ArrayList<Relationship> rels = h.getRelationships(bt.type);
                if (rels != null) {
                    for (Relationship r : rels) {
                        Persistable p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        if (p.getAttributeValue("Name").contentEquals(bt.name)) {
                            bt.object = p;
                        }
                    }
                }
                if (bt.object == null) {
                   Persistable p = createPersistable(bt.type);
                   if (p == null) {
                       throw new Exception("Unknown Bowtie Element type " + bt.type);
                   }
                   if (bt.type.contentEquals("Effect")) {
                       p.setAttribute("Type", "New");
                   }
                   if (bt.type.contentEquals("Control")) {
                       p.setAttribute("Type", "New");
                       p.setAttribute("State", "New");
                   }
                   p.setAttribute("Name", bt.name);
                   p.setAttribute("GraphCellId", bt.cellId);
                   p.setAttribute("ConditionID", 0);
                   p.setAttribute("ProjectID", projectid);
                   bt.object = p;
                   MetaFactory.getInstance().getFactory(bt.type).put(p);
                } else {
                    MetaFactory.getInstance().getFactory(bt.type).put(bt.object);
                }
            }
        }
        
        // Then, see what it is conected to. Make a Relationship to the Hazard with a comment "Bowtie diagram"
        // Then make a Relationship with the target. Set the comment depending on what the two are:
        // Cause -> Control "Controlled by"
        // Cause -> Hazard "Causes"
        // Control -> Hazard "Controls"
        // Hazard -> Effect "Has effect"
        // Hazard -> Control "Is mitigated by"
        // Control -> Effect "Mitigates"
        //
        // Also make a relationship from the Hazard to the other object so we can edit it graphically
        // later, and save the Hazard
        
        
        for (BowtieElement bt : bowtieElements.values()) {
            if (!bt.type.contentEquals("Hazard")) {
                ArrayList<Relationship> rels = bt.object.getRelationships("Hazard");
                if (rels == null) {
                    Relationship r = new Relationship(bt.object.getId(), h.getId(), "Hazard");
                    r.setComment("Bowtie diagram");
                    r.setManagementClass("Diagram");
                    bt.object.addRelationship(r);
                }
                Relationship hr = new Relationship(h.getId(), bt.object.getId(), bt.type);
                hr.setComment("Bowtie diagram");
                hr.setManagementClass("Diagram");
                h.addRelationship(hr);
            }
            if (bt.type.contentEquals("Cause")) {
                BowtieElement target = bowtieElements.get(Integer.toString(bt.toCell));
                if (target != null) {
                    Relationship r = new Relationship(bt.object.getId(), target.object.getId(), target.type);
                    if (target.type.contentEquals("Hazard")) {
                        r.setComment("Causes");
                    } else {
                        r.setComment("Controlled by");
                    }
                    r.setManagementClass("Connection");
                    bt.object.addRelationship(r);
                }
            } else if (bt.type.contentEquals("Hazard")) {
                BowtieElement target = bowtieElements.get(Integer.toString(bt.toCell));
                if (target != null) {
                    Relationship r = new Relationship(bt.object.getId(), target.object.getId(), target.type);
                    if (target.type.contentEquals("Effect")) {
                        r.setComment("Has effect");
                    } else {
                        r.setComment("Is mitigated by");
                    }
                    r.setManagementClass("Connection");
                    bt.object.addRelationship(r);
                }                
            } else if (bt.type.contentEquals("Control")) {
                BowtieElement target = bowtieElements.get(Integer.toString(bt.toCell));
                if (target != null) {
                    Relationship r = new Relationship(bt.object.getId(), target.object.getId(), target.type);
                    if (target.type.contentEquals("Hazard")) {
                        r.setComment("Controls");
                    } else {
                        r.setComment("Mitigates");
                    }
                    r.setManagementClass("Connection");
                    bt.object.addRelationship(r);
                }                
            }
            MetaFactory.getInstance().getFactory(bt.type).put(bt.object);
        }       
        MetaFactory.getInstance().getFactory("Hazard").put(h);
    }
    
    Persistable createPersistable(String t) {
        if (t.contentEquals("Cause"))
            return new Cause();
        if (t.contentEquals("Control")) {
            Control p = new Control();
            p.setAttribute("Type", "New");
            p.setAttribute("State", "New");
            return p;
        }
        if (t.contentEquals("Effect")) {
            Effect e = new Effect();
            e.setAttribute("Type", "New");
            return e;
        }
        return null;
    }
    
    private NodeList parseHazard(String xml, Hazard h)
            throws Exception
    {
        // Find the Hazard, get the name, and store it.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(xml);
        InputSource is = new InputSource(sr);
        Element d = db.parse(is).getDocumentElement();
        NodeList nl = d.getElementsByTagName("mxCell");
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element)nl.item(i);
            if (cell.hasAttribute("style") && 
                    cell.getAttribute("style").contentEquals("image;image=/uk/nhs/digital/safetycase/ui/bowtie/hazard.png")) {
                String id = cell.getAttribute("id");
                String name = cell.getAttribute("value");
                h.setAttribute("Name", name);
                h.setAttribute("GraphCellId", Integer.parseInt(id));
                h.setAttribute("GraphXml", xml);
                h.setAttribute("Status", "New");
            }
        }
        return nl;
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
