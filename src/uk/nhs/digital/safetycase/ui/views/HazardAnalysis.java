/*
 * 
 *   Copyright 2018  NHS Digital
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
package uk.nhs.digital.safetycase.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.DataNotificationSubscriber;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Project;
import uk.nhs.digital.safetycase.ui.processeditor.HazardTableCellRenderer;

/**
 *
 * @author damian
 */
public class HazardAnalysis 
        extends javax.swing.JPanel
        implements ViewConstructor, DataNotificationSubscriber
{
    private static final String[] COLUMNS = {"Hazard ID", "Hazard Name", "Initial rating", "Residual rating", "Status"};
    private DefaultTableCellRenderer hazrenderer = new HazardTableCellRenderer();
    private Project project = null;
//    private ArrayList<Hazard> hazards = new ArrayList<>();
    private ArrayList<Hazard> displayedHazards = new ArrayList<>();
    private HashMap<String, ArrayList<Hazard>> hazardStates = new HashMap<>();

    /**
     * Creates new form HazardAnalysis
     */
    public HazardAnalysis() {
        initComponents();
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
        hazardsTable.setModel(dtm);
        hazardsTable.setDefaultEditor(Object.class, null);
        hazardsTable.setDefaultRenderer(Object.class, hazrenderer);
        hazardsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hazardsTable.setCellSelectionEnabled(false);
        hazardsTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        ListSelectionModel lsm = hazardsTable.getSelectionModel();
        lsm.addListSelectionListener((ListSelectionEvent evt) -> {
            int sel = hazardsTable.getSelectedRow();
            if (sel == -1)
                return;
            Hazard h = displayedHazards.get(sel);
            populateHazardPanel(h);
        });
        SmartProject.getProject().addNotificationSubscriber(this);
    }

    @Override
    public void unsubscribe() {
        SmartProject.getProject().removeNotificationSubscriber(this);
    }
    
    private void populateHazardPanel(Hazard h) {
        if (h == null)
            return;
        nameTextField.setText(h.getAttributeValue("Name"));
        conditionTextField.setText(h.getAttributeValue("GroupingType"));
        statusTextField.setText((h.getAttributeValue("Status")));
        descriptionTextArea.setText(h.getAttributeValue("Description"));
        clinicalJustificationTextArea.setText(h.getAttributeValue("ClinicalJustification"));
        initialSeverityTextField.setText(Hazard.translateSeverity(h.getAttribute("InitialSeverity").getIntValue()));
        initialLikelihoodTextField.setText(Hazard.translateLikelihood(h.getAttribute("InitialLikelihood").getIntValue()));
        residualSeverityTextField.setText(Hazard.translateSeverity(h.getAttribute("ResidualSeverity").getIntValue()));
        residualLikelihoodTextField.setText(Hazard.translateLikelihood(h.getAttribute("ResidualLikelihood").getIntValue()));
        initialRatingTextField.setText(h.getAttributeValue("InitialRiskRating"));
        residualRatingTextField.setText(h.getAttributeValue("ResidualRiskRating"));
    }

    private void populateHazardsTable(String type) {
        clearDisplayedHazards();
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
        displayedHazards.clear();
        if (hazardStates.get(type) != null) {
            for (Hazard h : hazardStates.get(type)) {
                displayedHazards.add(h);
                String[] row = new String[COLUMNS.length];
                row[0] = Integer.toString(h.getId());
                row[1] = h.getAttributeValue("Name");
                row[2] = h.getAttributeValue("InitialRiskRating");
                row[3] = h.getAttributeValue("ResidualRiskRating");
                row[4] = h.getAttributeValue("Status");
                dtm.addRow(row);
            }
        }
        hazardsTable.setModel(dtm);

    }
    
    private void clearDisplayedHazards() {
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
        displayedHazards.clear();
        hazardsTable.setModel(dtm);        
        nameTextField.setText("");
        conditionTextField.setText("");
        statusTextField.setText("");
        descriptionTextArea.setText("");
        clinicalJustificationTextArea.setText("");
        initialSeverityTextField.setText("");
        initialLikelihoodTextField.setText("");
        residualSeverityTextField.setText("");
        residualLikelihoodTextField.setText("");
        initialRatingTextField.setText("");
        residualRatingTextField.setText("");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectorPanel = new javax.swing.JPanel();
        statePanel = new javax.swing.JPanel();
        openCountLabel = new javax.swing.JLabel();
        controlledCountLabel = new javax.swing.JLabel();
        closedAcceptedCountLabel = new javax.swing.JLabel();
        openButton = new javax.swing.JButton();
        controlledButton = new javax.swing.JButton();
        closedAcceptedButton = new javax.swing.JButton();
        hazardsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        hazardsTable = new javax.swing.JTable();
        hazardPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        conditionTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        clinicalJustificationTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        statusTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        initialLikelihoodTextField = new javax.swing.JTextField();
        initialSeverityTextField = new javax.swing.JTextField();
        initialRatingTextField = new javax.swing.JTextField();
        residualLikelihoodTextField = new javax.swing.JTextField();
        residualSeverityTextField = new javax.swing.JTextField();
        residualRatingTextField = new javax.swing.JTextField();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        selectorPanel.setLayout(new javax.swing.BoxLayout(selectorPanel, javax.swing.BoxLayout.PAGE_AXIS));

        statePanel.setBackground(new java.awt.Color(229, 239, 248));
        statePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hazards in state"));

        openCountLabel.setText("jLabel12");

        controlledCountLabel.setText("jLabel12");

        closedAcceptedCountLabel.setText("jLabel12");

        openButton.setText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        controlledButton.setText("Controlled");
        controlledButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controlledButtonActionPerformed(evt);
            }
        });

        closedAcceptedButton.setText("Closed");
        closedAcceptedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closedAcceptedButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statePanelLayout = new javax.swing.GroupLayout(statePanel);
        statePanel.setLayout(statePanelLayout);
        statePanelLayout.setHorizontalGroup(
            statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(openButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(controlledButton, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                    .addComponent(closedAcceptedButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(openCountLabel)
                    .addComponent(controlledCountLabel)
                    .addComponent(closedAcceptedCountLabel))
                .addContainerGap(109, Short.MAX_VALUE))
        );
        statePanelLayout.setVerticalGroup(
            statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openCountLabel)
                    .addComponent(openButton))
                .addGap(18, 18, 18)
                .addGroup(statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(controlledCountLabel)
                    .addComponent(controlledButton))
                .addGap(18, 18, 18)
                .addGroup(statePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closedAcceptedCountLabel)
                    .addComponent(closedAcceptedButton))
                .addContainerGap(97, Short.MAX_VALUE))
        );

        selectorPanel.add(statePanel);

        hazardsPanel.setBackground(new java.awt.Color(229, 239, 248));
        hazardsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hazards"));

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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
                .addContainerGap())
        );
        hazardsPanelLayout.setVerticalGroup(
            hazardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardsPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );

        selectorPanel.add(hazardsPanel);

        add(selectorPanel);

        hazardPanel.setBackground(new java.awt.Color(229, 239, 248));
        hazardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hazard"));

        jLabel1.setText("Name");

        nameTextField.setEditable(false);
        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Type");

        conditionTextField.setEditable(false);

        jLabel3.setText("Description");

        descriptionTextArea.setEditable(false);
        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        jScrollPane3.setViewportView(descriptionTextArea);

        jLabel4.setText("Clinical justification");

        clinicalJustificationTextArea.setEditable(false);
        clinicalJustificationTextArea.setColumns(20);
        clinicalJustificationTextArea.setRows(5);
        jScrollPane4.setViewportView(clinicalJustificationTextArea);

        jLabel5.setText("Status");

        statusTextField.setEditable(false);

        jLabel6.setText("Initial likelihood");

        jLabel7.setText("Initial severity");

        jLabel8.setText("Initial risk rating");

        jLabel9.setText("Residual likelihood");

        jLabel10.setText("Residual severity");

        jLabel11.setText("Residual risk rating");

        initialLikelihoodTextField.setEditable(false);

        initialSeverityTextField.setEditable(false);

        initialRatingTextField.setEditable(false);

        residualLikelihoodTextField.setEditable(false);

        residualSeverityTextField.setEditable(false);

        residualRatingTextField.setEditable(false);

        javax.swing.GroupLayout hazardPanelLayout = new javax.swing.GroupLayout(hazardPanel);
        hazardPanel.setLayout(hazardPanelLayout);
        hazardPanelLayout.setHorizontalGroup(
            hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4)
                    .addGroup(hazardPanelLayout.createSequentialGroup()
                        .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(hazardPanelLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(initialSeverityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(hazardPanelLayout.createSequentialGroup()
                                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(initialLikelihoodTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(initialRatingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(hazardPanelLayout.createSequentialGroup()
                                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hazardPanelLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(residualLikelihoodTextField)
                            .addComponent(residualRatingTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                            .addComponent(residualSeverityTextField)))
                    .addGroup(hazardPanelLayout.createSequentialGroup()
                        .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(hazardPanelLayout.createSequentialGroup()
                        .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField)
                            .addComponent(conditionTextField)
                            .addComponent(statusTextField))))
                .addContainerGap())
        );
        hazardPanelLayout.setVerticalGroup(
            hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hazardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(conditionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(hazardPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7))
                    .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(residualSeverityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(initialSeverityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(residualLikelihoodTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initialLikelihoodTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hazardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initialRatingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(residualRatingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(hazardPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        populateHazardsTable("open");
    }//GEN-LAST:event_openButtonActionPerformed

    private void controlledButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_controlledButtonActionPerformed
        populateHazardsTable("controlled");
    }//GEN-LAST:event_controlledButtonActionPerformed

    private void closedAcceptedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closedAcceptedButtonActionPerformed
        populateHazardsTable("closed");
    }//GEN-LAST:event_closedAcceptedButtonActionPerformed

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameTextFieldActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea clinicalJustificationTextArea;
    private javax.swing.JButton closedAcceptedButton;
    private javax.swing.JLabel closedAcceptedCountLabel;
    private javax.swing.JTextField conditionTextField;
    private javax.swing.JButton controlledButton;
    private javax.swing.JLabel controlledCountLabel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JPanel hazardPanel;
    private javax.swing.JPanel hazardsPanel;
    private javax.swing.JTable hazardsTable;
    private javax.swing.JTextField initialLikelihoodTextField;
    private javax.swing.JTextField initialRatingTextField;
    private javax.swing.JTextField initialSeverityTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton openButton;
    private javax.swing.JLabel openCountLabel;
    private javax.swing.JTextField residualLikelihoodTextField;
    private javax.swing.JTextField residualRatingTextField;
    private javax.swing.JTextField residualSeverityTextField;
    private javax.swing.JPanel selectorPanel;
    private javax.swing.JPanel statePanel;
    private javax.swing.JTextField statusTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setProjectID(int p) 
            throws Exception
    {
        project = (Project)MetaFactory.getInstance().getFactory("Project").get(p);
        Collection<Hazard> hcollection = MetaFactory.getInstance().getFactory("Hazard").getEntries(p);

        for (Hazard h : hcollection) {
            if (h.isDeleted())
                continue;
//            hazards.add(h);
            // Rules:
            // A hazard with a residual rating of > 3 goes in "unacceptable" UNLESS
            // it has a non-empty clinical justification
            // A hazard with a residual rating of <= 3 goes in "accepted"
            // A Hazard with no residual rating goes in "workinprogress"
            if ((h.getAttributeValue("Status").equals("Select...") ) || (h.getAttributeValue("Status").equals("Open"))) {
                manageHazardStateArrays("open", h);
                continue;
            }
            if (h.getAttributeValue("Status").equals("Controlled")) {
                manageHazardStateArrays("controlled", h);
            } 
            if (h.getAttributeValue("Status").equals("Closed")) {
                manageHazardStateArrays("closed", h);
            }
        }
        openCountLabel.setText(getHazardStateCount("open"));
        controlledCountLabel.setText(getHazardStateCount("controlled"));
        closedAcceptedCountLabel.setText(getHazardStateCount("closed"));
    }

    private String getHazardStateCount(String t) {
        ArrayList<Hazard> list = hazardStates.get(t);
        if (list == null)
            return "0";
        return Integer.toString(list.size());
    }
    private void manageHazardStateArrays(String t, Hazard h) {
        if (hazardStates.get(t) == null) {
            hazardStates.put(t, new ArrayList<>());
        }
        hazardStates.get(t).add(h);
    }

    @Override
    public boolean notification(int evtype, Object o) {
        
        try {
            hazardStates = new HashMap<>();
            clearDisplayedHazards();
            setProjectID(project.getId());
            return false;
        }
        catch (Exception e) {
            SmartProject.getProject().log("Failed to process notification in HazardAnalysis", e);
            return true;
        }
    }
   @Override
    public JPanel getEditor(Object o) {
        return null;
    }    

    @Override
    public boolean isModified() {
        return false;
    }

}
