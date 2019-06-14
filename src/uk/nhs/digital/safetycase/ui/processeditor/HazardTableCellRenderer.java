/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.safetycase.ui.processeditor;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import uk.nhs.digital.safetycase.data.Attribute;

/**
 *
 * @author Shakir
 */
public class HazardTableCellRenderer extends DefaultTableCellRenderer{


    public HazardTableCellRenderer() {
    }
        String[] status = {"Open", "Select..."};
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
           JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
           
           Color col = Color.WHITE;           
           Object tableStatusCol = table.getValueAt(row, 4);
           if (status[0].equalsIgnoreCase(tableStatusCol.toString()) || status[1].equalsIgnoreCase(tableStatusCol.toString())){
               col = Color.BLUE;
               label.setBackground(col);
               label.setForeground(Color.WHITE);
           } else {
               label.setBackground(col);
               label.setForeground(Color.BLACK);
           }             
         return label;           
    }    
}
