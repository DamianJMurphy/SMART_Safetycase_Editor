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

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.w3c.dom.Document;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.Project;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.PersistableFilter;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.Report;
import uk.nhs.digital.safetycase.data.SystemFunction;
import uk.nhs.digital.safetycase.data.System;
import uk.nhs.digital.safetycase.data.Process;
import uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor;
import uk.nhs.digital.safetycase.ui.processeditor.ProcessGraphEditor;
import uk.nhs.digital.safetycase.ui.systemeditor.SystemEditorDetails;
import uk.nhs.digital.safetycase.ui.systemeditor.SystemGraphEditor;

/**
 *
 * @author SHUL1
 */
public class SafetyReportEditor
        extends javax.swing.JPanel
        implements uk.nhs.digital.safetycase.ui.PersistableEditor {

    private EditorComponent editorComponent = null;
    private Project project = null;
    
    private boolean modified = false;

    private Report report = null;

    private int newObjectProjectId = -1;

    private Project proj = null;
    protected PersistableFactory<System> systemFactory = null;
    protected PersistableFactory<SystemFunction> systemFunctionFactory = null;
    protected List<processStepRelations> psRels = null;
    
    private Persistable focus = null;

    private ArrayList<Relationship> tableMap = null;
    private ArrayList<Persistable> targetInstances = null;

    //Report path
    private static String FILEPATH = "C:/SMART/Report/";

    // HTML TAGS
    private static final String TITLE_START = "<div style=\"text-align:center\"><p style=\"display: inline;background-color: #f0f0f0;font-size:42;padding:7px; border-bottom:#4e95f4 1px solid;\" >";
    private static final String TITLE_END = "</P></div><br/><br/>\n";

    private static final String H1_START = "<H1>"; //"<H1 style=\"text-decoration: underline;color:#4169E1\" >";
    private static final String H1_END = "</H1>\n";
    private static final String H2_ANCHOR_START = "<H2 style=\"color:#4682B4\" id=\"%s\" >";
    private static final String H2_START = "<H2 style=\"color:#4682B4\" >";
    private static final String H2_END = "</H2>\n";
    private static final String H3_START = "<H3 style=\"color:#7B68EE\" >";
    private static final String H3_END = "</H3>\n";
    private static final String TABLE_START = "<table class=\"AssociationsTable\" >";
    private static final String TABLE_END = "</table>\n";
    private static final String TR_START = "<tr>";
    private static final String TR_END = "</tr>\n";
    private static final String TD_START = "<td>";
    private static final String TD_END = "</td>\n";

    private static final String P_START = "<P>";
    private static final String PCLASS_START = "<P class=\"rating%s\">";
    private static final String P_END = "</P>\n";
    private static final String DIV_START = "<div>";//<div><img src="data:image/png;base64, iVB" alt="System" /></div> 
    private static final String DIV_END = "</div>\n";
    private static String IMG_TAG = "<div style=\"text-align: center;\"><img src=\"data:image/png;base64, %s \" alt=\"%s \" /></div>\n";
    private static String RELATED_TO = " Used in %s ( %s ).";
    // Titles for sections
    private static final String INTRO_SECTION = H1_START + "Introduction" + H1_END;
    private static final String SYSTEM_DEFINITION_SECTION = H1_START + "System Definition / Overview" + H1_END;
    private static final String CLINICAL_RISK_MANAGEMENT_SECTION = H1_START + "Hazard Analysis" + H1_END;
    private static final String RISK_ANALYSIS = H1_START + "Clinical Risk Analysis" + H1_END;
    private static final String RISK_EVALUATION = H1_START + "Clinical Risk Evaluation " + H1_END;
    private static final String RISK_CONTROL = H1_START + "Clinical Risk Control" + H1_END;
    private static final String HAZARD_LOG = H1_START + "Hazard Log " + H1_END;
    private static final String TEST_ISSUES = H1_START + "Test Issues " + H1_END;
    private static final String SUMMARY_SAFETY_STATEMENT = H1_START + "Summary Safety Statement " + H1_END;
    private static final String QA_OCUMENT_APPROVAL = H1_START + "Quality Assurance and Document Approval " + H1_END;
    private static final String CONFIG_CONTROL = H1_START + "Configuration Control and Management " + H1_END;

    private static final String HTML = "<html>\n<head>\n<title> Project Report</title>\n__CSSSTYLES__\n</head>\n<body bgcolor=\"#FBFCFC\">\n __HTMLBODY__ \n</body>\n</html>";
    private static final String CSS = "<style type=\"text/css\">\n"
            + " .LinksTable{\n"
            + "		border-collapse:collapse; \n"
            + "	}\n"
            + "	.LinksTable td{ \n"
            + "		padding:7px; border-bottom:#4e95f4 1px solid;\n"
            + "	}\n"
            + "	.LinksTable tr td:nth-child(odd){ \n"
            + "		background: #f0f0f0;\n"
            + "	}\n"
            + "	.LinksTable tr td:nth-child(even){\n"
            + "		background: #eaece5;\n"
            + "	}\n"
            + "	.AssociationsTable{\n"
            + "		border-collapse:collapse;\n"
            + "		background: #f0f0f0;\n"
            + "	}\n"
            + "	.AssociationsTable td{ \n"
            + "		padding:7px;border-bottom:#4e95f4 1px solid;\n"
            + "	}\n"
            + "	.tr2{\n"
            + "		background: #eaece5;\n"
            + "	}\n"
            + "	h1{\n"
            + "		display: inline; \n"
            + "		border-bottom: 1px solid #990000; \n"
            + "		background-color: #F0EBEB;\n"
            + "	}\n"
            + "	.h2{\n"
            + "		display: inline;\n"
            + "		border-left: 2px solid #FF8000;\n"
            + "		border-bottom: 1px outset #FF9999; \n"
            + "		background-color: #F0EBEB;\n"
            + "	}\n"
            + "	li{\n"
            + "		float:left; \n"
            + "		margin-right : 5px;\n"
            + "	}\n"
            + "	\n"
            + "	.rating0{\n"
            + "	display: inline;\n"
            + "	background:#00FF00;\n"
            + "	}\n"
            + "	.rating1{\n"
            + "	display: inline;\n"
            + "	background:#00FF00;\n"
            + "	}\n"
            + "	.rating2{\n"
            + "	display: inline;\n"
            + "	background:#00FF00;\n"
            + "	}\n"
            + "	.rating3{\n"
            + "	display: inline;\n"
            + "	background:#FFC200;\n"
            + "	}\n"
            + "	.rating4{\n"
            + "	display: inline;\n"
            + "	background:red;\n"
            + "       color:white;\n"
            + "	}\n"
            + "	.rating5{\n"
            + "	display: inline;\n"
            + "	background:red;\n"
            + "       color:white;\n"
            + "	}\n"
            + "</style>";
    private static String CSS1 = "";
    //private static String SYSTEMS_TABLE_START = "<table style=\"width:90%;\"><tr><td> System Links:</td>";
    private static String SYSTEMS_TABLE_START = "<table class=\"LinksTable\">\n<tr>\n<td> Systems:</td>\n<td>\n";
    private static String SYSTEMS_TABLE_END = "</tr>\n</table>\n";

    private static String SYSTEM_TABLE = null;
    private static String HAZARD_TABLE = null;

    //private static String HAZARD_TABLE_START = "<table style=\"margin: auto;\"><tr><td> Hazard Links:</td>";
    private static String HAZARD_TABLE_START = "<table class=\"LinksTable\">\n<tr>\n<td> Hazards:</td>\n<td>\n";
    private static String HAZARD_TABLE_END = "</tr>\n</table>\n<br /><br />";

    private static String SYSTEM_REPORT = null;
    private static String HAZARD_REPORT = null;

    /**
     * Creates new form ReportsEditor
     *
     * @throws java.lang.Exception
     */
    public SafetyReportEditor() throws Exception {
        initComponents();
        SmartProject.getProject().addNotificationSubscriber(this);

    }

    @Override
    public boolean wantsScrollPane() {
        return true;
    }

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

        sectionsPanel = new javax.swing.JTabbedPane();
        introPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        titleTextField = new javax.swing.JTextField();
        introductionEditor = new uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel();
        crmPanel = new javax.swing.JPanel();
        crmTextArea = new uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel();
        safetySystemPanel = new javax.swing.JPanel();
        safetyTextArea = new uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel();
        qaPanel = new javax.swing.JPanel();
        qaTextArea = new uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel();
        configPanel = new javax.swing.JPanel();
        configTextArea = new uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel();
        buttonBar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();

        jLabel1.setText("Title");

        javax.swing.GroupLayout introPanelLayout = new javax.swing.GroupLayout(introPanel);
        introPanel.setLayout(introPanelLayout);
        introPanelLayout.setHorizontalGroup(
            introPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(introPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(introPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(introductionEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(introPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(titleTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)))
                .addContainerGap())
        );
        introPanelLayout.setVerticalGroup(
            introPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, introPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(introPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(introductionEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE))
        );

        sectionsPanel.addTab("Introduction", introPanel);

        javax.swing.GroupLayout crmPanelLayout = new javax.swing.GroupLayout(crmPanel);
        crmPanel.setLayout(crmPanelLayout);
        crmPanelLayout.setHorizontalGroup(
            crmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(crmTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
        );
        crmPanelLayout.setVerticalGroup(
            crmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(crmTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
        );

        sectionsPanel.addTab("Clinical risk management", crmPanel);

        javax.swing.GroupLayout safetySystemPanelLayout = new javax.swing.GroupLayout(safetySystemPanel);
        safetySystemPanel.setLayout(safetySystemPanelLayout);
        safetySystemPanelLayout.setHorizontalGroup(
            safetySystemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(safetyTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
        );
        safetySystemPanelLayout.setVerticalGroup(
            safetySystemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(safetyTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
        );

        sectionsPanel.addTab("Safety statement", safetySystemPanel);

        javax.swing.GroupLayout qaPanelLayout = new javax.swing.GroupLayout(qaPanel);
        qaPanel.setLayout(qaPanelLayout);
        qaPanelLayout.setHorizontalGroup(
            qaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qaTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
        );
        qaPanelLayout.setVerticalGroup(
            qaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qaTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
        );

        sectionsPanel.addTab("QA and Development", qaPanel);

        javax.swing.GroupLayout configPanelLayout = new javax.swing.GroupLayout(configPanel);
        configPanel.setLayout(configPanelLayout);
        configPanelLayout.setHorizontalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(configTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
        );
        configPanelLayout.setVerticalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(configTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
        );

        sectionsPanel.addTab("Config management", configPanel);

        buttonBar.setRollover(true);

        newButton.setText("New");
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        buttonBar.add(newButton);

        saveButton.setText("Save");
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonBar.add(saveButton);

        deleteButton.setText("Delete");
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        buttonBar.add(deleteButton);

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        buttonBar.add(exportButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(sectionsPanel))
                    .addComponent(buttonBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(buttonBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sectionsPanel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed

        //String test = ProcessReport();
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FILEPATH = fc.getSelectedFile().toString();
        } else {
            return;
        }

        StringBuilder body = new StringBuilder(TITLE_START);
        body.append(titleTextField.getText());
        body.append(TITLE_END);
        body.append("__SYSTEMTABLEPLACEHOLDER__");
        body.append("__HAZARDABLEPLACEHOLDER__");
        body.append(INTRO_SECTION);
        body.append(P_START);
        body.append(introductionEditor.getBodyText());
        body.append(P_END);
        body.append("<br /><br />");
        body.append(SYSTEM_DEFINITION_SECTION);
        body.append(P_START);
        //body.append(GenerateSystemReport());
        body.append(SystemReport()); // System Report
        body.append(P_END);
        body.append("<br /><br />");
        body.append(CLINICAL_RISK_MANAGEMENT_SECTION);
        body.append(P_START);
        body.append(crmTextArea.getBodyText());
        body.append(P_END);
        body.append(P_START);
        body.append(ProcessReport());
        //body.append(HazardReport()); // HAZARD Report
        body.append(P_END);
        body.append("<br /><br />");
        body.append(QA_OCUMENT_APPROVAL);
        body.append(P_START);
        body.append(qaTextArea.getBodyText());
        body.append(P_END);
        body.append("<br /><br />");
        body.append(SUMMARY_SAFETY_STATEMENT);
        body.append(P_START);
        body.append(safetyTextArea.getBodyText());
        body.append(P_END);
        body.append("<br /><br />");
        body.append(CONFIG_CONTROL);
        body.append(P_START);
        body.append(configTextArea.getBodyText());
        body.append(P_END);
        body.append("<br /><br />");

        String data = HTML.replace("__HTMLBODY__", body.toString());
        data = data.replace("__CSSSTYLES__", CSS);
        data = data.replace("__SYSTEMTABLEPLACEHOLDER__", SYSTEM_TABLE);
        data = data.replace("__HAZARDABLEPLACEHOLDER__", HazardLinks());
        data = data.replace("\\n", "  ");
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String dateTime = df.format(new Date(java.lang.System.currentTimeMillis()));
        String name;
        if (proj != null) {
            name = proj.getTitle().trim() + "_" + dateTime + ".html";
        } else {
            name = "TestReport" + "_" + dateTime + ".html";
        }
        String fileName = FILEPATH + "/" + name;
        if (new File(FILEPATH).exists()) {
            FileWriter fWriter = null;
            BufferedWriter writer = null;
            try {
                fWriter = new FileWriter(fileName);
                writer = new BufferedWriter(fWriter);
                writer.write(data);
                writer.close();
                fWriter.close();
                JOptionPane.showMessageDialog(null, fileName, "Report Created", JOptionPane.INFORMATION_MESSAGE);
            } catch (HeadlessException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_exportButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        titleTextField.setText("");
        introductionEditor.setText("");
        configTextArea.setText("");
        crmTextArea.setText("");
        qaTextArea.setText("");
        safetyTextArea.setText("");
    }//GEN-LAST:event_newButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to Delete this Report?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION) {
            return;
        }
        try {
            MetaFactory.getInstance().getFactory(report.getDatabaseObjectName()).delete(report);
            editorComponent.notifyEditorEvent(uk.nhs.digital.projectuiframework.Project.DELETE, report);
            newButtonActionPerformed(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to delete report. Send logs to support", "Delete failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to delete in ReportEditor", e);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        boolean newreport = false;
        if (report == null) {
            report = new Report();
            newreport = true;
        }
        report.setAttribute("Title", titleTextField.getText());
        report.setAttribute("Introduction", introductionEditor.getText());
        report.setAttribute("CCMdetails", configTextArea.getText());
        report.setAttribute("CRMdetails", crmTextArea.getText());
        report.setAttribute("QAADdetails", qaTextArea.getText());
        report.setAttribute("SummarySafetySystemDetails", safetyTextArea.getText());
        report.setAttribute("PreparedBy", java.lang.System.getProperty("user.name"));
        if (proj == null) {
            report.setAttribute("ProjectID", newObjectProjectId);
        } else {
            report.setAttribute("ProjectID", proj.getId());
        }
        try {
            MetaFactory.getInstance().getFactory(report.getDatabaseObjectName()).put(report);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save report. Send logs to support", "Save failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in reportEditor", e);
        }
        if (newreport) {
            editorComponent.notifyEditorEvent(uk.nhs.digital.projectuiframework.Project.ADD, report);
        } else {
            editorComponent.notifyEditorEvent(uk.nhs.digital.projectuiframework.Project.UPDATE, report);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private String SystemReport() {
        StringBuilder stSB = new StringBuilder();
        StringBuilder systemSB = new StringBuilder();
        stSB.append(SYSTEMS_TABLE_START);
        ArrayList<Persistable> sys = null;
        try {
            if (proj != null) {
                sys = MetaFactory.getInstance().getChildren("System", "ProjectID", proj.getId());
            } else {
                sys = MetaFactory.getInstance().getChildren("System", "ProjectID", newObjectProjectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (sys != null) {
            for (Persistable p : sys) {
                if (!p.isDeleted()) {
                    uk.nhs.digital.safetycase.data.System sy = (uk.nhs.digital.safetycase.data.System) p;
                    int pid = Integer.parseInt(sy.getAttributeValue("ParentSystemID"));
                    if (pid == -1) {
                        //systemsNode.add(populateSystemWithChildren(sy));
//                        String xml = sy.getAttributeValue("GraphXml");
//                        if (xml != null || xml.trim().length() != 0) {
//                            systemSB.append(String.format(IMG_TAG, GeneratePersistableImage(p), p.getTitle()));
//                        }
                        //stSB.append(String.format("<td> <a href=\"#%s\">%s</a></td>", ("S"+p.getId()), p.getTitle()));
                        stSB.append(String.format("<li><a href=\"#%s\">%s</a></li>\n", ("S" + p.getId()), p.getTitle()));
                        systemSB.append(String.format(H2_ANCHOR_START, ("S" + p.getId())));
                        systemSB.append(p.getAttributeValue("Name"));
                        systemSB.append(H2_END);
                        systemSB.append(P_START);
                        systemSB.append("System Version: ");
                        systemSB.append(p.getAttributeValue("Version"));
                        //systemSB.append(SmartProject.getProject().getApplicationIdentifier());
                        systemSB.append(P_END);
                        systemSB.append(P_START);
                        systemSB.append(p.getAttributeValue("Description"));
                        systemSB.append(P_END);
                        String xml = sy.getAttributeValue("GraphXml");
                        if ((xml != null) && (xml.trim().length() != 0)) {
                            systemSB.append(String.format(IMG_TAG, GeneratePersistableImage(p), p.getTitle()));
                        }
                        // SystemFunction section
                        systemSB.append(systemFunctionReport(p));

                        // subsystem section
                        systemSB.append(subSystemReport(p));
//                       
                    }
                }
            }
        }
        stSB.append("</td>\n").append(SYSTEMS_TABLE_END);
        SYSTEM_TABLE = stSB.toString();
        return systemSB.toString();
    }

    private String systemFunctionReport(Persistable p) {
        StringBuilder sfSB = new StringBuilder();
        ArrayList<Relationship> systemFunctions = p.getRelationships("SystemFunction");
        if ((systemFunctions == null) && p.getDatabaseObjectName().contentEquals("SystemFunction")) {
            return sfSB.toString();
        } else if (systemFunctions == null) { // if a system has no functions
            sfSB.append(P_START).append(String.format("This %s has no immediate functions.", p.getDatabaseObjectName())).append(P_END);
            return sfSB.toString();
        }
        for (Relationship r : systemFunctions) {
            // if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
            try {
                Persistable sf = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                if (!sf.isDeleted()) {
                    sfSB.append(H3_START);
                    sfSB.append(sf.getAttributeValue("Name"));
                    sfSB.append(H3_END);
                    sfSB.append(P_START);
                    sfSB.append(sf.getAttributeValue("Description"));
                    sfSB.append(P_END);
                    // check for children/sub children realtionship
                    //List<Persistable> lp= new ArrayList<>();
                    //TODO: to ask hannah if she wants sub systems and their function in main system or display each system separately..
                    if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
                        List<Persistable> pl = findFunctionRelations(sf, new ArrayList<>());
                        Boolean relFound = false;
                        for (Persistable pe : pl) {
                            //systemSB.append(p.getTitle());
                            relFound = true;
                            sfSB.append(P_START).append(String.format(RELATED_TO, pe.getDatabaseObjectName(), pe.getTitle())).append(P_END);
                        }
                        if (!relFound) {
                            sfSB.append(P_START).append(String.format("This %s is not associated with any care settings.", sf.getDatabaseObjectName())).append(P_END);
                        }
                    }

                    // write subfunction text only if it exist
                    String subFunc = systemFunctionReport(sf);
                    if (subFunc != null && !subFunc.isEmpty()) {
                        sfSB.append(P_START).append("Sub Function").append(P_END);
                        sfSB.append(systemFunctionReport(sf));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sfSB.toString();

    }

    private String subSystemReport(Persistable p) {
        StringBuilder ssSB = new StringBuilder();
        ArrayList<Relationship> subSystems = p.getRelationships("System");
        if (subSystems == null) {
            //sfSB.append(P_START).append(String.format("This %s has no SystemFunction.", p.getDatabaseObjectName())).append(P_END);
            return ssSB.toString();
            //return null;
        }
        for (Relationship r : subSystems) {
            // if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
            try {
                Persistable ss = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                if (!ss.isDeleted()) {
                    ssSB.append(H2_START);
                    ssSB.append(ss.getAttributeValue("Name"));
                    ssSB.append(H2_END);
                    ssSB.append(P_START);
                    ssSB.append("Sub-System Version: ");
                    //ssSB.append(ss.getAttributeValue("Version"));
                    ssSB.append(SmartProject.getProject().getApplicationIdentifier());
                    ssSB.append(P_END);
                    ssSB.append(P_START);
                    ssSB.append(ss.getAttributeValue("Description"));
                    ssSB.append(P_END);
                    // SystemFunction section
                    ssSB.append(systemFunctionReport(ss));
                    // end of systemfunction section

                    // check for child subSystem
                    ssSB.append(subSystemReport(ss));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }

        return ssSB.toString();

    }
private List<Persistable> findFunctionRelations(Persistable relatedObject, List<Persistable> persistablelist)
            throws Exception {
        List<Persistable> lp = persistablelist;
        PersistableFactory<uk.nhs.digital.safetycase.data.SystemFunction> pfsf = MetaFactory.getInstance().getFactory("SystemFunction");
        
        
        
        Persistable p;
        try {
            if (relatedObject.getDatabaseObjectName().equals("SystemFunction")) {
//                uk.nhs.digital.safetycase.data.SystemFunction systemfunction = pfsf.get(relatedObject.getId());
//                HashMap<String, ArrayList<Relationship>> hrels = systemfunction.getRelationshipsForLoad();
//                if (hrels != null) {
//                    for (ArrayList<Relationship> a : hrels.values()) {
//                        for (Relationship r : a) {
//                            if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
//                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
//                                lp.add(p);
//                                // find any sub children realtions
//                                findFunctionRelations(p, lp);
//                            }
//                        }
//                    }
//                }  
                 HashMap<String, ArrayList<Relationship>>  hrels = MetaFactory.getInstance().findFirstOrderRelationshipsForTarget(relatedObject, true, true);
                 if (hrels != null) {
                    for (Map.Entry<String, ArrayList<Relationship>> entry : hrels.entrySet()) {
                        if(!entry.getKey().equalsIgnoreCase("System"))
                        {
                        String Source = entry.getKey();
                        ArrayList<Relationship> Rels = entry.getValue();
                    //for (ArrayList<Relationship> a : entry.getValue()) {
                        for (Relationship r : Rels) {
                                p = MetaFactory.getInstance().getFactory(Source).get(r.getSource());
                               // p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                                lp.add(p);
                                // find any sub children realtions
                                
                        }
                   // }
                    }
                    }
                }  
                  
                         
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        
        return lp;
    }

private Persistable findParent(Persistable ch, String parentType)
{
 ArrayList<Persistable> parent=null;
 ArrayList<Relationship> pRels = null;
         ArrayList<Persistable> sf=null;
          try {
            if (proj != null) {
                parent = MetaFactory.getInstance().getChildren(parentType, "ProjectID", proj.getId());
                
            }
            } catch (Exception e) {
                        e.printStackTrace();
                    }
        Persistable pl = null;
        for(Persistable p: parent)
        {
            pRels = p.getRelationships(ch.getDatabaseObjectName());
            if (pRels != null)
            {
                for(Relationship r: pRels)
                {
                    if(r.getTarget() == p.getId())
                    {
                        
                    }
                }
            }
        }
        ArrayList<Relationship> allRels = new ArrayList<>();
        ArrayList<Relationship> hRels = ch.getRelationships(parentType);
        
        if (hRels == null) {
            hRels = ch.getRelationships("System");
            if(hRels == null)
            {
            return pl;
            }
        }
        for (Relationship r : hRels) {
            if (!r.isDeleted()) {
                String m = r.getManagementClass();
                if ((m == null) || (m.contains("Diagram"))) {
                    try {
                        Persistable hr = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        return hr;
                       // if(r.getTargetType().equalsIgnoreCase("System"))
                       // hazardDependandsystemANDfunction(hr, r.getTargetType(), pl);
                        
                            
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return pl;
    
}
// <editor-fold defaultstate="collapsed" desc="Unused_Code"> 
//    private List<Persistable> findFunctionRelations(Persistable relatedObject, List<Persistable> persistablelist)
//            throws Exception {
//        List<Persistable> lp = persistablelist;
//        PersistableFactory<uk.nhs.digital.safetycase.data.SystemFunction> pfsf = MetaFactory.getInstance().getFactory("SystemFunction");
//        Persistable p;
//        try {
//            if (relatedObject.getDatabaseObjectName().equals("SystemFunction")) {
//                uk.nhs.digital.safetycase.data.SystemFunction systemfunction = pfsf.get(relatedObject.getId());
//                HashMap<String, ArrayList<Relationship>> hrels = systemfunction.getRelationshipsForLoad();
//                if (hrels != null) {
//                    for (ArrayList<Relationship> a : hrels.values()) {
//                        for (Relationship r : a) {
//                            if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
//                                p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
//                                lp.add(p);
//                                // find any sub children realtions
//                                findFunctionRelations(p, lp);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return lp;
//    }
// </editor-fold>

    private String ProcessReport() {
        StringBuilder processSB = new StringBuilder();
        ArrayList<Persistable> processList = null;
        try {
            if (proj != null) {
                processList = MetaFactory.getInstance().getChildren("Process", "ProjectID", proj.getId());
            } else {
                processList = MetaFactory.getInstance().getChildren("Process", "ProjectID", newObjectProjectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (processList != null) {
            for (Persistable p : processList) {
                if (!p.isDeleted()) {
                    Process process = (Process) p;
//                    int pid = Integer.parseInt(process.getAttributeValue("ParentSystemID"));
//                    if (pid == -1) {
                    //systemsNode.add(populateSystemWithChildren(sy));
//                        String xml = sy.getAttributeValue("GraphXml");
//                        if (xml != null || xml.trim().length() != 0) {
//                            systemSB.append(String.format(IMG_TAG, GeneratePersistableImage(p), p.getTitle()));
//                        }
                    //stSB.append(String.format("<td> <a href=\"#%s\">%s</a></td>", ("S"+p.getId()), p.getTitle()));
                    //stSB.append(String.format("<li><a href=\"#%s\">%s</a></li>\n", ("S" + p.getId()), p.getTitle()));
                    processSB.append(String.format(H2_ANCHOR_START, ("P" + p.getId())));
                    processSB.append("Care Process : ");
                    processSB.append(p.getAttributeValue("Name"));
                    processSB.append(H2_END);
                    processSB.append(P_START);
                    processSB.append("Process Version: ");
                    processSB.append(p.getAttributeValue("Version"));
                    //systemSB.append(SmartProject.getProject().getApplicationIdentifier());
                    processSB.append(P_END);
//                    processSB.append(P_START);
//                    processSB.append(p.getAttributeValue("Description"));
//                    processSB.append(P_END);
                    String xml = process.getAttributeValue("GraphXml");
                    if ((xml != null) && (xml.trim().length() != 0)) {
                        processSB.append(String.format(IMG_TAG, GeneratePersistableImage(p), p.getTitle()));
                    }
                    
                    // Proces Step section
                    String psRelations = processStepRelations(p);
                    if( psRelations == null || psRelations.isEmpty()){
                        String t = "";
                    }
                    if(psRels.size()>0){
                        processSB.append(HazardReport(p.getAttributeValue("Name")));
                    }else{
                        processSB.append("This Process has no hazard links");
                    }
                        
                    
                }
            }
        }

        return processSB.toString();
    }

    private String processStepRelations(Persistable p) {
        StringBuilder psSB = new StringBuilder();

        // Map<Hazard, Persistable> maps = new HashMap<Hazard, Persistable>();
        psRels = new ArrayList<processStepRelations>();

        ArrayList<Persistable> processSteps;
        try {
            processSteps = MetaFactory.getInstance().getChildren("ProcessStep", "ProcessID", p.getId());

            // ArrayList<Relationship> processSteps = p.getRelationships("ProcessStep");
//            if ((processSteps == null) && p.getDatabaseObjectName().contentEquals("Hazard")) {
//                return psSB.toString();
//            } else 
                if (processSteps == null) { // if a ProcessStep has no Relations
                psSB.append(P_START).append(String.format("This %s has no ProcessSteps.", p.getDatabaseObjectName())).append(P_END);
                return psSB.toString();
            }
            for (Persistable ps : processSteps) {
                String ss = ps.getAttributeValue("Name");
                if ((ps.getTitle() != null) && ((!ps.getAttributeValue("Name").equalsIgnoreCase("Start")) && (!ps.getAttributeValue("Name").equalsIgnoreCase("Stop")))) {
                    ArrayList<Relationship> Relations = ps.getRelationships("Hazard");
                    if (Relations != null) {
                        ArrayList<Hazard> hl  = new ArrayList<>();
                        for (Relationship r : Relations) {
                            Persistable hz = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                            //maps.put((Hazard)hz, ps);
                            hl.add((Hazard) hz);
                        }
                         psRels.add(new processStepRelations((ProcessStep) ps, hl));
                    }
//                if (!ps.isDeleted()) {
//                    psSB.append(H3_START);
//                    psSB.append(ps.getAttributeValue("Name"));
//                    psSB.append(H3_END);
//                    psSB.append(P_START);
//                    psSB.append(ps.getAttributeValue("Description"));
//                    psSB.append(P_END);
//                    // check for children/sub children realtionship
//                    //List<Persistable> lp= new ArrayList<>();
//                    
//                        List<Persistable> pl = findFunctionRelations(ps, new ArrayList<>());
//                        Boolean relFound = false;
//                        for (Persistable pe : pl) {
//                            //systemSB.append(p.getTitle());
//                            relFound = true;
//                            psSB.append(P_START).append(String.format(RELATED_TO, pe.getDatabaseObjectName(), pe.getTitle())).append(P_END);
//                        }
//                        if (!relFound) {
//                            psSB.append(P_START).append(String.format("This %s is not associated with any care settings.", ps.getDatabaseObjectName())).append(P_END);
//                        }
//                    // write subfunction text only if it exist
//                    String subFunc = systemFunctionReport(ps);
//                    if (subFunc != null && !subFunc.isEmpty()) {
//                        psSB.append(P_START).append("Sub Function").append(P_END);
//                        psSB.append(systemFunctionReport(ps));
//                    }
//                }
                }
            }
        } catch (Exception ex) {
            SmartProject.getProject().log("Failed to open process step", ex);
        }

        return psSB.toString();

    }
    
     private String HazardReport(String processTitle) {
        StringBuilder hazardSB = new StringBuilder();

        ArrayList<Persistable> hl = null;
        HashMap<String, ArrayList<Hazard>> hgta = new HashMap(); // HAZARD GroupType ArrayList
        for(processStepRelations psr: psRels)
        {
        for (Hazard h : psr.hList) {
            if (!h.isDeleted()) {
                String key = h.getAttributeValue("GroupingType");
                if (hgta.containsKey(key)) {
                    hgta.get(key).add(h);
                } else {
                    ArrayList<Hazard> a = new ArrayList<>();
                    a.add(h);
                    hgta.put(key, a);
                }
            }

        }
        }
        //Map<String, ArrayList<Persistable>> treeMap = new TreeMap<>(htl);
        //style="display: inline; border-bottom: 6px solid red; background-color: #F0EBEB;"  
       // hazardSB.append("<span style=\"margin-left: 40px;\">");
        for (ArrayList<Hazard> pa : hgta.values()) {
//                hazardSB.append("<H2 class=\"h2\">Hazard Type: ");
//                hazardSB.append(pa.get(0).getAttributeValue("GroupingType")).append(H2_END);
            for (Hazard h : pa) {
                if (!h.isDeleted()) {
                    //Hazard h = (Hazard) p;
                    hazardSB.append("<H2 class=\"h2\">Hazard Type: ");
                    hazardSB.append(pa.get(0).getAttributeValue("GroupingType")).append(H2_END);
                    hazardSB.append(String.format(H2_ANCHOR_START, ("H" + h.getId())));
                    hazardSB.append(h.getAttributeValue("Name"));
                    hazardSB.append(H2_END);
                    hazardSB.append(H3_START);
//                    hazardSB.append("Hazard Type: ");
//                    hazardSB.append(p.getAttributeValue("GroupingType"));
                    hazardSB.append(H3_END);
                    hazardSB.append(P_START);
                    hazardSB.append(h.getAttributeValue("Description"));
                    hazardSB.append(P_END);
                    String xml = h.getAttributeValue("GraphXml");
                    if ((xml != null) && (xml.trim().length() != 0)) {
                        hazardSB.append(String.format(IMG_TAG, GeneratePersistableImage(h), h.getTitle()));
                    }

                     hazardSB.append(TABLE_START);
                     hazardSB.append(TR_START).append(TD_START).append("Associated Care Process : ").append(TD_END).append(TD_START).append(P_START).append(processTitle).append(P_END).append(TR_END);
                     hazardSB.append(HazardProcessStep(h, "Associated Care Process Step: "));
                     hazardSB.append(HazardAssociatedReport(h, "Location", "Linked Care Setting : "));
                    // hazardSB.append(HazardAssociatedReport(h, "SystemFunction", "Associated System Function : "));
                    
                    hazardSB.append(HazardAssociatedSystemReport(h, "Associated System Function : ")); //HazardProcessStep
                    
                    //hazardSB.append(TR_END).append(TR_START);
                    hazardSB.append(HazardAssociatedReport(h, "Effect", "Potential Clinical Effects : "));
                    hazardSB.append(TABLE_END);

//                    hazardSB.append(TABLE_START).append(TR_START);
//                    hazardSB.append(HazardAssociatedReport(p, "SystemFunction", "Associated System Function : "));
//                    hazardSB.append(TR_END).append(TR_START);
//                    //hazardSB.append(HazardAssociatedReport(p, "Process", "Associated Care Process : ")); //HazardProcessStep
//                    hazardSB.append(HazardProcessStep(h, "Associated Care Process Step: ")); 
//                    hazardSB.append(TR_END).append(TR_START);
//                    hazardSB.append(HazardAssociatedReport(p, "Effect", "Potential Clinical Effects : "));
//                    hazardSB.append(TR_END).append(TABLE_END);
                    //get cause, effect, controls  = {"Cause", "Effect", "Control"};
                    hazardSB.append(H3_START).append("Initial Risk Assessment").append(H3_END);;
                    hazardSB.append(H3_START).append("Possible Causes").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Cause", "Existing Controls"));
                    hazardSB.append(H3_START).append("Possible Effects").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Effect", "Existing Controls"));
                    hazardSB.append(H3_START).append("Existing Controls").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Control", "Existing Controls"));
                    hazardSB.append(H3_START).append("Initial Risk").append(H3_END);
                    hazardSB.append(HazradRiskAssessmentReport(h, "iniitalAssessment"));
                    
                    hazardSB.append(H3_START).append("Residual Risk Assessment").append(H3_END);
                    hazardSB.append(H3_START).append("Possible Causes").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Cause", "Additional Controls"));
                    hazardSB.append(H3_START).append("Possible Effects").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Effect", "Additional Controls"));
                    hazardSB.append(H3_START).append("Additional Controls").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Control", "Additional Controls"));
                    hazardSB.append(H3_START).append("Residual Risk").append(H3_END);
                    hazardSB.append(HazradRiskAssessmentReport(h, "residualAssessment"));

//                    hazardSB.append(P_START);
//                    hazardSB.append("Maximum Initial Severity: ");                    
//                    hazardSB.append(p.getAttributeValue("")); //hazard.getAttribute("InitialSeverity").getIntValue()
//                    hazardSB.append(P_END);
                }
            }
        }
        return hazardSB.toString();
    }
     private String HazardLinks(){
        ArrayList<Persistable> hl = null;
        StringBuilder htSB = new StringBuilder();
        htSB.append(HAZARD_TABLE_START);
        
                try {
            if (proj != null) {
                hl = MetaFactory.getInstance().getChildren("Hazard", "ProjectID", proj.getId());
            } else {
                hl = MetaFactory.getInstance().getChildren("Hazard", "ProjectID", newObjectProjectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "No Hazards found.";
        }
         for (Persistable p : hl) {
                if (!p.isDeleted()) {
                    Hazard h = (Hazard) p;
                    htSB.append(String.format("<li> <a href=\"#%s\">%s</a></li>\n", ("H" + h.getId()), h.getTitle()));    
                }
         }
        
       htSB.append("</td>\n").append(HAZARD_TABLE_END);
        return htSB.toString();
     }
// <editor-fold defaultstate="collapsed" desc="Unused_Code"> 
//    private String HazardReport() {
//        StringBuilder hazardSB = new StringBuilder();
//
//        ArrayList<Persistable> hl = null;
//
//        StringBuilder htSB = new StringBuilder();
//        htSB.append(HAZARD_TABLE_START);
//
//        try {
//            if (proj != null) {
//                hl = MetaFactory.getInstance().getChildren("Hazard", "ProjectID", proj.getId());
//            } else {
//                hl = MetaFactory.getInstance().getChildren("Hazard", "ProjectID", newObjectProjectId);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//        //HashMap<String, ArrayList<Persistable>> htest = new HashMap<String, ArrayList<Persistable>>();
//        HashMap<String, ArrayList<Persistable>> hgta = new HashMap(); // HAZARD GroupType ArrayList
//        for (Persistable p : hl) {
//            if (!p.isDeleted()) {
//                String key = p.getAttributeValue("GroupingType");
//                if (hgta.containsKey(key)) {
//                    hgta.get(key).add(p);
//                } else {
//                    ArrayList<Persistable> a = new ArrayList<>();
//                    a.add(p);
//                    hgta.put(key, a);
//                }
//            }
//
//        }
//        //Map<String, ArrayList<Persistable>> treeMap = new TreeMap<>(htl);
//        //style="display: inline; border-bottom: 6px solid red; background-color: #F0EBEB;"  
//
//        for (ArrayList<Persistable> pa : hgta.values()) {
////                hazardSB.append("<H2 class=\"h2\">Hazard Type: ");
////                hazardSB.append(pa.get(0).getAttributeValue("GroupingType")).append(H2_END);
//            for (Persistable p : pa) {
//                if (!p.isDeleted()) {
//                    Hazard h = (Hazard) p;
//                    hazardSB.append("<H2 class=\"h2\">Hazard Type: ");
//                    hazardSB.append(pa.get(0).getAttributeValue("GroupingType")).append(H2_END);
//                    // htSB.append(String.format("<td> <a href=\"#%s\">%s</a></td>", ("H"+p.getId()), p.getTitle()));
//                    htSB.append(String.format("<li> <a href=\"#%s\">%s</a></li>\n", ("H" + p.getId()), p.getTitle()));
//                    hazardSB.append(String.format(H2_ANCHOR_START, ("H" + p.getId())));
//                    hazardSB.append(p.getAttributeValue("Name"));
//                    hazardSB.append(H2_END);
//                    hazardSB.append(H3_START);
////                    hazardSB.append("Hazard Type: ");
////                    hazardSB.append(p.getAttributeValue("GroupingType"));
//                    hazardSB.append(H3_END);
//                    hazardSB.append(P_START);
//                    hazardSB.append(p.getAttributeValue("Description"));
//                    hazardSB.append(P_END);
//                    String xml = h.getAttributeValue("GraphXml");
//                    if ((xml != null) && (xml.trim().length() != 0)) {
//                        hazardSB.append(String.format(IMG_TAG, GeneratePersistableImage(p), p.getTitle()));
//                    }
//
//                    hazardSB.append(TABLE_START);
//                    hazardSB.append(HazardAssociatedReport(p, "SystemFunction", "Associated System Function : "));
//                    //hazardSB.append(HazardAssociatedReport(p, "Process", "Associated Care Process : ")); //HazardProcessStep
//                    hazardSB.append(HazardProcessStep(h, "Associated Care Process Step: "));
//                    //hazardSB.append(TR_END).append(TR_START);
//                    hazardSB.append(HazardAssociatedReport(p, "Effect", "Potential Clinical Effects : "));
//                    hazardSB.append(TABLE_END);
//
////                    hazardSB.append(TABLE_START).append(TR_START);
////                    hazardSB.append(HazardAssociatedReport(p, "SystemFunction", "Associated System Function : "));
////                    hazardSB.append(TR_END).append(TR_START);
////                    //hazardSB.append(HazardAssociatedReport(p, "Process", "Associated Care Process : ")); //HazardProcessStep
////                    hazardSB.append(HazardProcessStep(h, "Associated Care Process Step: ")); 
////                    hazardSB.append(TR_END).append(TR_START);
////                    hazardSB.append(HazardAssociatedReport(p, "Effect", "Potential Clinical Effects : "));
////                    hazardSB.append(TR_END).append(TABLE_END);
//                    //get cause, effect, controls  = {"Cause", "Effect", "Control"};
//                    hazardSB.append(HazardDependents(h, "Cause", "Possible Causes"));
//                    hazardSB.append(HazardDependents(h, "Effect", "Possible Effects"));
//                    hazardSB.append(H3_START);
//                    hazardSB.append("Initial Risk Assessment");
//                    hazardSB.append(H3_END);
//                    hazardSB.append(HazradRiskAssessmentReport(h, "iniitalAssessment"));
//
//                    hazardSB.append(HazardDependents(h, "Control", "Controls"));
//                    hazardSB.append(H3_START);
//                    hazardSB.append("Residual Risk Assessment");
//                    hazardSB.append(H3_END);
//                    hazardSB.append(HazradRiskAssessmentReport(h, "residualAssessment"));
//
////                    hazardSB.append(P_START);
////                    hazardSB.append("Maximum Initial Severity: ");                    
////                    hazardSB.append(p.getAttributeValue("")); //hazard.getAttribute("InitialSeverity").getIntValue()
////                    hazardSB.append(P_END);
//                }
//            }
//        }
//
//        htSB.append("</td>\n").append(HAZARD_TABLE_END);
//        HAZARD_TABLE = htSB.toString();
//        return hazardSB.toString();
//    }
// </editor-fold>  

    private String HazardAssociatedReport(Persistable p, String type, String InitialText) {
        StringBuilder haSB = new StringBuilder();

        //haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END);
        haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END).append(TD_START);
        ArrayList<Relationship> hRels = p.getRelationships(type);
        if (hRels == null) {
            haSB.append("No Association").append(TD_END).append(TR_END);
            //haSB.append(TD_START).append("No Association").append(TD_END).append(TR_END);
            return haSB.toString();
        }
        for (Relationship r : hRels) {
            if (!r.isDeleted()) {
                String m = r.getManagementClass();
                if ((m == null) || (m.contains("Diagram"))) {
                    try {
                        Persistable hr = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        haSB.append(P_START).append(hr.getTitle()).append(P_END);
//                    if(!firstRow){
//                        firstRow = true;
//                        haSB.append(P_START).append(hr.getTitle()).append(P_END);
//                    }
//                    else
//                        haSB.append(P_START).append(hr.getTitle()).append(P_END);
//                        //haSB.append(P_START).append(TD_START).append(TD_END).append(TD_START).append(hr.getTitle()).append(TD_END).append(TR_END);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        haSB.append(TR_END);
        return haSB.toString();

    }
    
// <editor-fold defaultstate="collapsed" desc="Unused_Code"> 
//    private String HazardAssociatedSystemReport(Persistable p, String InitialText){
//                StringBuilder haSB = new StringBuilder();
//        ArrayList<Persistable> pl = new ArrayList<>(); 
//        //haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END);
//        haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END).append(TD_START);
//        
//        String Source = "SystemFunction";
//        try {
//         HashMap<String, ArrayList<Relationship>>  hrels = MetaFactory.getInstance().findFirstOrderRelationshipsForTarget(p, true, true);
//                 if (hrels != null) {
//                      ArrayList<Relationship> hRels = hrels.get("SystemFunction");
//                      if(hRels.isEmpty()){
//                          Source = "System";
//                          hRels = hrels.get(Source);
//                      }
//                     if(hRels.isEmpty()){
//                         
//                        haSB.append("No Association").append(TD_END).append(TR_END);
//                        //haSB.append(TD_START).append("No Association").append(TD_END).append(TR_END);
//                        return haSB.toString();
//                     } 
//                     for (Relationship r : hRels) {
//                         p = MetaFactory.getInstance().getFactory(Source).get(r.getSource());
//                         haSB.append(P_START).append(p.getTitle()).append(P_END);
//                     }  
//                }  
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        
//                haSB.append(TR_END);
//        return haSB.toString();
//    }
//    private ArrayList<Persistable> hazardDependandsystemANDfunction(Persistable p, String relType, ArrayList<Persistable> pl){
//       ArrayList<Persistable> s=null;
//         ArrayList<Persistable> sf=null;
//          try {
//            if (proj != null) {
//                s = MetaFactory.getInstance().getChildren("System", "ProjectID", proj.getId());
//                sf = MetaFactory.getInstance().getChildren("SystemFunction", "ProjectID", proj.getId());
//            }
//            } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//        // ArrayList<Persistable> pl = new ArrayList<>();
//         ArrayList<Relationship> allRels = new ArrayList<>();
//        ArrayList<Relationship> hRels = p.getRelationships(relType);
//        
//        if (hRels == null) {
//            hRels = p.getRelationships("System");
//            if(hRels == null)
//            {
//            return pl;
//            }
//        }
//        for (Relationship r : hRels) {
//            if (!r.isDeleted()) {
//                String m = r.getManagementClass();
//                if ((m == null) || (m.contains("Diagram"))) {
//                    try {
//                        Persistable hr = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
//                        pl.add(hr);
//                       // if(r.getTargetType().equalsIgnoreCase("System"))
//                        hazardDependandsystemANDfunction(hr, r.getTargetType(), pl);
//                        
//                            
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return pl;   
//    }
// </editor-fold>  
     private String HazardAssociatedSystemReport(Persistable p, String InitialText) {
        StringBuilder haSB = new StringBuilder();
        ArrayList<Persistable> pl = new ArrayList<>(); 
        //haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END);
        haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END).append(TD_START);
        
        pl = hazardDependandsystemANDfunction(p, "SystemFunction", pl);
          
        if (pl == null) {
            
            haSB.append("No Association").append(TD_END).append(TR_END);
            //haSB.append(TD_START).append("No Association").append(TD_END).append(TR_END);
            return haSB.toString();
            
        }
        for (Persistable r : pl) {
            if (!r.isDeleted()) {
                    try {               
                        
                        haSB.append(P_START).append(r.getTitle()).append(P_END);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        
        haSB.append(TR_END);
        return haSB.toString();

    }
     private ArrayList<Persistable> hazardDependandsystemANDfunction(Persistable p, String relType, ArrayList<Persistable> pl)
     {
         ArrayList<Persistable> s=null;
         ArrayList<Persistable> sf=null;
          try {
            if (proj != null) {
                s = MetaFactory.getInstance().getChildren("System", "ProjectID", proj.getId());
                sf = MetaFactory.getInstance().getChildren("SystemFunction", "ProjectID", proj.getId());
            }
            } catch (Exception e) {
                        e.printStackTrace();
                    }
        // ArrayList<Persistable> pl = new ArrayList<>();
         ArrayList<Relationship> allRels = new ArrayList<>();
        ArrayList<Relationship> hRels = p.getRelationships(relType);
        
        if (hRels == null) {
            hRels = p.getRelationships("System");
            if(hRels == null)
            {
            return pl;
            }
        }
        for (Relationship r : hRels) {
            if (!r.isDeleted()) {
                String m = r.getManagementClass();
                if ((m == null) || (m.contains("Diagram"))) {
                    try {
                        Persistable hr = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        pl.add(hr);
                       // if(r.getTargetType().equalsIgnoreCase("System"))
                        hazardDependandsystemANDfunction(hr, r.getTargetType(), pl);
                        
                            
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return pl;
     }
    private String HazardDependents(Hazard h, String type, String InitialText) {
        //String[] HAZARDEPENDENTS = {"Cause", "Effect", "Control"};
        // String[] TYPETEXT = {"Possible Causes", "Possible Effects", "Controls"};
        StringBuilder hdSB = new StringBuilder();
        String description=null;
        try {
           // hdSB.append(H3_START).append(InitialText).append(H3_END);
            ArrayList<Relationship> rels = h.getRelationships(type);
            if (rels == null) {
                hdSB.append("No Related ").append(type);
                return hdSB.toString();
            }
            for (Relationship r : rels) {
                String m = r.getManagementClass();
                if ((m == null) || (m.contains("Diagram"))) { // controls creating two relations, filtering one.
                    Persistable p = MetaFactory.getInstance().getFactory(type).get(r.getTarget());
                    description = CheckforNullorEmptyValue(p);
//                    if (p.getAttributeValue("Description") == null || (p.getAttributeValue("Description")).equals("")) {
//                        description = "Not Provided.";
//                    } else {
//                        description = p.getAttributeValue("Description");
//                    }
                    if (type.equals("Control")) { // control needs to show the state of the control
                        if((!p.getAttributeValue("State").equalsIgnoreCase("Additional"))&& InitialText.equalsIgnoreCase("Existing Controls")){
                            hdSB.append(P_START).append("<b>Title : </b>").append(p.getTitle()).append(" (").append(p.getAttributeValue("State")).append(")").append("<br/><b>Description : </b>").append(description).append(P_END);
                            
                        }
                        else if(p.getAttributeValue("State").equalsIgnoreCase("Additional")&& InitialText.equalsIgnoreCase("Additional Controls"))
                            hdSB.append(P_START).append("<b>Title : </b>").append(p.getTitle()).append(" (").append(p.getAttributeValue("State")).append(")").append("<br/><b>Description : </b>").append(description).append(P_END);
                    } else if(type.equals("Cause") && (InitialText.equalsIgnoreCase("Existing Controls"))) {
                        //hdSB.append(P_START).append("<b>Title : </b>").append(title).append("<br/><b>Description : </b>").append(description);
                        hdSB.append(RelatedControlReport(p, "Control", "Existing Controls"));
                     } else if(type.equals("Cause") && InitialText.equalsIgnoreCase("Additional Controls")) {
                        //hdSB.append(P_START).append("<b>Title : </b>").append(p.getTitle()).append("<br/><b>Description : </b>").append(description);
                        hdSB.append(RelatedControlReport(p, "Control", "Additional Controls"));
                    } else if(type.equals("Effect") && (InitialText.equalsIgnoreCase("Existing Controls"))) {
                        //hdSB.append(P_START).append("<b>Title : </b>").append(p.getTitle()).append("<br/><b>Description : </b>").append(description);
                        hdSB.append(RelatedControlReport(p, "Control", "Existing Controls"));
                    } else if(type.equals("Effect") && InitialText.equalsIgnoreCase("Additional Controls")) {
                        //hdSB.append(P_START).append("<b>Title : </b>").append(p.getTitle()).append("<br/><b>Description : </b>").append(description);
                        hdSB.append(RelatedControlReport(p, "Control", "Additional Controls"));
                    }else  if(type.equals("Control") && InitialText.equalsIgnoreCase("Existing Controls")){
                        hdSB.append(P_START).append("<b>Title : </b>").append(p.getTitle()).append("<br/><b>Description : </b>").append(description).append(P_END);
                    }else  if(type.equals("Control") && InitialText.equalsIgnoreCase("Additional Controls")){
                        hdSB.append(P_START).append("<b>Title : </b>").append(p.getTitle()).append("<br/><b>Description : </b>").append(description).append(P_END);
                    }
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
       return hdSB.toString().length()<1 ? "No " + type  + " exists with " + InitialText  : hdSB.toString();
        //return hdSB.toString();
    }
    private String RelatedControlReport(Persistable P, String type, String InitialText)
    {
        StringBuilder hdSB = new StringBuilder();
        String ControlDescription, PersistableDescription;
        PersistableDescription = CheckforNullorEmptyValue(P);
//        if (P.getAttributeValue("Description") == null || (P.getAttributeValue("Description")).equals("")) {
//                        CauseDescription = "Not Provided.";
//                    } else {
//                        CauseDescription = P.getAttributeValue("Description");
//                    }
        try {            
            ArrayList<Relationship> rels = P.getRelationships(type);
            if (rels == null) {
                hdSB.append(P_START).append("<b>Title : </b>").append(P.getTitle()).append("<br/><b>Description : </b>").append(PersistableDescription);
                hdSB.append("<br/>No Releted ").append(type).append((P_END));
                return hdSB.toString();
            }
            for (Relationship r : rels) {
                
              //  if ((m == null) || (m.contains("Diagram"))) { // controls creating two relations, filtering one.
                    Persistable p = MetaFactory.getInstance().getFactory(type).get(r.getTarget());
                    ControlDescription = CheckforNullorEmptyValue(p);
//                    if (p.getAttributeValue("Description") == null || (p.getAttributeValue("Description")).equals("")) {
//                        ControlDescription = "Not Provided.";
//                    } else {
//                        ControlDescription = p.getAttributeValue("Description");
//                    }
                     // control needs to show the state of the control
                        if((!p.getAttributeValue("State").equalsIgnoreCase("Additional"))&& InitialText.equalsIgnoreCase("Existing Controls")){
                            hdSB.append(P_START).append("<b>Title : </b>").append(P.getTitle()).append("<br/><b>Description : </b>").append(PersistableDescription);
                            hdSB.append("<br/><b> Related Control Title : </b>").append(p.getTitle()).append(" (").append(p.getAttributeValue("State")).append(")").append("<br/><b>Related Control Description : </b>").append(ControlDescription);
                        }else if(p.getAttributeValue("State").equalsIgnoreCase("Additional")&& InitialText.equalsIgnoreCase("Additional Controls")){
                            hdSB.append(P_START).append("<b>Title : </b>").append(P.getTitle()).append("<br/><b>Description : </b>").append(PersistableDescription);
                            hdSB.append("<br/><b>Related Control Title : </b>").append(p.getTitle()).append(" (").append(p.getAttributeValue("State")).append(")").append("<br/><b>Related Control Description : </b>").append(ControlDescription);
                        }
//                        }else if (!RelatedControlExists){
//                            hdSB.append(P_START).append("<b>Title : </b>").append(P.getTitle()).append("<br/><b>Description : </b>").append(PersistableDescription);
//                            hdSB.append("<br/><b>No Related Control.</b>");
//                        }
                            
//                        }else if(InitialText.equalsIgnoreCase("Related Controls"))
//                            hdSB.append("<b>Related Control Title : </b>").append(p.getTitle()).append(" (").append(p.getAttributeValue("State")).append(")").append("<br/><b>Related Control Description : </b>").append(ControlDescription);
                    
              //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if(hdSB.length()>1)
        hdSB.append(P_END);
        return hdSB.toString();
        
    }
    private String HazradRiskAssessmentReport(Hazard h, String type) {
        StringBuilder hraSB = new StringBuilder();
        int is = h.getAttribute("InitialSeverity").getIntValue();
        int rs = h.getAttribute("ResidualSeverity").getIntValue();

        int ilh = h.getAttribute("InitialLikelihood").getIntValue();
        int rlh = h.getAttribute("ResidualLikelihood").getIntValue();

        int irr = h.getAttribute("InitialRiskRating").getIntValue();
        int rrr = h.getAttribute("ResidualRiskRating").getIntValue();

        if (type.equals("iniitalAssessment")) {

            hraSB.append(P_START);
            hraSB.append("Initial Severity: ");
            // hraSB.append(String.format(PCLASS_START, is));
            hraSB.append(Hazard.translateSeverity(is));
            hraSB.append(P_END);

            hraSB.append(P_START);
            hraSB.append("Initial Likelihood: ");
            //hraSB.append(String.format(PCLASS_START, ilh));
            hraSB.append(Hazard.translateLikelihood(ilh));
            hraSB.append(P_END);

            hraSB.append("Initial Risk Rating: ");
            hraSB.append(String.format(PCLASS_START, irr));
            //hraSB.append("Initial Risk Rating: ");
            hraSB.append(" &nbsp;").append(h.getAttribute("InitialRiskRating").getIntValue()).append(" &nbsp;");
            hraSB.append(P_END).append("<br /><br />");
            return hraSB.toString();
        } else {

            hraSB.append(P_START);
            hraSB.append("Residual Severity: ");
            //hraSB.append(String.format(PCLASS_START, rs));
            hraSB.append(Hazard.translateSeverity(rs));   //TODO check if the value sent are in right order   
            hraSB.append(P_END);

            hraSB.append(P_START);
            hraSB.append("Residual Likelihood: ");
            //hraSB.append(String.format(PCLASS_START, rlh));
            hraSB.append(Hazard.translateLikelihood(rlh));  //TODO check if the value sent are in right order      
            hraSB.append(P_END);

            hraSB.append("Residual Risk Rating: ");
            hraSB.append(String.format(PCLASS_START, rrr));
            //hraSB.append("Residual Risk Rating: ");
            hraSB.append(" &nbsp;").append(h.getAttribute("ResidualRiskRating").getIntValue()).append(" &nbsp;");
            hraSB.append(P_END).append("<br /><br />");
            return hraSB.toString();
        }
    }

    private String HazardProcessStep(Hazard h, String InitialText) {        
        Boolean found = false;
        StringBuilder psSB = new StringBuilder();
        //psSB.append(TD_START).append(InitialText).append(TD_END).append(TD_START);
        psSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END);
        Collection<ProcessStep> PSF = null;
        try {
            PSF = MetaFactory.getInstance().getFactory("ProcessStep").getEntries();
        } catch (Exception e) {
            e.printStackTrace();
            psSB.append("No Association").append(TD_END);
            return psSB.toString();
        }
        if (PSF != null) {
            for (ProcessStep p : PSF) {
                ArrayList<Relationship> PSR = p.getRelationships("Hazard");
                if (PSR == null) {
                    continue;
                }
                for (Relationship r : PSR) {
                    if (!r.isDeleted()) {
                        try {
                            if (r.getTarget() == h.getId()) {
                                if (!found) {
                                    found = true;
                                    psSB.append(TD_START).append(p.getAttributeValue("Name")).append(TD_END).append(TR_END);
                                } else {
                                    psSB.append(TR_START).append(TD_START).append(TD_END).append(TD_START).append(p.getAttributeValue("Name")).append(TD_END).append(TR_END);
                                }
                                //psSB.append(p.getAttributeValue("Name")).append("  ");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (!found) {
            psSB.append(TD_START).append("No Association").append(TD_END).append(TR_END);
        }

        return psSB.toString();
    }

    private String GeneratePersistableImage(Persistable p) {
        String imageData = "";
        BasicGraphEditor editor = null;
        HashMap<String, DiagramEditorElement> ex = null;
        //String FName = FILEPATH + "/" + p.getTitle() + ".png";
        String xml = p.getAttributeValue("GraphXml");
        if (p.getDatabaseObjectName().contentEquals("System")) {
            SystemEditorDetails sed = new SystemEditorDetails();
            SystemGraphEditor sge = new SystemGraphEditor(p.getId());
            sed.setSystem((System) p);
            //ex = sed.getExistingGraph(xml);
            ex = sed.getExistingGraph(p, null);
            sge.setExistingGraph(ex);
            if (ex != null) {
                editor = (BasicGraphEditor) sge;
            }
        } else if(p.getDatabaseObjectName() == "Process"){
                    ProcessGraphEditor pge = new ProcessGraphEditor(p.getId());
                    pge.setProcessId(p.getId(), xml);
                   try{
                    PersistableFactory<ProcessStep> pfs = MetaFactory.getInstance().getFactory("ProcessStep");
                    ArrayList<PersistableFilter> filter = new ArrayList<>();
                    filter.add(new PersistableFilter("ProjectID", p.getAttributeValue("ProjectID")));
                    filter.add(new PersistableFilter("ProcessID", p.getAttributeValue("ProcessID")));
                    Collection<ProcessStep> steps = pfs.getEntries(filter);
                    HashMap<String, DiagramEditorElement> existingSteps = new HashMap<>();
                    for (ProcessStep ps : steps) {
                        existingSteps.put(ps.getAttributeValue("GraphCellId"), new DiagramEditorElement(ps));
                    }
                    pge.setExistingSteps(existingSteps);
                    editor = (BasicGraphEditor) pge;
                    }
                   catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Failed to build details for generatiing Process image. Send logs to support", "Warning", JOptionPane.ERROR_MESSAGE);
                    SmartProject.getProject().log("Failed to build details for graphical Process editor in ReportEditor", e);
                   }
                } 
        else if (p.getDatabaseObjectName().contentEquals("Hazard")) {
            BowtieGraphEditor bge = new BowtieGraphEditor(p.getId());
            bge.setHazardId(p.getId(), xml);
            HazardEditor he = new HazardEditor();
            if ((xml != null) && (xml.trim().length() > 0)) {
                he.setHazard((Hazard) p);
                ex = he.getExistingBowtie(xml);
                if (ex != null) {
                    bge.setExistingBowtie(ex);
                    editor = (BasicGraphEditor) bge;
                }
            }
        }

        if (editor != null) {
            try {
                mxGraphComponent graphComponent = editor.getGraphComponent();
                mxGraph graph = graphComponent.getGraph();
                Document doc = mxXmlUtils.parseXml(xml);
                mxCodec codec = new mxCodec(doc);
                codec.decode(doc.getDocumentElement(), graph.getModel());
                //RenderedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, false, null);

                BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, false, null);
                int w;
                int h;
                float rateX = (float) 600 / (float) image.getWidth();
                float rateY = (float) 500 / (float) image.getHeight();
                if((float) image.getWidth()< (float) 600 && (float) image.getHeight()< (float) 500){
                     w = (int) image.getWidth() ;
                    h = (int) image.getHeight();
                }else if (rateX > rateY) {
                    w = (int) (image.getWidth() * rateY);
                    h = (int) (image.getHeight() * rateY);

                } else {
                    w = (int) (image.getWidth() * rateX);
                    h = (int) (image.getHeight() * rateX);

                }
                //BufferedImage resizeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); //TYPE_INT_ARGB
                BufferedImage resizeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); //TYPE_INT_ARGB
//                 Graphics2D g2d = thumb.createGraphics();
//                  g2d.setColor(Color.WHITE);
                //g2d.drawImage(image1, h, h, Color.yellow, editor)
                resizeImage.createGraphics().drawImage(image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
                // resizeImage.createGraphics().drawImage(image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, Color.WHITE, null);

                imageData = encodeToBase64String(resizeImage, "png");
                // imageData = encodeToBase64String(image, "png");
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            ImageIO.write(resizeImage, "png", os);
//            imageData = Base64.getEncoder().encodeToString(os.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imageData;
    }

    public static String encodeToBase64String(RenderedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, type, baos);
            imageString = Base64.getEncoder().encodeToString(baos.toByteArray());

            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
    
    public String CheckforNullorEmptyValue(Persistable p){
        if (p.getAttributeValue("Description") == null || (p.getAttributeValue("Description")).equals("")) {
                        return "Not Provided.";
                    } else {
                        return p.getAttributeValue("Description");
                    }
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    public class processStepRelations {

        ProcessStep pStep;
        ArrayList<Hazard> hList;

        public processStepRelations(ProcessStep p, ArrayList<Hazard> hl) {
            this.pStep = p;
            this.hList = hl;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar buttonBar;
    private javax.swing.JPanel configPanel;
    private uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel configTextArea;
    private javax.swing.JPanel crmPanel;
    private uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel crmTextArea;
    private javax.swing.JButton deleteButton;
    public static javax.swing.JButton exportButton;
    private javax.swing.JPanel introPanel;
    private uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel introductionEditor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton newButton;
    private javax.swing.JPanel qaPanel;
    private uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel qaTextArea;
    private javax.swing.JPanel safetySystemPanel;
    private uk.nhs.digital.safetycase.ui.ekit.HtmlEditorPanel safetyTextArea;
    private javax.swing.JButton saveButton;
    private javax.swing.JTabbedPane sectionsPanel;
    private javax.swing.JTextField titleTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setPersistableObject(Persistable p) {
        try {
            if (p == null) {
                newButtonActionPerformed(null);
                return;
            }
            report = (Report) p;
            proj = (Project) MetaFactory.getInstance().getFactory("Project").get(report.getAttribute("ProjectID").getIntValue());
            titleTextField.setText(report.getAttributeValue("Title"));
            introductionEditor.setText(report.getAttributeValue("Introduction"));
            configTextArea.setText(report.getAttributeValue("CCMdetails"));
            crmTextArea.setText(report.getAttributeValue("CRMdetails"));
            qaTextArea.setText(report.getAttributeValue("QAADdetails"));
            safetyTextArea.setText(report.getAttributeValue("SummarySafetySystemDetails"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load reports for editing", "Load failed", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to set persistable object in ReportEditor", ex);
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

        if (evtype == uk.nhs.digital.projectuiframework.Project.SAVE) {
            saveButtonActionPerformed(null);
            return false;
        }

        return true;
    }

    @Override
    public JPanel getEditor(Object o) {
        return this;
    }

    @Override
    public void unsubscribe() {
        SmartProject.getProject().removeNotificationSubscriber(this);
    }

}
