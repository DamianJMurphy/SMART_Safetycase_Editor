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
import uk.nhs.digital.safetycase.data.IssuesLog;
import uk.nhs.digital.safetycase.data.Location;
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
import uk.nhs.digital.safetycase.data.Role;
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
    protected ArrayList<ProcessStep> HRPSList = null;

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
    //private static final String H2_ANCHOR_START = "<H2 style=\"color:#4682B4\" id=\"%s\" >";
    //private static final String H2_START = "<H2 style=\"color:#4682B4\" >";
    private static final String H2_ANCHOR_START = "<H2 style=\"color:#507add\" id=\"%s\" ><a name=\"%s\">";
    private static final String H2_START = "<H2 style=\"color:#507add\" >";
    private static final String H2_ANCHOR_END = "</a></H2>\n";
    private static final String H2_END = "</H2>\n";
    // private static final String H3_START = "<H3 style=\"color:#7B68EE\" >"; //92B6D5
    private static final String H3_START = "<H3 style=\"color:#C02F1D\" >";
    private static final String H3_END = "</H3>\n";
    private static final String TABLE_START = "<table class=\"AssociationsTable\" >";
    private static final String TABLE_END = "</table>\n";
    private static final String TR_START = "<tr>";
    private static final String TR_END = "</tr>\n";
    private static final String TD_START = "<td>";
    private static final String TD_END = "</td>\n";

    private static final String P_START = "<P>";
    private static final String PCLASS_START = "<P class=\"rating%s\">";
    private static final String SPANCLASS_START = "<span class=\"rating%s\">";
    private static final String SPAN_END = "</span>\n";
    private static final String P_END = "</P>\n";
    private static final String DIV_START = "<div>";//<div><img src="data:image/png;base64, iVB" alt="System" /></div> 
    private static final String DIV_END = "</div>\n";
    private static String IMG_TAG = "<div><img src=\"data:image/png;base64, %s \" alt=\"%s \" /></div><br />\n";
    //private static String IMG_TAG = "<div style=\"text-align: center;\"><img src=\"data:image/png;base64, %s \" alt=\"%s \" /></div><br />\n";
    private static String RELATED_TO = " Used in %s ( %s ).";
    // Titles for sections
    private static final String INTRO_SECTION = H1_START + "Introduction" + H1_END;
    private static final String SYSTEM_DEFINITION_SECTION = H1_START + "System Definition / Overview" + H1_END;
    private static final String CARE_SETTINGS_SECTION = H1_START + "Care Settings" + H1_END;
    private static final String ROLE_SECTION = H1_START + "Roles" + H1_END;
    private static final String CLINICAL_RISK_MANAGEMENT_SECTION = H1_START + "Hazard Analysis" + H1_END;
    private static final String ISSUES_SECTION = H1_START + "Issues Log " + H1_END;
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
             + "span.rating0{\n"
            + "	background:#00FF00;\n"
            + "	}\n"
            + "	span.rating1{\n"
            + "	background:#00FF00;\n"
            + "	}\n"
            + "	span.rating2{\n"
            + "	background:#00FF00;\n"
            + "	}\n"
            + "	span.rating3{\n"
            + "	background:#FFC200;\n"
            + "	}\n"
            + "	span.rating4{\n"
            + "	background:red;\n"
            + "       color:white;\n"
            + "	}\n"
            + "	span.rating5{\n"
            + "	background:red;\n"
            + " color:white;\n"
            + "	}\n"
            + "table.HeaderTable { \n"
            + "	border-collapse:collapse;\n"
            + "	width:97%\n"
            + "}\n"
            + "\n"
            + "table.HeaderTable td, table.HeaderTable th { \n"
            + "	border:1px solid rgb(150, 150, 150);\n"
            + "	padding:5px;\n"
            + "}\n"
            + "\n"
            + "tr.title td {\n"
            + "	border-left: 0px solid;\n"
            + "	border-right: 0px solid;\n"
            + "}\n"
            + "table.HeaderTable tr td:first-child {\n"
            + "  border-left: 0;\n"
            + "}\n"
            + "table.HeaderTable tr td:last-child {\n"
            + "  border-right: 0;\n"
            + "}\n"
            + "td.normal {\n"
            + "    width: 10%;\n"
            + "}\n"
            + "\n"
            + "td.extended {\n"
            + "    width: 80%;\n"
            + "}\n"
            + "td.medium {\n"
            + "    width: 20%;\n"
            + "}\n"
            + "td.larger {\n"
            + "    width: 40%;\n"
            + "}\n"
            +"br {\n" +
"         display: block; \n" +
"         content: \"\"; \n" +
"         margin-top: 6;\n" +
"}\n" +
"br.extendedheight {\n" +
"         display: block; \n" +
"         content: \"\"; \n" +
"         margin-top: 30;\n" +
"}"
            + "</style>";
    private static String CSS1 ="";
    private static String SYSTEMS_TABLE_START = "<table class=\"LinksTable\">\n<tr>\n<td> Systems:</td>\n<td>\n";
    private static String SYSTEMS_TABLE_END = "</tr>\n</table>\n";
    private static String SYSTEM_TABLE = null;
    private static String HAZARD_TABLE = null;
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
        jSeparator1 = new javax.swing.JToolBar.Separator();
        saveButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        exportButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        deleteButton = new javax.swing.JButton();

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
        buttonBar.add(jSeparator1);

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
        buttonBar.add(jSeparator2);

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        buttonBar.add(exportButton);
        buttonBar.add(jSeparator3);

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

        String fileName = null;
        boolean needFileName = false;
        do {
            needFileName = false;
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = fc.getSelectedFile().toString();
            } else {
                return;
            }
            if (new File(fileName).exists()) {
                int replace = JOptionPane.showConfirmDialog(this, "There is already a file with that name, replace it ?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION);
                switch (replace) {
                    case JOptionPane.YES_OPTION:
                        needFileName = false;
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                    case JOptionPane.NO_OPTION:
                    default:
                        needFileName = true;
                        break;
                }
            }
        } while (needFileName);
        
        StringBuilder body = new StringBuilder(TITLE_START);
        body.append(titleTextField.getText());
        body.append(TITLE_END);
        body.append(headerTable);
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
        body.append(CARE_SETTINGS_SECTION);
        body.append(P_START);
        body.append(CareSettingReport()); // Care Settings Report
        body.append(P_END);
        body.append("<br /><br />");
        body.append(ROLE_SECTION);
        body.append(P_START);
        body.append(RolesReport()); // Roles Report
        body.append(P_END);
        body.append("<br /><br />");
        body.append(CLINICAL_RISK_MANAGEMENT_SECTION);
        body.append(P_START);
        body.append(crmTextArea.getBodyText());
        body.append(P_END);
        body.append(P_START);
        body.append(ProcessReport()); //Process Report
        //body.append(HazardReport()); // HAZARD Report  //
        body.append(P_END);
        body.append("<br /><br />");
        body.append(ISSUES_SECTION);
        body.append(P_START);
        body.append(IssuesReport()); // ISSUES Report
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
//        String fileName = FILEPATH + "/" + name;
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
                    if (pid == -1) { // root node
                        stSB.append(String.format("<li><a href=\"#%s\">%s</a></li>\n", ("S" + p.getId()), p.getTitle()));
                        systemSB.append(String.format(H2_ANCHOR_START, ("S" + p.getId()), ("S" + p.getId())));
                        systemSB.append(p.getAttributeValue("Name"));
                        systemSB.append(H2_ANCHOR_END);
                        systemSB.append(P_START);
                        systemSB.append("System Version: ");
                        systemSB.append(p.getAttributeValue("Version"));
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
            try {
                Persistable sf = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                if (!sf.isDeleted()) {
                    sfSB.append(H3_START);
                    sfSB.append(sf.getAttributeValue("Name"));
                    sfSB.append(H3_END);
                    sfSB.append(P_START);
                    sfSB.append(sf.getAttributeValue("Description"));
                    sfSB.append(P_END);
                    if ((r.getComment() != null) && (r.getComment().contains("system diagram"))) {
                        List<Persistable> pl = findAllRelatedRelations(sf, new ArrayList<>());
                        Boolean relFound = false;
                        for (Persistable pe : pl) {
                            String t = pe.getDatabaseObjectName();
                            if (!"System".equals(t) && !relFound) {
                                relFound = true;
                                sfSB.append(P_START).append(String.format(RELATED_TO, pe.getDatabaseObjectName(), pe.getTitle())).append(P_END);
                            }
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
            return ssSB.toString();
            //return null;
        }
        for (Relationship r : subSystems) {
            try {
                Persistable ss = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                if (!ss.isDeleted()) {
                    ssSB.append(H2_START);
                    ssSB.append(ss.getAttributeValue("Name"));
                    ssSB.append(H2_END);
                    ssSB.append(P_START);
                    ssSB.append("Sub-System Version: ");
                    ssSB.append(ss.getAttributeValue("Version"));
                    ssSB.append(P_END);
                    ssSB.append(P_START);
                    ssSB.append(ss.getAttributeValue("Description"));
                    ssSB.append(P_END);
                    // SystemFunction section
                    ssSB.append(systemFunctionReport(ss));
                    // check for child subSystem
                    ssSB.append(subSystemReport(ss));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return ssSB.toString();

    }

    private List<Persistable> findAllRelatedRelations(Persistable relatedObject, List<Persistable> persistablelist)
            throws Exception {
        List<Persistable> lp = persistablelist;
        PersistableFactory<uk.nhs.digital.safetycase.data.SystemFunction> pfsf = MetaFactory.getInstance().getFactory(relatedObject.getDatabaseObjectName());
        Persistable p;
        try {
            HashMap<String, ArrayList<Relationship>> hrels = MetaFactory.getInstance().findFirstOrderRelationshipsForTarget(relatedObject, true, true);
            if (hrels != null) {
                for (Map.Entry<String, ArrayList<Relationship>> entry : hrels.entrySet()) {
                    String Source = entry.getKey();
                    ArrayList<Relationship> Rels = entry.getValue();
                    for (Relationship r : Rels) {
                        p = MetaFactory.getInstance().getFactory(Source).get(r.getSource());
                        lp.add(p);
                        // find any sub children
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lp;
    }

    private String CareSettingReport() {
        StringBuilder LocationSB = new StringBuilder();
        ArrayList<Persistable> Locations = null;
        Map<Integer, Location> Locs = new HashMap<Integer, Location>();
        try {
            if (proj != null) {
                Locations = MetaFactory.getInstance().getChildren("Location", "ProjectID", proj.getId());
            } else {
                Locations = MetaFactory.getInstance().getChildren("Location", "ProjectID", newObjectProjectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (Locations != null) {
            for (Persistable p : Locations) {
                if (!p.isDeleted()) {
                    Locs.put(p.getId(), (Location) p);
                }
            }
            for (Location loc : Locs.values()) {
                int pid = Integer.parseInt(loc.getAttributeValue("ParentLocationID"));
                LocationSB.append(H2_START);
                LocationSB.append(loc.getAttributeValue("Name"));
                LocationSB.append(H2_END);
                LocationSB.append(P_START);
                LocationSB.append("Mnemonic: ");
                LocationSB.append(loc.getAttributeValue("Mnemonic"));
                LocationSB.append(P_END);
                LocationSB.append(P_START);
                LocationSB.append("Parent Location: ");
                if (pid == -1) {
                    LocationSB.append("N/A");
                } else {
                    Location pl = Locs.get(pid);
                    LocationSB.append(pl.getAttributeValue("Name"));
                }
                LocationSB.append(P_END);
                LocationSB.append(P_START);
                LocationSB.append("Description: ");
                LocationSB.append(loc.getAttributeValue("Description"));
                LocationSB.append(P_END);
                LocationSB.append(P_START).append("Related Role :");
                try {
                    List<Persistable> pl = findAllRelatedRelations((Persistable) loc, new ArrayList<>());
                    Boolean relFound = false;
                    for (Persistable pe : pl) {
                        if (pe.getDatabaseObjectName().contentEquals("Role")) {
                            relFound = true;
                            LocationSB.append(String.format(RELATED_TO, pe.getDatabaseObjectName(), pe.getTitle())).append("<br/>");
                        }
                    }
                    if (!relFound) {
                        LocationSB.append(String.format("This %s is not associated with any Role.", loc.getDatabaseObjectName()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LocationSB.append(P_END);
            }
        }else{
            LocationSB.append("No locations found. ");
        }
        return LocationSB.toString();

    }

    private String RolesReport() {
        StringBuilder RoleSB = new StringBuilder();
        ArrayList<Persistable> Roles = null;
        Map<Integer, Role> Rols = new HashMap<Integer, Role>();
        try {
            if (proj != null) {
                Roles = MetaFactory.getInstance().getChildren("Role", "ProjectID", proj.getId());
            } else {
                Roles = MetaFactory.getInstance().getChildren("Role", "ProjectID", newObjectProjectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (Roles != null) {
            for (Persistable p : Roles) {
                if (!p.isDeleted()) {
                    Rols.put(p.getId(), (Role) p);
                }
            }
            for (Role rol : Rols.values()) {
                RoleSB.append(H2_START);
                RoleSB.append(rol.getAttributeValue("Name"));
                RoleSB.append(H2_END);
                RoleSB.append(P_START);
                RoleSB.append("Description: ");
                RoleSB.append(rol.getAttributeValue("Description"));
                RoleSB.append(P_END);
            }
        } 
        return RoleSB.toString();

    }

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
                    processSB.append(String.format(H2_ANCHOR_START, ("P" + p.getId()), ("P" + p.getId())));
                    processSB.append("Care Process : ");
                    processSB.append(p.getAttributeValue("Name"));
                    processSB.append(H2_ANCHOR_END);
                    String xml = process.getAttributeValue("GraphXml");
                    if ((xml != null) && (xml.trim().length() != 0)) {
                        processSB.append(String.format(IMG_TAG, GeneratePersistableImage(p), p.getTitle()));
                    }

                    // Proces Step section
                    String psRelations = processStepRelations(p);
                    if (psRelations == null || psRelations.isEmpty()) {
                        String t = "";
                    }
                    if (psRels.size() > 0) {
                        processSB.append(HazardReport(p.getAttributeValue("Name")));
                    } else {
                        processSB.append("This Process has no hazard links");
                    }

                }
            }
        }

        return processSB.toString();
    }

    private String processStepRelations(Persistable p) {
        StringBuilder psSB = new StringBuilder();
        psRels = new ArrayList<processStepRelations>();
        ArrayList<Hazard> hzl = new ArrayList<>(); // hazards list for process
        ArrayList<Persistable> processSteps;
        try {
            processSteps = MetaFactory.getInstance().getChildren("ProcessStep", "ProcessID", p.getId());
            if (processSteps == null) { // if a ProcessStep has no Relations
                psSB.append(P_START).append(String.format("This %s has no ProcessSteps.", p.getDatabaseObjectName())).append(P_END);
                return psSB.toString();
            }
            for (Persistable ps : processSteps) {
                String ss = ps.getAttributeValue("Name");
                if ((ps.getTitle() != null) && ((!ps.getAttributeValue("Name").equalsIgnoreCase("Start")) && (!ps.getAttributeValue("Name").equalsIgnoreCase("Stop")))) {
                    ArrayList<Relationship> Relations = ps.getRelationships("Hazard");
                    if (Relations != null) {
                        ArrayList<Hazard> hl = new ArrayList<>();
                        for (Relationship r : Relations) {
                            if (!r.isDeleted()) {
                                Persistable hz = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                                if (!hzl.contains((Hazard) hz)) {
                                    hl.add((Hazard) hz);
                                    hzl.add((Hazard) hz);
                                }
                            }
                        }
                        if (hl.size() > 0) {
                            psRels.add(new processStepRelations((ProcessStep) ps, hl));
                        }
                    }
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
        HashMap<ProcessStep, Hazard> pshl = new HashMap();
        for (processStepRelations psr : psRels) {
            ProcessStep ps = psr.pStep;
            for (Hazard h : psr.hList) {
                if (!h.isDeleted()) {
                    pshl.put(ps, h);
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

        for (ArrayList<Hazard> pa : hgta.values()) {
            for (Hazard h : pa) {
                if (!h.isDeleted()) {
                    hazardSB.append("<H2 class=\"h2\">Hazard Type: ");
                    hazardSB.append(pa.get(0).getAttributeValue("GroupingType")).append(H2_END);
                    hazardSB.append(String.format(H2_ANCHOR_START, ("H" + h.getId()), ("H" + h.getId())));
                    hazardSB.append("Hazard Name: ").append(h.getAttributeValue("Name"));
                    hazardSB.append(H2_ANCHOR_END);
                    hazardSB.append(H3_START);
                    hazardSB.append(H3_END);
                    hazardSB.append(P_START);
                    hazardSB.append("Hazard Description: ").append(h.getAttributeValue("Description"));
                    hazardSB.append(P_END);
                    String xml = h.getAttributeValue("GraphXml");
                    if ((xml != null) && (xml.trim().length() != 0)) {
                        hazardSB.append(String.format(IMG_TAG, GeneratePersistableImage(h), h.getTitle()));
                    }
                    hazardSB.append(TABLE_START);
                    hazardSB.append(TR_START).append(TD_START).append("Associated Care Process : ").append(TD_END).append(TD_START).append(P_START).append(processTitle).append(P_END).append(TR_END);
                    hazardSB.append(HazardProcessStep(h, "Associated Care Process Step: "));
                    hazardSB.append(HazardAssociatedReport(h, "Location", "Linked Care Setting : "));
                    hazardSB.append(HazardAssociatedSystemReport(h, "Associated System Function : ")); //HazardProcessStep
                    hazardSB.append(HazardAssociatedReport(h, "Effect", "Potential Clinical Effects : "));
                    hazardSB.append(TABLE_END);
                    hazardSB.append(H2_START).append("Initial Risk Assessment").append(H2_END);;
                    hazardSB.append(H3_START).append("Possible Causes").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Cause", "Existing Controls"));
                    hazardSB.append(H3_START).append("Possible Effects").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Effect", "Existing Controls"));
                    //  hazardSB.append(H3_START).append("Existing Controls").append(H3_END);//not required any more
                    // hazardSB.append(HazardDependents(h, "Control", "Existing Controls")); //not required any more
                    hazardSB.append(H3_START).append("Initial Risk").append(H3_END);
                    hazardSB.append(HazradRiskAssessmentReport(h, "iniitalAssessment"));

                    hazardSB.append(H2_START).append("Residual Risk Assessment").append(H2_END);
                    hazardSB.append(H3_START).append("Possible Causes").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Cause", "Additional Controls"));
                    hazardSB.append(H3_START).append("Possible Effects").append(H3_END);
                    hazardSB.append(HazardDependents(h, "Effect", "Additional Controls"));
                    // hazardSB.append(H3_START).append("Additional Controls").append(H3_END); //not required any more
                    // hazardSB.append(HazardDependents(h, "Control", "Additional Controls")); // not required any more
                    hazardSB.append(H3_START).append("Residual Risk").append(H3_END);
                    hazardSB.append(HazradRiskAssessmentReport(h, "residualAssessment"));
                    hazardSB.append(P_START).append("Overall Hazard Clinical Justification: ").append(CheckforNullorEmptyValue(h, "ClinicalJustification")).append("<br />");
                    hazardSB.append("Overall Hazard  Status: ").append(CheckforNullorEmptyValue(h, "Status")).append(P_END);
                }
            }
        }
        return hazardSB.toString();
    }

    private String HazardLinks() {
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
        if (hl == null) {
            return (htSB.append("No Hazards found.</td>\n").append(HAZARD_TABLE_END)).toString();
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

    private String HazardAssociatedReport(Persistable p, String type, String InitialText) {
        StringBuilder haSB = new StringBuilder();
        haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END).append(TD_START);
        ArrayList<Relationship> hRels = p.getRelationships(type);
        if (hRels == null) {
            haSB.append("No Association").append(TD_END).append(TR_END);
            return haSB.toString();
        }
        for (Relationship r : hRels) {
            if (!r.isDeleted()) {
                String m = r.getManagementClass();
                if ((m == null) || (m.contains("Diagram"))) {
                    try {
                        Persistable hr = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        haSB.append(P_START).append(hr.getTitle()).append(P_END);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        haSB.append(TR_END);
        return haSB.toString();
    }

    private String HazardAssociatedSystemReport(Persistable p, String InitialText) {
        StringBuilder haSB = new StringBuilder();
        ArrayList<Persistable> pl = new ArrayList<>();
        haSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END).append(TD_START);
        pl = hazardDependentSystemAndFunction2(p, "SystemFunction", pl);
        if (pl.isEmpty()) {
            haSB.append("No Association").append(TD_END).append(TR_END);
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

    private ArrayList<Persistable> hazardDependentSystemAndFunction2(Persistable h, String relType, ArrayList<Persistable> pl) {
        ArrayList<Relationship> PSRels = null;
        if (h == null) {
            return pl;
        }
        if (HRPSList != null) {
            for (ProcessStep p : HRPSList) {
                ArrayList<Relationship> PSR = p.getRelationships("Hazard");
                if (PSR == null) {
                    continue;
                }
                for (Relationship psr : PSR) {
                    if (!psr.isDeleted()) {
                        try {
                            if (psr.getTarget() == h.getId()) {
                                PSRels = p.getRelationships(relType);
                                if (PSRels == null) {
                                    PSRels = p.getRelationships("System");
                                    if (PSRels == null) {
                                        continue;
                                    }
                                }
                                for (Relationship r : PSRels) {
                                    if (!r.isDeleted()) {
                                        String m = r.getManagementClass();
                                        if ((m == null) || (m.contains("Diagram"))) {
                                            try {
                                                Persistable pr = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                                                 if(!pl.contains(pr))
                                                pl.add(pr);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return pl;
    }

    private String HazardDependents(Hazard h, String type, String InitialText) {
        StringBuilder hdSB = new StringBuilder();
        String description = null;
        try {
            ArrayList<Relationship> rels = h.getRelationships(type);
            if (rels == null) {
                hdSB.append("No Related ").append(type);
                return hdSB.toString();
            }
            for (Relationship r : rels) {
                String m = r.getManagementClass();
                if ((m == null) || (m.contains("Diagram"))) { // controls creating two relations, filtering one relation.
                    Persistable p = MetaFactory.getInstance().getFactory(type).get(r.getTarget());
                    description = CheckforNullorEmptyValue(p, "Description");
                    if (type.equals("Cause") && (InitialText.equalsIgnoreCase("Existing Controls"))) {
                        hdSB.append(RelatedControlReport(p, "Control", "Existing Controls"));
                    } else if (type.equals("Cause") && InitialText.equalsIgnoreCase("Additional Controls")) {
                        hdSB.append(RelatedControlReport(p, "Control", "Additional Controls"));
                    } else if (type.equals("Effect") && (InitialText.equalsIgnoreCase("Existing Controls"))) {
                        hdSB.append(RelatedControlReport(p, "Control", "Existing Controls"));
                    } else if (type.equals("Effect") && InitialText.equalsIgnoreCase("Additional Controls")) {
                        hdSB.append(RelatedControlReport(p, "Control", "Additional Controls"));
                    } else if (p.getDatabaseObjectName().equalsIgnoreCase("Control")) {
                        if (p.getAttributeValue("GroupingType").equalsIgnoreCase("Existing") && InitialText.equalsIgnoreCase("Existing Controls")) {
                            hdSB.append(P_START).append("Title : ").append(p.getTitle()).append("<br/>Description : ").append(description).append(P_END);
                        } else if (p.getAttributeValue("GroupingType").equalsIgnoreCase("Additional") && InitialText.equalsIgnoreCase("Additional Controls")) {
                            hdSB.append(P_START).append("Title : ").append(p.getTitle()).append("<br/>Description : ").append(description).append(P_END);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return hdSB.toString().length() < 1 ? "No " + type + " exists with " + InitialText : hdSB.toString();
    }

    private String RelatedControlReport(Persistable P, String type, String InitialText) {
        StringBuilder hdSB = new StringBuilder();
        String ControlState,ControlDescription, ControlClicicalJustification, ControlEvidence, PersistableDescription;
        PersistableDescription = CheckforNullorEmptyValue(P, "Description");
        try {
            ArrayList<Relationship> rels = P.getRelationships(type);

            if (rels == null) {
                hdSB.append(P_START).append("Title : ").append(P.getTitle()).append("<br/>Description : ").append(PersistableDescription);
                hdSB.append("<br/>No Related ").append(type).append((P_END));
                return hdSB.toString();
            }
            for (Relationship r : rels) {
                Persistable p = MetaFactory.getInstance().getFactory(type).get(r.getTarget());
                ControlState = CheckforNullorEmptyValue(p, "State");
                ControlDescription = CheckforNullorEmptyValue(p, "Description");
                ControlClicicalJustification = CheckforNullorEmptyValue(p, "ClinicalJustification");
                ControlEvidence = CheckforNullorEmptyValue(p, "Evidence");
                if (((p.getAttributeValue("GroupingType").equalsIgnoreCase("Existing")) || (p.getAttributeValue("Type").equalsIgnoreCase("Existing"))) && InitialText.equalsIgnoreCase("Existing Controls")) { // p
                    hdSB.append("Title : ").append(P.getTitle()).append("<br/>Description : ").append(PersistableDescription);
                    hdSB.append("<br/> Related Control Title : ").append(p.getTitle()).append("<br/>Related Control Description : ").append(ControlDescription).append("<br/>Related Control State : ").append(ControlState);
                    hdSB.append("<br/> Related Control Clinical Justification : ").append(ControlClicicalJustification).append("<br/>Related Control Evidence : ").append(ControlEvidence).append("<br class=\"extendedheight\" /><br />");
                } else if ((p.getAttributeValue("GroupingType").equalsIgnoreCase("Additional") || (p.getAttributeValue("Type").equalsIgnoreCase("Additional"))) && InitialText.equalsIgnoreCase("Additional Controls")) { //p
                    hdSB.append("Title : ").append(P.getTitle()).append("<br/>Description : ").append(PersistableDescription);
                    hdSB.append("<br/>Related Control Title : ").append(p.getTitle()).append("<br/>Related Control Description : ").append(ControlDescription).append("<br/>Related Control State : ").append(ControlState);
                    hdSB.append("<br/> Related Control Clinical Justification : ").append(ControlClicicalJustification).append("<br />Related Control Evidence : ").append(ControlEvidence).append("<br class=\"extendedheight\" /><br />");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
//        if (hdSB.length() > 1) {
//            hdSB.append(P_END);
//        }
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
        hraSB.append(P_START);
        if (type.equals("iniitalAssessment")) {
            //hraSB.append("<br />");
            hraSB.append("Initial Severity: ");
            hraSB.append(Hazard.translateSeverity(is));
            hraSB.append("<br />");

//           // hraSB.append(P_START);
            hraSB.append("Initial Likelihood: ");
            hraSB.append(Hazard.translateLikelihood(ilh));
//           hraSB.append(P_END);
//           hraSB.append("<br />");

//            hraSB.append("Initial Risk Rating: ");
//            hraSB.append(String.format(PCLASS_START, irr));
//            hraSB.append(" &nbsp;").append(h.getAttribute("InitialRiskRating").getIntValue()).append(" &nbsp;");
//            hraSB.append(P_END).append("<br class=\"extendedheight\" />");
            hraSB.append("Initial Risk Rating: ");
            hraSB.append(String.format(SPANCLASS_START, irr));
            hraSB.append(" &nbsp;").append(h.getAttribute("InitialRiskRating").getIntValue()).append(" &nbsp;");
            hraSB.append(SPAN_END).append("<br class=\"extendedheight\" />").append(P_END);
            return hraSB.toString();
        } else {
            //hraSB.append("<br />");
            hraSB.append("Residual Severity: ");
            hraSB.append(Hazard.translateSeverity(rs));
            //hraSB.append(P_END);
             hraSB.append("<br/>");

           // hraSB.append(P_START);
            hraSB.append("Residual Likelihood: ");
            hraSB.append(Hazard.translateLikelihood(rlh));
           // hraSB.append(P_END);
            hraSB.append("<br/>");

//            hraSB.append("Residual Risk Rating: ");
//            hraSB.append(String.format(PCLASS_START, rrr));
//            hraSB.append(" &nbsp;").append(h.getAttribute("ResidualRiskRating").getIntValue()).append(" &nbsp;");
//            hraSB.append(P_END).append("<br /><br />");
            
            hraSB.append("Residual Risk Rating: ");
            hraSB.append(String.format(SPANCLASS_START, rrr));
            hraSB.append(" &nbsp;").append(h.getAttribute("ResidualRiskRating").getIntValue()).append(" &nbsp;");
            hraSB.append(SPAN_END).append("<br class=\"extendedheight\" />").append(P_END);
            return hraSB.toString();
        }
    }

    private String HazardProcessStep(Hazard h, String InitialText) {
        Boolean found = false;
        StringBuilder psSB = new StringBuilder();
        psSB.append(TR_START).append(TD_START).append(InitialText).append(TD_END);
        Collection<ProcessStep> PSF = null;
        HRPSList = new ArrayList<>(); // hazard related ProcessStep list
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
                                    HRPSList.add(p);
                                    psSB.append(TD_START).append(p.getAttributeValue("Name")).append(TD_END).append(TR_END);
                                } else {
                                    if (!HRPSList.contains(p)) {
                                        HRPSList.add(p);
                                        psSB.append(TR_START).append(TD_START).append(TD_END).append(TD_START).append(p.getAttributeValue("Name")).append(TD_END).append(TR_END);
                                    }
                                }
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

     // <editor-fold defaultstate="collapsed" desc="Unused Code">  
    private ArrayList<ProcessStep> HazardProcessStep(Hazard h)
    {
     ArrayList<ProcessStep> PSRels = new ArrayList<>();
     Collection<ProcessStep> PSC = null;
     try {
            PSC = MetaFactory.getInstance().getFactory("ProcessStep").getEntries();
        } catch (Exception e) {
            e.printStackTrace();
            return PSRels;
        }
        if (PSC != null) {
            for (ProcessStep p : PSC) {
                ArrayList<Relationship> PSR = p.getRelationships("Hazard");
                if (PSR == null) {
                    continue;
                }
                for (Relationship r : PSR) {
                    if (!r.isDeleted()) {
                        try {
                            if (r.getTarget() == h.getId() && PSRels.isEmpty()) {
                                    PSRels.add(p);
                                } else if (r.getTarget() == h.getId() && !PSRels.contains(p)) {
                                        PSRels.add(p);
                                    }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
     return PSRels;
    }// </editor-fold> 
    private String GeneratePersistableImage(Persistable p) {
        String imageData = "";
        BasicGraphEditor editor = null;
        HashMap<String, DiagramEditorElement> ex = null;
        String xml = p.getAttributeValue("GraphXml");
        if (p.getDatabaseObjectName().contentEquals("System")) {
            SystemEditorDetails sed = new SystemEditorDetails();
            SystemGraphEditor sge = new SystemGraphEditor(p.getId());
            sed.setSystem((System) p);
            ex = sed.getExistingGraph(p, null);
            sge.setExistingGraph(ex);
            if (ex != null) {
                editor = (BasicGraphEditor) sge;
            }
        } else if (p.getDatabaseObjectName() == "Process") {
            ProcessGraphEditor pge = new ProcessGraphEditor(p.getId());
            pge.setProcessId(p.getId(), xml, false);
            try {
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
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to build details for generatiing Process image. Send logs to support", "Warning", JOptionPane.ERROR_MESSAGE);
                SmartProject.getProject().log("Failed to build details for graphical Process editor in ReportEditor", e);
            }
        } else if (p.getDatabaseObjectName().contentEquals("Hazard")) {
            BowtieGraphEditor bge = new BowtieGraphEditor(p.getId());
            bge.setHazardId(p.getId(), xml, false);
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
                if ((float) image.getWidth() < (float) 600 && (float) image.getHeight() < (float) 500) {
                    w = (int) image.getWidth();
                    h = (int) image.getHeight();
                } else if (rateX > rateY) {
                    w = (int) (image.getWidth() * rateY);
                    h = (int) (image.getHeight() * rateY);

                } else {
                    w = (int) (image.getWidth() * rateX);
                    h = (int) (image.getHeight() * rateX);

                }
                //BufferedImage resizeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); //TYPE_INT_ARGB
                BufferedImage resizeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); //TYPE_INT_ARGB
                resizeImage.createGraphics().drawImage(image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
                imageData = encodeToBase64String(resizeImage, "png");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imageData;
    }

     private String IssuesReport() {
        StringBuilder IssuesSB = new StringBuilder();
        ArrayList<Persistable> IssuesList = null;
        Map<Integer, IssuesLog> Issues = new HashMap<Integer, IssuesLog>();
        try {
            if (proj != null) {
                IssuesList = MetaFactory.getInstance().getChildren("IssuesLog", "ProjectID", proj.getId());
            } else {
                IssuesList = MetaFactory.getInstance().getChildren("IssuesLog", "ProjectID", newObjectProjectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (IssuesList != null) {
            for (Persistable p : IssuesList) {
                if (!p.isDeleted()) {
                    Issues.put(p.getId(), (IssuesLog) p);
                }
            }
            for (IssuesLog issue : Issues.values()) {
                IssuesSB.append(H2_START);
                IssuesSB.append(issue.getAttributeValue("Name"));
                IssuesSB.append(H2_END);
                IssuesSB.append("Description: ").append(issue.getAttributeValue("Description"));
                IssuesSB .append("<br/>Reference: ").append(issue.getAttributeValue("ExternalIdentifier"));
                IssuesSB .append("<br/>Grouping Type: ").append(issue.getAttributeValue("GroupingType"));
                IssuesSB .append("<br/>CreatedDate: ").append(issue.getAttributeValue("CreatedDate"));
                IssuesSB .append("<br/>Resolved Date: ").append(issue.getAttributeValue("ResolvedDate"));
                IssuesSB .append("<br/>Resolution Type: ").append(issue.getAttributeValue("ResolutionType"));
                IssuesSB.append("<br/>Resolution Description: ").append(issue.getAttributeValue("Resolution")).append("<br/><br/>");
            }
        }else {
            IssuesSB.append("No issues found");
        }
        return IssuesSB.toString();

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

//    public String CheckforNullorEmptyValue(Persistable p) {
//        if (p.getAttributeValue("Description") == null || (p.getAttributeValue("Description")).equals("")) {
//            return "Not Provided.";
//        } else {
//            return p.getAttributeValue("Description");
//        }
//    }
    public String CheckforNullorEmptyValue(Persistable p, String Attribute) {
        if (p.getAttributeValue(Attribute) == null || (p.getAttributeValue(Attribute)).equals("")) {
            return "Not Provided.";
        } else {
            return p.getAttributeValue(Attribute);
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
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
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

    private static final String headerTable = "<h2><strong style=\"color: #507add;\">Document Management</strong></h2>\n"
            + "<h3><strong style=\"color: #507add;\">Revision History</strong></h3>\n"
            + "<table class=\"HeaderTable\">\n"
            + "    <tr class=\"title\">\n"
            + "        <td ><strong>Version</strong></td>\n"
            + "        <td ><strong>Date </strong></td>\n"
            + "        <td ><strong>Summary of Changes</strong></td>\n"
            + "    </tr>\n"
            + "    <tr>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"extended\"><p>&nbsp;</p></td>\n"
            + "    </tr>\n"
            + "	<tr>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"extended\"><p>&nbsp;</p></td>\n"
            + "    </tr>\n"
            + "	<tr>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"extended\"><p>&nbsp;</p></td>\n"
            + "    </tr>\n"
            + "	<tr>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"extended\"><p>&nbsp;</p></td>\n"
            + "    </tr>\n"
            + "	<tr>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"normal\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"extended\"><p>&nbsp;</p></td>\n"
            + "    </tr>\n"
            + "</table>\n"
            + "\n"
            + "<p>&nbsp;</p>\n"
            + "<h3><strong style=\"color: #507add;\">Reviewers</strong></h3>\n"
            + "<p>This document must be reviewed by the following people:</p>\n"
            + "<table class=\"HeaderTable\">\n"
            + "    <tr class=\"title\">\n"
            + "        <td ><strong>Reviewer Name</strong></td>\n"
            + "        <td ><strong>Title / Responsibility </strong></td>\n"
            + "        <td ><strong>Date</strong></td>\n"
            + "		<td ><strong>Version</strong></td>\n"
            + "    </tr>\n"
            + "    <tr>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"larger\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "		<td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "		\n"
            + "    </tr>\n"
            + "	<tr>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"larger\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "		<td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "    </tr>\n"
            + "	</table>\n"
            + "\n"
            + "<p>&nbsp;</p>\n"
            + "<h3><strong style=\"color: #507add;\">Approved by</strong></h3>\n"
            + "<p>This document must be approved by the following people:</p>\n"
            + "<table class=\"HeaderTable\">\n"
            + "    <tr class=\"title\">\n"
            + "        <td ><strong>Approver Name</strong></td>\n"
            + "        <td ><strong>Title / Responsibility </strong></td>\n"
            + "        <td ><strong>Date</strong></td>\n"
            + "		<td ><strong>Version</strong></td>\n"
            + "    </tr>\n"
            + "    <tr>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"larger\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "		<td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "		\n"
            + "    </tr>\n"
            + "	<tr>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"larger\"><p>&nbsp;</p></td>\n"
            + "        <td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "		<td class=\"medium\"><p>&nbsp;</p></td>\n"
            + "    </tr>\n"
            + "	</table>\n"
            + "\n"
            + "<p>&nbsp;</p>";

}
