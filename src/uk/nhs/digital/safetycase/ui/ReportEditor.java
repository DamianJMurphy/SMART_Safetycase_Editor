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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.safetycase.data.Project;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.Database;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.Report;
import uk.nhs.digital.safetycase.data.SystemFunction;

/**
 *
 * @author SHUL1
 */
public class ReportEditor extends javax.swing.JPanel implements uk.nhs.digital.safetycase.ui.PersistableEditor {

    private EditorComponent editorComponent = null;
    private Project project = null;
    
    private Report report = null;
    
    private int newObjectProjectId = -1;

    private Project proj = null;
    protected PersistableFactory<uk.nhs.digital.safetycase.data.System> systemFactory = null;
    protected PersistableFactory<SystemFunction> systemFunctionFactory = null;

    private Persistable focus = null;
    private HashMap<String, ArrayList<Relationship>> relationships = null;
    private ArrayList<Relationship> tableMap = null;
    private ArrayList<Persistable> targetInstances = null;

    //Report path
    private static String FILEPATH = "C:/SMART/Report/";

    // HTML TAGS
    private static final String TITLE_START = "<p style=\"text-align:center;font-size:42;color:##0000CD\" >";
    private static final String TITLE_END = "</P>\n\n\n\n\n";

    private static final String H1_START = "<H1 style=\"text-decoration: underline;color:#4169E1\" >";
    private static final String H1_END = "</H1>\n";
    private static final String H2_START = "<H2 style=\"color:#4682B4\" >";
    private static final String H2_END = "</H2>\n";
    private static final String H3_START = "<H3 style=\"color:#7B68EE\" >";
    private static final String H3_END = "</H3>\n";

    private static final String P_START = "<P>";
    private static final String P_END = "</P>\n";

    // Titles for sections
    private static final String INTRO_SECTION = H1_START + "Introduction" + H1_END;
    private static final String SYSTEM_DEFINITION_SECTION = H1_START + "System Definition / Overview" + H1_END;
    private static final String CLINICAL_RISK_MANAGEMENT_SECTION = H1_START + "Clinical Risk Management System" + H1_END;
    private static final String RISK_ANALYSIS = H1_START + "Clinical Risk Analysis" + H1_END;
    private static final String RISK_EVALUATION = H1_START + "Clinical Risk Evaluation " + H1_END;
    private static final String RISK_CONTROL = H1_START + "Clinical Risk Control" + H1_END;
    private static final String HAZARD_LOG = H1_START + "Hazard Log " + H1_END;
    private static final String TEST_ISSUES = H1_START + "Test Issues " + H1_END;
    private static final String SUMMARY_SAFETY_STATEMENT = H1_START + "Summary Safety Statement " + H1_END;
    private static final String QA_OCUMENT_APPROVAL = H1_START + "Quality Assurance and Document Approval " + H1_END;
    private static final String CONFIG_CONTROL = H1_START + "Configuration Control and Management " + H1_END;

    private static String HTML = "<html>\n<head>\n<title> Project Report</title>\n</head>\n<body>\n __HTMLBODY__ \n</body>\n</html>";

    private static String SYSTEM_REPORT = null;
    private static String HAZARD_REPORT = null;

    
    /**
     * Creates new form ReportsEditor
     */
    public ReportEditor() throws Exception {
        initComponents();

//        java.lang.System.setProperty(Database.CONNECTIONURLPROPERTY, "jdbc:hsqldb:file:C:/SMART/db/safety;shutdown=true");
//            java.lang.System.setProperty("user", "SA");
//            java.lang.System.setProperty("password", "");
//        metaFactory = MetaFactory.getInstance();
//        metaFactory.initialise();
//        PersistableFactory<Project> pf = metaFactory.getFactory("Project");
//        proj = (Project) pf.get(0);
        //get all the functions of each system and didplay name and description
        //systemFunctionFactory = metaFactory.getFactory("SystemFunction");
    }

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

        sectionsPanel.addTab("Safety system", safetySystemPanel);

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
        body.append(INTRO_SECTION);
        body.append(P_START);
        body.append(introductionEditor.getBodyText());
        body.append(P_END);
        body.append(SYSTEM_DEFINITION_SECTION);
        body.append(P_START);
        body.append(GenerateSystemReport());
        body.append(P_END);
        body.append(CLINICAL_RISK_MANAGEMENT_SECTION);        
        body.append(P_START);
        body.append(crmTextArea.getBodyText());
        body.append(P_END);
        body.append(QA_OCUMENT_APPROVAL);
        body.append(P_START);
        body.append(qaTextArea.getBodyText());
        body.append(P_END);
        body.append(SUMMARY_SAFETY_STATEMENT);
        body.append(P_START);
        body.append(safetyTextArea.getBodyText());
        body.append(P_END);
        body.append(CONFIG_CONTROL);
        body.append(P_START);
        body.append(configTextArea.getBodyText());
        body.append(P_END);

        String data = HTML.replace("__HTMLBODY__", body.toString());
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String dateTime = df.format(new Date(System.currentTimeMillis()));
        String name = proj.getTitle().toString().trim()+"_"+dateTime + ".html";
        String fileName = FILEPATH+"/"+ name;
        if(new File(FILEPATH).exists()) {
            FileWriter fWriter = null;
            BufferedWriter writer = null;
            try {
                fWriter = new FileWriter(fileName);
                writer = new BufferedWriter(fWriter);
                writer.write(data);
                writer.close();
                fWriter.close();
                JOptionPane.showMessageDialog(null, fileName, "Report Created", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage().toString(), "Error Occured", JOptionPane.ERROR_MESSAGE);
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
        }
        catch (Exception e) {
            e.printStackTrace();
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (newreport)
            editorComponent.notifyEditorEvent(uk.nhs.digital.projectuiframework.Project.ADD, report);
        else
            editorComponent.notifyEditorEvent(uk.nhs.digital.projectuiframework.Project.UPDATE, report);
    }//GEN-LAST:event_saveButtonActionPerformed

    private String GenerateSystemReport() {
        StringBuilder systemSB = new StringBuilder();

        ArrayList<Persistable> sys = null;
        try {
            if (proj != null)
                sys = MetaFactory.getInstance().getChildren("System", "ProjectID", proj.getId());
            else
                sys = MetaFactory.getInstance().getChildren("System", "ProjectID", newObjectProjectId);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        for (int i = 0; i < sys.size(); i++) {
            try {
                uk.nhs.digital.safetycase.data.System s = (uk.nhs.digital.safetycase.data.System) sys.get(i);
                if (s.isDeleted()) {
                    continue;
                }

                systemSB.append(H2_START);
                systemSB.append(s.getAttributeValue("Name"));
                systemSB.append(H2_END);
                systemSB.append(P_START);
                systemSB.append("System Version: ");
                systemSB.append(s.getAttributeValue("Version"));
                systemSB.append(P_END);
                systemSB.append(P_START);
                systemSB.append(s.getAttributeValue("Description"));
                systemSB.append(P_END);
                relationships = s.getRelationshipsForLoad();
                for (String st : relationships.keySet()) {
                    for (Relationship r : relationships.get(st)) {
                        systemSB.append(P_START);
                        systemSB.append(st);
                        systemSB.append(": ");
                        // row[1] = Integer.toString(r.getTarget());
                        Persistable target = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        systemSB.append(target.getAttributeValue("Name"));
                        systemSB.append(P_END);
                    }
                }

                systemSB.append(GenerateFunctionReport(s.getId()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return systemSB.toString();
    }

    private String GenerateFunctionReport(int sysID) throws Exception {
        String r = null;
        StringBuilder functionSB = new StringBuilder();
        ArrayList<Persistable> functions = null;
        try {
            functions = MetaFactory.getInstance().getChildren("SystemFunction", "SystemID", sysID);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (functions != null) {
            for (Persistable p : functions) {
                SystemFunction sf = (SystemFunction) p;
                functionSB.append(H3_START);
                functionSB.append(sf.getTitle());
                functionSB.append(H2_END);
                functionSB.append(P_START);
                functionSB.append(sf.getAttributeValue("Description"));
                functionSB.append(P_END);
                relationships = sf.getRelationshipsForLoad();
                //To do
                //find the usage of this function
            }
            return functionSB.toString();
        } else {
            return (P_START + "This system has no functions." + P_END);
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
            proj = (Project)MetaFactory.getInstance().getFactory("Project").get(report.getAttribute("ProjectID").getIntValue());
            titleTextField.setText(report.getAttributeValue("Title"));
            introductionEditor.setText(report.getAttributeValue("Introduction"));
            configTextArea.setText(report.getAttributeValue("CCMdetails"));
            crmTextArea.setText(report.getAttributeValue("CRMdetails"));
            qaTextArea.setText(report.getAttributeValue("QAADdetails"));
            safetyTextArea.setText(report.getAttributeValue("SummarySafetySystemDetails"));
        } catch (Exception ex) {
            ex.printStackTrace();
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
        return true;
    }

}
