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
package uk.nhs.digital.safetycase.ui.bowtie;

import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.safetycase.data.Hazard;

/**
 *
 * @author murff
 */
public class HazardListForm extends javax.swing.JPanel {

    private static final String[] COLUMNS = {"ID", "Name", "Status", "Initial rating", "Residual rating"};
    private BowtieEditor bowtieEditor = null;
    /**
     * Creates new form HazardListForm
     */
    public HazardListForm(BowtieEditor b) {
        initComponents();
        bowtieEditor = b;
        DefaultTableModel dtm = new DefaultTableModel(COLUMNS, 0);
        hazardsTable.setModel(dtm);
    }

    public void addHazard(Hazard h) {
        if (h == null)
            return;
        String[] row = new String[COLUMNS.length];
        row[0] = Integer.toString(h.getId());
        row[1] = h.getAttributeValue("Name");
        row[2] = h.getAttributeValue("Status");
        row[3] = h.getAttributeValue("InitialRiskRating");
        row[4] = h.getAttributeValue("ResidualRiskRating");
        ((DefaultTableModel)hazardsTable.getModel()).addRow(row);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hazardsToolBar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        hazardsTable = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Hazards"));

        hazardsToolBar.setRollover(true);

        newButton.setText("New");
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        hazardsToolBar.add(newButton);

        editButton.setText("Edit");
        editButton.setFocusable(false);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        hazardsToolBar.add(editButton);

        deleteButton.setText("Delete");
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        hazardsToolBar.add(deleteButton);

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
        hazardsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hazardsTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(hazardsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hazardsToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(hazardsToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void hazardsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hazardsTableMouseClicked
        int r = hazardsTable.getSelectedRow();
        String id = (String)hazardsTable.getModel().getValueAt(r, 0);
        bowtieEditor.setSelectedHazard(Integer.parseInt(id));
    }//GEN-LAST:event_hazardsTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JTable hazardsTable;
    private javax.swing.JToolBar hazardsToolBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newButton;
    // End of variables declaration//GEN-END:variables
}