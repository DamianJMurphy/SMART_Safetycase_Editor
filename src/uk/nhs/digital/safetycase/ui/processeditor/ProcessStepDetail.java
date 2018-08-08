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

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.DataNotificationSubscriber;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.ProjectLink;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.HazardEditor;
import uk.nhs.digital.safetycase.ui.LinkEditor;
import uk.nhs.digital.safetycase.ui.LinkExplorerTableCellRenderer;
import uk.nhs.digital.safetycase.ui.LinkTableCellRenderer;

/**
 *
 * @author murff
 */
public class ProcessStepDetail 
        extends javax.swing.JPanel 
        implements DataNotificationSubscriber
{

    private static final String[] COLUMNS = {"Name", "Status", "Initial rating", "Residual rating"};
    private final String[] linkcolumns = {"Type", "Name", "Comment", "Via"};
    
    private JDialog parent = null;
    private ProcessStep processStep = null;
    private final ArrayList<Hazard> hazardList = new ArrayList<>();
    private boolean modified = false;
    /**
     * Creates new form ProcessStepDetail
     * @param ps
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ProcessStepDetail(ProcessStep ps) {
        initComponents();
        processStep = ps;
        SmartProject.getProject().addNotificationSubscriber(this);
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
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
                        row[0] = h.getAttributeValue("Name");
                        row[1] = h.getAttributeValue("Status");
                        row[2] = h.getAttributeValue("InitialRiskRating");
                        row[3] = h.getAttributeValue("ResidualRiskRating") ;
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
        namePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        hazardsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        hazardsTable = new javax.swing.JTable();
        editSelectedHazardButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        directLinksOnlyCheckBox = new javax.swing.JCheckBox();
        editLinksButton = new javax.swing.JButton();

        jLabel2.setText("Description");

        setBorder(javax.swing.BorderFactory.createTitledBorder("Process step"));
        setMaximumSize(new java.awt.Dimension(695, 505));
        setMinimumSize(new java.awt.Dimension(695, 505));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel1.setText("Name");

        nameTextField.setEditable(false);

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

        javax.swing.GroupLayout namePanelLayout = new javax.swing.GroupLayout(namePanel);
        namePanel.setLayout(namePanelLayout);
        namePanelLayout.setHorizontalGroup(
            namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(namePanelLayout.createSequentialGroup()
                .addGroup(namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(namePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE))
                    .addGroup(namePanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(namePanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(namePanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(20, 20, 20)
                                .addComponent(nameTextField)))))
                .addContainerGap())
        );
        namePanelLayout.setVerticalGroup(
            namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(namePanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(namePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(namePanel);
        add(filler1);

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

        editSelectedHazardButton.setText("Edit selected hazard");
        editSelectedHazardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSelectedHazardButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout hazardsPanelLayout = new javax.swing.GroupLayout(hazardsPanel);
        hazardsPanel.setLayout(hazardsPanelLayout);
        hazardsPanelLayout.setHorizontalGroup(
            hazardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(hazardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hazardsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(editSelectedHazardButton)))
                .addContainerGap())
        );
        hazardsPanelLayout.setVerticalGroup(
            hazardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(editSelectedHazardButton))
        );

        add(hazardsPanel);

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

        directLinksOnlyCheckBox.setSelected(true);
        directLinksOnlyCheckBox.setText("Show direct links only");

        editLinksButton.setText("Links...");
        editLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinksButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(editLinksButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(directLinksOnlyCheckBox))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directLinksOnlyCheckBox)
                    .addComponent(editLinksButton))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void editLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLinksButtonActionPerformed
        JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(this), true);
        linkEditor.add(new LinkEditor(processStep).setParent(linkEditor));
        linkEditor.pack();
        linkEditor.setVisible(true);
        
        populate();
    }//GEN-LAST:event_editLinksButtonActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JCheckBox directLinksOnlyCheckBox;
    private javax.swing.JButton editLinksButton;
    private javax.swing.JButton editSelectedHazardButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel hazardsPanel;
    private javax.swing.JTable hazardsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable linksTable;
    private javax.swing.JPanel namePanel;
    private javax.swing.JTextField nameTextField;
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


}
