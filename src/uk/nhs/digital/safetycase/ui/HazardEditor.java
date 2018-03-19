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

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerListModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.UndockTabComponent;
import uk.nhs.digital.projectuiframework.ui.resources.ResourceUtils;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.ValueSet;
import uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor;

/**
 *
 * @author damian
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class HazardEditor extends javax.swing.JPanel 
    implements uk.nhs.digital.safetycase.ui.PersistableEditor
{
    private static final String RISK_MATRIX_IMAGE = "/uk/nhs/digital/safetycase/ui/risk_matrix_image.jpg";
    private static final int RISK_MATRIX_X = 722;
    private static final int RISK_MATRIX_Y = 186;
    private EditorComponent editorComponent = null;
    private final ArrayList<Hazard> hazards = new ArrayList<>();
    private final ArrayList<Relationship> displayedLinks = new ArrayList<>();
    private Hazard hazard = null;
    
    private ProcessStep parentProcessStep = null;
    
    private final String[] linkcolumns = {"Type", "Name", "Comment"};
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
            severity.add("Not set");
            severity.addAll(Arrays.asList(Hazard.SEVERITIES));
            ArrayList<String> likelihood = new ArrayList<>();
            likelihood.add("Not set");
            likelihood.addAll(Arrays.asList(Hazard.LIKELIHOODS));
            initialSeveritySpinnerModel.setList(severity);
            initialLikelihoodSpinnerModel.setList(likelihood);
            severity = new ArrayList<>();
            severity.add("Not set");
            severity.addAll(Arrays.asList(Hazard.SEVERITIES));  
            likelihood = new ArrayList<>();
            likelihood.add("Not set");
            likelihood.addAll(Arrays.asList(Hazard.LIKELIHOODS));            
            residualSeveritySpinnerModel.setList(severity);
            residualLikelihoodSpinnerModel.setList(likelihood);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void init() {
        initComponents();
        linksTable.setDefaultEditor(Object.class, null);
        linksTable.setDefaultRenderer(Object.class, new LinkTableCellRenderer());
        
        riskMatrixImageLabel.setIcon(riskMatrixImageIcon);
        initialSeveritySpinner.setModel(initialSeveritySpinnerModel);
        initialLikelihoodSpinner.setModel(initialLikelihoodSpinnerModel);
        residualSeveritySpinner.setModel(residualSeveritySpinnerModel);
        residualLikelihoodSpinner.setModel(residualLikelihoodSpinnerModel);
        try {
            ValueSet hazardStatus = MetaFactory.getInstance().getValueSet("HazardStatus");
            Iterator<String> statii = hazardStatus.iterator();
            statusComboBox.addItem("Select...");
            while(statii.hasNext()) {
                String s = statii.next();
                if (!s.contentEquals("Select..."))
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
            SmartProject.getProject().log("Failed to initialise HazardEditor", e);
        }
        DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
        linksTable.setModel(dtm);
        bowtieButton.setText("Create bowtie");                
    }
    /**
     * Creates new form HazardEditor
     */
    public HazardEditor() {
        init();
        SmartProject.getProject().addNotificationSubscriber(this);
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
    
    public HazardEditor setParent(ProcessStep ps) {
        parentProcessStep = ps;
        initialRiskRatingTextField.setText("-1");
        residualRiskRatingTextField.setText("-1");
        create = true;
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

        editorPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        summaryTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        conditionsComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        clinicalJustificationTextArea = new javax.swing.JTextArea();
        saveButton = new javax.swing.JButton();
        discardButton = new javax.swing.JButton();
        bowtieButton = new javax.swing.JButton();
        analysisPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        riskMatrixImageLabel = new javax.swing.JLabel();
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
        jLabel3 = new javax.swing.JLabel();
        statusComboBox = new javax.swing.JComboBox<>();
        linksPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        linksTable = new javax.swing.JTable();
        editLinksButton = new javax.swing.JButton();

        editorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Name");

        jLabel2.setText("Type");

        conditionsComboBox.setEditable(true);

        jLabel4.setText("Description");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        jScrollPane2.setViewportView(descriptionTextArea);

        jLabel5.setText("Clinical justification for risk acceptance");

        clinicalJustificationTextArea.setColumns(20);
        clinicalJustificationTextArea.setLineWrap(true);
        clinicalJustificationTextArea.setRows(5);
        clinicalJustificationTextArea.setWrapStyleWord(true);
        jScrollPane3.setViewportView(clinicalJustificationTextArea);

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        discardButton.setText("Delete");
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });

        bowtieButton.setText("Bowtie");
        bowtieButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bowtieButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(summaryTextField))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(editorPanelLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(49, 49, 49)
                        .addComponent(conditionsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(bowtieButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(discardButton)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bowtieButton)
                    .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(discardButton)
                        .addComponent(saveButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        analysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Create Bowtie before completing analysis"));
        analysisPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setText("Risk matrix");
        analysisPanel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 29, -1, -1));
        analysisPanel.add(riskMatrixImageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 50, 722, 186));

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
                    .addComponent(initialLikelihoodSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
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
                .addContainerGap(23, Short.MAX_VALUE))
        );

        analysisPanel.add(initialPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 250, -1, 140));

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
                    .addComponent(residualRiskRatingTextField)
                    .addComponent(residualSeveritySpinner))
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
                .addContainerGap(23, Short.MAX_VALUE))
        );

        analysisPanel.add(residualPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 250, -1, 140));

        jLabel3.setText("Status");
        analysisPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 413, -1, -1));

        analysisPanel.add(statusComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(88, 408, 467, -1));

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
        jScrollPane4.setViewportView(linksTable);

        javax.swing.GroupLayout linksPanelLayout = new javax.swing.GroupLayout(linksPanel);
        linksPanel.setLayout(linksPanelLayout);
        linksPanelLayout.setHorizontalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        linksPanelLayout.setVerticalGroup(
            linksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linksPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        analysisPanel.add(linksPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 439, -1, -1));

        editLinksButton.setText("Links ...");
        editLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinksButtonActionPerformed(evt);
            }
        });
        analysisPanel.add(editLinksButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(649, 408, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(analysisPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(analysisPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            displayedLinks.clear();
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                if (a != null) {
                    for (Relationship r : a) {
                        String m = r.getManagementClass();
                        if ((m == null) || (!m.contentEquals("Diagram"))) {                    
                            displayedLinks.add(r);

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
            SmartProject.getProject().log("Failed to process editLinks action in HazardEditor", e);
        }

    }//GEN-LAST:event_editLinksButtonActionPerformed

    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed

        if (hazard == null)
            return;
        
        int r = JOptionPane.showConfirmDialog(this, "Really delete this Hazard ?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);        
        if (r == JOptionPane.CANCEL_OPTION)
            return;
        
        try {
            MetaFactory.getInstance().getFactory("Hazard").delete(hazard);
            SmartProject.getProject().editorEvent(Project.DELETE, hazard);
//            SmartProject.getProject().removeNotificationSubscriber(this);
            SmartProject.getProject().getProjectWindow().closeContainer(this);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Error deleting Hazard. Send logs to support", "Delete failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to delete in HazardEditor", e);
        }
        
    }//GEN-LAST:event_discardButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveButton.setEnabled(false);

        if (statusComboBox.getSelectedIndex() == -1)
            statusComboBox.setSelectedIndex(0);
        if (hazard == null) {
            try {
                String duplicateWarning = MetaFactory.getInstance().getDuplicateCheckMessage("Hazard", "Hazard", summaryTextField.getText(),SmartProject.getProject().getCurrentProjectID(), hazard);
                if (duplicateWarning != null) {
                    JOptionPane.showMessageDialog(this, duplicateWarning, "Duplicate hazard name", JOptionPane.ERROR_MESSAGE);
                    saveButton.setEnabled(true);
                    return;
                }
            }
            catch (Exception e) {}
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
        hazard.setAttribute("ResidualLikelihood", Hazard.getLikelihood((String)residualLikelihoodSpinner.getValue()));
        hazard.setAttribute("ResidualSeverity", Hazard.getSeverity((String)residualSeveritySpinner.getValue()));
        hazard.setAttribute("ResidualRiskRating", (Integer.parseInt(residualRiskRatingTextField.getText())));
//        if (newObjectProjectId == -1)
//            hazard.setAttribute("ProjectID", Integer.parseInt(hazard.getAttributeValue("ProjectID")));
//        else 
            hazard.setAttribute("ProjectID",SmartProject.getProject().getCurrentProjectID());
        try {
            MetaFactory.getInstance().getFactory(hazard.getDatabaseObjectName()).put(hazard);
            if (create) {
                SmartProject.getProject().editorEvent(Project.ADD, hazard);
            } else {
                SmartProject.getProject().editorEvent(Project.UPDATE, hazard);
            }
            SmartProject.getProject().getProjectWindow().setViewTitle(this, "Hazard:" + hazard.getAttributeValue("Name"));
            if (parentProcessStep != null) {
               Relationship r = new Relationship(parentProcessStep.getId(), hazard.getId(), hazard.getDatabaseObjectName());
               parentProcessStep.addRelationship(r);
               MetaFactory.getInstance().getFactory(parentProcessStep.getDatabaseObjectName()).put(parentProcessStep);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to save Hazard. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in HazardEditor", e);
        }
        String g = hazard.getAttributeValue("GraphXml");
        if ((g == null) || (g.trim().length() == 0)) {
            bowtieButton.setText("Create bowtie");
            ((TitledBorder) analysisPanel.getBorder()).setTitle("Create Bowtie before completing analysis");
            bowtieButton.setBackground(Color.red);
            analysisPanel.setEnabled(false);
        } else {
            bowtieButton.setText("Edit bowtie");
            ((TitledBorder) analysisPanel.getBorder()).setTitle("");
            analysisPanel.setEnabled(true);
        }
        saveButton.setEnabled(true);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void bowtieButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bowtieButtonActionPerformed

        if (hazard == null) {
            JOptionPane.showMessageDialog(this, "Save this Hazard first, before editing the Bowtie", "Save first", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JPanel pnl = SmartProject.getProject().getExistingEditor(hazard, this);
        if (pnl != null) {
            SmartProject.getProject().getProjectWindow().selectPanel(pnl);
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
        EditorComponent ec = new EditorComponent(bge, hazard.getAttributeValue("Name"), SmartProject.getProject());
//        ExternalEditorView editorView = new ExternalEditorView(bge, hazard.getAttributeValue("Name"), tp);
        tp.setSelectedComponent(tp.add(ec.getTitle(), ec.getComponent()));
        tp.setTabComponentAt(tp.getSelectedIndex(), new UndockTabComponent(tp, SmartProject.getProject().getIcon("Bowtie")));  
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
    
    void setHazard(Hazard h) { hazard = h; }
    
    HashMap<String,DiagramEditorElement> getExistingBowtie(String xml)
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
            SmartProject.getProject().log("Failed to build existingBowtie in hazard editpr", e);
            return null;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel analysisPanel;
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
        displayedLinks.clear();
    }
    @Override
    public void setPersistableObject(Persistable p) 
    {
        if (p == null) {
            create = true;
            bowtieButton.setText("Create bowtie");
            bowtieButton.setBackground(Color.red);
            ((TitledBorder)analysisPanel.getBorder()).setTitle("Create Bowtie before completing analysis");
            analysisPanel.setEnabled(false);
            clearHazard();
            return;
        }
        int populated = -1;
        try {
            hazard = (Hazard) p;
            String g = hazard.getAttributeValue("GraphXml");
            if ((g == null) || (g.trim().length() == 0)) {
                bowtieButton.setText("Create bowtie");
                ((TitledBorder)analysisPanel.getBorder()).setTitle("Create Bowtie before completing analysis");
                bowtieButton.setBackground(Color.red);
                analysisPanel.setEnabled(false);
            } else {
                bowtieButton.setText("Edit bowtie");
                ((TitledBorder)analysisPanel.getBorder()).setTitle("");
                analysisPanel.setEnabled(true);
            }    
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
            SmartProject.getProject().getProjectWindow().setViewTitle(this, hazard.getAttributeValue("Name"));

            descriptionTextArea.setText(hazard.getAttributeValue("Description"));
            clinicalJustificationTextArea.setText(hazard.getAttributeValue("ClinicalJustification"));
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(editorPanel, "Failed to load Hazard for editing", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to set persistable object in HazardEditor", e);
        }
        try {
            HashMap<String,ArrayList<Relationship>> rels = hazard.getRelationshipsForLoad();
            DefaultTableModel dtm = new DefaultTableModel(linkcolumns, 0);
            for (String t : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(t);
                if (a != null) {
                    for (Relationship r : a) {
                        String m = r.getManagementClass();
                        if ((m == null) || (!m.contentEquals("Diagram"))) {
                            displayedLinks.add(r);
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
            JOptionPane.showMessageDialog(editorPanel, "Failed to load Hazard relationshis for editing", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to load hazard relationships", e);
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
        if (hazard == null)
            return false;
        if (o instanceof uk.nhs.digital.safetycase.data.Hazard) {
            Hazard c = (Hazard)o;
            if (c == hazard) {
                if (evtype == Project.DELETE) {
                    // Close this form and its container... 
                    SmartProject.getProject().getProjectWindow().closeContainer(this);
                    // then return true so that this form can be removed from the
                    // notifications list
                    return true;
                }
                SmartProject.getProject().getProjectWindow().setViewTitle(this, "Hazard:" + c.getTitle());
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
            Hazard c = (Hazard)o;
            if (c.getTitle().equals(hazard.getTitle()))
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
