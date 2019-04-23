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
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.Control;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.ValueSet;

/**
 *
 * @author damian
 */
public class ControlEditor extends javax.swing.JPanel 
         implements uk.nhs.digital.safetycase.ui.PersistableEditor
{
    private final String[] linkcolumns = {"Type", "Name", "Comment"};

    private EditorComponent editorComponent = null;
    private Control control = null;
    private int newObjectProjectId = -1;
    private boolean modified = false;
    /**
     * Creates new form ControlEditor
     */
    public ControlEditor() {
        initComponents();
        DefaultTableModel linkModel = new DefaultTableModel(linkcolumns, 0);
        SmartProject.getProject().addNotificationSubscriber(this);
        linksTable.setModel(linkModel);
        linksTable.setDefaultEditor(Object.class, null);
        linksTable.setDefaultRenderer(Object.class, new LinkTableCellRenderer());        
        linksTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        try {
//            ValueSet controlType = MetaFactory.getInstance().getValueSet("ControlType");
//            Iterator<String> ctypes = controlType.iterator();
//            while(ctypes.hasNext()) {
//                String s = ctypes.next();
//                conditionsComboBox.addItem(s);
//            }
            ValueSet controlState = MetaFactory.getInstance().getValueSet("ControlState");
            Iterator<String> cstates = controlState.iterator();
            while(cstates.hasNext()) {
                String s = cstates.next();
                stateComboBox.addItem(s);
            }
            ArrayList<String> conds = MetaFactory.getInstance().getFactory("Control").getDistinctSet("GroupingType");

            
            
            ArrayList<String> typelist = MetaFactory.getInstance().getFactory("Control").getDistinctSet("Type");
            conditionsComboBox.addItem("Design");
            conditionsComboBox.addItem("Test");
            conditionsComboBox.addItem("Training");
            conditionsComboBox.addItem("Business process change");

            ArrayList<String> states = MetaFactory.getInstance().getFactory("Control").getDistinctSet("State");
            for (String s : states) {
                stateComboBox.addItem(s);
            }
        }
        catch (Exception e) {
            SmartProject.getProject().log("Failed to initialise ControlEditor", e);
        }
        descriptionTextArea.setFont(nameTextField.getFont());
        clinicalJustificationTextArea.setFont(nameTextField.getFont());
        evidenceTextArea.setFont(nameTextField.getFont());
        modified = false;
        
    }

    @Override
    public boolean wantsScrollPane() { return false; }
    
//    @Override
//    public void addNotify() {
//        super.addNotify();
//        SmartProject.getProject().addNotificationSubscriber(this);
//    }
    
//    @Override
//    public void removeNotify() {
//        super.removeNotify();
//        SmartProject.getProject().removeNotificationSubscriber(this);
//    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        editorPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        stateComboBox = new javax.swing.JComboBox<>();
        conditionsComboBox = new javax.swing.JComboBox<>();
        groupComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        linksPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        descriptionAndJustificationContainer = new javax.swing.JPanel();
        descriptionPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        justificationPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        clinicalJustificationTextArea = new javax.swing.JTextArea();
        evidenceContainer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        evidenceTextArea = new javax.swing.JTextArea();
        commonToolBar = new javax.swing.JToolBar();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        saveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        editLinksButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable1);

        setBackground(new java.awt.Color(255, 255, 255));

        editorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        editorPanel.setLayout(new javax.swing.BoxLayout(editorPanel, javax.swing.BoxLayout.PAGE_AXIS));

        mainPanel.setBackground(new java.awt.Color(229, 239, 248));

        jLabel1.setText("Name");

        jLabel3.setText("State");

        jLabel4.setText("Group");

        nameTextField.setEditable(false);

        stateComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stateComboBoxMouseClicked(evt);
            }
        });
        stateComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                stateComboBoxKeyTyped(evt);
            }
        });

        conditionsComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                conditionsComboBoxMouseClicked(evt);
            }
        });
        conditionsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conditionsComboBoxActionPerformed(evt);
            }
        });
        conditionsComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                conditionsComboBoxKeyTyped(evt);
            }
        });

        groupComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Additional", "Existing" }));
        groupComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                groupComboBoxMouseClicked(evt);
            }
        });
        groupComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                groupComboBoxKeyTyped(evt);
            }
        });

        jLabel2.setText("Type");

        linksPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Linked to"));

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
        jScrollPane3.setViewportView(linksTable);

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, linksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, linksPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField)
                            .addComponent(groupComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(conditionsComboBox, 0, 816, Short.MAX_VALUE)
                            .addComponent(stateComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(linksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(conditionsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(groupComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(linksPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        editorPanel.add(mainPanel);

        descriptionAndJustificationContainer.setBackground(new java.awt.Color(229, 239, 248));
        descriptionAndJustificationContainer.setLayout(new javax.swing.BoxLayout(descriptionAndJustificationContainer, javax.swing.BoxLayout.LINE_AXIS));

        descriptionPanel.setBackground(new java.awt.Color(229, 239, 248));
        descriptionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                descriptionTextAreaKeyTyped(evt);
            }
        });
        jScrollPane4.setViewportView(descriptionTextArea);

        javax.swing.GroupLayout descriptionPanelLayout = new javax.swing.GroupLayout(descriptionPanel);
        descriptionPanel.setLayout(descriptionPanelLayout);
        descriptionPanelLayout.setHorizontalGroup(
            descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
            .addGroup(descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(descriptionPanelLayout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        descriptionPanelLayout.setVerticalGroup(
            descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
            .addGroup(descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(descriptionPanelLayout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addGap(11, 11, 11)))
        );

        descriptionAndJustificationContainer.add(descriptionPanel);

        justificationPanel.setBackground(new java.awt.Color(229, 239, 248));
        justificationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Clinical justification"));

        clinicalJustificationTextArea.setColumns(20);
        clinicalJustificationTextArea.setLineWrap(true);
        clinicalJustificationTextArea.setRows(5);
        clinicalJustificationTextArea.setWrapStyleWord(true);
        clinicalJustificationTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                clinicalJustificationTextAreaKeyTyped(evt);
            }
        });
        jScrollPane5.setViewportView(clinicalJustificationTextArea);

        javax.swing.GroupLayout justificationPanelLayout = new javax.swing.GroupLayout(justificationPanel);
        justificationPanel.setLayout(justificationPanelLayout);
        justificationPanelLayout.setHorizontalGroup(
            justificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
            .addGroup(justificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(justificationPanelLayout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        justificationPanelLayout.setVerticalGroup(
            justificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
            .addGroup(justificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(justificationPanelLayout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addGap(11, 11, 11)))
        );

        descriptionAndJustificationContainer.add(justificationPanel);

        editorPanel.add(descriptionAndJustificationContainer);

        evidenceContainer.setBackground(new java.awt.Color(229, 239, 248));
        evidenceContainer.setBorder(javax.swing.BorderFactory.createTitledBorder("Evidence"));

        evidenceTextArea.setColumns(20);
        evidenceTextArea.setLineWrap(true);
        evidenceTextArea.setRows(5);
        evidenceTextArea.setWrapStyleWord(true);
        evidenceTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                evidenceTextAreaKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(evidenceTextArea);

        javax.swing.GroupLayout evidenceContainerLayout = new javax.swing.GroupLayout(evidenceContainer);
        evidenceContainer.setLayout(evidenceContainerLayout);
        evidenceContainerLayout.setHorizontalGroup(
            evidenceContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 846, Short.MAX_VALUE)
            .addGroup(evidenceContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(evidenceContainerLayout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        evidenceContainerLayout.setVerticalGroup(
            evidenceContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
            .addGroup(evidenceContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(evidenceContainerLayout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addGap(11, 11, 11)))
        );

        editorPanel.add(evidenceContainer);

        commonToolBar.setBackground(new java.awt.Color(41, 156, 214));
        commonToolBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        commonToolBar.setRollover(true);
        commonToolBar.add(jSeparator3);

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

        editLinksButton.setText("Links...");
        editLinksButton.setToolTipText("Edit Links");
        editLinksButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        editLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinksButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(editLinksButton);
        commonToolBar.add(jSeparator2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(commonToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commonToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void doDelete() {
                
        if (control == null)
            return;
        
        for (Relationship r : control.getRelationships("Effect")) {
            if (r.getManagementClass() != null) {
                JOptionPane.showMessageDialog(this, "Cause still connected to a Bowtie diagram. Remove from the diagram then delete.", "Cause in use", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        for (Relationship r : control.getRelationships("Hazard")) {
            if (r.getManagementClass() != null) {
                JOptionPane.showMessageDialog(this, "Control still connected to a Bowtie diagram. Remove from the diagram then delete.", "Control in use", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to Delete this Control?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION) {
            return;
        }
        try {
            MetaFactory.getInstance().getFactory(control.getDatabaseObjectName()).delete(control);
            editorComponent.notifyEditorEvent(Project.DELETE, control);
        }
        catch (Exception e) {
            SmartProject.getProject().log("Failed to delete Control", e);
        }        
    }
    
    
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
//        if (conditionsComboBox.getSelectedIndex() == -1)
//            conditionsComboBox.setSelectedIndex(0);
        control.setAttribute("Name", nameTextField.getText());
        control.setAttribute("Description", descriptionTextArea.getText());
        control.setAttribute("ClinicalJustification", clinicalJustificationTextArea.getText());
        control.setAttribute("Type", (String)groupComboBox.getSelectedItem());
        control.setAttribute("State", (String)stateComboBox.getSelectedItem());
        control.setAttribute("GroupingType", (String)conditionsComboBox.getSelectedItem());
        control.setAttribute("Evidence", evidenceTextArea.getText());
//        if (newObjectProjectId == -1)
//            control.setAttribute("ProjectID", Integer.parseInt(control.getAttributeValue("ProjectID")));
//        else 
            control.setAttribute("ProjectID",SmartProject.getProject().getCurrentProjectID());
        try {
            MetaFactory.getInstance().getFactory(control.getDatabaseObjectName()).put(control);
            SmartProject.getProject().editorEvent(Project.UPDATE, control);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to save Control. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in ControlEditor", e);
        }
        modified = false;
        String cos = System.getProperty("SMART.closeonsave");
        if ((cos != null) && (cos.contains("Control"))) {
           unsubscribe();
           SmartProject.getProject().getProjectWindow().closeContainer(this);
        }
        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void editLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLinksButtonActionPerformed
        JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(this), true);
        linkEditor.add(new LinkEditor(control).setParent(linkEditor));
        linkEditor.pack();
        linkEditor.setVisible(true);

        try {
            HashMap<String,ArrayList<Relationship>> rels = control.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                for (Relationship r : a) {
                    String m = r.getManagementClass();
                    if ((m == null) || (!m.contentEquals("Diagram"))) {                    
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
            SmartProject.getProject().log("Failed to process editLinks action in ControlEditor", e);
        }
    }//GEN-LAST:event_editLinksButtonActionPerformed

    private void conditionsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conditionsComboBoxActionPerformed
        modified = true;
    }//GEN-LAST:event_conditionsComboBoxActionPerformed

    private void descriptionTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descriptionTextAreaKeyTyped
        modified = true;
    }//GEN-LAST:event_descriptionTextAreaKeyTyped

    private void stateComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stateComboBoxMouseClicked
        modified = true;
    }//GEN-LAST:event_stateComboBoxMouseClicked

    private void conditionsComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_conditionsComboBoxMouseClicked
        modified = true;
    }//GEN-LAST:event_conditionsComboBoxMouseClicked

    private void groupComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_groupComboBoxMouseClicked
        modified = true;
    }//GEN-LAST:event_groupComboBoxMouseClicked

    private void stateComboBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stateComboBoxKeyTyped
        modified = true;
    }//GEN-LAST:event_stateComboBoxKeyTyped

    private void conditionsComboBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_conditionsComboBoxKeyTyped
        modified = true;
    }//GEN-LAST:event_conditionsComboBoxKeyTyped

    private void groupComboBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_groupComboBoxKeyTyped
        modified = true;
    }//GEN-LAST:event_groupComboBoxKeyTyped

    private void clinicalJustificationTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clinicalJustificationTextAreaKeyTyped
        modified = true;
    }//GEN-LAST:event_clinicalJustificationTextAreaKeyTyped

    private void evidenceTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_evidenceTextAreaKeyTyped
        modified = true;
    }//GEN-LAST:event_evidenceTextAreaKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea clinicalJustificationTextArea;
    private javax.swing.JToolBar commonToolBar;
    private javax.swing.JComboBox<String> conditionsComboBox;
    private javax.swing.JPanel descriptionAndJustificationContainer;
    private javax.swing.JPanel descriptionPanel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton editLinksButton;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JPanel evidenceContainer;
    private javax.swing.JTextArea evidenceTextArea;
    private javax.swing.JComboBox<String> groupComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel justificationPanel;
    private javax.swing.JPanel linksPanel;
    private javax.swing.JTable linksTable;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox<String> stateComboBox;
    // End of variables declaration//GEN-END:variables

    private void newControl() {
        JOptionPane.showMessageDialog(this, "Controls should be created via the Hazard Bowtie editor.", "Context!", JOptionPane.ERROR_MESSAGE);
        
        
//         DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
//         linksTable.setModel(dtm);
//        editorPanel.setEnabled(true);
//        conditionsComboBox.setSelectedIndex(-1);
//       nameTextField.setText("");
//        descriptionTextArea.setText("");        
        
    }
    
    @Override
    public void setPersistableObject(Persistable p) {
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Controls should be created via the Hazard Bowtie editor.", "Context!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        control = (Control)p;
        nameTextField.setText(control.getAttributeValue("Name"));
        clinicalJustificationTextArea.setText(control.getAttributeValue("ClinicalJustification"));
        descriptionTextArea.setText(control.getAttributeValue("Description"));
        evidenceTextArea.setText(control.getAttributeValue("Evidence"));
        if (control.getAttributeValue("Type").contentEquals("Additional"))
            groupComboBox.setSelectedIndex(0);
        else
            groupComboBox.setSelectedIndex(1);
        for (int i = 0; i < conditionsComboBox.getModel().getSize(); i++) {
            if (conditionsComboBox.getItemAt(i).contentEquals(control.getAttributeValue("GroupingType"))) {
                conditionsComboBox.setSelectedIndex(i);
                break;
            }
        }
//        String s = control.getAttributeValue("Type");
//        for (int j = 0; j < typeComboBox.getItemCount(); j++) {
//            if (s.contentEquals(typeComboBox.getItemAt(j))) {
//                typeComboBox.setSelectedIndex(j);
//                break;
//            }
//        }
        String s = control.getAttributeValue("State");
        for (int j = 0; j < stateComboBox.getItemCount(); j++) {
            if (s.contentEquals(stateComboBox.getItemAt(j))) {
                stateComboBox.setSelectedIndex(j);
                break;
            }
        }        
        try {
            HashMap<String,ArrayList<Relationship>> rels = control.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                for (Relationship r : a) {
                    if (r.isDeleted())
                        continue;
                    
                    String m = r.getManagementClass();
                    if ((m == null) || (!m.contentEquals("Diagram"))) {                    
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
            JOptionPane.showMessageDialog(editorPanel, "Failed to load Control for editing", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to set persistable object in ControlEditor", e);
        }
        modified = false;
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
        
        if (control == null)
            return false;
        if (o instanceof uk.nhs.digital.safetycase.data.Control) {
            Control c = (Control)o;
            if (c == control) {
                if (evtype == Project.DELETE) {
                    // Close this form and its container... 
                    SmartProject.getProject().getProjectWindow().closeContainer(this);
                    // then return true so that this form can be removed from the
                    // notifications list
                    return true;
                }
                SmartProject.getProject().getProjectWindow().setViewTitle(this, "Control:" + c.getTitle());
                setPersistableObject(c);
                if (evtype == Project.SAVE) {
                    saveButtonActionPerformed(null);
                }
            }
        }
        return false;
    }

    @Override
    public JPanel getEditor(Object o) {
        try {            
            Control c = (Control)o;
            if (c.getTitle().equals(control.getTitle()))
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
