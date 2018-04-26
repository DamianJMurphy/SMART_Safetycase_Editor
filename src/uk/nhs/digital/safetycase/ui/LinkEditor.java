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
package uk.nhs.digital.safetycase.ui;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.RelationshipSemantics;

/**
 *
 * @author damian
 */
public class LinkEditor extends javax.swing.JPanel {

    private Persistable focus = null;
    private final String[] columns = {"Type", "Name", "Comment"};
    private HashMap<String, ArrayList<Relationship>> relationships = null;
    private ArrayList<Relationship> tableMap = null;
//    private final String[] targets = {"Hazard","Control","Effect","System","Function", "Role","Care Setting", "Process", "Proces step"};
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final String[] dbtarget = {"Hazard","Control","Effect", "System","SystemFunction", "Role","Location","Process","ProcessStep"};
    private ArrayList<Persistable> targetInstances = null;
    private JDialog parent = null;
    private Relationship editedRelationship = null;
    private ArrayList<RelationshipSemantics> allowedRelationships = null;
    /**
     * Creates new form LinkEditor
     * @param p
     */
    public LinkEditor(Persistable p) {
        initComponents();
        focus = p;
        StringBuilder sb = new StringBuilder(p.getDisplayName());
        sb.append(": ");
        sb.append(p.getTitle());
        linkFromTextField.setText(sb.toString());
        DefaultTableModel dtm = new DefaultTableModel(columns, 0);
        tableMap = new ArrayList<>();
        relationships = focus.getRelationshipsForLoad();
        if (relationships != null) {
            for (String s : relationships.keySet()) {
                ArrayList<Relationship> a = relationships.get(s);
                if (a != null) {
                    for (Relationship r : a) {
                        if ((r.getManagementClass() != null) && (r.getManagementClass().contentEquals("Diagram")))
                            continue;
                        
                        tableMap.add(r);
                        Object[] row = new Object[columns.length];
                        for (int i = 0; i < row.length; i++)
                            row[i] = r;
                        dtm.addRow(row);
                    }
                }
            }
        }
        relationshipsTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        relationshipsTable.setModel(dtm);
        relationshipsTable.setDefaultEditor(Object.class, null);
        relationshipsTable.setDefaultRenderer(Object.class, new LinkTableCellRenderer());
        DefaultComboBoxModel targetTypes = new DefaultComboBoxModel();
        try {
            allowedRelationships = MetaFactory.getInstance().getDatabase().getAllowedRelationships(p.getDatabaseObjectName());
            if (allowedRelationships != null) {
                for (RelationshipSemantics rs : allowedRelationships) {
                    targetTypes.addElement(rs.getDisplayName());
                }
            }
            targetTypeComboBox.setModel(targetTypes);
        }
        catch (Exception e) {
            SmartProject.getProject().log("Failed to initialise LinkEditor", e);
        }
    }

    public LinkEditor setParent(JDialog p) {
        parent = p;
        return this;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        linkFromTextField = new javax.swing.JTextField();
        existingRelationshipsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        relationshipsTable = new javax.swing.JTable();
        newButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        linkDetailPanel = new javax.swing.JPanel();
        targetTypeComboBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        targetList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        commentTextArea = new javax.swing.JTextArea();
        discardButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        linkFromTextField.setEditable(false);
        linkFromTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Linked from"));

        existingRelationshipsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Existing links"));

        relationshipsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(relationshipsTable);

        newButton.setText("New");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout existingRelationshipsPanelLayout = new javax.swing.GroupLayout(existingRelationshipsPanel);
        existingRelationshipsPanel.setLayout(existingRelationshipsPanelLayout);
        existingRelationshipsPanelLayout.setHorizontalGroup(
            existingRelationshipsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(existingRelationshipsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(existingRelationshipsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(existingRelationshipsPanelLayout.createSequentialGroup()
                        .addComponent(newButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        existingRelationshipsPanelLayout.setVerticalGroup(
            existingRelationshipsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(existingRelationshipsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(existingRelationshipsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newButton)
                    .addComponent(editButton)
                    .addComponent(deleteButton))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        linkDetailPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        linkDetailPanel.setEnabled(false);

        targetTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hazard", "Control", "Effect", "Condition", "System", "Function", "Role", "Care Setting" }));
        targetTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetTypeComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Target type");

        jScrollPane2.setViewportView(targetList);

        jLabel2.setText("Comment");

        commentTextArea.setColumns(20);
        commentTextArea.setLineWrap(true);
        commentTextArea.setRows(5);
        commentTextArea.setWrapStyleWord(true);
        jScrollPane3.setViewportView(commentTextArea);

        discardButton.setText("Clear");
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout linkDetailPanelLayout = new javax.swing.GroupLayout(linkDetailPanel);
        linkDetailPanel.setLayout(linkDetailPanelLayout);
        linkDetailPanelLayout.setHorizontalGroup(
            linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linkDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(linkDetailPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(targetTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(linkDetailPanelLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(linkDetailPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)))
                    .addGroup(linkDetailPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(discardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        linkDetailPanelLayout.setVerticalGroup(
            linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linkDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addGroup(linkDetailPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(linkDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(discardButton)
                            .addComponent(saveButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(existingRelationshipsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(linkFromTextField)
                    .addComponent(linkDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(linkFromTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(existingRelationshipsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linkDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        parent.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void targetTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetTypeComboBoxActionPerformed
 
        int selected = targetTypeComboBox.getSelectedIndex();
        if (selected == -1)
            return;
        
        String dbtype = allowedRelationships.get(selected).getTargetType();
        try {
            int id = -1;
            if (focus.getDatabaseObjectName().contentEquals("ProcessStep")) {
                int procid = Integer.parseInt(focus.getAttributeValue("ProcessID")); 
                id = Integer.parseInt(MetaFactory.getInstance().getFactory("Process").get(procid).getAttributeValue("ProjectID"));
            } else {
                id = Integer.parseInt(focus.getAttributeValue("ProjectID"));
            }
            ArrayList<Persistable> targets = MetaFactory.getInstance().getChildren(dbtype, "ProjectID", id);
            if (targets == null) {
                DefaultListModel dlm = new DefaultListModel();
                targetList.setModel(dlm);
                return;
            }
            DefaultListModel dlm = new DefaultListModel();
            targetInstances = new ArrayList<>();
            for (Persistable p : targets) {
                if (!p.isDeleted()) {
                    dlm.addElement(p.getTitle());
                    targetInstances.add(p);
                }
            }
            targetList.setModel(dlm);
        }
        catch (Exception e) {
            SmartProject.getProject().log("Link editor failed to get target type in combo box", e);
        }
    }//GEN-LAST:event_targetTypeComboBoxActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        linkDetailPanel.setEnabled(true);
        relationshipsTable.clearSelection();
        discardButtonActionPerformed(null);
        editedRelationship = null;
    }//GEN-LAST:event_newButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        linkDetailPanel.setEnabled(true);
        int selected = relationshipsTable.getSelectedRow();
        if (selected == -1)
            return;
        editedRelationship = tableMap.get(selected);
        try {
            Persistable p = MetaFactory.getInstance().getFactory(editedRelationship.getTargetType()).get(editedRelationship.getTarget()); 
            if (p.isDeleted()) {
                StringBuilder sb = new StringBuilder("This link points to a ");
                sb.append(p.getDisplayName());
                sb.append(" that has been deleted, and is visible pending that being purged.\nIt cannot be edited unless the target ");
                sb.append(p.getDisplayName());
                sb.append(" is un-deleted.");
                JOptionPane.showMessageDialog(this, sb.toString(), "Deleted relationship", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred checking the link for editing", "Error checking link", JOptionPane.WARNING_MESSAGE);
            SmartProject.getProject().log("Error checking if relationship target is deleted", e);
            return;
        }
        targetTypeComboBox.setSelectedItem(editedRelationship.getTargetType());
        commentTextArea.setText(editedRelationship.getComment());
        try {
            int id = -1;
            if (focus.getDatabaseObjectName().contentEquals("ProcessStep")) {
                int procid = Integer.parseInt(focus.getAttributeValue("ProcessID")); 
                id = Integer.parseInt(MetaFactory.getInstance().getFactory("Process").get(procid).getAttributeValue("ProjectID"));
            } else {
                id = Integer.parseInt(focus.getAttributeValue("ProjectID"));
            }
            ArrayList<Persistable> targets = MetaFactory.getInstance().getChildren(editedRelationship.getTargetType(), "ProjectID", id);
            if (targets == null) 
                return;
            DefaultListModel dlm = new DefaultListModel();
            targetInstances = new ArrayList<>();
            int select = -1;
            int i = 0;
            for (Persistable p : targets) {
                if (!p.isDeleted()) {
                    targetInstances.add(p);
                    dlm.addElement(p.getTitle());
                    if (p.getId() == editedRelationship.getTarget())
                        select = i;
                    i++;                
                }
            }
            targetList.setModel(dlm);
            if (select != -1)
                targetList.setSelectedIndex(select);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to get link for editing. Send logs to support", "Edit failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in LinkEditor", e);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int selected = relationshipsTable.getSelectedRow();
        if (selected == -1)
            return;
        Relationship r = tableMap.get(selected);
        if (r.getManagementClass() != null) {
            JOptionPane.showMessageDialog(this, "This relationship is managed by the SMART application. It cannot be deleted manually.", "Not allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            editedRelationship = null;
            focus.deleteRelationship(r);
            MetaFactory.getInstance().getFactory(focus.getDatabaseObjectName()).put(focus);
            ((DefaultTableModel)relationshipsTable.getModel()).removeRow(selected);
            tableMap.remove(selected);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to delete link. Send logs to support", "Delete failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to delete in LinkEditor", e);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed
        targetList.setModel(new DefaultListModel());
        commentTextArea.setText("");
        linkDetailPanel.setEnabled(false);
        editedRelationship = null;
    }//GEN-LAST:event_discardButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        
        int selectedTarget = targetList.getSelectedIndex();
        if (selectedTarget == -1)
            return;
        Persistable target = targetInstances.get(selectedTarget);
        if (editedRelationship != null) {
            // Update the relationship (call Database.save()) and the relationships table view
            editedRelationship.setComment(commentTextArea.getText());
            ((DefaultTableModel)relationshipsTable.getModel()).setValueAt(commentTextArea.getText(), relationshipsTable.getSelectedRow(), 2);
            try {
                MetaFactory.getInstance().getDatabase().save(editedRelationship);
            }
            catch (Exception e) {
                e.printStackTrace();
            }            
        } else {
            Relationship r = new Relationship(focus.getId(), target.getId(), target.getDatabaseObjectName());
            r.setComment(commentTextArea.getText());
            try {
                focus.addRelationship(r);
                MetaFactory.getInstance().getFactory(focus.getDatabaseObjectName()).put(focus);
                Object[] row = new Object[columns.length];
                tableMap.add(r);
                for (int i = 0; i < columns.length; i++) {
                    row[i] = r;
                }
                ((DefaultTableModel)relationshipsTable.getModel()).addRow(row);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to save Link. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
                SmartProject.getProject().log("Failed to save in LinkEditor", e);
            }
        }
        editedRelationship = null;
        targetList.clearSelection();
    }//GEN-LAST:event_saveButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTextArea commentTextArea;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton discardButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel existingRelationshipsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel linkDetailPanel;
    private javax.swing.JTextField linkFromTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JTable relationshipsTable;
    private javax.swing.JButton saveButton;
    private javax.swing.JList<String> targetList;
    private javax.swing.JComboBox<String> targetTypeComboBox;
    // End of variables declaration//GEN-END:variables
}
