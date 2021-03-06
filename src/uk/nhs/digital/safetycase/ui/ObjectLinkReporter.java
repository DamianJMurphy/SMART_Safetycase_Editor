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
package uk.nhs.digital.safetycase.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.ProjectLink;

/**
 *
 * @author damian
 */
public class ObjectLinkReporter 
        extends javax.swing.JPanel 
{
    private ArrayList<ProjectLink> links = null;
    private static final String[] LINKCOLUMNS = {"Link", "Type", "Comment", "Via"};
    private HashMap<String,JCheckBox> filters = new HashMap<>();
    
    private JTabbedPane container = null;
    
    /**
     * Creates new form ObjectLinkReporter
     */
    public ObjectLinkReporter(Persistable p, JTabbedPane c) {
        initComponents();
        initFilters();
        container = c;
        DefaultTableModel dtm = new DefaultTableModel(LINKCOLUMNS, 0);
        projectLinksTable.setModel(dtm);
        projectLinksTable.setDefaultRenderer(Object.class, new LinkExplorerTableCellRenderer());
        projectLinksTable.setRowHeight(SmartProject.getProject().getTableRowHeight());
        try {
            links = MetaFactory.getInstance().exploreLinks(p, p, links, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        displayFilteredLinks();
    }

    private void initFilters() 
    {
        filters.put("Cause", causesCheckBox);
        filters.put("Role", rolesCheckBox);
        filters.put("Hazard", hazardsCheckBox);
        filters.put("Effect", effectsCheckBox);
        filters.put("System", systemsCheckBox);
        filters.put("SystemFunction", systemFunctionsCheckBox);
        filters.put("Process", processesCheckBox);
        filters.put("ProcessStep", processStepsCheckBox);
        filters.put("Locations", locationsCheckBox);
        filters.put("Control", controlsCheckBox);
    }
    
    private void displayFilteredLinks() 
    {
        DefaultTableModel dtm = new DefaultTableModel(LINKCOLUMNS, 0);
        for (ProjectLink l : links) {
            if (filters.get(l.getRemoteType()).isSelected()) {
                Object[] row = new Object[LINKCOLUMNS.length];
                for (int i = 0; i < LINKCOLUMNS.length; i++) {
                    row[i] = l;
                }
                dtm.addRow(row);
            }
        }
        projectLinksTable.setModel(dtm);
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        systemsCheckBox = new javax.swing.JCheckBox();
        systemFunctionsCheckBox = new javax.swing.JCheckBox();
        rolesCheckBox = new javax.swing.JCheckBox();
        locationsCheckBox = new javax.swing.JCheckBox();
        processesCheckBox = new javax.swing.JCheckBox();
        processStepsCheckBox = new javax.swing.JCheckBox();
        hazardsCheckBox = new javax.swing.JCheckBox();
        causesCheckBox = new javax.swing.JCheckBox();
        controlsCheckBox = new javax.swing.JCheckBox();
        effectsCheckBox = new javax.swing.JCheckBox();
        libraryCheckBox = new javax.swing.JCheckBox();
        otherProjectsCheckBox = new javax.swing.JCheckBox();
        buttonPanel = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectLinksTable = new javax.swing.JTable();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Include links to/from"));

        systemsCheckBox.setSelected(true);
        systemsCheckBox.setText("Systems");

        systemFunctionsCheckBox.setSelected(true);
        systemFunctionsCheckBox.setText("System functions");

        rolesCheckBox.setSelected(true);
        rolesCheckBox.setText("Roles");

        locationsCheckBox.setSelected(true);
        locationsCheckBox.setText("Care settings");

        processesCheckBox.setSelected(true);
        processesCheckBox.setText("Processes");

        processStepsCheckBox.setSelected(true);
        processStepsCheckBox.setText("Process steps");

        hazardsCheckBox.setSelected(true);
        hazardsCheckBox.setText("Hazards");

        causesCheckBox.setSelected(true);
        causesCheckBox.setText("Causes");

        controlsCheckBox.setSelected(true);
        controlsCheckBox.setText("Controls");

        effectsCheckBox.setSelected(true);
        effectsCheckBox.setText("Effects");

        libraryCheckBox.setText("Library");
        libraryCheckBox.setEnabled(false);

        otherProjectsCheckBox.setText("Other projects");
        otherProjectsCheckBox.setEnabled(false);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(refreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(refreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportButton)
                .addGap(12, 12, 12)
                .addComponent(closeButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(systemFunctionsCheckBox)
                    .addComponent(rolesCheckBox)
                    .addComponent(systemsCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(locationsCheckBox)
                    .addComponent(processesCheckBox)
                    .addComponent(processStepsCheckBox))
                .addGap(86, 86, 86)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hazardsCheckBox)
                    .addComponent(causesCheckBox)
                    .addComponent(controlsCheckBox))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(libraryCheckBox)
                            .addComponent(effectsCheckBox))
                        .addGap(69, 69, 69))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(otherProjectsCheckBox)
                        .addGap(18, 18, 18)))
                .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(systemsCheckBox)
                            .addComponent(locationsCheckBox)
                            .addComponent(hazardsCheckBox)
                            .addComponent(effectsCheckBox))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(libraryCheckBox)
                            .addComponent(causesCheckBox)
                            .addComponent(processesCheckBox)
                            .addComponent(systemFunctionsCheckBox))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rolesCheckBox)
                            .addComponent(processStepsCheckBox)
                            .addComponent(controlsCheckBox)
                            .addComponent(otherProjectsCheckBox))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(jPanel1);

        projectLinksTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(projectLinksTable);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        displayFilteredLinks();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        container.remove(this);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(true);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setDialogTitle("Save unfiltered details");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();
        if (f.exists()) {
            int response = JOptionPane.showConfirmDialog(this, "A file with that name already exists. Overwrite it ?", "File exists", JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.OK_OPTION)
                return;
                
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter(f))) {
            br.write("Object\tType\tLinked to type\tName\tComment\tVia\tDeleted object en route\tLinked to deleted");
            br.newLine();
            for (ProjectLink l : links) {
                br.write(l.getLocalTitle());
                br.write("\t");
                br.write(l.getLocalDisplayName());
                br.write("\t");
                br.write(l.getRemoteDisplayName());
                br.write("\t");
                br.write(l.getRemoteTitle());
                br.write("\t");
                br.write(l.getDirectComment());
                br.write("\t");
                br.write(l.getRemotePath());
                br.write("\t");
                br.write(l.pathHasDeleted() ? "Yes" : "No");
                br.write("\t");
                br.write(l.remoteIsDeleted() ? "Yes" : "No");
                br.newLine();
                br.flush();
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error trying to export links report", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_exportButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JCheckBox causesCheckBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JCheckBox controlsCheckBox;
    private javax.swing.JCheckBox effectsCheckBox;
    private javax.swing.JButton exportButton;
    private javax.swing.JCheckBox hazardsCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox libraryCheckBox;
    private javax.swing.JCheckBox locationsCheckBox;
    private javax.swing.JCheckBox otherProjectsCheckBox;
    private javax.swing.JCheckBox processStepsCheckBox;
    private javax.swing.JCheckBox processesCheckBox;
    private javax.swing.JTable projectLinksTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JCheckBox rolesCheckBox;
    private javax.swing.JCheckBox systemFunctionsCheckBox;
    private javax.swing.JCheckBox systemsCheckBox;
    // End of variables declaration//GEN-END:variables
}
