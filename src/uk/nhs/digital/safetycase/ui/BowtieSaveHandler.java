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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
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

/**
 *
 * @author damian
 */
public class BowtieSaveHandler 
        extends AbstractSaveHandler
{
//    private final ArrayList<Persistable> added = new ArrayList<>();
//    private final ArrayList<Persistable> updated = new ArrayList<>();
    private final ArrayList<Persistable> removed = new ArrayList<>();
    
    private BowtieGraphEditor bge = null; 
    private Hazard hazard = null;
    @Override
    public void handle(BasicGraphEditor ge) 
            throws Exception 
    {
        boolean existingHazard = false;
        try {
            int processStepId = -1;
            bge = (BowtieGraphEditor)ge;
            ProcessStep ps = null;
            MetaFactory mf = MetaFactory.getInstance();            
            PersistableFactory<Hazard> hf = mf.getFactory("Hazard");
            PersistableFactory<uk.nhs.digital.safetycase.data.Process> pf = mf.getFactory("Process");
            Process process = null;
            if (bge.getHazardId() !=  -1)
                hazard = hf.get(bge.getHazardId());

            ps = bge.getProcessStep();
            if ((ps == null) && (hazard == null))
                return;
            if (ps != null)
                processStepId = ps.getId();
            String xml = getXml(ge);

            HashMap<String,DiagramEditorElement> existingBowtie = bge.getExistingBowtie(); 
            if (checkNames(xml, existingBowtie))
                return;
            int projectid = -1;
            if (hazard == null) {
                // Note: this branch should only be true if ps != null...
                hazard = new Hazard();
                process = pf.get(ps.getAttribute("ProcessID").getIntValue());
                projectid = process.getAttribute("ProjectID").getIntValue();
                hazard.setAttribute("ProjectID", projectid);
                hazard.setAttribute("GroupingType", "Generic");
                hazard.setAttribute("Status", "Select...");
  //              added.add(hazard);                
            } else {
                existingHazard = true;
                projectid = hazard.getAttribute("ProjectID").getIntValue();
//                updated.add(hazard);
            }
            NodeList cells = parseHazard(xml, hazard);
            HashMap<String,DiagramEditorElement> bowtieElements = parseBowtie(cells, projectid);

            hf.put(hazard);
            
            if (existingBowtie != null) {

                for (String cid : existingBowtie.keySet()) {
                    DiagramEditorElement bt = existingBowtie.get(cid);
                    ArrayList<Relationship> d = bt.object.deleteAutomaticRelationships();
                    MetaFactory.getInstance().getFactory(bt.object.getDatabaseObjectName()).put(bt.object);
//                    MetaFactory.getInstance().getFactory(bt.object.getDatabaseObjectName()).refresh(bt.object.getId());
                    bt.object.purgeAutomaticRelationships(d);
                    if (bowtieElements.containsKey(cid)) {                        
                        DiagramEditorElement btupdate = bowtieElements.get(cid);
                        if (btupdate != null) {
                            btupdate.object = bt.object;
                            if (!btupdate.object.getAttributeValue("Name").contentEquals(btupdate.name)) {
                                btupdate.object.setAttribute("Name", btupdate.name);
//                                updated.add(btupdate.object);
                            }
                        }
                    } else {
                        removed.add(bt.object);
                    }
                }
            } else {
                createProcessStepHazardRelationship(hazard, processStepId);
            }
            saveBowtie(hazard, bowtieElements, projectid);
            bge.setExistingBowtie(bowtieElements);
            bge.setHazardId(hazard.getId(), xml);
            System.out.println(xml);
            SmartProject sp = SmartProject.getProject();
            if (existingHazard) {
                sp.editorEvent(Project.UPDATE, hazard);
            } else {
                sp.editorEvent(Project.ADD, hazard);
            }
            deleteRemovedNodes();
//            HazardListForm hlf = bge.getHazardListForm();
//            if (hlf != null)
//                hlf.updateHazard(hazard);
        } 
        catch (BrokenConnectionException bce) {
            JOptionPane.showMessageDialog(ge, "The diagram has a broken link and has not been saved: " + bce.getMessage(), "Diagram incomplete", JOptionPane.ERROR_MESSAGE);
//            System.err.println("TODO: Notify user that the diagram has a broken link and has not been saved: " + bce.getMessage());
        }
        catch (Exception ex) {
            SmartProject.getProject().log("Failed to save bowtie", ex);
        }            
    }

    private void deleteRemovedNodes()
    {
        if (removed == null)
            return;
        for (Persistable p : removed) {
            try {
                MetaFactory.getInstance().getFactory(p.getDatabaseObjectName()).delete(p);
            }
            catch (Exception e) {
                SmartProject.getProject().log("Removing deleted node failed: " + p.getTitle(), e);
            }
        }
    }
    
    private void createProcessStepHazardRelationship(Hazard h, int psid) 
            throws Exception
    {
        if (psid == -1)
            return;
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
    
    private HashMap<String,DiagramEditorElement> parseBowtie(NodeList nl, int projectid)
            throws Exception
    {
        // Get the list of causes, controls and effects in the bowtie...
        
        HashMap<String,DiagramEditorElement> bowtieElements = new HashMap<>();
        
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element)nl.item(i);
            if (!cell.hasAttribute("edge")) { // Vertices only
                if (cell.hasAttribute("style")) { // Ignore the couple of "virtual" cells at the start of the model
                    DiagramEditorElement bt = new DiagramEditorElement(cell.getAttribute("style"), cell.getAttribute("value"), cell.getAttribute("id"));
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
                        throw new BrokenConnectionException("Link to " + c + " with no source bowtie element");
                    }
                }
                if (t.length() == 0) {
                    String c = cell.getAttribute("value");
                    throw new BrokenConnectionException("Link from " + c + " with no target bowtie element");
                }
                DiagramEditorElement bt = bowtieElements.get(s);
//                bt.fromCell = Integer.parseInt(s);
//                bt.toCell = Integer.parseInt(t);
                bt.connections.add(t);
            }
        }
        for (DiagramEditorElement dee : bowtieElements.values()) {
            if (!dee.type.contentEquals("Effect") && (dee.connections.size() == 0))
                throw new BrokenConnectionException("Bowtie element " + dee.name + " is not connected to anything");
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
    
    private void saveBowtie(Hazard h, HashMap<String,DiagramEditorElement> bowtieElements, int projectid) 
            throws Exception
    {
        
        // For each... except the Hazard itself
        // 1. See if we have a Relationship for an entity of that name and cell id to the hazard
        // 2. If not, create an appropriate Persistable, and set the Name and GraphCellId
        // 3. if we do, get it out of the factory
        
        for (DiagramEditorElement bt : bowtieElements.values()) {
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
                   if (bt.type.contentEquals("Control")) {
                       p.setAttribute("State", "To be implemented");
                   }
                   p.setAttribute("Name", bt.name);
                   p.setAttribute("GraphCellId", bt.cellId);
                   p.setAttribute("GroupingType", "Generic");
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
        
        
        for (DiagramEditorElement bt : bowtieElements.values()) {
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
                for (String t : bt.connections) {
                    DiagramEditorElement target = bowtieElements.get(t);
                    if (target != null) {
                        Relationship rto = new Relationship(bt.object.getId(), target.object.getId(), target.type);
                        Relationship rfrom = new Relationship(target.object.getId(), bt.object.getId(), bt.type);
                        if (target.type.contentEquals("Hazard")) {
                            rto.setComment("Causes");
                            rfrom.setComment("Caused by");
                        } else {
                            rto.setComment("Controlled by");
                            rfrom.setComment("Controls");
                        }
                        rto.setManagementClass("ConnectionTo");
                        rfrom.setManagementClass("ConnectionFrom");
                        bt.object.addRelationship(rto);
                        target.object.addRelationship(rfrom);
                        MetaFactory.getInstance().getFactory(target.type).put(target.object);
                    }
                }
            } else if (bt.type.contentEquals("Hazard")) {
                for (String t : bt.connections) {
                    DiagramEditorElement target = bowtieElements.get(t);
                    if (target != null) {
                        Relationship rto = new Relationship(bt.object.getId(), target.object.getId(), target.type);
                        Relationship rfrom = new Relationship(target.object.getId(), bt.object.getId(), bt.type);
                        if (target.type.contentEquals("Effect")) {
                            rto.setComment("Has effect");
                            rfrom.setComment("Effect of");
                        } else {
                            rto.setComment("Mitigated by");
                            rfrom.setComment("Mitigates");
                        }
                        rto.setManagementClass("ConnectionTo");
                        rfrom.setManagementClass("ConnectionFrom");
                        bt.object.addRelationship(rto);
                        target.object.addRelationship(rfrom);
                        MetaFactory.getInstance().getFactory(target.type).put(target.object);
                    }                
                }
            } else if (bt.type.contentEquals("Control")) {
                for (String t : bt.connections) {
                    DiagramEditorElement target = bowtieElements.get(t);
                    if (target != null) {
                        Relationship rto = new Relationship(bt.object.getId(), target.object.getId(), target.type);
                        Relationship rfrom = new Relationship(target.object.getId(), bt.object.getId(), bt.type);
                        if (target.type.contentEquals("Hazard")) {
                            rto.setComment("Controls");
                            rfrom.setComment("Controlled by");
                        } else {
                            rto.setComment("Mitigates");
                            rfrom.setComment("Mitigated by");
                        }
                        rto.setManagementClass("ConnectionTo");
                        rfrom.setManagementClass("ConnectionFrom");
                        bt.object.addRelationship(rto);
                        target.object.addRelationship(rfrom);
                        MetaFactory.getInstance().getFactory(target.type).put(target.object);
                    }                
                }
            }
            MetaFactory.getInstance().getFactory(bt.type).put(bt.object);
        }       
        MetaFactory.getInstance().getFactory("Hazard").put(h);
    }
    
    Persistable createPersistable(String t) {
        Persistable p = null;
        if (t.contentEquals("Cause"))
            p = new Cause();
        if (t.contentEquals("Control")) {
            p = new Control();
            p.setAttribute("Type", "Select...");
            p.setAttribute("State", "Select...");
        }
        if (t.contentEquals("Effect")) {
            p = new Effect();
        }
        if (p != null)
            p.setAttribute("GroupingType", "Generic");
        
        return p;
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
                h.setAttribute("Status", "Select...");
            }
        }
        return nl;
    } 
    
    private boolean checkNames(String xml, HashMap<String,DiagramEditorElement> existingBowtie) {
        try {
            int pid = SmartProject.getProject().getCurrentProjectID();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Element d = db.parse(is).getDocumentElement();
            NodeList nl = d.getElementsByTagName("mxCell");
            for (int i = 0; i < nl.getLength(); i++) {
                Element cell = (Element)nl.item(i);
                if (cell.hasAttribute("style")) {
                    String id = cell.getAttribute("id");
                    String name = cell.getAttribute("value");
                   
                    // Get any existing object of this sort
                    Persistable p = null;
                    if (existingBowtie != null) {
                        DiagramEditorElement dee = existingBowtie.get(id);
                        if (dee != null)
                            p = dee.object;
                    }    
                    
                    if (cell.getAttribute("style").contentEquals("image;image=/uk/nhs/digital/safetycase/ui/bowtie/hazard.png")) {
                        try {
                            String duplicateWarning = MetaFactory.getInstance().getDuplicateCheckMessage("Hazard", "Hazard", name,pid, hazard);
                            if (duplicateWarning != null) {
                                JOptionPane.showMessageDialog(bge, duplicateWarning, "Duplicate hazard name", JOptionPane.ERROR_MESSAGE);
                                return true;
                            }
                        }
                        catch (Exception e) {}                        
                    }
                    if (cell.getAttribute("style").contentEquals("image;image=/uk/nhs/digital/safetycase/ui/bowtie/cause.png")) {
                        try {
                            String duplicateWarning = MetaFactory.getInstance().getDuplicateCheckMessage("Cause", "Cause", name,pid, p);
                            if (duplicateWarning != null) {
                                JOptionPane.showMessageDialog(bge, duplicateWarning, "Duplicate cause name", JOptionPane.ERROR_MESSAGE);
                                return true;
                            }
                        }
                        catch (Exception e) {}                        
                        
                    }
                    if (cell.getAttribute("style").contentEquals("image;image=/uk/nhs/digital/safetycase/ui/bowtie/effect.png")) {
                        try {
                            String duplicateWarning = MetaFactory.getInstance().getDuplicateCheckMessage("Effect", "Effect", name,pid, p);
                            if (duplicateWarning != null) {
                                JOptionPane.showMessageDialog(bge, duplicateWarning, "Duplicate effect name", JOptionPane.ERROR_MESSAGE);
                                return true;
                            }
                        }
                        catch (Exception e) {}                                                
                    }
                    if (cell.getAttribute("style").contentEquals("image;image=/uk/nhs/digital/safetycase/ui/bowtie/control.png")) {
                        try {
                            String duplicateWarning = MetaFactory.getInstance().getDuplicateCheckMessage("Control", "Control", name,pid, p);
                            if (duplicateWarning != null) {
                                JOptionPane.showMessageDialog(bge, duplicateWarning, "Duplicate control name", JOptionPane.ERROR_MESSAGE);
                                return true;
                            }
                        }
                        catch (Exception e) {}                                                
                    }
                }
            }
        }
        catch (IOException | ParserConfigurationException | SAXException e) {
            JOptionPane.showMessageDialog(bge, "Error checking for duplicate names, send logs to support", "Error", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Error in bowtie duplicate name check", e);
        }
        return false;
    }
}
