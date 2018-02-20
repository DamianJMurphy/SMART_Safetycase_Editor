/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.safetycase.ui.systemeditor;

import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import java.awt.Color;
import java.awt.Component;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.JSplitPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;
import uk.nhs.digital.safetycase.ui.PersistableEditor;
import uk.nhs.digital.safetycase.ui.systemeditor.SystemGraphEditor;
import uk.nhs.digital.safetycase.ui.systemeditor.SystemListForm;

/**
 *
 * @author SHUL1
 */

public class SystemEditor extends javax.swing.JSplitPane
        implements PersistableEditor {

    private SystemListForm table = null;
    private SystemGraphEditor editor = null;
    private final String[] columns = {"ID", "Name", "Description", "Created"};
    private EditorComponent editorComponent = null;
    private int newObjectProjectId = -1;

    
    

    public SystemEditor() {
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);

        mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
        table = new SystemListForm(this);
        editor = new SystemGraphEditor();
        this.setTopComponent(table);
        this.setBottomComponent(editor);

    }

    // <editor-fold defaultstate="collapsed" desc="Unused Code"> 
//     public SystemEditor(uk.nhs.digital.safetycase.data.System s) {
//        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
//
//        mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
//        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
//        table = new SystemListForm(this);
//        editor = new SystemGraphEditor();
//        this.setTopComponent(table);
//        this.setBottomComponent(editor);
//        
//        try {
//            uk.nhs.digital.safetycase.data.System system = s;
//            PersistableFactory<uk.nhs.digital.safetycase.data.System> pfp = MetaFactory.getInstance().getFactory("System");
//            int projectid = Integer.parseInt(system.getAttributeValue("ProjectID"));
//            Collection<uk.nhs.digital.safetycase.data.System> cp = pfp.getEntries(projectid);
//            if ((cp == null) || (cp.isEmpty())) {
//                return;
//            }
//            DefaultTableModel dtm = new DefaultTableModel(columns, 0);
//            int i = 0;
//            int selected = -1;
//            for (uk.nhs.digital.safetycase.data.System pr : cp) {
//                if (pr.isDeleted()) {
//                    continue;
//                }
//                String[] row = new String[columns.length];
//                row[0] = Integer.toString(pr.getId());
//                row[1] = pr.getAttributeValue("Name");
//                row[2] = pr.getAttributeValue("Description");
//                row[3] = pr.getAttributeValue("CreatedDate");
//                dtm.addRow(row);
//                if (pr.getId() == system.getId()) {
//                    selected = i; 
//                }
//                i++;
//            }
//            table.setTableModel(dtm);
//            if (selected != -1) {
//                table.setSelected(selected);
//                editor.setSystemId(system.getId(), system.getAttributeValue("GraphXml"));
//                setSelectedSystem(system.getId());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    // </editor-fold>
    EditorComponent getEditorComponent() {
        return editorComponent;
    }

    int getNewObjectProjectId() {
        return newObjectProjectId;
    }

    @Override
    public void setEditorComponent(EditorComponent ed) {
        editorComponent = ed;
    }

    void addNewSystem(uk.nhs.digital.safetycase.data.System pr) {
        String[] row = new String[columns.length];
        row[0] = Integer.toString(pr.getId());
        row[1] = pr.getAttributeValue("Name");
        row[2] = pr.getAttributeValue("Description");
        row[3] = pr.getAttributeValue("CreatedDate");
        table.getTableModel().addRow(row);
        editorComponent.notifyEditorEvent(Project.ADD, pr);
    }

    @Override
    public void setPersistableObject(Persistable p) {
        // This editor is used for all systemes in the current project, so find the
        // systemes, and load them into the table in the list form... but then 
        // highlight the one passed here unless it is null.
        if (p == null) {
            return;
        }
        try {
            uk.nhs.digital.safetycase.data.System system = (uk.nhs.digital.safetycase.data.System) p;
            PersistableFactory<uk.nhs.digital.safetycase.data.System> pfp = MetaFactory.getInstance().getFactory("System");
            int projectid = Integer.parseInt(system.getAttributeValue("ProjectID"));
            Collection<uk.nhs.digital.safetycase.data.System> cp = pfp.getEntries(projectid);
            if ((cp == null) || (cp.isEmpty())) {
                return;
            }
            DefaultTableModel dtm = new DefaultTableModel(columns, 0);
            int i = 0;
            int selected = -1;
            for (uk.nhs.digital.safetycase.data.System pr : cp) {
                if (pr.isDeleted()) {
                    continue;
                }
                String[] row = new String[columns.length];
                row[0] = Integer.toString(pr.getId());
                row[1] = pr.getAttributeValue("Name");
                row[2] = pr.getAttributeValue("Description");
                row[3] = pr.getAttributeValue("CreatedDate");
                dtm.addRow(row);
                if (pr.getId() == system.getId()) {
                    selected = i; 
                }
                i++;
            }
            table.setTableModel(dtm);
            if (selected != -1) {
                table.setSelected(selected);
                editor.setSystemId(system.getId(), system.getAttributeValue("GraphXml"));
                setSelectedSystem(system.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void setSelectedSystem(int s) {
        try {
            PersistableFactory<uk.nhs.digital.safetycase.data.System> pfs = MetaFactory.getInstance().getFactory("System");
            uk.nhs.digital.safetycase.data.System system = pfs.get(s);
            
            String xml = system.getAttributeValue("GraphXml");
            editor.setSystemId(system.getId(), xml);
            if ((xml == null) || (xml.trim().length() == 0)) {
                return;
            }

            HashMap<String, DiagramEditorElement> systemElements = new HashMap<>();
            
            systemElements.put(system.getAttributeValue("GraphCellId"), new DiagramEditorElement(system));
            // load sub system and functions for the same system

            //  ArrayList<Relationship> sRels = system.getRelationships("System");
            //ArrayList<Relationship> sfRels = system.getRelationshipsForClass("SystemFunction");
            HashMap<String, ArrayList<Relationship>> hrels = system.getRelationshipsForLoad();
            if (hrels == null) {
                return;
            }
            // to do. check the relation of child systems and system fucntions
            for (ArrayList<Relationship> a : hrels.values()) {
                for (Relationship r : a) {
                    if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
                        Persistable p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        systemElements.put(p.getAttributeValue("GraphCellId"), new DiagramEditorElement(p));
                        // check for children/sub children realtionship
                        List<Persistable> lp= new ArrayList<>();
                        List<Persistable> pa = findchildrelations(p, lp);
                        //List<Persistable> pa = findchildrelations(p.getId(), p.getDatabaseObjectName(), lp);
                         for (Persistable per : pa) {
                             systemElements.put(per.getAttributeValue("GraphCellId"), new DiagramEditorElement(per));
                         }
                    }
                }
            }
            // Get the graph xml from the system 
            // tie everything together
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Element d = db.parse(is).getDocumentElement();
            NodeList nl = d.getElementsByTagName("mxCell");
            for (int i = 0; i < nl.getLength(); i++) {
                Element cell = (Element) nl.item(i);
                if (cell.hasAttribute("edge")) {
                    String src = cell.getAttribute("source");
                    String t = cell.getAttribute("target");
                    DiagramEditorElement bt = systemElements.get(src);
//                    bt.fromCell = Integer.parseInt(s);
//                    bt.toCell = Integer.parseInt(t);
                    bt.connections.add(t);
                }
            }

            editor.setExistingBowtie(systemElements);

        } catch (Exception e) {
            java.lang.System.err.println("Unprocessed Element : " + e.toString());
            e.printStackTrace();
        }
    }

     private List<Persistable> findchildrelations(Persistable relatedObject, List<Persistable> persistablelist)
     throws Exception {
        List<Persistable> lp= persistablelist;
        PersistableFactory<uk.nhs.digital.safetycase.data.System> pfs = MetaFactory.getInstance().getFactory("System");
        PersistableFactory<uk.nhs.digital.safetycase.data.SystemFunction> pfsf = MetaFactory.getInstance().getFactory("SystemFunction");
        Persistable p;
        try {
            if (relatedObject.getDatabaseObjectName().equals("System")) {
                
                uk.nhs.digital.safetycase.data.System system = pfs.get(relatedObject.getId());
                HashMap<String, ArrayList<Relationship>> hrels = system.getRelationshipsForLoad();
                if (hrels != null) {
                    for (ArrayList<Relationship> a : hrels.values()) {
                        for (Relationship r : a) {
                            if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                                lp.add(p);
                                //  find any sub children realtions
                                findchildrelations(p,lp);
                            }
                        }
                    }
                }
            }
                if (relatedObject.getDatabaseObjectName().equals("SystemFunction")) {
                uk.nhs.digital.safetycase.data.SystemFunction systemfunction = pfsf.get(relatedObject.getId());
                HashMap<String, ArrayList<Relationship>> hrels = systemfunction.getRelationshipsForLoad();
                if (hrels != null) {
                    for (ArrayList<Relationship> a : hrels.values()) {
                        for (Relationship r : a) {
                            if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                                lp.add(p);
                                // find any sub children realtions
                                findchildrelations(p,lp);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lp;
    }
    // <editor-fold defaultstate="collapsed" desc=" Unused Code ">
//    private List<Persistable> findchildrelations(int relatedObjectID, String relatedObjectType, List<Persistable> persistablelist)
//     throws Exception {
//        List<Persistable> lp= persistablelist;
//        PersistableFactory<uk.nhs.digital.safetycase.data.System> pfs = MetaFactory.getInstance().getFactory("System");
//        PersistableFactory<uk.nhs.digital.safetycase.data.SystemFunction> pfsf = MetaFactory.getInstance().getFactory("SystemFunction");
//        Persistable p;
//        try {
//            if (relatedObjectType.equals("System")) {
//                
//                uk.nhs.digital.safetycase.data.System system = pfs.get(relatedObjectID);
//                HashMap<String, ArrayList<Relationship>> hrels = system.getRelationshipsForLoad();
//                if (hrels != null) {
//                    for (ArrayList<Relationship> a : hrels.values()) {
//                        for (Relationship r : a) {
//                            if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
//                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
//                                lp.add(p);
//                                // recursively call method to find any sub children realtions
//                                findchildrelations(p.getId(), p.getDatabaseObjectName(),lp);
//                            }
//                        }
//                    }
//                }
//            }
//                if (relatedObjectType.equals("SystemFunction")) {
//                uk.nhs.digital.safetycase.data.SystemFunction systemfunction = pfsf.get(relatedObjectID);
//                HashMap<String, ArrayList<Relationship>> hrels = systemfunction.getRelationshipsForLoad();
//                if (hrels != null) {
//                    for (ArrayList<Relationship> a : hrels.values()) {
//                        for (Relationship r : a) {
//                            if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
//                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
//                                lp.add(p);
//                                // recursively call method to find any sub children realtions
//                                findchildrelations(p.getId(), p.getDatabaseObjectName(),lp);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return lp;
//    }
    //</editor-fold>

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void setNewObjectProjectId(int i) {
        newObjectProjectId = i;
    }
}
