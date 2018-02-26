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

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.Cause;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.Relationship;

/**
 *
 * @author sharif
 */
public class CauseEditor extends javax.swing.JPanel 
    implements uk.nhs.digital.safetycase.ui.PersistableEditor
{
    private EditorComponent editorComponent = null;
    private Cause cause = null;
    
 
    private final String[] linkcolumns = {"Type", "Name", "Comment"};
    private int newObjectProjectId = -1;
    /**
     * Creates new form HazardEditor
     */
    public CauseEditor() {
        initComponents();
        DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
        SmartProject.getProject().addNotificationSubscriber(this);
        linksTable.setModel(dtm);
        try {
            ArrayList<String> conds = MetaFactory.getInstance().getFactory("Cause").getDistinctSet("GroupingType");
            if (conds.isEmpty()) {
                conditionsComboBox.addItem("Generic");
            } else {
                for (String s : conds) {
                    conditionsComboBox.addItem(s);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SmartProject.getProject().addNotificationSubscriber(this);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        SmartProject.getProject().removeNotificationSubscriber(this);
    }
    
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
        conditionsComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        linksPanel = new javax.swing.JPanel();
        editLinksButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        saveButton = new javax.swing.JButton();

        editorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Name");

        jLabel2.setText("Condition");

        conditionsComboBox.setEditable(true);

        jLabel4.setText("Description");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        jScrollPane2.setViewportView(descriptionTextArea);

        linksPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Links"));

        editLinksButton.setText("Links ...");
        editLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinksButtonActionPerformed(evt);
            }
        });

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
        jScrollPane4.setViewportView(linksTable);

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, linksPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editLinksButton)
                .addContainerGap())
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editLinksButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(linksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameTextField))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conditionsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editorPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(conditionsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(saveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(linksPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLinksButtonActionPerformed
        
        JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(this), true);
        linkEditor.add(new LinkEditor(cause).setParent(linkEditor));
        linkEditor.pack();
        linkEditor.setVisible(true);

        try {
            HashMap<String,ArrayList<Relationship>> rels = cause.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                for (Relationship r : a) {
                    String m = r.getManagementClass();
                    if ((m == null) || (!m.contentEquals("Diagram"))) {                    
                        Persistable tgt = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        String[] row = new String[linkcolumns.length];
                        row[0] = tgt.getDisplayName();
                        row[1] = tgt.getAttributeValue("Name");
                        row[2] = r.getComment();
                        dtm.addRow(row);
                    }
                }
            }
            linksTable.setModel(dtm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_editLinksButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        boolean created = false;
        if (cause == null) {
            JOptionPane.showMessageDialog(this, "Causes should be created via the Hazard Bowtie editor.", "Context!", JOptionPane.ERROR_MESSAGE);
            return;
//            cause = new Cause();
//            created = true;
        }
        // Only set the name if we're creating the cause. Otherwise do it via the
        // bowtie editor
        if (created)
            cause.setAttribute("Name", nameTextField.getText());
        cause.setAttribute("Description", descriptionTextArea.getText());
        cause.setAttribute("GroupingType", (String)conditionsComboBox.getSelectedItem());
        
//        if (newObjectProjectId == -1)
//            cause.setAttribute("ProjectID", Integer.parseInt(cause.getAttributeValue("ProjectID")));
//        else 
            cause.setAttribute("ProjectID",SmartProject.getProject().getCurrentProjectID());
        try {
            MetaFactory.getInstance().getFactory(cause.getDatabaseObjectName()).put(cause);
            editorComponent.notifyEditorEvent((created) ? Project.ADD : Project.UPDATE, cause);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_saveButtonActionPerformed
    
    private void doDelete() {
                
        if (cause == null)
            return;
        
        for (Relationship r : cause.getRelationships("Control")) {
            if (r.getManagementClass() != null) {
                JOptionPane.showMessageDialog(this, "Cause still connected to a Bowtie diagram. Remove from the diagram then delete.", "Cause in use", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        for (Relationship r : cause.getRelationships("Hazard")) {
            if (r.getManagementClass() != null) {
                JOptionPane.showMessageDialog(this, "Cause still connected to a Bowtie diagram. Remove from the diagram then delete.", "Cause in use", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to Delete this Cause?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION) {
            return;
        }
        try {
            MetaFactory.getInstance().getFactory(cause.getDatabaseObjectName()).delete(cause);
            editorComponent.notifyEditorEvent(Project.DELETE, cause);
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    private void newCause() {
        JOptionPane.showMessageDialog(this, "Causes should be created via the Hazard Bowtie editor.", "Context!", JOptionPane.ERROR_MESSAGE);
        
        
//         DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
//         linksTable.setModel(dtm);
//        editorPanel.setEnabled(true);
//        conditionsComboBox.setSelectedIndex(-1);
//       nameTextField.setText("");
//        descriptionTextArea.setText("");        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> conditionsComboBox;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton editLinksButton;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel linksPanel;
    private javax.swing.JTable linksTable;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setPersistableObject(Persistable p) 
    {
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Causes should be created via the Hazard Bowtie editor.", "Context!", JOptionPane.ERROR_MESSAGE);
            return;            
        }
        cause = (Cause)p;
        nameTextField.setText(cause.getAttributeValue("Name"));
        
        for (int i = 0; i < conditionsComboBox.getModel().getSize(); i++) {
            if (conditionsComboBox.getItemAt(i).contentEquals(cause.getAttributeValue("GroupingType"))) {
                conditionsComboBox.setSelectedIndex(i);
                break;
            }
        }
        descriptionTextArea.setText(cause.getAttributeValue("Description"));
        try {
            HashMap<String,ArrayList<Relationship>> rels = cause.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                for (Relationship r : a) {
                    String m = r.getManagementClass();
                    if ((m == null) || (!m.contentEquals("Diagram"))) {    
                        Persistable tgt = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        String[] row = new String[linkcolumns.length];
                        row[0] = tgt.getDisplayName();
                        row[1] = tgt.getAttributeValue("Name");
                        row[2] = r.getComment();
                        dtm.addRow(row);
                    }
                }
            }
            linksTable.setModel(dtm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
        newObjectProjectId = i;
    }

    @Override
    public boolean notification(int evtype, Object o) {
        if (evtype == Project.SAVE) {
            saveButtonActionPerformed(null);
            return false;
        }
        
        if (cause == null)
            return false;
        if (o instanceof uk.nhs.digital.safetycase.data.Cause) {
            Cause c = (Cause)o;
            if (c == cause) {
                if (evtype == Project.DELETE) {
                    // Close this form and its container... 
                    SmartProject.getProject().getProjectWindow().closeContainer(this);
                    // then return true so that this form can be removed from the
                    // notifications list
                    return true;
                }
                setPersistableObject(c);
                if (evtype == Project.SAVE) {
                    saveButtonActionPerformed(null);
                }
            }
        }
        return false;
    }
}
