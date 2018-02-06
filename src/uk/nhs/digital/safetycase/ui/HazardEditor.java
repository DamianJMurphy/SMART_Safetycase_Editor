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
import java.awt.Image;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.UndockTabComponent;
import uk.nhs.digital.projectuiframework.ui.resources.ResourceUtils;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.ValueSet;
import uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor;

/**
 *
 * @author damian
 */
public class HazardEditor extends javax.swing.JPanel 
    implements uk.nhs.digital.safetycase.ui.PersistableEditor
{
    private static final String RISK_MATRIX_IMAGE = "/uk/nhs/digital/safetycase/ui/risk_matrix_image.jpg";
    private static final int RISK_MATRIX_X = 722;
    private static final int RISK_MATRIX_Y = 186;
    private EditorComponent editorComponent = null;
    private ArrayList<Hazard> hazards = new ArrayList<>();
    private Hazard hazard = null;
    
    private final String[] linkcolumns = {"Type", "ID", "Name", "Comment"};
    private int newObjectProjectId = -1;
    private boolean create;
    
    private static ImageIcon riskMatrixImageIcon = null;
    private static final SpinnerListModel initialSeveritySpinnerModel = new SpinnerListModel();
    private static final SpinnerListModel initialLikelihoodSpinnerModel = new SpinnerListModel();
    private static final SpinnerListModel residualSeveritySpinnerModel = new SpinnerListModel();
    private static final SpinnerListModel residualLikelihoodSpinnerModel = new SpinnerListModel();

    
    static {
        try {
            riskMatrixImageIcon = ResourceUtils.getImageIcon(RISK_MATRIX_IMAGE);
            riskMatrixImageIcon = new ImageIcon(riskMatrixImageIcon.getImage().getScaledInstance(RISK_MATRIX_X, RISK_MATRIX_Y, Image.SCALE_DEFAULT));
            ArrayList<String> severity = new ArrayList<>();
            for (String s : Hazard.SEVERITIES)
                severity.add(s);
            ArrayList<String> likelihood = new ArrayList<>();
            for (String s : Hazard.LIKELIHOODS)
                likelihood.add(s);
            initialSeveritySpinnerModel.setList(severity);
            initialLikelihoodSpinnerModel.setList(likelihood);
            severity = new ArrayList<>();
            for (String s : Hazard.SEVERITIES)
                severity.add(s);  
            likelihood = new ArrayList<>();
            for (String s : Hazard.LIKELIHOODS)
                likelihood.add(s);            
            residualSeveritySpinnerModel.setList(severity);
            residualLikelihoodSpinnerModel.setList(likelihood);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates new form HazardEditor
     */
    public HazardEditor() {
        initComponents();
        riskMatrixImageLabel.setIcon(riskMatrixImageIcon);
        initialSeveritySpinner.setModel(initialSeveritySpinnerModel);
        initialLikelihoodSpinner.setModel(initialLikelihoodSpinnerModel);
        residualSeveritySpinner.setModel(residualSeveritySpinnerModel);
        residualLikelihoodSpinner.setModel(residualLikelihoodSpinnerModel);
        try {
            ValueSet hazardStatus = MetaFactory.getInstance().getValueSet("HazardStatus");
            Iterator<String> statii = hazardStatus.iterator();
            while(statii.hasNext()) {
                String s = statii.next();
                statusComboBox.addItem(s);
            }
            ArrayList<String> conds = MetaFactory.getInstance().getFactory("Hazard").getDistinctSet("GroupingType");
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
        DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
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

        editorPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        summaryTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        conditionsComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        statusComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        clinicalJustificationTextArea = new javax.swing.JTextArea();
        initialPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        initialSeveritySpinner = new javax.swing.JSpinner();
        initialLikelihoodSpinner = new javax.swing.JSpinner();
        initialRiskRatingTextField = new javax.swing.JTextField();
        residualPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        residualSeveritySpinner = new javax.swing.JSpinner();
        residualLikelihoodSpinner = new javax.swing.JSpinner();
        residualRiskRatingTextField = new javax.swing.JTextField();
        linksPanel = new javax.swing.JPanel();
        editLinksButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        bowtieButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        discardButton = new javax.swing.JButton();
        riskMatrixImageLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        editorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Summary");

        jLabel2.setText("Condition");

        conditionsComboBox.setEditable(true);

        jLabel3.setText("Status");

        jLabel4.setText("Description");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        jScrollPane2.setViewportView(descriptionTextArea);

        jLabel5.setText("Clinical justification");

        clinicalJustificationTextArea.setColumns(20);
        clinicalJustificationTextArea.setLineWrap(true);
        clinicalJustificationTextArea.setRows(5);
        jScrollPane3.setViewportView(clinicalJustificationTextArea);

        initialPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Initial ratings"));

        jLabel6.setText("Severity");

        jLabel7.setText("Likelihood");

        jLabel8.setText("Risk rating");

        initialSeveritySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                initialSeveritySpinnerStateChanged(evt);
            }
        });

        initialLikelihoodSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                initialLikelihoodSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout initialPanelLayout = new javax.swing.GroupLayout(initialPanel);
        initialPanel.setLayout(initialPanelLayout);
        initialPanelLayout.setHorizontalGroup(
            initialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(initialPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(initialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(26, 26, 26)
                .addGroup(initialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(initialLikelihoodSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(initialSeveritySpinner)
                    .addComponent(initialRiskRatingTextField))
                .addContainerGap())
        );
        initialPanelLayout.setVerticalGroup(
            initialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(initialPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(initialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(initialSeveritySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(initialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(initialLikelihoodSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(initialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(initialRiskRatingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        residualPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Residual ratings"));

        jLabel9.setText("Severity");

        jLabel10.setText("Likelihood");

        jLabel11.setText("Risk rating");

        residualSeveritySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                residualSeveritySpinnerStateChanged(evt);
            }
        });

        residualLikelihoodSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                residualLikelihoodSpinnerStateChanged(evt);
            }
        });

        residualRiskRatingTextField.setText("jTextField1");

        javax.swing.GroupLayout residualPanelLayout = new javax.swing.GroupLayout(residualPanel);
        residualPanel.setLayout(residualPanelLayout);
        residualPanelLayout.setHorizontalGroup(
            residualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residualPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(residualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addGap(32, 32, 32)
                .addGroup(residualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(residualLikelihoodSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                    .addComponent(residualSeveritySpinner)
                    .addComponent(residualRiskRatingTextField))
                .addContainerGap())
        );
        residualPanelLayout.setVerticalGroup(
            residualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residualPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(residualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(residualSeveritySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(residualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(residualLikelihoodSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(residualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(residualRiskRatingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

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

        bowtieButton.setText("Bowtie");
        bowtieButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bowtieButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editLinksButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bowtieButton))
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editLinksButton)
                    .addComponent(bowtieButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        discardButton.setText("Discard");
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });

        jLabel12.setText("Risk matrix");

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(riskMatrixImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(linksPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(summaryTextField))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conditionsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, editorPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(35, 35, 35)
                                .addComponent(statusComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(initialPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(editorPanelLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editorPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editorPanelLayout.createSequentialGroup()
                                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(discardButton))
                                    .addComponent(residualPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel12))
                .addContainerGap())
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(summaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(conditionsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addGap(3, 3, 3)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(riskMatrixImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(initialPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(residualPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton)
                    .addComponent(discardButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(linksPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLinksButtonActionPerformed
        
        if (hazard == null) {
            JOptionPane.showMessageDialog(this, "Save this Hazard first, before adding links", "Save first", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(this), true);
        linkEditor.add(new LinkEditor(hazard).setParent(linkEditor));
        linkEditor.pack();
        linkEditor.setVisible(true);

        try {
            HashMap<String,ArrayList<Relationship>> rels = hazard.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                for (Relationship r : a) {
                    String[] row = new String[linkcolumns.length];
                    row[0] = t;
                    row[1] = Integer.toString(r.getTarget());
                    row[2] = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget()).getAttributeValue("Name");
                    row[3] = r.getComment();
                    dtm.addRow(row);
                }
            }
            linksTable.setModel(dtm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_editLinksButtonActionPerformed

    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed
        summaryTextField.setText("");
        conditionsComboBox.setSelectedIndex(-1);
        statusComboBox.setSelectedIndex(-1);
        descriptionTextArea.setText("");
        clinicalJustificationTextArea.setText("");
        initialLikelihoodSpinner.setValue(Hazard.translateLikelihood(0));
        initialSeveritySpinner.setValue(Hazard.translateSeverity(0));
        initialRiskRatingTextField.setText(Integer.toString(Hazard.getRating(0, 0)));
        residualLikelihoodSpinner.setValue(Hazard.translateLikelihood(0));
        residualSeveritySpinner.setValue(Hazard.translateSeverity(0));
        residualRiskRatingTextField.setText(Integer.toString(Hazard.getRating(0, 0)));
    }//GEN-LAST:event_discardButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (statusComboBox.getSelectedIndex() == -1)
            statusComboBox.setSelectedIndex(0);
        if (hazard == null) {
            hazard = new Hazard();
            hazard.setAttribute("Name", summaryTextField.getText());
        }
        hazard.setAttribute("Description", descriptionTextArea.getText());
        hazard.setAttribute("ClinicalJustification", clinicalJustificationTextArea.getText());
        hazard.setAttribute("Status", (String)statusComboBox.getSelectedItem());
        hazard.setAttribute("GroupingType", (String)conditionsComboBox.getSelectedItem());
        hazard.setAttribute("InitialSeverity", Hazard.getSeverity((String)initialSeveritySpinner.getValue()));
        hazard.setAttribute("InitialLikelihood", Hazard.getLikelihood((String)initialLikelihoodSpinner.getValue()));
        hazard.setAttribute("InitialRiskRating", (Integer.parseInt(initialRiskRatingTextField.getText())));
        hazard.setAttribute("ResidualSeverity", Hazard.getLikelihood((String)residualLikelihoodSpinner.getValue()));
        hazard.setAttribute("ResidualLikelihood", Hazard.getSeverity((String)residualSeveritySpinner.getValue()));
        hazard.setAttribute("ResidualRiskRating", (Integer.parseInt(residualRiskRatingTextField.getText())));
//        if (newObjectProjectId == -1)
//            hazard.setAttribute("ProjectID", Integer.parseInt(hazard.getAttributeValue("ProjectID")));
//        else 
            hazard.setAttribute("ProjectID",SmartProject.getProject().getCurrentProjectID());
        try {
            MetaFactory.getInstance().getFactory(hazard.getDatabaseObjectName()).put(hazard);
            if (create) {
                editorComponent.notifyEditorEvent(Project.ADD, hazard);
            } else {
                editorComponent.notifyEditorEvent(Project.UPDATE, hazard);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void bowtieButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bowtieButtonActionPerformed

        if (hazard == null) {
            JOptionPane.showMessageDialog(this, "Save this Hazard first, before editing the Bowtie", "Save first", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        BowtieGraphEditor bge = new BowtieGraphEditor();
        String xml = hazard.getAttributeValue("GraphXml");
        bge.setHazardId(hazard.getId(), xml);
        if ((xml != null) && (xml.trim().length() > 0)) {
            HashMap<String,DiagramEditorElement> ex = getExistingBowtie(xml);
            if (ex != null)
                bge.setExistingBowtie(ex);
        }
        JTabbedPane tp = null;
        ProjectWindow pw = SmartProject.getProject().getProjectWindow();
        tp = pw.getMainWindowTabbedPane();
        EditorComponent ec = new EditorComponent(bge, "Bowtie:" + hazard.getAttributeValue("Name"), SmartProject.getProject());
//        ExternalEditorView editorView = new ExternalEditorView(bge, hazard.getAttributeValue("Name"), tp);
        tp.setSelectedComponent(tp.add(ec.getTitle(), ec.getComponent()));
        tp.setTabComponentAt(tp.getSelectedIndex(), new UndockTabComponent(tp));  
    }//GEN-LAST:event_bowtieButtonActionPerformed

    private void initialSeveritySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_initialSeveritySpinnerStateChanged
        initialRiskRatingTextField.setText(Integer.toString(Hazard.getRating((String)initialLikelihoodSpinner.getValue(), (String)initialSeveritySpinner.getValue())));
    }//GEN-LAST:event_initialSeveritySpinnerStateChanged

    private void initialLikelihoodSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_initialLikelihoodSpinnerStateChanged
        
        initialRiskRatingTextField.setText(Integer.toString(Hazard.getRating((String)initialLikelihoodSpinner.getValue(), (String)initialSeveritySpinner.getValue())));
        
    }//GEN-LAST:event_initialLikelihoodSpinnerStateChanged

    private void residualSeveritySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_residualSeveritySpinnerStateChanged
        residualRiskRatingTextField.setText(Integer.toString(Hazard.getRating((String)residualLikelihoodSpinner.getValue(), (String)residualSeveritySpinner.getValue())));
    }//GEN-LAST:event_residualSeveritySpinnerStateChanged

    private void residualLikelihoodSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_residualLikelihoodSpinnerStateChanged
        residualRiskRatingTextField.setText(Integer.toString(Hazard.getRating((String)residualLikelihoodSpinner.getValue(), (String)residualSeveritySpinner.getValue())));
    }//GEN-LAST:event_residualLikelihoodSpinnerStateChanged
    
    private HashMap<String,DiagramEditorElement> getExistingBowtie(String xml)
    {
        try {
        HashMap<String, DiagramEditorElement> bowtieElements = new HashMap<>();
        bowtieElements.put(hazard.getAttributeValue("GraphCellId"), new DiagramEditorElement(hazard));
        HashMap<String, ArrayList<Relationship>> hrels = hazard.getRelationshipsForLoad();
        if (hrels == null) {
            return null;
        }

        for (ArrayList<Relationship> a : hrels.values()) {
            for (Relationship r : a) {
                if ((r.getManagementClass() != null) && (r.getManagementClass().contentEquals("Diagram"))) {
                    Persistable p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                    bowtieElements.put(p.getAttributeValue("GraphCellId"), new DiagramEditorElement(p));
                }
            }
        }
        // Get the graph xml from the hazard, and use the same process that the "save" function does to
        // tie everything together
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(xml);
        InputSource is = new InputSource(sr);
        Element d = db.parse(is).getDocumentElement();
        NodeList nl = d.getElementsByTagName("mxCell");
        for (int i = 0; i < nl.getLength(); i++) {
            Element cell = (Element) nl.item(i);
            if (cell.hasAttribute("edge")) {
                String s = cell.getAttribute("source");
                String t = cell.getAttribute("target");
                DiagramEditorElement bt = bowtieElements.get(s);
//                    bt.fromCell = Integer.parseInt(s);
//                    bt.toCell = Integer.parseInt(t);
                bt.connections.add(t);
            }
        }
        return bowtieElements;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bowtieButton;
    private javax.swing.JTextArea clinicalJustificationTextArea;
    private javax.swing.JComboBox<String> conditionsComboBox;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton discardButton;
    private javax.swing.JButton editLinksButton;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JSpinner initialLikelihoodSpinner;
    private javax.swing.JPanel initialPanel;
    private javax.swing.JTextField initialRiskRatingTextField;
    private javax.swing.JSpinner initialSeveritySpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JPanel linksPanel;
    private javax.swing.JTable linksTable;
    private javax.swing.JSpinner residualLikelihoodSpinner;
    private javax.swing.JPanel residualPanel;
    private javax.swing.JTextField residualRiskRatingTextField;
    private javax.swing.JSpinner residualSeveritySpinner;
    private javax.swing.JLabel riskMatrixImageLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox<String> statusComboBox;
    private javax.swing.JTextField summaryTextField;
    // End of variables declaration//GEN-END:variables

    private void clearHazard() {
        this.discardButtonActionPerformed(null);
    }
    @Override
    public void setPersistableObject(Persistable p) 
    {
        if (p == null) {
            create = true;
            clearHazard();
            return;
        }
        int selected = -1;
        int populated = -1;
        try {
            hazard = (Hazard) p;
            summaryTextField.setText(hazard.getAttributeValue("Name"));
            for (int i = 0; i < conditionsComboBox.getModel().getSize(); i++) {
                if (conditionsComboBox.getItemAt(i).contentEquals(hazard.getAttributeValue("GroupingType"))) {
                    conditionsComboBox.setSelectedIndex(i);
                    break;
                }
            }
            String s = hazard.getAttributeValue("Status");
            for (int j = 0; j < statusComboBox.getItemCount(); j++) {
                if (s.contentEquals(statusComboBox.getItemAt(j))) {
                    statusComboBox.setSelectedIndex(j);
                    break;
                }
            }
            initialSeveritySpinner.setValue(Hazard.translateSeverity(hazard.getAttribute("InitialSeverity").getIntValue()));
            residualSeveritySpinner.setValue(Hazard.translateSeverity(hazard.getAttribute("ResidualSeverity").getIntValue()));
            initialLikelihoodSpinner.setValue(Hazard.translateLikelihood(hazard.getAttribute("InitialLikelihood").getIntValue()));
            residualLikelihoodSpinner.setValue(Hazard.translateLikelihood(hazard.getAttribute("ResidualLikelihood").getIntValue()));
            initialRiskRatingTextField.setText(hazard.getAttributeValue("InitialRiskRating"));
            residualRiskRatingTextField.setText(hazard.getAttributeValue("ResidualRiskRating"));

            descriptionTextArea.setText(hazard.getAttributeValue("Description"));
            clinicalJustificationTextArea.setText(hazard.getAttributeValue("ClinicalJustification"));
/*
            HashMap<String, ArrayList<Relationship>> rels = hazard.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                for (Relationship r : a) {
                    // Suppress "diagram editor management" relationships so they don't clutter up the view
                    if ((r.getManagementClass() != null) && (r.getManagementClass().contentEquals("Diagram"))) {
                        continue;
                    }
                    String[] row = new String[linkcolumns.length];
                    row[0] = t;
                    row[1] = Integer.toString(r.getTarget());
                    row[2] = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget()).getAttributeValue("Name");
                    row[3] = r.getComment();
                    dtm.addRow(row);
                }
            }
            linksTable.setModel(dtm);
*/
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            HashMap<String,ArrayList<Relationship>> rels = hazard.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                for (Relationship r : a) {
                    String m = r.getManagementClass();
                    if ((m == null) || (!m.contentEquals("Diagram"))) {
                        String[] row = new String[linkcolumns.length];
                        row[0] = t;
                        row[1] = Integer.toString(r.getTarget());
                        row[2] = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget()).getAttributeValue("Name");
                        row[3] = r.getComment();
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
}
