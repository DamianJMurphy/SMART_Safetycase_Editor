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
package uk.nhs.digital.projectuiframework.smart;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.ViewComponent;
import uk.nhs.digital.projectuiframework.ui.resources.ResourceUtils;
import uk.nhs.digital.safetycase.data.*;
import uk.nhs.digital.safetycase.ui.IssuesLogEditor;
import uk.nhs.digital.safetycase.ui.PersistableEditor;
import uk.nhs.digital.safetycase.ui.views.ViewConstructor;

/**
 *
 * @author damian
 */
public class SmartProject 
        implements uk.nhs.digital.projectuiframework.Project
{
    public static final String ISSUEREPORT_ICON = "/uk/nhs/digital/projectuiframework/smart/bell.png";
    public static final String SAFETYREPORT_ICON = "/uk/nhs/digital/projectuiframework/smart/diagram.gif";
    public static final String SYSTEM_ICON = "/uk/nhs/digital/projectuiframework/smart/workplace.png";
    public static final String FUNCTION_ICON = "/uk/nhs/digital/projectuiframework/smart/wrench.png";
    public static final String PROCESS_ICON = "/uk/nhs/digital/projectuiframework/smart/gear.png";
    public static final String HAZARD_ICON = "/uk/nhs/digital/safetycase/ui/bowtie/hazard.png";
    public static final String CAUSE_ICON = "/uk/nhs/digital/safetycase/ui/bowtie/cause.png";
    public static final String CONTROL_ICON = "/uk/nhs/digital/safetycase/ui/bowtie/control.jpg";
    public static final String EFFECT_ICON = "/uk/nhs/digital/safetycase/ui/bowtie/effect.png";
    public static final String ROLE_ICON = "/uk/nhs/digital/projectuiframework/smart/dude3.png";
    public static final String LOCATION_ICON = "/uk/nhs/digital/projectuiframework/smart/earth.png";
    public static final String VIEW_ICON = "/uk/nhs/digital/projectuiframework/smart/view.png";
    public static final String ISSUE_LOG_ICON = "/uk/nhs/digital/safetycase/ui/issueslog.png";
    
    private DefaultMutableTreeNode root = null;
    private static final String[] PROJECTCOMPONENTS = {"Process", "Hazard", "Cause", "Effect", "Control", "Care Settings", "Role", "Report"};
    private static final String[] PROJECTOTHERCOMPONENTS = { "Care Settings", "Role", "Report"};
    private static final String[] PROJECTEDITORS = {"Process", "Hazard", "Cause", "Effect", "Control", "Location", "Role", "Report"};
    private static final String[] PROJECTNEWABLES = {"Process", "Hazard", "Cause", "Effect", "Control", "Care Settings", "Role"};
    private static final String PROJECTNAME = "SMART";
    private MetaFactory metaFactory = null;
    private int currentProjectId = -1;
    
    public static final String EDITORCLASSROOT = "uk.nhs.digital.safetycase.ui.";
    private DefaultTreeModel treeModel = null;
    
    private static SmartProject project = null;
    private ProjectWindow projectWindow;
    private final HashMap<String,ImageIcon> icons = new HashMap<>();
    
    public SmartProject()
            throws Exception
    {
        metaFactory = MetaFactory.getInstance();
        metaFactory.initialise();
        project = this;
    }
 
    private ImageIcon getIcon(String s, ProjectTreeCellRenderer r) {
        try {
            ImageIcon icon = ResourceUtils.getImageIcon(s);
            icon = new ImageIcon(icon.getImage().getScaledInstance(r.getDefaultLeafIcon().getIconWidth(), r.getDefaultLeafIcon().getIconWidth(), Image.SCALE_DEFAULT));
            return icon;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
    }
    public static final SmartProject getProject() { return project; }
    
    private EditorComponent resolveNonContainedComponent(TreePath t) {
        
        Project p = null;
        DefaultMutableTreeNode d = (DefaultMutableTreeNode)t.getLastPathComponent();
        String s = null;
        String eclass = null;
        if (!d.isLeaf()) {
            s = d.getUserObject().toString();
            if (!s.contentEquals("Systems")) {
                boolean found = false;
                for (String comp : PROJECTNEWABLES) {
                    if (s.contentEquals(comp)) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    return null;
            }
        } else {
            try {
                s = (String) d.getUserObject();
            } 
            catch (ClassCastException cce) {}
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (s == null) {
//            java.lang.System.err.println("DEVELOPMENT: Could not identify where user clicked");
            return null;
        } else {
            if (s.contentEquals("Projects")) {
                java.lang.System.err.println("New Project editor not yet made");
            } else {
                if (s.contentEquals("Systems")) {
                    eclass = "System";
                } else {
                    for (int i = 0; i < PROJECTCOMPONENTS.length; i++) {
                        if (s.contentEquals(PROJECTCOMPONENTS[i])) {
                            eclass = PROJECTEDITORS[i];
                            break;
                        }
                    }
                }
            }
        }
        if (eclass == null) {
            java.lang.System.err.println("DEVELOPMENT: Could not identify where user clicked: " + s);
            return null;            
        } else {
            String check = checkCreatableViaForm(eclass);
            if (check != null)
                return null;
        }
        EditorComponent ec = null;
        eclass = java.lang.System.getProperty(EDITORCLASSROOT + eclass);
        try {
            uk.nhs.digital.safetycase.ui.PersistableEditor pe = (uk.nhs.digital.safetycase.ui.PersistableEditor)Class.forName(eclass).newInstance();
            ec = new EditorComponent(pe.getComponent(), "New " + s, this);
            pe.setEditorComponent(ec);
            pe.setPersistableObject(null);
            pe.setNewObjectProjectId(getSelectedProject(t));
        }
        catch (Exception e) {
            e.printStackTrace();
            JLabel l = new JLabel("Editor for " + p.getEditorType() + ":" + p.getTitle() + " not found");
            ec = new EditorComponent(l, p.getTitle(), this);
        }   
        return ec;
    }
    
    private int getSelectedProject(TreePath t) {
        DefaultMutableTreeNode d = (DefaultMutableTreeNode)t.getLastPathComponent();
        DefaultMutableTreeNode p = null;
        Persistable pr = null;
        while ((p = (DefaultMutableTreeNode)d.getParent()) != null) {
            try {
                pr = (Persistable)p.getUserObject();
                if (pr.getDatabaseObjectName().contentEquals("Project")) {
                    return pr.getId();
                }
            }
            catch (Exception e) {}
        }
        return -1;
    }
    
    @Override
    public EditorComponent getEditorComponent(TreePath t) {
        if (t.getLastPathComponent().toString().contentEquals("Issues Log")) {
           if (((DefaultMutableTreeNode)t.getLastPathComponent()).getUserObject() instanceof java.lang.String) {
               EditorComponent ecl = null;
                try {
                    PersistableEditor ile = new IssuesLogEditor();
                    ecl = new EditorComponent(ile.getComponent(), "Issues Log", this);
                    ile.setEditorComponent(ecl);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    JLabel l = new JLabel("Editor for Issues Log not found");
                    ecl = new EditorComponent(l, "Issues Log", this);
                }   
                return ecl;
               
           }
        }
        Persistable p = null;
        try {
            // This will only work for leaf nodes that contain Persistable instances.
            // That isn't true for a "Project" (which may be OK) or for a case where there are
            // no instances of the Persistable of that type. So detect this and instantiate an
            // editor with no content to the EditorComponent which should do the same thing as
            // if the "New" button had been clicked.
            p = (Persistable)((DefaultMutableTreeNode)t.getLastPathComponent()).getUserObject();
            if (p == null) {
                String check = checkNewFromPopupMenu(t);
                if (check != null)
                    return null;
            }
        }
        catch (ClassCastException e) {
            return resolveNonContainedComponent(t);
        }
        String s = p.getEditorType();
        String eclass = java.lang.System.getProperty(EDITORCLASSROOT + s);
        if (eclass == null)
            return null;
        EditorComponent ec = null;
        try {
            uk.nhs.digital.safetycase.ui.PersistableEditor pe = (uk.nhs.digital.safetycase.ui.PersistableEditor)Class.forName(eclass).newInstance();
            pe.setPersistableObject(p);
            ec = new EditorComponent(pe.getComponent(), s + ":" + p.getTitle(), this);
            pe.setEditorComponent(ec);
        }
        catch (Exception e) {
            e.printStackTrace();
            JLabel l = new JLabel("Editor for " + p.getEditorType() + ":" + p.getTitle() + " not found");
            ec = new EditorComponent(l, p.getTitle(), this);
        }   
        return ec;
    }

    @Override
    public String getName() {
        return PROJECTNAME;
    }

    @Override
    public void setName(String n) { }

    @Override
    public DefaultMutableTreeNode getProjectRoot() { return root; }

    @Override
    public DefaultTreeModel getTreeModel() { return treeModel; }
    
    private void populateSystemFunctions(DefaultMutableTreeNode sdn, int id)
            throws Exception
    {
        ArrayList<Persistable> functions = metaFactory.getChildren("System", "SystemID", id);
        if (functions == null)
            return;
        for (Persistable p : functions) {
            SystemFunction sf = (SystemFunction)p;
            if (sf.getAttributeValue("ParentSystemFunctionID").contentEquals("-1")) {
                DefaultMutableTreeNode d = new DefaultMutableTreeNode(sf.getTitle());
                d.setUserObject(sf);
                ArrayList<Persistable> subFunctions = metaFactory.getChildren("SystemFunction", "ParentSystemFunctionID", sf.getId());
                if (subFunctions != null) {
                    for (Persistable pssf : subFunctions) {
                        SystemFunction ssf = (SystemFunction)pssf;
                        DefaultMutableTreeNode sfn = new DefaultMutableTreeNode(ssf.getTitle());
                        sfn.setUserObject(ssf);
                        d.add(sfn);
                    }
                }            
                sdn.add(d);
            }
        }
    }
        
    @Override
    public void initialise()
            throws Exception
    {    
        root = new DefaultMutableTreeNode(getName());

        PersistableFactory<uk.nhs.digital.safetycase.data.System> sf = metaFactory.getFactory("System");
        PersistableFactory<SystemFunction> sff = metaFactory.getFactory("SystemFunction");
        DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");
        root.add(projectsNode);
        PersistableFactory<Project> pf = metaFactory.getFactory("Project");
        Collection<Project> projects = pf.getEntries();
                
        for (Project proj : projects) {
            DefaultMutableTreeNode p = new DefaultMutableTreeNode(proj.getTitle());
            p.setUserObject(proj);
            ArrayList<Persistable> list = metaFactory.getChildren("System", "ProjectID", proj.getId());
            DefaultMutableTreeNode systemsNode = new DefaultMutableTreeNode("Systems");
            p.add(systemsNode);
            if (list != null) {
                for (Persistable s : list) {                
                    DefaultMutableTreeNode sn = new DefaultMutableTreeNode(s.getTitle());
                    uk.nhs.digital.safetycase.data.System sys = (uk.nhs.digital.safetycase.data.System)s;
                    sn.setUserObject(sys);
                    systemsNode.add(sn);
                    populateSystemFunctions(sn, s.getId());
                }
            }
            populateProjectComponent("Process", p, proj.getId());
            p.add(populateHazard(proj.getId()));
            
            for (String s : PROJECTOTHERCOMPONENTS) {
                populateProjectComponent(s, p, proj.getId());
            }
            DefaultMutableTreeNode viewsNode = populateViewsNode(proj.getId());
            p.add(viewsNode);
            DefaultMutableTreeNode issuesNode = new DefaultMutableTreeNode("Issues Log");
            p.add(issuesNode);
            // TO DO: Data quality check reports and issues need to be added here, but the
            // users need some understanding of this first, and we might not do it properly
            // for the demo
            projectsNode.add(p);
        }
        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("SMART"));
        ((DefaultMutableTreeNode)treeModel.getRoot()).add(root);
    }

    private DefaultMutableTreeNode populateViewsNode(int pid) 
    {
        DefaultMutableTreeNode views = new DefaultMutableTreeNode("Views");
        DefaultMutableTreeNode hazardTypeView = new DefaultMutableTreeNode();
        ViewComponent vc = new ViewComponent("HazardTypes", "uk.nhs.digital.safetycase.ui.views.HazardTypeView");
        vc.setProjectId(pid);
        hazardTypeView.setUserObject(vc);
        views.add(hazardTypeView);
        return views;
    }
    
    private DefaultMutableTreeNode populateSingleHazard(Hazard h)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(h.getTitle());
        DefaultMutableTreeNode hz = new DefaultMutableTreeNode(h.getTitle());
        hz.setUserObject(h);
        node.add(hz);
        populateHazardDependents("Cause", node, h);
        populateHazardDependents("Control", node, h);
        populateHazardDependents("Effect", node, h);
        return node;
    }

    private DefaultMutableTreeNode populateHazard(int id)
    {
        DefaultMutableTreeNode hazardsNode = new DefaultMutableTreeNode("Hazard");

        ArrayList<Persistable> list = metaFactory.getChildren("Hazard", "ProjectID", id);
        if (list != null) {            
            for (Persistable p : list) {
                Hazard hz = (Hazard)p;
                hazardsNode.add(populateSingleHazard(hz));
            }
        }
        return hazardsNode;
    }
    
    private void populateHazardDependents(String type, DefaultMutableTreeNode hazardnode, Hazard h)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(type);
        hazardnode.add(node);
        try {
            ArrayList<Relationship> rels = h.getRelationships(type);
            if (rels == null)
                return;
            for (Relationship r : rels) {
                if ((r.getManagementClass() != null) && r.getManagementClass().contentEquals("Diagram")) {
                    Persistable p = MetaFactory.getInstance().getFactory(type).get(r.getTarget());
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(p.getTitle());
                    n.setUserObject(p);
                    node.add(n);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void populateProjectComponent(String type, DefaultMutableTreeNode n, int id)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(type);
        n.add(node);
        String s = type;
        if (type.contentEquals("Care Settings"))
            s = "Location";
        ArrayList<Persistable> list = metaFactory.getChildren(s, "ProjectID", id);
        if (list == null)
            return;
        for (Persistable p : list) {
            if (p.isDeleted())
                continue;
            DefaultMutableTreeNode pn = new DefaultMutableTreeNode(p.getTitle());
            pn.setUserObject(p);
            node.add(pn);
        }
    }
    
    @Override
    public void load(File f) throws Exception {
        throw new UnsupportedOperationException("Project import from file not supported yet"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void load() throws Exception {  }

    @Override
    public String getFileName() {
        return PROJECTNAME;
    }

    @Override
    public void save(File f) throws Exception {
        throw new UnsupportedOperationException("Project export to file not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() throws Exception { }

    @Override
    public boolean hasChanged() {
        return false;
    }    

    @Override
    public int getProjectID(DefaultMutableTreeNode n) {
        
        if (n == null)
            return -1;
        DefaultMutableTreeNode node = n;
        try {
            while (node != null) {
                if (!(node.getUserObject() instanceof java.lang.String)) {
                    Persistable p = (Persistable)node.getUserObject();
                    String id = null;
                    if (p.getDatabaseObjectName().contentEquals("Project")) {
                        return p.getId();
                    } else {
                        id = p.getAttributeValue("ProjectID");
                    }
                    if (id != null)
                        return Integer.parseInt(id);
                }
                node = (DefaultMutableTreeNode)node.getParent();
            }
        }
        catch (Exception e) {
            // TODO: This will blow up if it isn't a ViewComponent... but that is OK for now
            ViewComponent vc = (ViewComponent)node.getUserObject();
            return vc.getProjectId();
        }
        return -1;
    }

    @Override
    public int getCurrentProjectID() {
        return currentProjectId;
    }
    
    @Override
    public void setCurrentProjectID(int id) {
        currentProjectId = id;
    }

    @Override
    public ImageIcon getIcon(Object o) 
    {
        if (o == null)
            return null;
        Persistable p = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
        try {
            p = (Persistable)node.getUserObject();
            return icons.get(p.getDatabaseObjectName());
        }
        catch (ClassCastException cce) {}
        try {
            ViewComponent v = (ViewComponent)node.getUserObject();
            return icons.get("View");
        }
        catch (ClassCastException cce) {}
        if (o.toString().contentEquals("Issues Log"))
            return icons.get("Issues Log");
        return null;
    }
    
    @Override
    public DefaultMutableTreeNode getTreeNode(Object o)
            throws Exception
    {
        if (o == null)
            return null;
        Persistable p = null;
        try {
            p = (Persistable)o;
        }
        catch (ClassCastException cce) {
            throw new Exception("Object must be an instance of Persistable", cce);
        }
        // For anything not in "HazardCauseControlEffect" just make a DefaultMutableTreeNode with a name of p.getTitle()
        // and with p as the user object.
        // Otherwise, identify the hazard and make a full node for that, by calling populateSingleHazard()
        
        if ("HazardCauseControlEffect".contains(p.getDatabaseObjectName())) {
            Hazard h = null;
            if (p.getDatabaseObjectName().contentEquals("Hazard")) {
                h = (Hazard)p;
            }  else {
                // Find the hazard, and set h to it
                Collection<Hazard> hazards = MetaFactory.getInstance().getFactory("Hazard").getEntries();
                for (Hazard hz : hazards) {
                    ArrayList<Relationship> rels = hz.getRelationships(p.getDatabaseObjectName());
                    if (rels != null) {
                        for (Relationship r : rels) {
                            if (r.getTarget() == p.getId()) {
                                h = hz;
                                break;
                            }
                        }
                    }
                    if (h != null)
                        break;
                }
            }                 
            if (h == null)
                return null;
            return populateSingleHazard(h);
        } else {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(p.getTitle());
            node.setUserObject(o);
            return node;
        }
    }
    
    
    @Override
    public void editorEvent(int ev, Object o) {
        Persistable p = (Persistable)o;
        DefaultMutableTreeNode containerNode = null;
        boolean updateroot = false;
        String search = null;
        if (p.getDatabaseObjectName().contentEquals("System")) {
            search = "Systems";
        } else if (p.getDatabaseObjectName().contentEquals("Location")) {
            search = "Care Settings";
        } else if (p.getDatabaseObjectName().contentEquals("Project")) {
            search = "Projects";
            updateroot = true;
        } else if ("HazardCauseControlEffect".contains(p.getDatabaseObjectName())) {
            search = "Hazard";
        } else {    
            search = p.getDatabaseObjectName();
        }

        // Do a preliminary scan to get the correct project, then run
        // the search to find the container node for the changed object
        Enumeration nodes = root.breadthFirstEnumeration();
        DefaultMutableTreeNode projectNode = null;
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode d = (DefaultMutableTreeNode)nodes.nextElement();
            uk.nhs.digital.safetycase.data.Project project = null;
            try {
                project = (uk.nhs.digital.safetycase.data.Project)d.getUserObject();
                if (project.getId() == (Integer.parseInt(p.getAttributeValue("ProjectID")))) {
                    projectNode = d;
                    break;
                }
            }
            catch (Exception eIgnore) {}
        }
        if (projectNode == null) {
            if (updateroot) {
                projectNode = root;
            } else {
                java.lang.System.err.println("Did not find project node");
                return;
            }
        }
        nodes = projectNode.depthFirstEnumeration();
        
        
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode d = (DefaultMutableTreeNode)nodes.nextElement();
            if ((d.getUserObject() instanceof java.lang.String) && (((String)d.getUserObject()).contentEquals(search))) {
                containerNode = d;
                break;
            }
        }
        if (containerNode == null)
            return;
        
        DefaultMutableTreeNode eventNode = null;
        try {
            eventNode = getTreeNode(p);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        // TODO: On add, inserting first breaks the "select index in the existing table" (though not for existing entries). FIX.
        switch (ev) {
            case uk.nhs.digital.projectuiframework.Project.ADD:
                treeModel.insertNodeInto(eventNode, containerNode, containerNode.getChildCount());
                if (p.getDatabaseObjectName().contentEquals("Project"))
                    fillOutNewProject(eventNode);
                break;
            case uk.nhs.digital.projectuiframework.Project.DELETE:
                for (int i = 0; i < containerNode.getChildCount(); i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)containerNode.getChildAt(i);
                    Persistable pr = (Persistable)node.getUserObject();
                    if (pr.getTitle().contentEquals(p.getTitle()) && (pr.getId() == p.getId())) {
                        treeModel.removeNodeFromParent(node);
                        break;
                    }
                }
                break;
            case uk.nhs.digital.projectuiframework.Project.UPDATE:    
                // Find the node we're replacing in the container node, by type and name
                for (int i = 0; i < containerNode.getChildCount(); i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)containerNode.getChildAt(i);
                    if (node.toString().contentEquals(eventNode.toString())) {
                        treeModel.removeNodeFromParent(node);
                        treeModel.insertNodeInto(eventNode, containerNode, i);
                        break;
                    }
                }
                break;
            default:
                return;
        }
    }
    
    private void fillOutNewProject(DefaultMutableTreeNode d) {
        DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode("Systems");
        d.add(dmtn);
        for (String s : PROJECTCOMPONENTS) {
            dmtn = new DefaultMutableTreeNode(s);
            d.add(dmtn);
        }
        dmtn = new DefaultMutableTreeNode("Issues");
    }

    @Override
    public boolean checkShowPopup(TreePath t) {
        TreePath path = t;
        if (t.getPathCount() < 5)
            return false;
        try {
            do {
                Object o = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                if (o instanceof java.lang.String) {
                    if (((String) o).contains("Views"))
                        return false;
                    if (((String) o).contains("Issues"))
                        return false;
                }
                path = path.getParentPath();
            } while(path != null);
        }
        catch (Exception e) {
            return false;
        }
       return true;
    }
    
    @Override
    public String checkNewFromPopupMenu(TreePath t) {
        String s = null;    
        Persistable p = null;
        Object o = ((DefaultMutableTreeNode)t.getLastPathComponent()).getUserObject();
        if (o instanceof uk.nhs.digital.safetycase.data.Persistable) {
            s = ((Persistable)o).getDatabaseObjectName();
        } else {
            s = (String)o.toString();
        }
        return checkCreatableViaForm(s);
    }
    
    private String checkCreatableViaForm(String s) {
        if (s.contentEquals("Cause")) {
            return "Create new Causes in context using the Bowtie editor";
        }
        if (s.contentEquals("Control")) {
            return "Create new Controls (and mitigations for Effects) in context using the Bowtie editor";
        }
        if (s.contentEquals("Effect")) {
            return "Create new Effects in context using the Bowtie editor";
        }
        return null;        
    }

    @Override
    public void setProjectWindow(ProjectWindow pw) {
        projectWindow = pw;
        ProjectTreeCellRenderer r = new ProjectTreeCellRenderer(this);
        icons.put("Report", getIcon(SAFETYREPORT_ICON, r));
        icons.put("System", getIcon(SYSTEM_ICON, r));
        icons.put("SystemFunction", getIcon(FUNCTION_ICON, r));
        icons.put("Process", getIcon(PROCESS_ICON, r));
        icons.put("Hazard", getIcon(HAZARD_ICON, r));
        icons.put("Cause", getIcon(CAUSE_ICON, r));
        icons.put("Control", getIcon(CONTROL_ICON, r));
        icons.put("Effect", getIcon(EFFECT_ICON, r));
        icons.put("Role", getIcon(ROLE_ICON, r));
        icons.put("Location", getIcon(LOCATION_ICON, r));
        icons.put("View", getIcon(VIEW_ICON, r));
        icons.put("Issues Log", getIcon(ISSUE_LOG_ICON, r));
        pw.setTreeCellRenderer(r);
    }
    
    @Override
    public ProjectWindow getProjectWindow() { return projectWindow; }

    @Override
    public ViewComponent getViewComponent(TreePath t) 
    {
        ViewComponent view = null;
        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)t.getLastPathComponent();
        try {
            view = (ViewComponent)dmtn.getUserObject();
        }
        catch (ClassCastException cce) {
            return null;
        }
        try {
            Component c = (Component)(Class.forName(view.getClassName()).newInstance());
            view.setComponent(c);
            ((ViewConstructor)c).setProjectID(currentProjectId);
            view.setProject(this);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }
}
