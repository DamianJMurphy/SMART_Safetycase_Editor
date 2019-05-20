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
package uk.nhs.digital.safetycase.ui.processeditor;

import java.awt.Component;
import uk.nhs.digital.safetycase.data.Process;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.ui.LinkEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.SaveRejectedException;
import uk.nhs.digital.projectuiframework.ui.UndockTabComponent;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFilter;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.ProjectLink;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;
import uk.nhs.digital.safetycase.ui.LinkExplorerTableCellRenderer;

/**
 *
 * @author damian
 */
public class SingleProcessEditor 
        extends javax.swing.JPanel 
        implements uk.nhs.digital.safetycase.ui.PersistableEditor
{

    private final String[] linkcolumns = {"Name", "Type", "Comment", "Via"};
    private final String[] hazColumns = {"Process Step", "Hazard ID", "Hazard Name", "Hazard Description", "Hazard Status"};
    private JDialog parent = null;
//    private SingleProcessEditorForm containerForm = null;
//    private ProcessEditor editor = null;
    private int processid = -1;
    private Process process = null;
    private int newObjectProjectId = -1;
    private EditorComponent editorComponent = null;
    private boolean modified = false;
    private DefaultTableCellRenderer hazrenderer = new HazardTableCellRenderer();
    
    /**
     * Creates new form SingleProcessEditorPanel
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public SingleProcessEditor() {
        initComponents();
        try {
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            linksTable.setDefaultEditor(Object.class, null);
            linksTable.setDefaultRenderer(Object.class, new LinkExplorerTableCellRenderer());
            linksTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
            linksTable.setModel(dtm);
            
            DefaultTableModel htm = new DefaultTableModel(hazColumns, 0);
            hazTable.setDefaultEditor(Object.class, null);
            hazTable.setDefaultRenderer(Object.class, hazrenderer);
            hazTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
            hazTable.setModel(htm);
               
        }
        catch (Exception e) {}
        descriptionTextArea.setFont(nameTextField.getFont());
        SmartProject.getProject().addNotificationSubscriber(this);
    }
        
    void setProcessId(int i) 
            throws Exception
    { 
        processid = i; 
        populateLinks();
    }
    
    SingleProcessEditor setParent(JDialog p) {
        parent = p;
        return this;
    }
    
//    SingleProcessEditor setParent(SingleProcessEditorForm p) {
//        containerForm = p;
//        return this;
//    }
    
//    SingleProcessEditor setEditor(ProcessEditor p) { 
//        editor = p; 
//        return this;
//    }
    
    void setData(String n, String v, String s, String d) {
        nameTextField.setText(n);
//        versionTextField.setText(v);
        descriptionTextArea.setText(d);        
    }
    
    void save() {
        saveButtonActionPerformed(null);
    }
    
    String getProcessName() { return nameTextField.getText(); }
    
    void populateLinks() 
//            throws Exception
    {
        if (processid == -1) {
            return;
        }
        try {
            if (process == null) {
                process = (Process) MetaFactory.getInstance().getFactory("Process").get(processid);
            }
            
//            HashMap<String,ArrayList<Relationship>> rels = hazard.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            ArrayList<ProjectLink> pls = new ArrayList<>();
            pls = MetaFactory.getInstance().exploreLinks(process, process, pls, false);
            for (ProjectLink pl : pls) {
                if (!directLinksOnlyCheckBox.isSelected() || (pl.getRemotePath().length() == 0)) {
                    Object[] row = new Object[linkcolumns.length];
                    for (int i = 0; i < linkcolumns.length; i++) {
                        row[i] = pl;
                    }
                    dtm.addRow(row);
                }
            }
             
            linksTable.setModel(dtm);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load Hazard relationshis for editing", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load hazard relationships", e);
        }
    }
    
    private void hazardSummary() {

        ArrayList<Persistable> hazards = null;
        if (processid == -1) {
            return;
        }       
        try {
            if (process == null) {
                process = (Process) MetaFactory.getInstance().getFactory("Process").get(processid);
            }
            
            ArrayList<Persistable> pSteps = null;
            DefaultTableModel htm = new DefaultTableModel(hazColumns, 0);
            
            pSteps = MetaFactory.getInstance().getChildren("ProcessStep", "ProcessID", process.getId());
            hazards = MetaFactory.getInstance().getChildren("Hazard", "ProjectID", SmartProject.getProject().getCurrentProjectID());
            
            if (pSteps == null) {
                    return;
                }                
            for (Persistable processstep: pSteps) {
                 ArrayList<Relationship> hazRels = processstep.getRelationships("Hazard");                 
                 if(hazRels != null) {
                     for (Relationship r: hazRels) {
                         if (!r.isDeleted()) {
                             try {                           
                            for (Persistable h : hazards) {                                
                                if (h.isDeleted()) {
                                    continue;  
                                }                                
                                if (r.getTarget() == h.getId()) {
                                    String hazId = Integer.toString(h.getId());
                                    Object[] row = {processstep.getAttribute("Name"),hazId ,h.getAttribute("Name"),h.getAttribute("Description"),h.getAttribute("Status")};
                                    htm.addRow(row);      
                                }               
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                       }
                     }
                 }                 
                 
                 hazTable.setModel(htm);
                }                            
            }   
            catch(Exception e) {
                 JOptionPane.showMessageDialog(this, "Failed to load Hazard Summary", "Load failed", JOptionPane.ERROR_MESSAGE);
                 SmartProject.getProject().log("Failed to load hazard summary", e);
            }         
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        commonToolBar = new javax.swing.JToolBar();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        saveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        processEditorButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        linksEditorButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        deleteButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        linksPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        directLinksOnlyCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        hazTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        commonToolBar.setBackground(new java.awt.Color(41, 156, 214));
        commonToolBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        commonToolBar.setRollover(true);
        commonToolBar.add(jSeparator5);

        saveButton.setText("Save");
        saveButton.setToolTipText("Click to Save");
        saveButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(saveButton);
        commonToolBar.add(jSeparator1);

        processEditorButton.setText("Process Editor...");
        processEditorButton.setToolTipText("Draw Care Process Diagram");
        processEditorButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        processEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processEditorButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(processEditorButton);
        commonToolBar.add(jSeparator2);

        linksEditorButton.setText("Links...");
        linksEditorButton.setToolTipText("Edit Links");
        linksEditorButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        linksEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linksEditorButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(linksEditorButton);
        commonToolBar.add(jSeparator3);

        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Delete Care Process");
        deleteButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(deleteButton);
        commonToolBar.add(jSeparator4);

        jPanel1.setBackground(new java.awt.Color(229, 239, 248));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel1.setText("Name");

        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nameTextFieldKeyTyped(evt);
            }
        });

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
        jScrollPane1.setViewportView(descriptionTextArea);

        linksPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Linked to"));
        linksPanel.setName(""); // NOI18N

        linksTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(linksTable);

        directLinksOnlyCheckBox.setSelected(true);
        directLinksOnlyCheckBox.setText("Show direct links only");
        directLinksOnlyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directLinksOnlyCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addContainerGap(500, Short.MAX_VALUE)
                .addComponent(directLinksOnlyCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane2)
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addComponent(directLinksOnlyCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Hazard Summary"));

        hazTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(hazTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(linksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameTextField))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(linksPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        linksPanel.getAccessibleContext().setAccessibleName("Linkes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(commonToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commonToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void setNewObjectProjectId(int i) {
        newObjectProjectId = i;
    }
    
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        try {
            String duplicateWarning = MetaFactory.getInstance().getDuplicateCheckMessage("Process", "Care process", nameTextField.getText(), SmartProject.getProject().getCurrentProjectID(), process);
            if (duplicateWarning != null) {
                JOptionPane.showMessageDialog(this, duplicateWarning, "Duplicate care process name", JOptionPane.ERROR_MESSAGE);
                saveButton.setEnabled(true);
                return;
            }
        } catch (Exception e) {
        }

        boolean create = false;
        if (process == null) {
            process = new Process();
            process.setAttribute("Name",nameTextField.getText());
            create = true;
        }
        process.setAttribute("Name",nameTextField.getText());
//        process.setAttribute("Version", versionTextField.getText());
        process.setAttribute("Source", "");
        process.setAttribute("Description", descriptionTextArea.getText());
//        if (newObjectProjectId == -1)
//            process.setAttribute("ProjectID", Integer.parseInt(process.getAttributeValue("ProjectID")));
//        else 
            process.setAttribute("ProjectID",SmartProject.getProject().getCurrentProjectID());
        try {
            MetaFactory.getInstance().getFactory(process.getDatabaseObjectName()).put(process);
            if (create) {
                SmartProject.getProject().editorEvent(Project.ADD, process);
            } else {
                SmartProject.getProject().editorEvent(Project.UPDATE, process);
            }
//            containerForm.setNewProcess(process);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save Process. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in SingleProcessEditor", e);
        }
        
        hazardSummary();      
        
        modified = false;
        String cos = System.getProperty("SMART.closeonsave");
        if ((cos != null) && (cos.contains("Process"))) {
           unsubscribe();
           SmartProject.getProject().getProjectWindow().closeContainer(this);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (process == null)
            return;

        int r = JOptionPane.showConfirmDialog(this, "Really delete this Care process ?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);        
        if (r != JOptionPane.OK_OPTION)
            return;
        
        try {
            MetaFactory.getInstance().getFactory("Process").delete(process);
            SmartProject.getProject().editorEvent(Project.DELETE, process);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to delete Process. Send logs to support", "Delete failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to delete in SingleProcessEditor", e);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void linksEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linksEditorButtonActionPerformed
        JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(this), true);
        linkEditor.add(new LinkEditor(process).setParent(linkEditor));
        linkEditor.pack();
        linkEditor.setVisible(true);
        
        try {
            populateLinks();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_linksEditorButtonActionPerformed

    private void processEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processEditorButtonActionPerformed

        if (process == null) {
            JOptionPane.showMessageDialog(this, "Save this Process first, before editing the process steps", "Save first", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
//        if (containerForm != null) {
            JPanel pnl = SmartProject.getProject().getExistingEditor(process, this);
            if (pnl != null) {
                SmartProject.getProject().getProjectWindow().selectPanel(pnl);
                return;
            }
//        }
        try {
            ProcessGraphEditor pge = new ProcessGraphEditor(process.getId());
            String xml = process.getAttributeValue("GraphXml");
            boolean newGraph = ((xml == null) || (xml.trim().length() == 0));
            pge.setProcessId(process.getId(), xml, newGraph);
            PersistableFactory<ProcessStep> pfs = MetaFactory.getInstance().getFactory("ProcessStep");
            ArrayList<PersistableFilter> filter = new ArrayList<>();
            filter.add(new PersistableFilter("ProjectID", process.getAttributeValue("ProjectID")));
            filter.add(new PersistableFilter("ProcessID", process.getAttributeValue("ProcessID")));
            Collection<ProcessStep> steps = pfs.getEntries(filter);
            HashMap<String, DiagramEditorElement> existingSteps = new HashMap<>();
            for (ProcessStep ps : steps) {
                existingSteps.put(ps.getAttributeValue("GraphCellId"), new DiagramEditorElement(ps));
            }
            pge.setExistingSteps(existingSteps);

            JTabbedPane tp = null;
            ProjectWindow pw = SmartProject.getProject().getProjectWindow();
            tp = pw.getMainWindowTabbedPane();
            EditorComponent ec = new EditorComponent(pge, process.getAttributeValue("Name"), SmartProject.getProject()); 
            tp.setSelectedComponent(tp.add(ec.getTitle(), ec.getComponent()));
            tp.setTabComponentAt(tp.getSelectedIndex(), new UndockTabComponent(tp, SmartProject.getProject().getIcon("Process")));              
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to build details for graphical Process editor. Send logs to support", "Warning", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to build details for graphical Process editor in SingleProcessEditor", e);
        }
    }//GEN-LAST:event_processEditorButtonActionPerformed

    private void nameTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameTextFieldKeyTyped
//       containerForm.setModified(true);
    }//GEN-LAST:event_nameTextFieldKeyTyped

    private void descriptionTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descriptionTextAreaKeyTyped
//        containerForm.setModified(true);
        modified = true;
    }//GEN-LAST:event_descriptionTextAreaKeyTyped

    private void directLinksOnlyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directLinksOnlyCheckBoxActionPerformed
        populateLinks();
    }//GEN-LAST:event_directLinksOnlyCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar commonToolBar;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JCheckBox directLinksOnlyCheckBox;
    private javax.swing.JTable hazTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JButton linksEditorButton;
    private javax.swing.JPanel linksPanel;
    private javax.swing.JTable linksTable;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton processEditorButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setPersistableObject(Persistable p) {
        if (p == null)
            return;
        process = (Process)p;
        processid = process.getId();
        setData(process.getAttributeValue("Name"), process.getAttributeValue("Version"), 
                    process.getAttributeValue("Source"), process.getAttributeValue("Description"));       
        populateLinks();
        hazardSummary();
    }

    @Override
    public void setEditorComponent(EditorComponent ed) {
        editorComponent = ed;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public boolean wantsScrollPane() {
        return false;
    }

    @Override
    public boolean notification(int evtype, Object o) throws SaveRejectedException {
        return false;
    }

    @Override
    public JPanel getEditor(Object o) {
        try {
            Process p = (Process)o;
            if (p == process)
                return this;
        }
        catch (Exception e) {}
        return null;
    }

    @Override
    public void unsubscribe() {
        SmartProject.getProject().removeNotificationSubscriber(this);
    }

    @Override
    public boolean isModified() {
       return modified;
    }

}
