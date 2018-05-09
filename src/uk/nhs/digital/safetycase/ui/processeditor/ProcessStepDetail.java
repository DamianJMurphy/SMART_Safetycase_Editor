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
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.HazardEditor;
import uk.nhs.digital.safetycase.ui.LinkEditor;
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
    private final String[] linkcolumns = {"Type", "Name", "Comment"};
    
    private JDialog parent = null;
    private ProcessStep processStep = null;
    private ArrayList<Hazard> hazardList = new ArrayList<>();
    /**
     * Creates new form ProcessStepDetail
     * @param ps
     */
    public ProcessStepDetail(ProcessStep ps) {
        initComponents();
        processStep = ps;
        SmartProject.getProject().addNotificationSubscriber(this);
        if (ps != null) {
            populate();
        } else {
            DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
            hazardsTable.setModel(dtm);
            dtm = new DefaultTableModel(linkcolumns, 0);
            linksTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
            linksTable.setDefaultEditor(Object.class, null);
            linksTable.setDefaultRenderer(Object.class, new LinkTableCellRenderer());        
            linksTable.setModel(dtm);            
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
            dtm = new DefaultTableModel(linkcolumns, 0);
            HashMap<String,ArrayList<Relationship>> rels = processStep.getRelationshipsForLoad();
            if (rels != null) {
                for (String t : rels.keySet()) {
                    ArrayList<Relationship> a = rels.get(t);
                    if (a == null)
                        continue;
                    for (Relationship r : a) {
                        if (! r.isDeleted()) {
                            Object[] row = new Object[linkcolumns.length];
                            for (int i = 0; i < linkcolumns.length; i++)
                                row[i] = r;
                            dtm.addRow(row);
                        }
                    }
                }
            }
            linksTable.setModel(dtm);
            
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

        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        hazardsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        hazardsTable = new javax.swing.JTable();
        editSelectedHazardButton = new javax.swing.JButton();
        editLinksButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 20), new java.awt.Dimension(100, 20), new java.awt.Dimension(100, 20));

        setBorder(javax.swing.BorderFactory.createTitledBorder("Process step"));
        setMaximumSize(new java.awt.Dimension(695, 505));
        setMinimumSize(new java.awt.Dimension(695, 505));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Name");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 31, -1, -1));
        add(nameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(109, 29, 569, -1));

        jLabel2.setText("Description");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 66, -1, -1));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                descriptionTextAreaFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(descriptionTextArea);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 87, 661, -1));

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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hazardsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(editSelectedHazardButton)))
                .addContainerGap())
        );
        hazardsPanelLayout.setVerticalGroup(
            hazardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(editSelectedHazardButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(hazardsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 171, 661, 210));

        editLinksButton.setText("Links...");
        editLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinksButtonActionPerformed(evt);
            }
        });
        add(editLinksButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, -1, -1));

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

        add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 660, 180));
        add(filler1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 610, 660, -1));
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton editLinksButton;
    private javax.swing.JButton editSelectedHazardButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel hazardsPanel;
    private javax.swing.JTable hazardsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable linksTable;
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


}
