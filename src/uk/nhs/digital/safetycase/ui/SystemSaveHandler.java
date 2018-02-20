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
import uk.nhs.digital.safetycase.data.System;
import uk.nhs.digital.safetycase.data.SystemFunction;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.systemeditor.SystemGraphEditor;

/**
 *
 * @author damian
 */
public class SystemSaveHandler
        extends AbstractSaveHandler {

    private final ArrayList<Persistable> added = new ArrayList<>();
    private final ArrayList<Persistable> updated = new ArrayList<>();
    private final ArrayList<Persistable> removed = new ArrayList<>();

    private final ArrayList<String> processedElements = new ArrayList<>();
    private System system = null;

    @Override
    public void handle(BasicGraphEditor ge)
            throws Exception {
        boolean systemExists = false;
        try {
            // int systemStepId = -1;
            SystemGraphEditor sge = (SystemGraphEditor) ge;
            //SystemStep ss = null;
            MetaFactory mf = MetaFactory.getInstance();
            PersistableFactory<System> sf = mf.getFactory("System");
            // PersistableFactory<uk.nhs.digital.safetycase.data.Process> pf = mf.getFactory("Process");
            // Process system = null;
            // System system = null;
            if (sge.getSystemId() != -1) {
                system = sf.get(sge.getSystemId());
            }
            String xml = getXml(ge);

            HashMap<String, DiagramEditorElement> existingSystem = null;
            int projectid = -1;
            if (system == null) {
                system = new System();

                SmartProject sp = SmartProject.getProject();
                projectid = sp.getCurrentProjectID();
                system.setAttribute("ProjectID", projectid);
                //added.add(system);
            } else {
                systemExists = true;
                projectid = system.getAttribute("ProjectID").getIntValue();
                existingSystem = sge.getExistingBowtie();
               // updated.add(system);
            }
            //when parsing XMl make sure that we find the root system and not the subsystem.
            // this can be done using mxCell id value . if this id value is not be used as target in any of the edsge cell i.e having no target value of the system. that cell is root system.
            // e.g mxCell edge="1" id="6" parent="1" source="2" style="straight" target="3" value=""> value 2 is not used at target in the whole xml string
            NodeList cells = parseSystem(xml, system);
            //todo check if this is working correctly............
            HashMap<String, DiagramEditorElement> systemElements = parseSystem(cells, projectid);

            sf.put(system);
            //systemID = system.getId();

            if (existingSystem != null) {

                for (String cid : existingSystem.keySet()) {
                    DiagramEditorElement bt = existingSystem.get(cid);
                    ArrayList<Relationship> d = bt.object.deleteAutomaticRelationships();
                    MetaFactory.getInstance().getFactory(bt.object.getDatabaseObjectName()).put(bt.object);
//                    MetaFactory.getInstance().getFactory(bt.object.getDatabaseObjectName()).refresh(bt.object.getId());
                    bt.object.purgeAutomaticRelationships(d);
                    if (systemElements.containsKey(cid)) {
                        DiagramEditorElement btupdate = systemElements.get(cid);
                        if (btupdate != null) {
                            btupdate.object = bt.object;
                            if (!btupdate.object.getAttributeValue("Name").contentEquals(btupdate.name)) {
                                btupdate.object.setAttribute("Name", btupdate.name);
                                updated.add(btupdate.object);
                                bt.object.setAttribute("Name", btupdate.name); // for updating name in the  database
                            }
                        }
                    } else {
                        removed.add(bt.object);
                    }
                }
            }
                else {
                 java.lang.System.out.println("Existing System is null ");
                //createSystemSystemFunctionRelationship(system, system.getId());
            }
            saveRootSystem(systemElements, projectid);
            sge.setExistingBowtie(systemElements);
            sge.setSystemId(system.getId(), xml);
            java.lang.System.out.println(xml);
            SmartProject sp = SmartProject.getProject();
            if (systemExists) {
                sp.editorEvent(Project.UPDATE, system);
            } else {
                sp.editorEvent(Project.ADD, system);
            }
//            for (Persistable n : added) {
////                MetaFactory.getInstance().getFactory(n.getDatabaseObjectName()).refresh(n.getId());
//                sp.editorEvent(Project.ADD, n);
//            }
//            for (Persistable n : updated) {
////                MetaFactory.getInstance().getFactory(n.getDatabaseObjectName()).refresh(n.getId());
//                sp.editorEvent(Project.UPDATE, n);
//            }
//            for (Persistable n : removed) {
//                sp.editorEvent(Project.DELETE, n);
//            }
        } catch (BrokenConnectionException bce) {
            java.lang.System.err.println("TODO: Notify user that the diagram has a broken link and has not been saved: " + bce.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" Unused Code ">
//    private void createSystemSystemFunctionRelationship(System s, int ssid)
//            throws Exception {
//        PersistableFactory<System> psf = MetaFactory.getInstance().getFactory("System");
//        System ss = psf.get(ssid);
//        ArrayList<Relationship> rels = ss.getRelationships("System");
//        if (rels != null) {
//            // See if this already exists
//            for (Relationship r : rels) {
//                if (r.getTarget() == s.getId()) {
//                    return;
//                }
//            }
//        }
//        Relationship pr = new Relationship(ssid, s.getId(), "System");
//        ss.addRelationship(pr);
//        psf.put(ss);
//    }
// </editor-fold>
    private HashMap<String, DiagramEditorElement> parseSystem(NodeList nl, int projectid)
            throws Exception {
        // Get the list of subsystems, functions and subFunctions in the bowtie...

        HashMap<String, DiagramEditorElement> systemElements = new HashMap<>();

        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element) nl.item(i);
            if (!cell.hasAttribute("edge")) { // Vertices only
                if (cell.hasAttribute("style")) { // Ignore the couple of "virtual" cells at the start of the model
                    DiagramEditorElement se = new DiagramEditorElement(cell.getAttribute("style"), cell.getAttribute("value"), cell.getAttribute("id"));
                    systemElements.put(cell.getAttribute("id"), se);
                }
            }
        }
        // create elements connections
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element) nl.item(i);
            if (cell.hasAttribute("edge")) {
                String s = cell.getAttribute("source");
                String t = cell.getAttribute("target");
                if (s.length() == 0) {
                    if (t.length() == 0) {
                        throw new BrokenConnectionException("Unconnected system/function link");
                    } else {
                        String c = getCellName(nl, t);
                        throw new BrokenConnectionException("Link to " + c + " has no source system/function element");
                    }
                }
                if (t.length() == 0) {
                    String c = getCellName(nl, t);
                    throw new BrokenConnectionException("Link from " + c + " has no target system/function element");
                }
                DiagramEditorElement se = systemElements.get(s);
                // bt.fromCell = Integer.parseInt(s);
                // bt.toCell = Integer.parseInt(t);
                se.connections.add(t);
            }
        }
        return systemElements;
    }

    private String getCellName(NodeList nl, String id) {
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element) nl.item(i);
            if (cell.getAttribute("id").contentEquals(id)) {
                return cell.getAttribute("value");
            }
        }
        return null;
    }
     private void saveRootSystem(HashMap<String, DiagramEditorElement> systemElements, int projectid)
            throws Exception {
       
        DiagramEditorElement rootelement = systemElements.get(system.getAttributeValue("GraphCellId"));

        rootelement.object = system;
        system.setAttribute("Name", rootelement.name);
        MetaFactory.getInstance().getFactory(rootelement.type).put(rootelement.object);
//        MetaFactory.getInstance().getFactory("System").put(system);
         
        processedElements.add(String.valueOf(rootelement.cellId));
        for (String t : rootelement.connections) {
            DiagramEditorElement target = systemElements.get(t);
            if (target.type.equals("System")) { //main methid
                savesubsystem(rootelement, target, systemElements, projectid);
                
            } else if (target.type.equals("SystemFunction")) {
                savesystemfunction(rootelement, target, systemElements, projectid);

            }
        }

        for (DiagramEditorElement bt : systemElements.values()) {
            if (!processedElements.contains(String.valueOf(bt.cellId))) {

                java.lang.System.out.println("Unprocessed Element : " + bt.name + " , Cell ID : " + bt.cellId + " , No of Connections : " + bt.connections.toString());
            }
        }
        
        MetaFactory.getInstance().getFactory(rootelement.type).put(rootelement.object);
        //if any relations been added to system
        MetaFactory.getInstance().getFactory("System").put(system);
       // MetaFactory.getInstance().initialise(projectid); //work around for loading the new relationships.
       
       
    }
    private void savesystemfunction(DiagramEditorElement parent, DiagramEditorElement target, HashMap<String, DiagramEditorElement> systemelements, int projectid) throws Exception {
        if (target.object == null) {
            if (!processedElements.contains(String.valueOf(target.cellId))) {
                Persistable p = createPersistable(target.type);
                if (p == null) {
                    throw new Exception("Unknown Bowtie Element type " + target.type);
                }

                p.setAttribute("Name", target.name);
                p.setAttribute("GraphCellId", target.cellId);
                p.setAttribute("ProjectID", projectid);
                target.object = p;

                MetaFactory.getInstance().getFactory(target.type).put(p);
                processedElements.add(String.valueOf(target.cellId));

                Relationship rel = new Relationship(parent.object.getId(), target.object.getId(), target.type);
                rel.setComment("system diagram");
                rel.setManagementClass("Diagram");
                if (parent.type.equals("SystemFunction")) {
                    p.setAttribute("ParentSystemFunctionID", parent.object.getId());
                    MetaFactory.getInstance().getFactory(target.type).put(p);
                    //target.object.addRelationship(rel);
                   // parent.object.addRelationship(rel);
                   // MetaFactory.getInstance().getFactory(target.type).put(p);
                } //else {
                    //system.addRelationship(rel); // if the request is not from systemfunction then we need to add relation to system object.
                    parent.object.addRelationship(rel);
                    MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
                //}
                
                //parent.object.addRelationship(rel);
               // MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
                
            }
        } else {
            Relationship rel = new Relationship(parent.object.getId(), target.object.getId(), target.type);
            //Relationship rel = new Relationship(parentid, target.object.getId(), requestsenderobject);
            rel.setComment("system diagram");
            rel.setManagementClass("Diagram");
//            if (parent.type.equals("SystemFunction")) {
//                target.object.addRelationship(rel);
//            } else {
//                //system.addRelationship(rel);
//                parent.object.addRelationship(rel);
//                MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
//            }
//            MetaFactory.getInstance().getFactory(target.type).put(target.object);
            
            //Replaced above if condition.
            parent.object.addRelationship(rel);
            MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
            processedElements.add(String.valueOf(target.cellId));
           
        }
        for (String t : target.connections) {
            DiagramEditorElement targetchild = systemelements.get(t);
            if (targetchild.object == null) {
                if (!processedElements.contains(String.valueOf(targetchild.cellId))) { 
                    if (targetchild.type.equals("SystemFunction")) {
                        savesystemfunction(target, targetchild, systemelements, projectid);
                        //processedElements.add(t);
                    } else if (targetchild.type.equals("System")) { 
                        java.lang.System.out.println("Wrong connection from " + target.name + " to  " + targetchild.name);
                        //savesystem(target, targetchild, bowtieelements, projectid);
                    }
                }
            } else {
                Relationship rel = new Relationship(target.object.getId(), targetchild.object.getId(), targetchild.type);
                //Relationship rel = new Relationship(parentid, target.object.getId(), requestsenderobject);
                rel.setComment("system diagram");
                rel.setManagementClass("Diagram");
                target.object.addRelationship(rel);
                MetaFactory.getInstance().getFactory(target.type).put(target.object);
//                if (target.type.equals("SystemFunction")) { 
//                    target.object.addRelationship(rel);
//                } else {
//                   // parent.object.addRelationship(rel);
//                   // MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
//                   // parent.object.addRelationship(rel);
//                   // MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
//                }
//                MetaFactory.getInstance().getFactory(target.type).put(target.object);
                processedElements.add(t);
                
                //check for subchild
                for (String st : targetchild.connections) {
                    DiagramEditorElement targetsubchild = systemelements.get(st);
                    if (targetsubchild.type.equals("SystemFunction")) {
                        savesystemfunction(targetchild, targetsubchild, systemelements, projectid);
                        //processedElements.add(st);
                    }else if (targetsubchild.type.equals("System")) {
                        // To do throw exception as systemfunction and systemfunction shouldn't have a cinnection to system/subsystem.
                        java.lang.System.err.println("Wrong connection from " + targetchild.name + " to  " + targetsubchild.name);
                        //savesystem(targetchild, targetsubchild, bowtieelements, projectid);
                        processedElements.add(st);
                    }
                }
            }
            MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
        }
    }

    private void savesubsystem(DiagramEditorElement parent, DiagramEditorElement target, HashMap<String, DiagramEditorElement> systemelements, int projectid) throws Exception {
        //System s = new System();
        if (target.object == null) {
            if (!processedElements.contains(String.valueOf(target.cellId))) {
                Persistable p = createPersistable(target.type);
                if (p == null) {
                    throw new Exception("Unknown Bowtie Element type " + target.type);
                }
                p.setAttribute("Name", target.name);
                p.setAttribute("GraphCellId", target.cellId);
                p.setAttribute("ParentSystemID", parent.object.getId());
                p.setAttribute("ProjectID", projectid);
                target.object = p;
                MetaFactory.getInstance().getFactory(target.type).put(p);
                Relationship rel = new Relationship(parent.object.getId(), target.object.getId(), target.type);
                //  Relationship rel = new Relationship(parent.object.getId(), target.object.getId(), parent.type);
                // Relationship rel = new Relationship(parentid, target.object.getId(), "System");
                rel.setComment("system diagram");
                rel.setManagementClass("Diagram");
                p.addRelationship(rel);
                //system.addRelationship(rel); //temp comment
                MetaFactory.getInstance().getFactory(target.type).put(p);
                processedElements.add(String.valueOf(target.cellId));
            }
        } else {
            Relationship rel = new Relationship(parent.object.getId(), target.object.getId(), target.type);
            //Relationship rel = new Relationship(parent.object.getId(), target.object.getId(), parent.type);
            rel.setComment("system diagram");
            rel.setManagementClass("Diagram");
            //target.object.addRelationship(rel);
            parent.object.addRelationship(rel);
            MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
            processedElements.add(String.valueOf(target.cellId));
        }
        for (String t : target.connections) {
            DiagramEditorElement targetchild = systemelements.get(t);
            if (targetchild.object == null) {
                if (!processedElements.contains(String.valueOf(targetchild.cellId))) {
                    if (targetchild.type.equals("System")) {
                        savesubsystem(target, targetchild, systemelements, projectid);
                    } else if (targetchild.type.equals("SystemFunction")) {
                        savesystemfunction(target, targetchild, systemelements, projectid);
                    }
                }
            } else {
                Relationship rel = new Relationship(target.object.getId(), targetchild.object.getId(), targetchild.type);
                rel.setComment("system diagram");
                rel.setManagementClass("Diagram");
                target.object.addRelationship(rel);
                MetaFactory.getInstance().getFactory(target.type).put(target.object);
                processedElements.add(t);
                //check for subchild
                for (String st : targetchild.connections) {
                    DiagramEditorElement targetsubchild = systemelements.get(st);
                    if (targetsubchild.type.equals("System")) {
                        savesubsystem(targetchild, targetsubchild, systemelements, projectid);
                        //processedElements.add(st);
                    } else if (targetsubchild.type.equals("SystemFunction")) {
                        savesystemfunction(targetchild, targetsubchild, systemelements, projectid);
                        //processedElements.add(st);
                    }
                }
            }
            //MetaFactory.getInstance().getFactory(parent.type).put(parent.object);
        }
    }
// <editor-fold defaultstate="collapsed" desc=" Unused Code ">
//     private void saveSystem(System rootsystem, HashMap<String, DiagramEditorElement> systemElements, int projectid)
//            throws Exception {
//       
//        DiagramEditorElement rootelement = systemElements.get(rootsystem.getAttributeValue("GraphCellId"));
//       // rootelement.object = rootsystem;
//       // rootsystem.setAttribute("Name", rootelement.name);
//        //MetaFactory.getInstance().getFactory(rootelement.type).put(rootelement.object);
//        rootelement.object = system;
//        system.setAttribute("Name", rootelement.name);
//         MetaFactory.getInstance().getFactory("System").put(system);
//         
//        processedElements.add(String.valueOf(rootelement.cellId));
//        for (String t : rootelement.connections) {
//            DiagramEditorElement target = systemElements.get(t);
//            if (target.type.equals("System")) { //main methid
//                savesubsystem(rootelement, target, systemElements, projectid);
//            } else if (target.type.equals("SystemFunction")) {
//                savesystemfunction(rootelement, target, systemElements, projectid);
//
//            }
//        }
//
//        for (DiagramEditorElement bt : systemElements.values()) {
//            if (!processedElements.contains(String.valueOf(bt.cellId))) {
//
//                java.lang.System.out.println("Unprocessed Element : " + bt.name + " , Cell ID : " + bt.cellId + " , No of Connections : " + bt.connections.toString());
//            }
//        }
//
//        //if any relations been added to system
//        MetaFactory.getInstance().getFactory("System").put(system);
//       // MetaFactory.getInstance().initialise(projectid); //work around for loading the new relationships.
//       
//       
//    }
    //    private void saveBowtie(System rootsystem, HashMap<String, DiagramEditorElement> bowtieElements, int projectid)
//            throws Exception {
//        //saveBowtie(system, bowtieElements, projectid);
//        Boolean rootsys = false;
//        for (DiagramEditorElement bt : bowtieElements.values()) {
//            if (!processedElements.contains(String.valueOf(bt.cellId))) {
//                if (bt.type.contentEquals("System") && rootsys == false) {
//                    //  if (bt.type.contentEquals("System")){
//                    bt.object = rootsystem;
//                    rootsystem.setAttribute("Name", bt.name);
//                    rootsys = true;
//                    processedElements.add(String.valueOf(bt.cellId));
//                    for (String t : bt.connections) {
//                        DiagramEditorElement target = bowtieElements.get(t);
//                        if (target.type.equals("System")) { //main methid
//                            savesystem(bt, target, bowtieElements, projectid);
//                            //System cs = savesystem(s.getId(), target, bowtieElements, projectid);
//                            // add the cell ID to arrayList for checking if the cell has already been addded/processed
//                            processedElements.add(t);
//                            // addedsystem.add(cs);
//                        } else if (target.type.equals("SystemFunction")) { //main methid
//                            savesystemfunction(bt, target, bowtieElements, projectid);
//                            //savesystemfunction(rootsystem.getId(), target, bowtieElements, projectid, "System");
//                            //SystemFunction csf = savesystemfunction(s.getId(), target, bowtieElements, projectid, "System");
//                            //parentID = csf.getId();
//                            processedElements.add(t);
//                            // addedsystemfunction.add(csf);
//                        }
//                    }
//                }
//            }
//        }
//        //if any relations been added to system
//        MetaFactory.getInstance().getFactory("System").put(system);
//    }

//    private void savesystemfunction(int parentid, DiagramEditorElement target, HashMap<String, DiagramEditorElement> bowtieelements, int projectid, String requestsenderobject) throws Exception {
//        //SystemFunction sf = new SystemFunction();
//        // int persistableid = -1;
//        if (target.object == null) {
//            if (!processedElements.contains(String.valueOf(target.cellId))) {
//                Persistable p = createPersistable(target.type);
//                if (p == null) {
//                    throw new Exception("Unknown Bowtie Element type " + target.type);
//                }
//
//                p.setAttribute("Name", target.name);
//                p.setAttribute("GraphCellId", target.cellId);
//                p.setAttribute("ProjectID", projectid);
//                target.object = p;
////                if (requestsenderobject.equals("SystemFunction")) {
////                    p.setAttribute("ParentSystemFunctionID", parentid);//if a system function is connected to system function then set the parent ParentSystemFunctionID. 
////                }
//                MetaFactory.getInstance().getFactory(target.type).put(p);
//                processedElements.add(String.valueOf(target.cellId));
//                
//                Relationship rel = new Relationship(parentid, target.object.getId(), target.type);
//                //Relationship rel = new Relationship(parentid, target.object.getId(), requestsenderobject);
//                rel.setComment("system diagram");
//                rel.setManagementClass("Diagram");
//                if (requestsenderobject.equals("SystemFunction")){
//                    p.setAttribute("ParentSystemFunctionID", parentid);
//                    target.object.addRelationship(rel);
//                    MetaFactory.getInstance().getFactory(target.type).put(p);
//                } else{
//                    system.addRelationship(rel); // if the request is not from systemfunction then we need to add relation to system object.
//                }
//                        
////                if (requestsenderobject.equals("SystemFunction"))  
////                {
////                    p.setAttribute("ParentSystemFunctionID", parentid);
////                    Relationship rel = new Relationship(parentid, target.object.getId(), requestsenderobject);
////                    rel.setComment("system diagram");
////                    rel.setManagementClass("Diagram");
////                    target.object.addRelationship(rel);
////                    MetaFactory.getInstance().getFactory(target.type).put(p);
////                }          
////                // Relationship rel = new Relationsip(psf.getId(), target.object.getId(), target.type); 
////                if (!requestsenderobject.equals("SystemFunction")) {
////                    Relationship rel = new Relationship(parentid, target.object.getId(), "SystemFunction");
////                    rel.setComment("system diagram");
////                    rel.setManagementClass("Diagram");
////                    system.addRelationship(rel); // if the request is not from systemfunction then we need to add relation to system object.
////                }
//                //MetaFactory.getInstance().getFactory(target.type).put(p); if relation is not added remove this comment
//            }
//        } else {
//            Relationship rel = new Relationship(parentid, target.object.getId(), target.type);
//            //Relationship rel = new Relationship(parentid, target.object.getId(), requestsenderobject);
//            rel.setComment("system diagram");
//            rel.setManagementClass("Diagram");
//            if (requestsenderobject.equals("SystemFunction")){
//            target.object.addRelationship(rel);
//            }else{
//                system.addRelationship(rel);
//            }
//            MetaFactory.getInstance().getFactory(target.type).put(target.object);
//            processedElements.add(String.valueOf(target.cellId));
//        }
//        for (String t : target.connections) {
//            DiagramEditorElement targetchild = bowtieelements.get(t);
//            if (targetchild.object == null) {
//                if (!processedElements.contains(String.valueOf(targetchild.cellId))) {
//                    // to do. Throw exception as systemfunction should not have system as target. 
//                    if (targetchild.type.equals("System")) { //systemfunction method
//                        savesystem(target.object.getId(), targetchild, bowtieelements, projectid);
//                        processedElements.add(t);
//                        // addedsystem.add(cs);
//                    } else if (targetchild.type.equals("SystemFunction")) {//systemfunction method
//                        savesystemfunction(target.object.getId(), targetchild, bowtieelements, projectid, "SystemFunction");
//                        processedElements.add(t);
//                    }
//                }
//            } else {
//                Relationship rel = new Relationship(parentid, target.object.getId(), target.type);
//                //Relationship rel = new Relationship(parentid, target.object.getId(), requestsenderobject);
//                rel.setComment("system diagram");
//                rel.setManagementClass("Diagram");
//                if (requestsenderobject.equals("SystemFunction")){
//                    target.object.addRelationship(rel);
//                }else{
//                    system.addRelationship(rel);
//            }
//                MetaFactory.getInstance().getFactory(target.type).put(target.object);
//                processedElements.add(String.valueOf(target.cellId));
//            }
//        }
//    }
//
//    private void savesystem(int parentid, DiagramEditorElement target, HashMap<String, DiagramEditorElement> bowtieelements, int projectid) throws Exception {
//        //System s = new System();
//        if (target.object == null) {
//            if (!processedElements.contains(String.valueOf(target.cellId))) {
//                Persistable p = createPersistable(target.type);
//                if (p == null) {
//                    throw new Exception("Unknown Bowtie Element type " + target.type);
//                }
//                p.setAttribute("Name", target.name);
//                p.setAttribute("GraphCellId", target.cellId);
//                p.setAttribute("ParentSystemID", parentid);
//                p.setAttribute("ProjectID", projectid);
//                target.object = p;
//                MetaFactory.getInstance().getFactory(target.type).put(p);
//                 Relationship rel = new Relationship(parentid, target.object.getId(), target.type);
//               // Relationship rel = new Relationship(parentid, target.object.getId(), "System");
//                rel.setComment("system diagram");
//                rel.setManagementClass("Diagram");
//                p.addRelationship(rel); 
//                //system.addRelationship(rel); //temp comment
//                MetaFactory.getInstance().getFactory(target.type).put(p);
//                processedElements.add(String.valueOf(target.cellId));
//            }
//        } else {
//            Relationship rel = new Relationship(parentid, target.object.getId(), target.type);
//            rel.setComment("system diagram");
//            rel.setManagementClass("Diagram");
//            target.object.addRelationship(rel);
//            MetaFactory.getInstance().getFactory(target.type).put(target.object);
//            processedElements.add(String.valueOf(target.cellId));
//        }
//        for (String t : target.connections) {
//            DiagramEditorElement targetchild = bowtieelements.get(t);
//            if (targetchild.object == null) {
//                if (!processedElements.contains(String.valueOf(targetchild.cellId))) {
//                    if (targetchild.type.equals("System")) {
//                        savesystem(target.object.getId(), targetchild, bowtieelements, projectid);
//                        //savesystem(p.getId(), targetchild, bowtieelements, projectid);
//                        // add the cell ID to arrayList to check if the cell has already been addded
//                        processedElements.add(t);
//                    } else if (targetchild.type.equals("SystemFunction")) {
//                        savesystemfunction(target.object.getId(), targetchild, bowtieelements, projectid, targetchild.type);
//                        //savesystemfunction(target.object.getId(), targetchild, bowtieelements, projectid, "System");
//                        //savesystemfunction(p.getId(), targetchild, bowtieelements, projectid, "System");
//                        processedElements.add(t);
//                    }
//                }
//            } else {
//                 Relationship rel = new Relationship(parentid, target.object.getId(), target.type);
//                rel.setComment("system diagram");
//                rel.setManagementClass("Diagram");
//                target.object.addRelationship(rel);
//                MetaFactory.getInstance().getFactory(target.type).put(target.object);
//                processedElements.add(String.valueOf(target.cellId));
//            }
//        }
//    }
    //  </editor-fold>   
    // <editor-fold defaultstate="collapsed" desc=" Unused Code ">
//    private void CheckForChildObjects(Object obj, HashMap<String, DiagramEditorElement> bowtieElements, int projectid, int parentobjectid)
//            throws Exception {
//        System sys = null;
//        SystemFunction sf = null;
//        if (obj.getClass() == sys.getClass()) {
//
//            sys = (System) obj;
//            for (DiagramEditorElement bt : bowtieElements.values()) {
//                if (bt.name == null) {
//                    Persistable p = createPersistable(bt.type);
//                    if (p == null) {
//                        throw new Exception("Unknown Bowtie Element type " + bt.type);
//                    }
//
//                    if (bt.type.contentEquals("System")) {
//                        for (String t : bt.connections) {
//                            DiagramEditorElement target = bowtieElements.get(t);
//                            if (Integer.parseInt(t) == target.object.getId()) {
//                                p.setAttribute("Name", bt.name);
//                                p.setAttribute("GraphCellId", bt.cellId);
//                                p.setAttribute("ParentSystemID", sys.getId());
//                                p.setAttribute("ProjectID", projectid);
//                                target.object = p;
//                                MetaFactory.getInstance().getFactory(target.type).put(p);
//                                Relationship rto = new Relationship(bt.object.getId(), target.object.getId(), target.type);
//                                rto.setComment("system diagram");
//                                rto.setManagementClass("Diagram");
////                        Relationship rfrom = new Relationship(target.object.getId(), bt.object.getId(), bt.type);
////                        if (target.type.contentEquals("SystemFunction")) {
////                            rto.setComment("bowtie diagram");
////                            //rfrom.setComment("Caused by");
////                        } 
//                            }
//                        }
//
//                    }
////                   if (bt.type.contentEquals("SystemFunction")) {
////                        p.setAttribute("Name", bt.name);
////                        p.setAttribute("GraphCellId", bt.cellId);
////                       // p.setAttribute("SystemID", s.getId() ); //if it's a subFunction then figure how to find parent Function
////                        p.setAttribute("ProjectID", projectid );
////                   }
//
//                    bt.object = p;
//                    MetaFactory.getInstance().getFactory(bt.type).put(p);
//                } else {
//                    MetaFactory.getInstance().getFactory(bt.type).put(bt.object);
//                }
//
//            }
//
//        } else if (obj.getClass() == sf.getClass()) {
//            sf = (SystemFunction) obj;
//
//            for (DiagramEditorElement bt : bowtieElements.values()) {
//                if (bt.name == null) {
//                    Persistable p = createPersistable(bt.type);
//                    if (p == null) {
//                        throw new Exception("Unknown Bowtie Element type " + bt.type);
//                    }
//
//                    if (bt.type.contentEquals("System")) {
//
//                        p.setAttribute("Name", bt.name);
//                        p.setAttribute("GraphCellId", bt.cellId);
//                        p.setAttribute("ParentSystemID", sf.getId());
//                        p.setAttribute("ProjectID", projectid);
//                    }
//                    if (bt.type.contentEquals("SystemFunction")) {
//                        p.setAttribute("Name", bt.name);
//                        p.setAttribute("GraphCellId", bt.cellId);
//                        // p.setAttribute("SystemID", s.getId() ); //if it's a subFunction then figure how to find parent Function
//                        p.setAttribute("ProjectID", projectid);
//                    }
//
//                    bt.object = p;
//                    MetaFactory.getInstance().getFactory(bt.type).put(p);
//                } else {
//                    MetaFactory.getInstance().getFactory(bt.type).put(bt.object);
//                }
//
//            }
//
//        }
//        //end test code
//    }
    //  </editor-fold>
    Persistable createPersistable(String t) {
        Persistable p = null;
        if (t.contentEquals("System")) {
            p = new System();
        }
        if (t.contentEquals("SystemFunction")) {
            p = new SystemFunction();
        }
//        if (t.contentEquals("SubFunction")) {
//            p = new SystemFunction();
//        }
        if (p != null) {
            added.add(p);
        }
        return p;
    }

    private NodeList parseSystem(String xml, System s)
            throws Exception {
        // Find the System, get the name, and store it.
        //when parsing XMl make sure that we find the root system and not the subsystem.
        // this can be done using mxCell id value . this will not be used as target in any of the edsge cell having no target value of the system
        // e.g mxCell edge="1" id="6" parent="1" source="2" style="straight" target="3" value=""> if value 2 is not used at target in the whole xml string then 2 is root element

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        HashMap<String, Element> dic = new HashMap<String, Element>();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(xml);
        InputSource is = new InputSource(sr);
        Element d = db.parse(is).getDocumentElement();
        NodeList nl = d.getElementsByTagName("mxCell");
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element) nl.item(i);
            if (cell.hasAttribute("style")
                    && cell.getAttribute("style").contentEquals("image;image=/uk/nhs/digital/safetycase/ui/systemeditor/system.png")) {
                String id = cell.getAttribute("id");
                dic.put(id, cell);
            }
        }
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element) nl.item(i);
            if (cell.hasAttribute("edge")) {
                String id = cell.getAttribute("target");
                if (dic.containsKey(id)) {
                    dic.remove(id);// reomve as it not the root node
                }
            }
        }
        HashMap.Entry<String, Element> entry = dic.entrySet().iterator().next();
        Element cell = entry.getValue();
        String id = cell.getAttribute("id"); //entry.getKey();
        String name = cell.getAttribute("value");
        s.setAttribute("Name", name);
        s.setAttribute("GraphCellId", Integer.parseInt(id));
        s.setAttribute("GraphXml", xml);

        return nl;
    }

}
