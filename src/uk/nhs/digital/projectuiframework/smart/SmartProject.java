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
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import uk.nhs.digital.projectuiframework.DataNotificationSubscriber;
import uk.nhs.digital.projectuiframework.smart.logging.SmartLogger;
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
    public static final String PROJECT_ICON = "/uk/nhs/digital/safetycase/ui/project.png";
    public static final String ANALYSIS_ICON = "/uk/nhs/digital/projectuiframework/smart/scales.png";
    public static final String BOWTIE_ICON = "/uk/nhs/digital/safetycase/ui/bowtie.png";
    
    public static final String HELP_ABOUT_ICON = "/uk/nhs/digital/projectuiframework/smart/smart-about.png";
    public static final String SMART_ICON = "/uk/nhs/digital/projectuiframework/smart/nhsd-16x16.png";
    
    private DefaultMutableTreeNode root = null;
    private static final String[] PROJECTCOMPONENTS = {"Care Process", "Hazard", "Cause", "Effect", "Control", "Care Settings", "Role", "Report"};
//    private static final String[] PROJECTOTHERCOMPONENTS = { "Care Settings", "Role", "Report"};
    private static final String[] PROJECTEDITORS = {"Process", "Hazard", "Cause", "Effect", "Control", "Location", "Role", "Report"};
    private static final String[] PROJECTNEWABLES = {"Care Process", "Hazard", "Cause", "Effect", "Control", "Care Settings", "Role"};
    private static final String PROJECTNAME = "SMART";
    private MetaFactory metaFactory = null;
    private int currentProjectId = -1;
    
    public static final String EDITORCLASSROOT = "uk.nhs.digital.safetycase.ui.";
    private DefaultTreeModel treeModel = null;
    
    private static SmartProject project = null;
    private ProjectWindow projectWindow;
    private final HashMap<String,ImageIcon> icons = new HashMap<>();
    private ImageIcon helpAboutIcon = null;
    private Font applicationFont = null;
    
    private final ArrayList<DataNotificationSubscriber> notificationSubscribers = new ArrayList<>();

    private SmartLogger logger = null;
    
    public SmartProject()
            throws Exception
    {
        project = this;
        logger = new SmartLogger();
        try {
            metaFactory = MetaFactory.getInstance();
            metaFactory.initialise();
        }
        catch (Exception e) {
            log(Level.SEVERE, "Failed to initialise metaFactory", e);
            throw new Exception("Failed to initialise database access", e);
        }
    }
    
    public void setDisplayFont(Font f) { applicationFont = f; }
    public Font getDisplayFont() { return applicationFont; }
    
    @Override
    public String getApplicationIdentifier() {
        return java.lang.System.getProperty("uk.nhs.digital.safetycase.applicationidentity");
    }
    
    @Override
    public void log(String message, Throwable thrown) {
        log(Level.INFO, message, thrown);
    }

    @Override
    public void log(Level level, String message, Throwable thrown) {
        LogRecord lr = new LogRecord(level, message);
        lr.setThrown(thrown);
        logger.log(lr);
    }

    
    private ImageIcon getIcon(String s, ProjectTreeCellRenderer r) {
        try {
            ImageIcon icon = ResourceUtils.getImageIcon(s);
            icon = new ImageIcon(icon.getImage().getScaledInstance(r.getDefaultLeafIcon().getIconWidth(), r.getDefaultLeafIcon().getIconWidth(), Image.SCALE_DEFAULT));
            return icon;
        }
        catch (Exception e) {
            log("Error loading icon resource", e);
            return null;
        }       
    }
    public static final SmartProject getProject() { return project; }
    
    @SuppressWarnings("UseSpecificCatch")
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
                log("Error resolving user-selected component from project tree", e);
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
            @SuppressWarnings("null")
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
                    log("Editor for Issues Log not found", e);
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
        @SuppressWarnings("null")
        String s = p.getEditorType();
        if (s == null) {
            log("No editor type: " + p.getDatabaseObjectName(), null);
            return null;
        }
        String eclass = java.lang.System.getProperty(EDITORCLASSROOT + s);
        if (eclass == null)
            return null;
        EditorComponent ec = null;
        try {
            uk.nhs.digital.safetycase.ui.PersistableEditor pe = (uk.nhs.digital.safetycase.ui.PersistableEditor)Class.forName(eclass).newInstance();
            pe.setPersistableObject(p);
            ec = new EditorComponent(pe.getComponent(), p.getDisplayName() + ":" + p.getTitle(), this);
            pe.setEditorComponent(ec);
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log("No editor found for " + p.getDisplayName(), e);
            JLabel l = new JLabel("Editor for " + p.getDisplayName() + ":" + p.getTitle() + " not found");
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

//        PersistableFactory<uk.nhs.digital.safetycase.data.System> sf = metaFactory.getFactory("System");
//        PersistableFactory<SystemFunction> sff = metaFactory.getFactory("SystemFunction");
        DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");
        root.add(projectsNode);
        PersistableFactory<Project> pf = metaFactory.getFactory("Project");
        Collection<Project> projects = pf.getEntries();
                
        for (Project proj : projects) {
            if (!proj.isDeleted()) {
                DefaultMutableTreeNode p = populateProject(proj);
                // TO DO: Data quality check reports and issues need to be added here, but the
                // users need some understanding of this first, and we might not do it properly
                // for the demo
                projectsNode.add(p);
            }
        }
        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("SMART"));
        ((DefaultMutableTreeNode)treeModel.getRoot()).add(root);
    }

    @Override
    public void reInitialiseProjectView()
            throws Exception
    {
        initialise();
        projectWindow.resetTreeModel(treeModel);
    }
    
    private DefaultMutableTreeNode populateProject(uk.nhs.digital.safetycase.data.Project proj) {
        DefaultMutableTreeNode p = new DefaultMutableTreeNode(proj.getTitle());
        p.setUserObject(proj);
        DefaultMutableTreeNode peditor = new DefaultMutableTreeNode(proj.getTitle());
        peditor.setUserObject(proj);
        p.add(peditor);
        p.add(populateSystem(proj.getId()));

        populateProjectComponent("Care Settings", p, proj.getId());
        populateProjectComponent("Role", p, proj.getId());
        populateProjectComponent("Care Process", p, proj.getId());
        p.add(populateHazard(proj.getId()));

        DefaultMutableTreeNode issuesNode = new DefaultMutableTreeNode("Issues Log");
        p.add(issuesNode);
        
        DefaultMutableTreeNode viewsNode = populateViewsNode(proj.getId());
        p.add(viewsNode);
        
        populateProjectComponent("Report", p, proj.getId());

        return p;
    }
    
    private DefaultMutableTreeNode populateViewsNode(int pid) 
    {
        DefaultMutableTreeNode views = new DefaultMutableTreeNode("Views");
        DefaultMutableTreeNode hazardTypeView = new DefaultMutableTreeNode();
        ViewComponent vc = new ViewComponent("Hazard Types", "uk.nhs.digital.safetycase.ui.views.HazardTypeView");
        vc.setProjectId(pid);
        hazardTypeView.setUserObject(vc);
        views.add(hazardTypeView);
        DefaultMutableTreeNode hazardAnalysis = new DefaultMutableTreeNode();
        vc = new ViewComponent("Hazard Analysis", "uk.nhs.digital.safetycase.ui.views.HazardAnalysis");
        vc.setProjectId(pid);
        hazardAnalysis.setUserObject(vc);
        views.add(hazardAnalysis);        
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
                if (!hz.isDeleted())
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
            log("Error getting hazard dependents", e);
        }
    }
    
    private void populateProjectComponent(String type, DefaultMutableTreeNode n, int id)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(type);
        n.add(node);
        String s = type;
        if (type.contentEquals("Care Settings"))
            s = "Location";
        if (type.contentEquals("Care Process"))
            s = "Process";
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
    @SuppressWarnings("UseSpecificCatch")
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
    public ImageIcon getIcon(String s) 
    {
        return icons.get(s);
    }
    
    @Override
    public ImageIcon getIcon(Object o) 
    {
        if (o == null)
            return null;
        Persistable p = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
//        if (!node.isLeaf())
//            return null;
        try {
            p = (Persistable)node.getUserObject();
            String dbo = p.getDatabaseObjectName();
            if (!dbo.contentEquals("System") && !node.isLeaf())
                return null;
            return icons.get(p.getDatabaseObjectName());
        }
        catch (ClassCastException cce) {}
        try {
            ViewComponent v = (ViewComponent)node.getUserObject();
            if (v.getClassName().contains("Analysis"))
                return icons.get("Analysis");
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
        } else if (p.getDatabaseObjectName().contentEquals("System")) {
            uk.nhs.digital.safetycase.data.System sys = (uk.nhs.digital.safetycase.data.System)p;
            return populateSystemWithChildren(sys);
        } else {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(p.getTitle());
            node.setUserObject(o);
            return node;
        }
    }
    
    private void updateProjectNodeInTree(Persistable p, int ev) {
        
        uk.nhs.digital.safetycase.data.Project proj = (uk.nhs.digital.safetycase.data.Project)p;
        
        DefaultMutableTreeNode plist = (DefaultMutableTreeNode)treeModel.getChild(root, 0);
        if (ev == uk.nhs.digital.projectuiframework.Project.ADD) {
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(proj.getTitle());
            n.setUserObject(proj);
            DefaultMutableTreeNode peditor = new DefaultMutableTreeNode(proj.getTitle());
            peditor.setUserObject(proj);
            n.add(peditor);
            fillOutNewProject(n, proj.getId());
            treeModel.insertNodeInto(n, plist, 0);
            return;
        }
        DefaultMutableTreeNode pcontainer = populateProject(proj);
        for (int i = 0; i < plist.getChildCount(); i++) {
            DefaultMutableTreeNode pn = (DefaultMutableTreeNode)plist.getChildAt(i);            
//            pcontainer.add(n);
            uk.nhs.digital.safetycase.data.Project existing = (uk.nhs.digital.safetycase.data.Project)pn.getUserObject();
            if (existing.getId() == p.getId()) {
                treeModel.removeNodeFromParent(pn);
                if (ev == uk.nhs.digital.projectuiframework.Project.UPDATE)
                    treeModel.insertNodeInto(pcontainer, plist, i);
                return;
            }
        }
    }
    
    @Override
    public JPanel getExistingEditor(Object o, Object caller) {
        if (notificationSubscribers != null) {
            for (DataNotificationSubscriber d : notificationSubscribers) {
                JPanel p = d.getEditor(o);
                if ((p != null) && (p != caller))
                    return p;
            }
        }
        return null;
    }
    
    @Override
    @SuppressWarnings({"UseSpecificCatch", "ConvertToStringSwitch"})
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
            if (ev == uk.nhs.digital.projectuiframework.Project.ADD) {
                // TODO: For "can't open editor on new project" bug, an existing panel
                // is being returned but with no editor component. Check. Something might
                // need to be done here to make sure that it is populated properly.
            }
            updateProjectNodeInTree(p, ev);
            return;
        } else if ("HazardCauseControlEffect".contains(p.getDatabaseObjectName())) {
            search = "Hazard";
        } else if (p.getDatabaseObjectName().contentEquals("Process")) {
            search = "Care Process";
        } else {    
            search = p.getDatabaseObjectName();
        }

        // Do a preliminary scan to get the correct project, then run
        // the search to find the container node for the changed object
        Enumeration nodes = root.breadthFirstEnumeration();
        DefaultMutableTreeNode projectNode = null;
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode d = (DefaultMutableTreeNode)nodes.nextElement();
            uk.nhs.digital.safetycase.data.Project proj = null;
            try {
                proj = (uk.nhs.digital.safetycase.data.Project)d.getUserObject();
                if (proj.getId() == (Integer.parseInt(p.getAttributeValue("ProjectID")))) {
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

        TreePath pathToContainer = new TreePath(containerNode.getPath());
        boolean expanded = projectWindow.getProjectTree().isExpanded(pathToContainer);
        
        DefaultMutableTreeNode eventNode = null;
        try {
            eventNode = getTreeNode(p);
        }
        catch (Exception e) {
            log("Cannot make eventNode processing editor notification event", e);
            return;
        }
        
        // TODO: On add, inserting first breaks the "select index in the existing table" (though not for existing entries). FIX.
        switch (ev) {
            case uk.nhs.digital.projectuiframework.Project.ADD:
                treeModel.insertNodeInto(eventNode, containerNode, containerNode.getChildCount());
                if (p.getDatabaseObjectName().contentEquals("Project"))
                    fillOutNewProject(eventNode, p.getId());
                break;
            case uk.nhs.digital.projectuiframework.Project.DELETE:
                for (int i = 0; i < containerNode.getChildCount(); i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)containerNode.getChildAt(i);
                    if (containerNode.getUserObject().toString().contentEquals("Hazard")) {
                        if (node.getUserObject().toString().contentEquals(p.getTitle())) {
                            treeModel.removeNodeFromParent(node);
                            break;                            
                        }
                    } else if (containerNode.getUserObject().toString().contentEquals("Systems")) {
                        // containerNode is grandparent of event node, so remove eventNode's parent
                        if (node.getUserObject().toString().contentEquals(p.getTitle())) {
                            treeModel.removeNodeFromParent(node);
                            break;
                        }
                    } else  {
                        Persistable pr = (Persistable)node.getUserObject();
                        if (pr.getTitle().contentEquals(p.getTitle()) && (pr.getId() == p.getId())) {
                            treeModel.removeNodeFromParent(node);
                            break;
                        }
                    }
                }
                break;
            case uk.nhs.digital.projectuiframework.Project.UPDATE:    
                // Find the node we're replacing in the container node, by type and name
                boolean foundByName = false;
                for (int i = 0; i < containerNode.getChildCount(); i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)containerNode.getChildAt(i);
                    if (node.toString().contentEquals(eventNode.toString())) {
                        treeModel.removeNodeFromParent(node);
                        treeModel.insertNodeInto(eventNode, containerNode, i);
                        foundByName = true;
                        break;
                    }
                }
                if (!foundByName) {
                    // Didn't find by name, probably because the event we're being notified of is a
                    // change of name. Search by type and id instead
                    nodes = containerNode.depthFirstEnumeration();
                    while (nodes.hasMoreElements()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)nodes.nextElement();
                        Object n = node.getUserObject();
                        if (n instanceof uk.nhs.digital.safetycase.data.Persistable) {
                            Persistable existing = (Persistable)n;
                            if (existing.getDatabaseObjectName().contentEquals(p.getDatabaseObjectName())) {
                                if (existing.getId() == p.getId()) {
                                    // If this is a Hazard, we need to replace the parent of "node" wiht the
                                    // event node.
                                    if (existing.getDatabaseObjectName().contentEquals("Hazard")) {
                                        node = (DefaultMutableTreeNode)node.getParent();
                                        int i = treeModel.getIndexOfChild(containerNode, node);
                                        treeModel.removeNodeFromParent(node);
                                        treeModel.insertNodeInto(eventNode, containerNode, i);
                                    } else if (existing.getDatabaseObjectName().contentEquals("System")) {
                                        node = (DefaultMutableTreeNode)node.getParent();
                                        int i = treeModel.getIndexOfChild(containerNode, node);
                                        treeModel.removeNodeFromParent(node);
                                        treeModel.insertNodeInto(eventNode, containerNode, i);
                                    } else {
                                        int i = treeModel.getIndexOfChild(containerNode, node);
                                        treeModel.removeNodeFromParent(node);
                                        try {
                                            treeModel.insertNodeInto(eventNode, containerNode, i);
                                        }
                                        catch (ArrayIndexOutOfBoundsException aiobe) {
                                            containerNode.add(eventNode);
                                            treeModel.nodeChanged(containerNode);
                                        }
                                    }
                                    break;                                    
                                }
                            }
                        }
                    }
                }
                break;
            default:
                return;
        }
        if (expanded)
            projectWindow.getProjectTree().expandPath(pathToContainer);
        
        // Do this to stop subscribers removing themselves as a result of the
        // notification, and causing a concurrency exception.
        //
        ArrayList<DataNotificationSubscriber> toBeRemoved = new ArrayList<>();
        try {
            for (DataNotificationSubscriber d : notificationSubscribers) {
                if (d.notification(ev, o)) {
                    toBeRemoved.add(d);
                }
            }
            for (DataNotificationSubscriber d : toBeRemoved) {
               notificationSubscribers.remove(d);
            }
        }
        catch (java.util.ConcurrentModificationException e) {
            log("Conflict managing notificationSubscribers", e);
        }
    }
    
    @Override
    public void saveAll() 
    {
        try {
            for (DataNotificationSubscriber d : notificationSubscribers) {
                d.notification(SAVE, null);
            }
        }
        catch (Exception e) {
            log("Exception caught processing notifications in SaveAll from main menu", e);
        }
    }
    
    private void fillOutNewProject(DefaultMutableTreeNode d, int pid) {
        DefaultMutableTreeNode dmtn = null;
        dmtn = new DefaultMutableTreeNode("Systems");
        d.add(dmtn);
        dmtn = new DefaultMutableTreeNode("Care Settings");
        d.add(dmtn);
        dmtn = new DefaultMutableTreeNode("Role");
        d.add(dmtn);
        dmtn = new DefaultMutableTreeNode("Care Process");
        d.add(dmtn);
        dmtn = new DefaultMutableTreeNode("Hazard");
        d.add(dmtn);
        dmtn = new DefaultMutableTreeNode("Issues Log");
        d.add(dmtn);
        DefaultMutableTreeNode viewsNode = populateViewsNode(pid);        
        d.add(viewsNode);
        dmtn = new DefaultMutableTreeNode("Report");
        d.add(dmtn);
        
    }

    @Override
    public boolean checkShowPopup(TreePath t) {
        TreePath path = t;
        if (t.getPathCount() < 5) {
            return false;
        }
        try {
            Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            if (o instanceof java.lang.String) {
                if (((String) o).contains("Systems")) {
                    return true;
                }
                if (((String) o).contains("Care Settings")) {
                    return true;
                }
                if (((String) o).contains("Role")) {
                    return true;
                }
                if (((String) o).contains("Care Process")) {
                    return true;
                }
                if (((String) o).contains("Hazard")) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
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

    public int getTableRowHeight() {
        return (int)(applicationFont.getSize() * 1.3);        
    }
    
    @Override
    public ProjectTreeCellRenderer getProjectTreeCellRenderer() {
        ProjectTreeCellRenderer r = new ProjectTreeCellRenderer(this);
        return r;
    }
    
    @Override
    public void setProjectWindow(ProjectWindow pw) {
        projectWindow = pw;
        applicationFont = projectWindow.getDisplayFont();
        ProjectTreeCellRenderer r = getProjectTreeCellRenderer();
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
        icons.put("Project", getIcon(PROJECT_ICON, r));
        icons.put("Analysis", getIcon(ANALYSIS_ICON, r));
        icons.put("Bowtie", getIcon(BOWTIE_ICON, r));
        icons.put("SMART", getIcon(SMART_ICON, r));
        
        try {
            helpAboutIcon = ResourceUtils.getImageIcon(HELP_ABOUT_ICON);
            pw.setIconImage(ResourceUtils.getImageIcon(SMART_ICON).getImage());
        }
        catch (Exception e) {
            log("Failed to load the help/about image resource", e);
        }
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
            log("Failed to load view: " + view.getClassName(), e);
            return null;
        }
        return view;
    }

    @Override
    public ImageIcon getHelpAboutIcon() {
        return helpAboutIcon;
    }
    
    private DefaultMutableTreeNode populateSystem(int id) {
        DefaultMutableTreeNode systemsNode = new DefaultMutableTreeNode("Systems");

        ArrayList<Persistable> list = metaFactory.getChildren("System", "ProjectID", id);
        if (list != null) {
            for (Persistable p : list) {
                if (!p.isDeleted()) {
                    uk.nhs.digital.safetycase.data.System sy = (uk.nhs.digital.safetycase.data.System) p;
                    int pid = Integer.parseInt(sy.getAttributeValue("ParentSystemID"));
                    if (pid == -1) {
                        systemsNode.add(populateSystemWithChildren(sy));
                    }
                }
            }
        }
        return systemsNode;
    }

    private DefaultMutableTreeNode populateSystemWithChildren(uk.nhs.digital.safetycase.data.System s) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(s.getTitle());
        DefaultMutableTreeNode sy = new DefaultMutableTreeNode(s.getTitle());
        sy.setUserObject(s);
        try {
            ArrayList<Relationship> funcs = s.getRelationships("SystemFunction");
            if ((funcs != null) && !funcs.isEmpty()) {
                for (Relationship r : funcs) {
                    if ((r.getManagementClass() != null) && r.getManagementClass().contentEquals("Diagram")) {
                        Persistable p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        DefaultMutableTreeNode subsysnode = new DefaultMutableTreeNode(p.getTitle());
                        subsysnode.setUserObject(p);
                        populateChildrenDependents(subsysnode, p);
                        sy.add(subsysnode);
                    }
                }
            }
            ArrayList<Relationship> subsystems = s.getRelationships("System");
            if ((subsystems != null) && !subsystems.isEmpty()) {
                for (Relationship r : subsystems) {
                    if ((r.getManagementClass() != null) && r.getManagementClass().contentEquals("Diagram")) {
                        Persistable p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        DefaultMutableTreeNode subsysnode = new DefaultMutableTreeNode(p.getTitle());
                        subsysnode.setUserObject(p);
                        populateChildrenDependents(subsysnode, p);
                        sy.add(subsysnode);
                    }
                }                
            }

        } catch (Exception e) {
            java.lang.System.err.println("Unprocessed Element : " + e.toString());
            log("Error displaying System child nodes", e);
        }
        sy.setUserObject(s);
        node.add(sy);
        // populateSystemDependents("SystemFunction", node, s);
        return node;
    }

    private void populateChildrenDependents(DefaultMutableTreeNode childnode, Persistable relatedObject)
            throws Exception {

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
                                DefaultMutableTreeNode gcnode = new DefaultMutableTreeNode(p.getDatabaseObjectName());
                                gcnode.setUserObject(p);
                                childnode.add(gcnode);
                                //  find any grand children realtions
                                populateChildrenDependents(gcnode, p);
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
                                DefaultMutableTreeNode gcnode = new DefaultMutableTreeNode(p.getDatabaseObjectName());
                                gcnode.setUserObject(p);
                                childnode.add(gcnode);
                                //  find any grand children realtions
                                populateChildrenDependents(gcnode, p);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log("Error building TreeNode instances for child nodes", e);
        }

    }

    @Override
    public synchronized void addNotificationSubscriber(DataNotificationSubscriber n) {
        if (!notificationSubscribers.contains(n)) {
            notificationSubscribers.add(n);
//            java.lang.System.out.println("Subscribed " + n.hashCode());
//        } else {
//            java.lang.System.out.println("Attempt to re-subscribe " + n.hashCode());
        }
    }

    @Override
    public synchronized void removeNotificationSubscriber(DataNotificationSubscriber n) {
        if (notificationSubscribers.contains(n)) {
            notificationSubscribers.remove(n);
//            java.lang.System.out.println("Unsubscribed " + n.hashCode());
//        } else {
//            java.lang.System.out.println("Attempt to unsubscribe already unsubscribed object " + n.hashCode());
        }
    }
    
}
