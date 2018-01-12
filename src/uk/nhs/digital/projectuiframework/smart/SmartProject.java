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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.*;

/**
 *
 * @author damian
 */
public class SmartProject 
        implements uk.nhs.digital.projectuiframework.Project
{
    private DefaultMutableTreeNode root = null;
    private static final String[] PROJECTCOMPONENTS = {"Process", "Hazard", "Cause", "Effect", "Control", "Care Settings", "Role", "Report"};
    private static final String[] PROJECTEDITORS = {"Process", "Hazard", "Cause", "Effect", "Control", "Location", "Role", "Report"};
    private static final String PROJECTNAME = "SMART";
    private MetaFactory metaFactory = null;
    private int currentProjectId = -1;
    
    private static final String EDITORCLASSROOT = "uk.nhs.digital.safetycase.ui.";
    private DefaultTreeModel treeModel = null;
    
    private static SmartProject project = null;
    
    public SmartProject()
            throws Exception
    {
        metaFactory = MetaFactory.getInstance();
        metaFactory.initialise();
        project = this;
    }
 
    public static final SmartProject getProject() { return project; }
    
    private EditorComponent resolveNonContainedComponent(TreePath t) {
        
        Project p = null;
        DefaultMutableTreeNode d = (DefaultMutableTreeNode)t.getLastPathComponent();
        if (!d.isLeaf()) {
            return null;
        }
        String s = null;
        String eclass = null;
        try {
            s = (String)d.getUserObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (s == null) {
            java.lang.System.err.println("DEVELOPMENT: Could not identify where user clicked");
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
        }
        EditorComponent ec = null;
        eclass = java.lang.System.getProperty(EDITORCLASSROOT + eclass);
        try {
            uk.nhs.digital.safetycase.ui.PersistableEditor pe = (uk.nhs.digital.safetycase.ui.PersistableEditor)Class.forName(eclass).newInstance();
            ec = new EditorComponent(pe.getComponent(), s, this);
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
        Persistable p = null;
        try {
            // TODO NEXT: This will only work for leaf nodes that contain Persistable instances.
            // That isn't true for a "Project" (which may be OK) or for a case where there are
            // no instances of the Persistable of that type. So detect this and instantiate an
            // editor with no content to the EditorComponent which should do the same thing as
            // if the "New" button had been clicked.
            p = (Persistable)((DefaultMutableTreeNode)t.getLastPathComponent()).getUserObject();
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
            ec = new EditorComponent(pe.getComponent(), p.getTitle(), this);
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
            for (String s : PROJECTCOMPONENTS) {
                populateProjectComponent(s, p, proj.getId());
            }
            DefaultMutableTreeNode issuesNode = new DefaultMutableTreeNode("Issues (not implemented for demonstration)");
            p.add(issuesNode);
            // TO DO: Data quality check reports and issues need to be added here, but the
            // users need some understanding of this first, and we might not do it properly
            // for the demo
            projectsNode.add(p);
        }
        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("SMART"));
        ((DefaultMutableTreeNode)treeModel.getRoot()).add(root);
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
                Persistable p = (Persistable)node.getUserObject();
                String id = p.getAttributeValue("ProjectID");
                if (id != null)
                    return Integer.parseInt(id);
                node = (DefaultMutableTreeNode)node.getParent();
            }
        }
        catch (Exception e) {
            return -1;
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
        }else {
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
        
        // TODO: On add, inserting first breaks the "select index in the existing table" (though not for existing entries). FIX.
        switch (ev) {
            case uk.nhs.digital.projectuiframework.Project.ADD:
                DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(p.getTitle());
                dmtn.setUserObject(o);
                treeModel.insertNodeInto(dmtn, containerNode, containerNode.getChildCount());
                if (p.getDatabaseObjectName().contentEquals("Project"))
                    fillOutNewProject(dmtn);
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
                for (int i = 0; i < containerNode.getChildCount(); i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)containerNode.getChildAt(i);
                    Persistable pr = (Persistable)node.getUserObject();
                    if (pr.getTitle().contentEquals(p.getTitle()) && (pr.getId() == p.getId())) {
                        treeModel.removeNodeFromParent(node);
                        DefaultMutableTreeNode d = new DefaultMutableTreeNode(p.getTitle());
                        d.setUserObject(p);
                        treeModel.insertNodeInto(d, containerNode, i);
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
}
