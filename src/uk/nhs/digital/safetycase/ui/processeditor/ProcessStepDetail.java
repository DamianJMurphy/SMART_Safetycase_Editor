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
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.ProjectLink;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.HazardEditor;
import uk.nhs.digital.safetycase.ui.LinkEditor;
import uk.nhs.digital.safetycase.ui.LinkExplorerTableCellRenderer;

/**
 *
 * @author murff
 */
public class ProcessStepDetail 
        extends javax.swing.JPanel 
        implements uk.nhs.digital.safetycase.ui.PersistableEditor
{

    private static final String[] COLUMNS = {"Hazard ID", "Hazard Name", "Initial rating", "Residual rating", "Status"};
    private final String[] linkcolumns = {"Name", "Type", "Comment", "Via"};
    private DefaultTableCellRenderer hazrenderer = new HazardTableCellRenderer();
    
    private EditorComponent editorComponent = null;
    private JDialog parent = null;
    private ProcessStep processStep = null;
    private final ArrayList<Hazard> hazardList = new ArrayList<>();
    private boolean modified = false;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public ProcessStepDetail() {
        initComponents();
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
        hazardsTable.setDefaultEditor(Object.class, null);
        hazardsTable.setDefaultRenderer(Object.class, hazrenderer);
        hazardsTable.setModel(dtm);
        hazardsTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        dtm = new DefaultTableModel(linkcolumns, 0);
        linksTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        linksTable.setDefaultEditor(Object.class, null);
        linksTable.setDefaultRenderer(Object.class, new LinkExplorerTableCellRenderer());
        linksTable.setModel(dtm);
        descriptionTextArea.setFont(nameTextField.getFont());
        SmartProject.getProject().addNotificationSubscriber(this);
    }
    
    /**
     * Creates new form ProcessStepDetail
     * @param ps
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ProcessStepDetail(ProcessStep ps) {
        initComponents();
        processStep = ps;
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
        hazardsTable.setDefaultEditor(Object.class, null);
        hazardsTable.setDefaultRenderer(Object.class, hazrenderer);
        hazardsTable.setModel(dtm);
        hazardsTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        dtm = new DefaultTableModel(linkcolumns, 0);
        linksTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        linksTable.setDefaultEditor(Object.class, null);
        linksTable.setDefaultRenderer(Object.class, new LinkExplorerTableCellRenderer());
        linksTable.setModel(dtm);
        if (ps != null) {
            populate();
        }
        SmartProject.getProject().addNotificationSubscriber(this);
    }

   private void populateLinks() {
        try {
            
//            HashMap<String,ArrayList<Relationship>> rels = hazard.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            ArrayList<ProjectLink> pls = new ArrayList<>();
            pls = MetaFactory.getInstance().exploreLinks(processStep, processStep, pls, false);
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
     
    private void populate() {
        nameTextField.setText(processStep.getAttributeValue("Name"));
        nameTextField.setEditable(false);
        descriptionTextArea.setText(processStep.getAttributeValue("Description"));
        
        try { 
            ArrayList<Relationship> hazards = processStep.getRelationships("Hazard");
            DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
            if (hazards != null) {
                for (Relationship r : hazards) {
                    Hazard h = (Hazard)MetaFactory.getInstance().getFactory("Hazard").get(r.getTarget());
                    if (!h.isDeleted()) {
                        String[] row = new String[COLUMNS.length];
                        row[0] = Integer.toString(h.getId());
                        row[1] = h.getAttributeValue("Name");
                        row[2] = h.getAttributeValue("InitialRiskRating");
                        row[3] = h.getAttributeValue("ResidualRiskRating");
                        row[4] = h.getAttributeValue("Status") ;
                        hazardList.add(h);
                        dtm.addRow(row);
                    }
                }
            }
            hazardsTable.setModel(dtm);
            populateLinks();            
            modified = false;
        }
        catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cannot make details view. Send logs to support", "Warning", JOptionPane.INFORMATION_MESSAGE);
                SmartProject.getProject().log("Failed to populate process step details view", e);
        }
    }
    
    public ProcessStepDetail setParent(JDialog p) {
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

        jLabel2 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        namePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        hazardsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        hazardsTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        directLinksOnlyCheckBox = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        commonToolBar = new javax.swing.JToolBar();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        saveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        editSelectedHazardButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        editLinksButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();

        jLabel2.setText("Description");

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(695, 505));
        setMinimumSize(new java.awt.Dimension(695, 505));

        namePanel.setBackground(new java.awt.Color(229, 239, 248));
        namePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel1.setText("Name");

        nameTextField.setEditable(false);
        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                descriptionTextAreaFocusLost(evt);
            }
        });
        descriptionTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                descriptionTextAreaKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(descriptionTextArea);

        jLabel3.setText("Description");

        hazardsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Identified hazards"));

        hazardsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(hazardsTable);

        javax.swing.GroupLayout hazardsPanelLayout = new javax.swing.GroupLayout(hazardsPanel);
        hazardsPanel.setLayout(hazardsPanelLayout);
        hazardsPanelLayout.setHorizontalGroup(
            hazardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 892, Short.MAX_VALUE))
        );
        hazardsPanelLayout.setVerticalGroup(
            hazardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardsPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Linked to"));

        directLinksOnlyCheckBox.setSelected(true);
        directLinksOnlyCheckBox.setText("Show direct links only");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(directLinksOnlyCheckBox)
                .addGap(68, 68, 68))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(directLinksOnlyCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout namePanelLayout = new javax.swing.GroupLayout(namePanel);
        namePanel.setLayout(namePanelLayout);
        namePanelLayout.setHorizontalGroup(
            namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(namePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(hazardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(namePanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(namePanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameTextField)))
                .addContainerGap())
        );
        namePanelLayout.setVerticalGroup(
            namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(namePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hazardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        commonToolBar.setBackground(new java.awt.Color(41, 156, 214));
        commonToolBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        commonToolBar.setRollover(true);
        commonToolBar.add(jSeparator5);

        saveButton.setText("Save");
        saveButton.setToolTipText("Click to Save");
        saveButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(saveButton);
        commonToolBar.add(jSeparator1);

        editSelectedHazardButton.setText("Edit selected hazard");
        editSelectedHazardButton.setToolTipText("Edit Selected Hazard");
        editSelectedHazardButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        editSelectedHazardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSelectedHazardButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(editSelectedHazardButton);
        commonToolBar.add(jSeparator2);

        editLinksButton.setText("Links...");
        editLinksButton.setToolTipText("Edit Links");
        editLinksButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        editLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinksButtonActionPerformed(evt);
            }
        });
        commonToolBar.add(editLinksButton);
        commonToolBar.add(jSeparator3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(commonToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(namePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(commonToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(namePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void descriptionTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_descriptionTextAreaFocusLost
        processStep.setAttribute("Description", descriptionTextArea.getText());
    }//GEN-LAST:event_descriptionTextAreaFocusLost

    private void editSelectedHazardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSelectedHazardButtonActionPerformed
        int row = hazardsTable.getSelectedRow();
        Hazard h = hazardList.get(row);
        HazardEditor he = new HazardEditor();
        he.setPersistableObject(h);
        EditorComponent ec = new EditorComponent(he, "Hazard:" + h.getTitle(), SmartProject.getProject());
        ExternalEditorView editorView = new ExternalEditorView(he.getComponent(), ec.getTitle(), SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());    }//GEN-LAST:event_editSelectedHazardButtonActionPerformed

    private void descriptionTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descriptionTextAreaKeyTyped
        modified = true;
    }//GEN-LAST:event_descriptionTextAreaKeyTyped

    private void editLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLinksButtonActionPerformed
        JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(this), true);
        linkEditor.add(new LinkEditor(processStep).setParent(linkEditor));
        linkEditor.pack();
        linkEditor.setVisible(true);

        populate();
    }//GEN-LAST:event_editLinksButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (processStep == null) {
            JOptionPane.showMessageDialog(this, "Care process steps should be created via the Care Process editor.", "Context!", JOptionPane.ERROR_MESSAGE);
            return;           
        }
        processStep.setAttribute("Description", descriptionTextArea.getText());
        try {
            MetaFactory.getInstance().getFactory(processStep.getDatabaseObjectName()).put(processStep);
            SmartProject.getProject().editorEvent(Project.UPDATE, processStep);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save Process Step. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in ProcessStepDetail", e);
        }
        modified = false;
        String cos = System.getProperty("SMART.closeonsave");
        if ((cos != null) && (cos.contains("Step"))) {
           unsubscribe();
           SmartProject.getProject().getProjectWindow().closeContainer(this);
        }
        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar commonToolBar;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JCheckBox directLinksOnlyCheckBox;
    private javax.swing.JButton editLinksButton;
    private javax.swing.JButton editSelectedHazardButton;
    private javax.swing.JPanel hazardsPanel;
    private javax.swing.JTable hazardsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JTable linksTable;
    private javax.swing.JPanel namePanel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean notification(int evtype, Object o) {
        return false;
    }

    @Override
    public JPanel getEditor(Object o) {
        try {
            uk.nhs.digital.safetycase.data.ProcessStep p = (uk.nhs.digital.safetycase.data.ProcessStep)o;
            if (p == processStep)
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

    @Override
    public void setPersistableObject(Persistable p) {
        processStep = (ProcessStep)p;    
        if (processStep != null)
            populate();
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
    public void setNewObjectProjectId(int i) { }

    @Override
    public boolean wantsScrollPane() {
        return false;
    }


}
