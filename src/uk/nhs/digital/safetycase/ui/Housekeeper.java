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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.Relationship;

/**
 *
 * @author damian
 */
public class Housekeeper extends javax.swing.JDialog {

    private static final String[] TYPES = {"All", "Project", "System", "Function", "Care Setting", "Role", "Care Process", "Process Step", 
                                            "Hazard", "Cause", "Control", "Effect", "Report", "Issue"}; 
    private static final String[] PERSISTABLES = {"Project", "System", "SystemFunction", "Location", "Role", "Process", "ProcessStep",
                                            "Hazard", "Cause", "Control", "Effect", "Report", "IssuesLog"};
    private static final String[] COLUMNS = {"Name", "Type", "Deleted date", "Relationships"};
    
    private final ArrayList<Persistable> currentSelection = new ArrayList<>();
    private Persistable selectedObject = null;
    /**
     * Creates new form Housekeeper
     * @param parent
     * @param modal
     */
    public Housekeeper(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        objectTypeComboBox.setSelectedIndex(-1);
        ListSelectionModel lsm = selectedTable.getSelectionModel();
        lsm.addListSelectionListener((ListSelectionEvent evt) -> {
            int sel = selectedTable.getSelectedRow();
            if (sel == -1)
                return;
            selectedObject = currentSelection.get(sel);
            populateDetails(selectedObject);
        });
        dependenciesTree.setCellRenderer(new HousekeeperDependencyTreeCellRenderer());
        DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode("Nothing selected...");
        DefaultTreeModel ntm = new DefaultTreeModel(dmtn);
        dependenciesTree.setModel(ntm);        
    }

    
    private void populateDetails(Persistable p) 
    {
        if (p == null)
            return;
        
        nameTextField.setText(p.getAttributeValue("Name"));
        deletedOnTextField.setText(p.getAttributeValue("DeletedDate"));
        
        // Populate the tree. Treat everything the same for now and
        // go through relationships, both from this Persistable and from other 
        // things to it. If a relationship is deleted or points to a deleted onject
        // then show in green. Otherwise in red. Distinguish links from and to
        // this.
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(p.getDisplayName() + " : " + p.getAttributeValue("Name"));
        DefaultTreeModel dtm = new DefaultTreeModel(root);
        HashMap<String, ArrayList<Relationship>> rels = p.getRelationshipsForLoad();
        if (rels != null) {
            for (String s : rels.keySet()) {
                ArrayList<Relationship> a = rels.get(s);
                if (a != null) {
                    for (Relationship r : a) {
                        try {
                            Persistable t = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                            HousekeeperDependency h = new HousekeeperDependency(r.getComment(), true, t, HousekeeperDependency.TO);
                            DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode();
                            dmtn.setUserObject(h);
                            root.add(dmtn);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        populateFromLinks(root, p);
        dependenciesTree.setModel(dtm);
    }
    
    private void populateFromLinks(DefaultMutableTreeNode n, Persistable p) {
        
        try {
            for (String s : MetaFactory.getInstance().getFactories()) {
                PersistableFactory pf = MetaFactory.getInstance().getFactory(s);
                Collection<Persistable> entries = pf.getEntries();
                if (entries != null) {
                    for (Persistable other : entries) {
                        HashMap<String, ArrayList<Relationship>> rels = other.getRelationshipsForLoad();
                        if (rels != null) {
                            for (String rs : rels.keySet()) {
                                ArrayList<Relationship> a = rels.get(rs);
                                if (a != null) {
                                    for (Relationship r : a) {
                                        if ((r.getTargetType().contentEquals(p.getDatabaseObjectName())) && (r.getTarget() == p.getId())) {
                                            Persistable t = MetaFactory.getInstance().getFactory(s).get(r.getSource());
                                            HousekeeperDependency h = new HousekeeperDependency(r.getComment(), true, t, HousekeeperDependency.FROM);
                                            DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode();
                                            dmtn.setUserObject(h);
                                            n.add(dmtn);
                                        }
                                    }    
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        objectTypeComboBox = new javax.swing.JComboBox<>();
        cleanSelectedButton = new javax.swing.JButton();
        cleanAllButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        bottomPanelSplitPane = new javax.swing.JSplitPane();
        objectSelectionPanel = new javax.swing.JPanel();
        selectedTableScrollPane = new javax.swing.JScrollPane();
        selectedTable = new javax.swing.JTable();
        detailsPanel = new javax.swing.JPanel();
        detailControlPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        deletedOnTextField = new javax.swing.JTextField();
        undeleteButton = new javax.swing.JButton();
        cleanButton = new javax.swing.JButton();
        dependenciesTreeScrollPane = new javax.swing.JScrollPane();
        dependenciesTree = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("Object type:");
        topPanel.add(jLabel1);

        objectTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Project", "System", "Function", "Care Setting", "Role", "Care Process", "Process Step", "Hazard", "Cause", "Control", "Effect", "Report", "Issue" }));
        objectTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                objectTypeComboBoxActionPerformed(evt);
            }
        });
        topPanel.add(objectTypeComboBox);

        cleanSelectedButton.setText("Clean up selected");
        topPanel.add(cleanSelectedButton);

        cleanAllButton.setText("Clean up all");
        topPanel.add(cleanAllButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        topPanel.add(closeButton);

        getContentPane().add(topPanel);

        bottomPanel.setLayout(new javax.swing.BoxLayout(bottomPanel, javax.swing.BoxLayout.LINE_AXIS));

        bottomPanelSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        objectSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Deleted records"));
        objectSelectionPanel.setLayout(new javax.swing.BoxLayout(objectSelectionPanel, javax.swing.BoxLayout.LINE_AXIS));

        selectedTable.setModel(new javax.swing.table.DefaultTableModel(
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
        selectedTableScrollPane.setViewportView(selectedTable);

        objectSelectionPanel.add(selectedTableScrollPane);

        bottomPanelSplitPane.setLeftComponent(objectSelectionPanel);

        detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));
        detailsPanel.setLayout(new java.awt.BorderLayout());

        detailControlPanel.setMaximumSize(new java.awt.Dimension(65536, 80));
        detailControlPanel.setMinimumSize(new java.awt.Dimension(844, 80));
        detailControlPanel.setOpaque(false);
        detailControlPanel.setPreferredSize(new java.awt.Dimension(1000, 80));
        detailControlPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Name");
        detailControlPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 14, -1, -1));

        nameTextField.setEditable(false);
        nameTextField.setText("jTextField1");
        detailControlPanel.add(nameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(102, 12, 740, -1));

        jLabel3.setText("Deleted on");
        detailControlPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 48, -1, -1));

        deletedOnTextField.setEditable(false);
        deletedOnTextField.setText("jTextField1");
        detailControlPanel.add(deletedOnTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(102, 46, 217, -1));

        undeleteButton.setText("Undelete");
        undeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undeleteButtonActionPerformed(evt);
            }
        });
        detailControlPanel.add(undeleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 40, -1, -1));

        cleanButton.setText("Clean");
        detailControlPanel.add(cleanButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 40, -1, -1));

        detailsPanel.add(detailControlPanel, java.awt.BorderLayout.NORTH);

        dependenciesTreeScrollPane.setViewportView(dependenciesTree);

        detailsPanel.add(dependenciesTreeScrollPane, java.awt.BorderLayout.CENTER);

        bottomPanelSplitPane.setRightComponent(detailsPanel);

        bottomPanel.add(bottomPanelSplitPane);

        getContentPane().add(bottomPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void objectTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_objectTypeComboBoxActionPerformed
        int selected = objectTypeComboBox.getSelectedIndex();
        if (selected == -1)
            return;
        String selectedType = null;
        if (selected != 0) {
            selectedType = PERSISTABLES[selected - 1];            
        }
        populateSelectionTable(selectedType);
    }//GEN-LAST:event_objectTypeComboBoxActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void undeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undeleteButtonActionPerformed
        if (selectedObject == null)
            return;
        
        try {
            MetaFactory.getInstance().getFactory(selectedObject.getDatabaseObjectName()).undelete(selectedObject);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        DefaultTableModel dtm = (DefaultTableModel)selectedTable.getModel();
        int i = currentSelection.indexOf(selectedObject);
        dtm.removeRow(i);
        currentSelection.remove(i);
        DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode("Nothing selected...");
        DefaultTreeModel ntm = new DefaultTreeModel(dmtn);
        dependenciesTree.setModel(ntm);
        nameTextField.setText("");
        deletedOnTextField.setText("");
        selectedObject = null;
        try {
            SmartProject.getProject().reInitialiseProjectView();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_undeleteButtonActionPerformed

    private void populateSelectionTable(String typeFilter) 
    {
        currentSelection.clear();
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
        for (String s : PERSISTABLES) {
            if ((typeFilter == null) || (typeFilter.contentEquals(s))) {
                try {
                    Collection<Persistable> entries = MetaFactory.getInstance().getFactory(s).getEntries();
                    if (entries != null) {
                        for (Persistable p : entries) {
                            if (p.isDeleted()) {
                                String[] row = new String[COLUMNS.length];
                                row[0] = p.getAttributeValue("Name");
                                row[1] = p.getDisplayName();
                                row[2] = p.getAttributeValue("DeletedDate");
                                if ((row[2] == null) || (row[2].trim().length() == 0)) { // Just done and not reloaded from the database
                                    row[2] = "Just now";
                                }
                                if (p.getRelationshipsForLoad() != null) {
                                    row[3] = "Yes";
                                } else {
                                    row[3] = "No";
                                }
                                dtm.addRow(row);
                                currentSelection.add(p);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        selectedTable.setModel(dtm);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JSplitPane bottomPanelSplitPane;
    private javax.swing.JButton cleanAllButton;
    private javax.swing.JButton cleanButton;
    private javax.swing.JButton cleanSelectedButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField deletedOnTextField;
    private javax.swing.JTree dependenciesTree;
    private javax.swing.JScrollPane dependenciesTreeScrollPane;
    private javax.swing.JPanel detailControlPanel;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPanel objectSelectionPanel;
    private javax.swing.JComboBox<String> objectTypeComboBox;
    private javax.swing.JTable selectedTable;
    private javax.swing.JScrollPane selectedTableScrollPane;
    private javax.swing.JPanel topPanel;
    private javax.swing.JButton undeleteButton;
    // End of variables declaration//GEN-END:variables
}
