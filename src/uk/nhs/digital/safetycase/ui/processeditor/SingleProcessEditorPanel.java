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
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.UndockTabComponent;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFilter;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;

/**
 *
 * @author damian
 */
public class SingleProcessEditorPanel extends javax.swing.JPanel {

    private final String[] linkcolumns = {"Type", "Name", "Comment"};
    private JDialog parent = null;
//    private ProcessEditor editor = null;
    private int processid = -1;
    private Process process = null;
    private int newObjectProjectId = -1;
    /**
     * Creates new form SingleProcessEditorPanel
     */
    public SingleProcessEditorPanel() {
        initComponents();
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
    
    
//    SingleProcessEditorPanel setEditor(ProcessEditor p) { 
//        editor = p; 
//        return this;
//    }
    
    void setData(String n, String v, String s, String d) {
        nameTextField.setText(n);
        versionTextField.setText(v);
        sourceTextField.setText(s);
        descriptionTextArea.setText(d);        
    }
    
    void save() {
        saveButtonActionPerformed(null);
    }
    
    void populateLinks() 
            throws Exception
    {
        if (processid == -1)
            return;
        if (process == null)
            process = (Process)MetaFactory.getInstance().getFactory("Process").get(processid);
            HashMap<String,ArrayList<Relationship>> rels = process.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            if (rels != null) {
                for (String t : rels.keySet()) {
                    ArrayList<Relationship> a = rels.get(t);
                    for (Relationship r : a) {
                        String m = r.getManagementClass();
                        if ((m == null) || (!m.contentEquals("Diagram"))) {                    
                            String[] row = new String[linkcolumns.length];
                            Persistable tgt = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget()); 
                            row[0] = tgt.getDisplayName();
                            row[1] = tgt.getAttributeValue("Name");
                            row[2] = r.getComment();
                            dtm.addRow(row);
                        }
                    }
                }
            }
            linksTable.setModel(dtm);        
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
        jLabel2 = new javax.swing.JLabel();
        versionTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        sourceTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        deleteButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        linksPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        linksEditorButton = new javax.swing.JButton();
        processEditorButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Name");

        jLabel2.setText("Version");

        jLabel3.setText("Source");

        jLabel4.setText("Description");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setRows(5);
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

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, linksPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(linksEditorButton)
                .addContainerGap())
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linksEditorButton)
                .addGap(0, 12, Short.MAX_VALUE))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(versionTextField)
                                .addGap(313, 313, 313))
                            .addComponent(nameTextField)
                            .addComponent(sourceTextField)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(versionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(sourceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(saveButton)
                        .addComponent(deleteButton))
                    .addComponent(processEditorButton))
                .addGap(18, 18, 18)
                .addComponent(linksPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    void setNewObjectProjectId(int i) {
        newObjectProjectId = i;
    }
    
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        boolean create = false;
        if (process == null) {
            process = new Process();
            process.setAttribute("Name",nameTextField.getText());
            create = true;
        }
        process.setAttribute("Name",nameTextField.getText());
        process.setAttribute("Version", versionTextField.getText());
        process.setAttribute("Source", sourceTextField.getText());
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
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
            e.printStackTrace();
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
                for (Relationship r : a) {
                    String[] row = new String[linkcolumns.length];
                    Persistable tgt = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                    row[0] = tgt.getDisplayName();
                    row[1] = tgt.getAttributeValue("Name");
                    row[2] = r.getComment();
                    dtm.addRow(row);
                }
            }
            linksTable.setModel(dtm);
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
        try {
            ProcessGraphEditor pge = new ProcessGraphEditor();
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
            EditorComponent ec = new EditorComponent(pge, "Process:" + process.getAttributeValue("Name"), SmartProject.getProject()); 
            tp.setSelectedComponent(tp.add(ec.getTitle(), ec.getComponent()));
            tp.setTabComponentAt(tp.getSelectedIndex(), new UndockTabComponent(tp));              
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_processEditorButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton linksEditorButton;
    private javax.swing.JPanel linksPanel;
    private javax.swing.JTable linksTable;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton processEditorButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField sourceTextField;
    private javax.swing.JTextField versionTextField;
    // End of variables declaration//GEN-END:variables
}
