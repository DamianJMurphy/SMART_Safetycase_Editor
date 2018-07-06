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
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.UndockTabComponent;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.PersistableFilter;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.ProjectLink;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;
import uk.nhs.digital.safetycase.ui.LinkExplorerTableCellRenderer;
import uk.nhs.digital.safetycase.ui.LinkTableCellRenderer;

/**
 *
 * @author damian
 */
public class SingleProcessEditorPanel 
        extends javax.swing.JPanel 
{

    private final String[] linkcolumns = {"Type", "Name", "Comment", "Via"};
    private JDialog parent = null;
    private SingleProcessEditorForm containerForm = null;
//    private ProcessEditor editor = null;
    private int processid = -1;
    private Process process = null;
    private int newObjectProjectId = -1;
    /**
     * Creates new form SingleProcessEditorPanel
     */
    public SingleProcessEditorPanel() {
        initComponents();
        try {
            linksTable.setDefaultEditor(Object.class, null);
            linksTable.setDefaultRenderer(Object.class, new LinkExplorerTableCellRenderer());
            linksTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        }
        catch (Exception e) {}
    }
    
    void setProcessId(int i) 
            throws Exception
    { 
        processid = i; 
        populateLinks();
    }
    
    SingleProcessEditorPanel setParent(JDialog p) {
        parent = p;
        return this;
    }
    
    SingleProcessEditorPanel setParent(SingleProcessEditorForm p) {
        containerForm = p;
        return this;
    }
    
//    SingleProcessEditorPanel setEditor(ProcessEditor p) { 
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
            throws Exception
    {
        if (processid == -1) {
            return;
        }
        if (process == null) {
            process = (Process) MetaFactory.getInstance().getFactory("Process").get(processid);
        }
        try {
            
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        deleteButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        linksPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        linksEditorButton = new javax.swing.JButton();
        directLinksOnlyCheckBox = new javax.swing.JCheckBox();
        processEditorButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        linksPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Links"));

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

        linksEditorButton.setText("Links...");
        linksEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linksEditorButtonActionPerformed(evt);
            }
        });

        directLinksOnlyCheckBox.setSelected(true);
        directLinksOnlyCheckBox.setText("Show direct links only");

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, linksPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(linksEditorButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(directLinksOnlyCheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(directLinksOnlyCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linksEditorButton)
                .addContainerGap())
        );

        processEditorButton.setText("Process Editor...");
        processEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processEditorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(53, 53, 53)
                        .addComponent(nameTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(248, 248, 248)
                        .addComponent(processEditorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteButton))
                    .addComponent(linksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(saveButton)
                        .addComponent(deleteButton))
                    .addComponent(processEditorButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(linksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    void setNewObjectProjectId(int i) {
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
            containerForm.setNewProcess(process);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save Process. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in SingleProcessEditor", e);
        }
        containerForm.setModified(false);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (process == null)
            return;

        int r = JOptionPane.showConfirmDialog(this, "Really delete this Care process ?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);        
        if (r == JOptionPane.CANCEL_OPTION)
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
            HashMap<String,ArrayList<Relationship>> rels = process.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                if (a != null) {
                    for (Relationship r : a) {
//                    String[] row = new String[linkcolumns.length];
//                    Persistable tgt = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
//                    row[0] = tgt.getDisplayName();
//                    row[1] = tgt.getAttributeValue("Name");
//                    row[2] = r.getComment();
                        Object[] row = new Object[linkcolumns.length];
                        for (int i = 0; i < linkcolumns.length; i++)
                            row[i] = r;
                        dtm.addRow(row);
                    }
                }
            }
            linksTable.setModel(dtm);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load existing links for Process. Send logs to support", "Display failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load existing links in SingleProcessEditor", e);
        }

    }//GEN-LAST:event_linksEditorButtonActionPerformed

    private void processEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processEditorButtonActionPerformed

        if (process == null) {
            JOptionPane.showMessageDialog(this, "Save this Process first, before editing the process steps", "Save first", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (containerForm != null) {
            JPanel pnl = SmartProject.getProject().getExistingEditor(process, containerForm);
            if (pnl != null) {
                SmartProject.getProject().getProjectWindow().selectPanel(pnl);
                return;
            }
        }
        try {
            ProcessGraphEditor pge = new ProcessGraphEditor(process.getId());
            String xml = process.getAttributeValue("GraphXml");
            pge.setProcessId(process.getId(), xml);
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
       containerForm.setModified(true);
    }//GEN-LAST:event_nameTextFieldKeyTyped

    private void descriptionTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descriptionTextAreaKeyTyped
        containerForm.setModified(true);
    }//GEN-LAST:event_descriptionTextAreaKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JCheckBox directLinksOnlyCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton linksEditorButton;
    private javax.swing.JPanel linksPanel;
    private javax.swing.JTable linksTable;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton processEditorButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

}
