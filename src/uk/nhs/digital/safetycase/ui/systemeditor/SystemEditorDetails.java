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
package uk.nhs.digital.safetycase.ui.systemeditor;

import uk.nhs.digital.safetycase.ui.*;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.UndockTabComponent;
import uk.nhs.digital.safetycase.data.System;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.SystemFunction;

/**
 *
 * @author shul1
 */
public class SystemEditorDetails extends javax.swing.JPanel
        implements uk.nhs.digital.safetycase.ui.PersistableEditor 
{
    private static final String NEW_ROOT_SYSTEM_TEMPLATE = "/uk/nhs/digital/safetycase/ui/new_root_system_template.txt";
    private static String newRootSystemTemplate = null;
    
    private JDialog parent = null;
    private EditorComponent editorComponent = null;
    private ArrayList<System> systems = new ArrayList<>();
     private ArrayList<SystemFunction> systemfunctions = new ArrayList<>();
    private System system = null;
    private boolean modified = false;

    private final String[] functioncolumns = {"Name", "Description", "ParentFunction", "System"};

    /**
     * Creates new form SystemEditor
     */
     public SystemEditorDetails() {
        initComponents();
        loadNewTemplate();
        functionsTable.setDefaultEditor(Object.class, null);
        functionsTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        SmartProject.getProject().addNotificationSubscriber(this);

     }
    @Override
    public void unsubscribe() {
        SmartProject.getProject().removeNotificationSubscriber(this);
    }
    
    private void loadNewTemplate() {
        if (newRootSystemTemplate != null)
            return;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(NEW_ROOT_SYSTEM_TEMPLATE)));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            newRootSystemTemplate = sb.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public SystemEditorDetails(Persistable p) {
        initComponents();
        loadNewTemplate();
        functionsTable.setDefaultEditor(Object.class, null);
        DefaultTableModel dsftm = new DefaultTableModel(functioncolumns, 0);
        SmartProject.getProject().addNotificationSubscriber(this);
        String psname= "";
        if (p == null) {
            functionsTable.setModel(dsftm);
            return;
        }
        try {
            system = (System) p;
            functionsTable.setModel(dsftm);
            int psid = Integer.parseInt(system.getAttributeValue("ParentSystemID"));
            if(psid !=-1) {
                psname = MetaFactory.getInstance().getFactory("System").get(psid).getAttributeValue("Name"); 
            } else {
                populateSubsystemList();
            }
            nameTextField.setText(system.getAttributeValue("Name"));
            versionTextField.setText(system.getAttributeValue("Version"));
            descriptionTextArea.setText(system.getAttributeValue("Description"));
            mnemonicTextField.setText(system.getAttributeValue("Mnemonic"));
            parentSystemTextField.setText(psname);
            // populate function table for this particular system
            populateFunctionTable(system);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to load System for display. Send logs to support", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load in SystemEditorDetails", e);
        }
    }

    
    @Override
    public boolean wantsScrollPane() { return true; }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        linksPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        functionsTable = new javax.swing.JTable();
        functionEdit = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        discardButton = new javax.swing.JButton();
        mnemonicTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        parentSystemTextField = new javax.swing.JTextField();
        systemEditorButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        subsystemsList = new javax.swing.JList<>();
        versionTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        editorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Name");

        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nameTextFieldKeyTyped(evt);
            }
        });

        jLabel2.setText("Mnemonic");

        jLabel4.setText("Description");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                descriptionTextAreaKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(descriptionTextArea);

        linksPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Functions"));

        functionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(functionsTable);

        functionEdit.setText("Edit");
        functionEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                functionEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addComponent(functionEdit)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(functionEdit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        discardButton.setText("Delete");
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });

        mnemonicTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                mnemonicTextFieldKeyTyped(evt);
            }
        });

        jLabel3.setText("Parent system");

        parentSystemTextField.setEditable(false);

        systemEditorButton.setText("System and function editor");
        systemEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                systemEditorButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Subsystems"));

        jScrollPane1.setViewportView(subsystemsList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        versionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                versionTextFieldKeyTyped(evt);
            }
        });

        jLabel5.setText("Version");

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(linksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(parentSystemTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(nameTextField)
                            .addComponent(versionTextField)))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(systemEditorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(discardButton))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(44, 44, 44)
                        .addComponent(mnemonicTextField)))
                .addContainerGap())
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(parentSystemTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(mnemonicTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(discardButton)
                    .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(saveButton)
                        .addComponent(systemEditorButton)))
                .addGap(18, 18, 18)
                .addComponent(linksPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed

        if (system == null)
            return;
        
         int r = JOptionPane.showConfirmDialog(this, "Really delete this System ?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);        
        if (r == JOptionPane.CANCEL_OPTION)
            return;
       
        try {
            MetaFactory.getInstance().getFactory("System").delete(system);
            SmartProject.getProject().editorEvent(Project.DELETE, system);
            SmartProject.getProject().getProjectWindow().closeContainer(this);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to delete System. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to delete in SystemEditorDetails", e);
        }


//        nameTextField.setText("");
//        parentSystemTextField.setText("");
//        mnemonicTextField.setText("");
//        descriptionTextArea.setText(""); 
    }//GEN-LAST:event_discardButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
//        if(system ==null){
//            JOptionPane.showMessageDialog(this, "Create System using Bowtie", "Create first", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
        boolean created = false;
        if (system == null) {
            try {
                String duplicateWarning = MetaFactory.getInstance().getDuplicateCheckMessage("System", "System", nameTextField.getText(),SmartProject.getProject().getCurrentProjectID(), null);
                if (duplicateWarning != null) {
                    JOptionPane.showMessageDialog(this, duplicateWarning, "Duplicate system name", JOptionPane.ERROR_MESSAGE);
                    saveButton.setEnabled(true);
                    return;
                }
            }
            catch (Exception e) {}
            system = new System();
            system.setAttribute("Name",nameTextField.getText());
            created = true;
        }
        system.setAttribute("Description", descriptionTextArea.getText());
        system.setAttribute("Version", versionTextField.getText());
        system.setAttribute("Mnemonic", mnemonicTextField.getText());
        system.setAttribute("ProjectID", SmartProject.getProject().getCurrentProjectID());
        try {
            MetaFactory.getInstance().getFactory(system.getDatabaseObjectName()).put(system);
            // TODO: Don't bother trying to update the tree if this is a subsystem
            //
            if (system.getAttribute("ParentSystemID").getIntValue() == -1) {
                if (created)
                    SmartProject.getProject().editorEvent(Project.ADD, system);
                else
                    SmartProject.getProject().editorEvent(Project.UPDATE, system);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to save System. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in SystemEditorDetails", e);            
        }
        modified = false;
    }//GEN-LAST:event_saveButtonActionPerformed

    private void functionEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_functionEditActionPerformed
        int selected = functionsTable.getSelectedRow();
        if (selected == -1 ) {
            return;
        }
        try {
            SystemFunction sf = systemfunctions.get(selected);
            
            JPanel pnl = SmartProject.getProject().getExistingEditor(sf, this);
            if (pnl != null) {
                SmartProject.getProject().getProjectWindow().selectPanel(pnl);
                return;
            }
            
            SystemFunctionEditor sfe = new SystemFunctionEditor();
            sfe.setPersistableObject(sf);
            ExternalEditorView systemView = new ExternalEditorView(sfe, "Function:" + sf.getAttributeValue("Name"), SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());
//            JDialog singleFunctionEditor = new JDialog(JOptionPane.getFrameForComponent(this), "System Function ", true);
//            singleFunctionEditor.add(new SystemFunctionEditorPanel(sf).setParent(singleFunctionEditor));
//            singleFunctionEditor.pack();
//            singleFunctionEditor.setVisible(true);
//            populateFunctionTable(system);
//            singleFunctionEditor.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to load Function. Send logs to support", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load function in SystemEditorDetails", e);
        }
    }//GEN-LAST:event_functionEditActionPerformed

    private void systemEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_systemEditorButtonActionPerformed
        if (system == null) {
            JOptionPane.showMessageDialog(this, "Save this system first, before using the graphical editor", "Save first", JOptionPane.INFORMATION_MESSAGE);
            
            return;
        }       
       
        JPanel pnl = SmartProject.getProject().getExistingEditor(system, this);
        if (pnl != null) {
            SmartProject.getProject().getProjectWindow().selectPanel(pnl);
            return;
        }
        
        if (system.getAttribute("ParentSystemID").getIntValue() != -1) {
            try {
                String pname = MetaFactory.getInstance().getFactory("System").get(system.getAttribute("ParentSystemID").getIntValue()).getTitle();
                JOptionPane.showMessageDialog(this, system.getTitle() + " is a subsystem, edit the diagram from the " + pname + " system instead", "Cannot edit subsystems", JOptionPane.INFORMATION_MESSAGE);                                
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, system.getTitle() + " is a subsystem, edit the diagram from the root system instead", "Cannot edit subsystems", JOptionPane.INFORMATION_MESSAGE);                
            }
            return;
            
        }
//        SystemEditor sge = new SystemEditor();
        SystemGraphEditor sge = new SystemGraphEditor(system.getId());
        String xml = system.getAttributeValue("GraphXml");
        if ((xml == null) || (xml.trim().length() == 0)) {
            StringBuilder sb = new StringBuilder(newRootSystemTemplate);
            int start = newRootSystemTemplate.indexOf("__SYSTEM_NAME__"); 
            sb.replace(start, start + "__SYSTEM_NAME__".length(), nameTextField.getText());
            xml = sb.toString();     
            system.setAttribute("GraphCellId", "2");
        }
//        sge.setExistingGraph(getExistingGraph(xml));
        sge.setExistingGraph(getExistingGraph(system, null));
        sge.setSystemId(system.getId(), xml);
//        sge.setPersistableObject(system);
        JTabbedPane tp = null;
        ProjectWindow pw = SmartProject.getProject().getProjectWindow();
        tp = pw.getMainWindowTabbedPane();
        EditorComponent ec = new EditorComponent(sge, system.getAttributeValue("Name"), SmartProject.getProject());
        tp.setSelectedComponent(tp.add(ec.getTitle(), ec.getComponent()));
        tp.setTabComponentAt(tp.getSelectedIndex(), new UndockTabComponent(tp, SmartProject.getProject().getIcon("System")));  
    }//GEN-LAST:event_systemEditorButtonActionPerformed

    private void nameTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameTextFieldKeyTyped
        modified = true;
    }//GEN-LAST:event_nameTextFieldKeyTyped

    private void versionTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_versionTextFieldKeyTyped
        modified = true;
    }//GEN-LAST:event_versionTextFieldKeyTyped

    private void mnemonicTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mnemonicTextFieldKeyTyped
        modified = true;
    }//GEN-LAST:event_mnemonicTextFieldKeyTyped

    private void descriptionTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descriptionTextAreaKeyTyped
        modified = true;
    }//GEN-LAST:event_descriptionTextAreaKeyTyped

    public void setSystem(System s) { system = s; }
    
    public HashMap<String,DiagramEditorElement> getExistingGraph(Persistable p, HashMap<String,DiagramEditorElement> elems)
    {
        HashMap<String,DiagramEditorElement> systemElements = elems;
        if (systemElements == null) 
            systemElements = new HashMap<>();
        systemElements.put(p.getAttributeValue("GraphCellId"), new DiagramEditorElement(p));
       
        HashMap<String, ArrayList<Relationship>> hrels = p.getRelationshipsForLoad();
        for (ArrayList<Relationship> a : hrels.values()) {
            for (Relationship r : a) {
                if ((r.getManagementClass() != null) && (r.getManagementClass().contentEquals("Diagram"))) {
                    Persistable child = null;
                    try {
                        child = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                    }
                    catch (Exception e) {
                        SmartProject.getProject().log("Error retrieving original diagram", e);
                    }
                    systemElements.put(child.getAttributeValue("GraphCellId"), new DiagramEditorElement(child));
                    systemElements = getExistingGraph(child, systemElements);
                }
            }
        }
        return systemElements;
    }
    
    public HashMap<String,DiagramEditorElement> getExistingGraph(String xml) {

        // TODO: Build the existing graph from the database relationships, *RECURSIVELY*
        // so that everything gets picked up. Do root system... then call for system
        // functions and then call for systems, then for systems call systems again
        
/*
        if ((xml == null) || (xml.length() == 0))
            return null;
        try { 
            HashMap<String, DiagramEditorElement> systemElements = new HashMap<>();
            
            systemElements.put(system.getAttributeValue("GraphCellId"), new DiagramEditorElement(system));
            // load sub system and functions for the same system

            //  ArrayList<Relationship> sRels = system.getRelationships("System");
            //ArrayList<Relationship> sfRels = system.getRelationshipsForClass("SystemFunction");
            HashMap<String, ArrayList<Relationship>> hrels = system.getRelationshipsForLoad();
            if (hrels == null) {
                return null;
            }
            // to do. check the relation of child systems and system fucntions
            
            // FIXME: This isn't properly recursive and is missing sub-sub systems
            
            for (ArrayList<Relationship> a : hrels.values()) {
                for (Relationship r : a) {
                    if ((r.getManagementClass() != null) && (r.getManagementClass().contentEquals("Diagram"))) {
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
                    if (bt == null) {
                        java.lang.System.err.println("Error in diagram XML: " + src + " node not found");
                    } else {
                        bt.connections.add(t);
                    }
                }
            }
            return systemElements;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to load System/Function diagram. Send logs to support", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load System/Function diagram in system editor details", e);
        }
*/
        return null;
    }
    
    private void populateSubsystemList()
            throws Exception
    {
        DefaultListModel dlm = new DefaultListModel();
        subsystemsList.setModel(dlm);
        if (system == null)
            return;
        HashMap<String, ArrayList<Relationship>> hrels = system.getRelationshipsForLoad();
        Persistable p = null;
        if (hrels != null) {
            for (ArrayList<Relationship> a : hrels.values()) {
                for (Relationship r : a) {
                    if (r.getTargetType().contentEquals("System")) {
                        p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        if (p.isDeleted())
                            continue;
                        dlm.addElement(p.getTitle());
                    }
                }
            }
        }
        subsystemsList.setModel(dlm);
    }
    
    private void populateFunctionTable(System s) throws Exception {
        systemfunctions = new ArrayList<>();
        DefaultTableModel dtm = new DefaultTableModel(functioncolumns, 0);
        PersistableFactory<System> pfs = MetaFactory.getInstance().getFactory("System");
        PersistableFactory<SystemFunction> pfsf = MetaFactory.getInstance().getFactory("SystemFunction");
        Persistable p = null;
        try {
            System sys = pfs.get(s.getId());
            HashMap<String, ArrayList<Relationship>> hrels = sys.getRelationshipsForLoad();
            if (hrels != null) {
                for (ArrayList<Relationship> a : hrels.values()) {
                    for (Relationship r : a) {
                        if (r.getTargetType().equals("SystemFunction")) {
                            p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                            // if (p.getDatabaseObjectName().equals("SystemFunction")) {
                            SystemFunction sf = (SystemFunction) p;
                            if (sf.isDeleted()) {
                                continue;
                            }
                            systemfunctions.add(sf);
                            String name = sf.getTitle();
                            String description = sf.getAttributeValue("Description");
                            String id = Integer.toString(sf.getId());
                            String parentSystmFunction = "None";
                            int parentSystmFunctionID = Integer.parseInt(sf.getAttributeValue("ParentSystemFunctionID"));
                            if (parentSystmFunctionID != -1) {
                                SystemFunction sfunction = pfsf.get(parentSystmFunctionID);
                                parentSystmFunction = sfunction.getTitle();
                            }
                            dtm.addRow(new Object[]{name, description, parentSystmFunction, sys.getTitle(), id});
                            List<Persistable> pa = new ArrayList<>();
                            pa = findchildrelations(p, pa);
                            for (Persistable cp : pa) {
                                SystemFunction csf = (SystemFunction) cp;
                                if (csf.isDeleted()) {
                                    continue;
                                }
                                systemfunctions.add(csf);
                                String cname = csf.getTitle();
                                String cdescription = csf.getAttributeValue("Description");
                                String cparentSystmFunction = "No Parent Function";
                                int cparentSystmFunctionID = Integer.parseInt(csf.getAttributeValue("ParentSystemFunctionID"));
                                if (cparentSystmFunctionID != -1) {
                                    SystemFunction sfunction = pfsf.get(cparentSystmFunctionID);
                                    cparentSystmFunction = sfunction.getTitle();
                                }
                                dtm.addRow(new Object[]{cname, cdescription, cparentSystmFunction, sys.getTitle(), id});
                            }

                            //  }
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to load function list. Send logs to support", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load function list in SystemEditorDetails", e);
        }
        this.functionsTable.setModel(dtm);
//        HideTableColumnsMethod();
    }

    private List<Persistable> findchildrelations(Persistable relatedObject, List<Persistable> persistablelist)
            throws Exception {
        List<Persistable> lp = persistablelist;
        PersistableFactory<uk.nhs.digital.safetycase.data.System> pfs = MetaFactory.getInstance().getFactory("System");
        PersistableFactory<uk.nhs.digital.safetycase.data.SystemFunction> pfsf = MetaFactory.getInstance().getFactory("SystemFunction");
        Persistable p;
        try {
            if (relatedObject.getDatabaseObjectName().equals("System")) {

                uk.nhs.digital.safetycase.data.System sys = pfs.get(relatedObject.getId());
                HashMap<String, ArrayList<Relationship>> hrels = sys.getRelationshipsForLoad();
                if (hrels != null) {
                    for (ArrayList<Relationship> a : hrels.values()) {
                        for (Relationship r : a) {
                            if (r.getTargetType().equals("SystemFunction")) {
                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                                lp.add(p);
                                //  find any sub children realtions
                                findchildrelations(p, lp);
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
                            if (r.getTargetType().equals("SystemFunction")) {
                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                                lp.add(p);
                                // find any sub children realtions
                                findchildrelations(p, lp);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to load child system/function list. Send logs to support", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load child function list in SystemEditorDetails", e);
        }
        return lp;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton discardButton;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JButton functionEdit;
    private javax.swing.JTable functionsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel linksPanel;
    private javax.swing.JTextField mnemonicTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField parentSystemTextField;
    private javax.swing.JButton saveButton;
    private javax.swing.JList<String> subsystemsList;
    private javax.swing.JButton systemEditorButton;
    private javax.swing.JTextField versionTextField;
    // End of variables declaration//GEN-END:variables

     SystemEditorDetails setParent(JDialog p) {
        parent = p;
        return this;
    }
    // <editor-fold defaultstate="collapsed" desc="Unused Code"> 
    @Override
    public void setPersistableObject(Persistable p) {
        DefaultTableModel dsftm = new DefaultTableModel(functioncolumns, 0);
        String psname= "";
        if (p == null) {
            functionsTable.setModel(dsftm); // add code to load new  system graph editor.
            return;
        }
        try {
            system = (System) p;
            functionsTable.setModel(dsftm);
            int psid = Integer.parseInt(system.getAttributeValue("ParentSystemID"));
            if(psid !=-1)
                psname = MetaFactory.getInstance().getFactory("System").get(psid).getAttributeValue("Name"); 
            else
                populateSubsystemList();
            nameTextField.setText(system.getAttributeValue("Name"));
            descriptionTextArea.setText(system.getAttributeValue("Description"));
            mnemonicTextField.setText(system.getAttributeValue("Mnemonic"));
            versionTextField.setText(system.getAttributeValue("Version"));
            parentSystemTextField.setText(psname);
            // populate function table for this particular system
            populateFunctionTable(system);
            String xml = system.getAttributeValue("GraphXml");
            if (xml != null)
                nameTextField.setEditable(false);
//            if ((xml == null) || (xml.trim().length() == 0))
//                systemEditorButton.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to load System for editing", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to set persistable object in SystemEditorDetails", e);
        }
//         HideTableColumnsMethod();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void setEditorComponent(EditorComponent ed) {
        editorComponent = ed;
    }

    @Override
    public void setNewObjectProjectId(int i) {
        
    }
    //</editor-fold>

    @Override
    @SuppressWarnings("null")
    public boolean notification(int evtype, Object o) {
        
        if (evtype == Project.SAVE) {
            saveButtonActionPerformed(null);
            return false;
        }
        if (o == null)
            return false;
        uk.nhs.digital.safetycase.data.System c = null;
        if (o instanceof uk.nhs.digital.safetycase.data.System)
            c = (uk.nhs.digital.safetycase.data.System)o;
        setPersistableObject(c);
//            nameTextField.setText(c.getAttributeValue("Name"));
//            SmartProject.getProject().getProjectWindow().setViewTitle(this, "System:" + c.getTitle());
        return false;
    }
    
   @Override
    public JPanel getEditor(Object o) {
        try {            
            uk.nhs.digital.safetycase.data.System c = (uk.nhs.digital.safetycase.data.System)o;
            if (c.getTitle().equals(system.getTitle()))
                return this;
        }
        catch (Exception e) {}
        return null;
    }    

    @Override
    public boolean isModified() {
        return modified;
    }
    
}
