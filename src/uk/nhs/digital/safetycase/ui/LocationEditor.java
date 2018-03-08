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
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.Location;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;

/**
 *
 * @author damian
 */
public class LocationEditor extends javax.swing.JPanel 
        implements uk.nhs.digital.safetycase.ui.PersistableEditor
{
    private EditorComponent editorComponent = null;
    private final String[] columns = {"Name", "Mnemonic", "Description", "Parent"};
    private Location location = null;
    private ArrayList<Location> projectLocs = new ArrayList<>();
    private int newObjectProjectId = -1;
    
    /**
     * Creates new form LocationEditor
     */
    public LocationEditor() {
        initComponents();
        parentLocationComboBox.setModel(new DefaultComboBoxModel());
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        discardButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        mnemonicTextField = new javax.swing.JTextField();
        parentLocationComboBox = new javax.swing.JComboBox<>();

        editorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        editorPanel.setEnabled(false);

        jLabel1.setText("Name");

        jLabel2.setText("Mnemonic");

        jLabel3.setText("Description");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        jScrollPane3.setViewportView(descriptionTextArea);

        discardButton.setText("Delete");
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Parent care setting");

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(discardButton))
                    .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, editorPanelLayout.createSequentialGroup()
                            .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                                .addComponent(mnemonicTextField)))
                        .addComponent(parentLocationComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(mnemonicTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parentLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(discardButton))
                .addGap(16, 16, 16))
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
                .addComponent(editorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed

        if (location == null)
            return;
        
        int r = JOptionPane.showConfirmDialog(this, "Really delete this Care setting ?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);        
        if (r == JOptionPane.CANCEL_OPTION)
            return;
        
        try {
            MetaFactory.getInstance().getFactory("Location").delete(location);
            SmartProject.getProject().editorEvent(Project.DELETE, location);
            SmartProject.getProject().getProjectWindow().closeContainer(this);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        

//        descriptionTextArea.setText("");
//        nameTextField.setText("");
//        mnemonicTextField.setText("");
//        locationsList.clearSelection();
    }//GEN-LAST:event_discardButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
    
        boolean created = false;
        if (location == null) {
            location = new Location();
            created = true;
        }
        location.setAttribute("Name", nameTextField.getText());
        location.setAttribute("Mnemonic", mnemonicTextField.getText());
        location.setAttribute("Description", descriptionTextArea.getText());
        if (newObjectProjectId == -1)
            location.setAttribute("ProjectID", Integer.parseInt(location.getAttributeValue("ProjectID")));
        else 
            location.setAttribute("ProjectID",newObjectProjectId);
        int parent = parentLocationComboBox.getSelectedIndex();
        if (parentLocationComboBox.getSelectedIndex() != -1) {
            Location p = projectLocs.get(parent);
            location.setAttribute("ParentLocationID", p.getId());
        } else {
            location.setAttribute("ParentLocationID", -1);
        }
        try {
            MetaFactory.getInstance().getFactory(location.getDatabaseObjectName()).put(location);
            if (created) {
                editorComponent.notifyEditorEvent(Project.ADD, location);
                projectLocs.add(location);
                ((DefaultComboBoxModel)parentLocationComboBox.getModel()).addElement(location.getAttributeValue("Name"));
            } else {
                editorComponent.notifyEditorEvent(Project.UPDATE, location);
            }
            SmartProject.getProject().getProjectWindow().setViewTitle(this, "Care setting:"  + location.getAttributeValue("Name"));
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void displayLocation() {
        if (location == null) {
            discardButtonActionPerformed(null);
            return;
        }
        descriptionTextArea.setText(location.getAttributeValue("Description"));
        nameTextField.setText(location.getAttributeValue("Name"));
        mnemonicTextField.setText(location.getAttributeValue("Mnemonic"));
        if (location.getAttribute("ParentLocationID").getIntValue() == -1) {
            parentLocationComboBox.setSelectedIndex(-1);
        } else {
            for (int i = 0; i < projectLocs.size(); i++) {
                if (projectLocs.get(i).getId() == location.getAttribute("ParentLocationID").getIntValue()) {
                    parentLocationComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    @Override
    public void setPersistableObject(Persistable p) {
        loadPotentialParents();
        if (p == null) {
            discardButtonActionPerformed(null);
            return;
        }
        try {
            location = (Location)p;
            displayLocation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

    private void loadPotentialParents() {
        try {
            projectLocs.clear();
            DefaultComboBoxModel dlm = new DefaultComboBoxModel();
            ArrayList<Persistable> locs = MetaFactory.getInstance().getChildren("Location", "ProjectID", SmartProject.getProject().getCurrentProjectID());
            if (locs != null) {
                for (Persistable s : locs) {
                    if (s.isDeleted())
                        continue;
                    Location l = (Location)s;
                    projectLocs.add(l);
                    dlm.addElement(l.getAttributeValue("Name"));
                }                    
            }
            parentLocationComboBox.setModel(dlm);
            parentLocationComboBox.setSelectedIndex(-1);
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
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton discardButton;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField mnemonicTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<String> parentLocationComboBox;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

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
        
        if (location == null)
            return false;
        if (o instanceof uk.nhs.digital.safetycase.data.Location) {
            Location c = (Location)o;
            if (c == location) {
                if (evtype == Project.DELETE) {
                    // Close this form and its container... 
                    SmartProject.getProject().getProjectWindow().closeContainer(this);
                    // then return true so that this form can be removed from the
                    // notifications list
                    return true;
                }
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
            Location c = (Location)o;
            if (c.getTitle().equals(location.getTitle()))
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
