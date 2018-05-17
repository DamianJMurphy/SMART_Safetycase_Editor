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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import uk.nhs.digital.safetycase.data.ProjectLink;

/**
 *
 * @author damian
 */
public class LinkExplorerTableCellRenderer 
        extends DefaultTableCellRenderer

{
    private static final int LINK = 0;
    private static final int TYPE = 1;
    private static final int COMMENT = 2;
    private static final int VIA = 3;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!(value instanceof uk.nhs.digital.safetycase.data.ProjectLink))
            return this;
        
        try {
            ProjectLink link = (ProjectLink)value;
            StringBuilder sb = new StringBuilder();
            String content = null;
            switch (column) {
                case LINK:
                    content = link.getRemoteTitle();
                    break;
                case TYPE:
                    content = link.getRemoteDisplayName();
                    break;
                case COMMENT:
                    content = link.getDirectComment();
                    break;
                case VIA:
                    content = link.getRemotePath();
                    break;
                default:
                    break;
            }
            if (link.pathHasDeleted()) {
                setBackground(Color.yellow);
            }
            if (link.remoteIsDeleted()) {
                setForeground(Color.red);
                sb.append("<html><strike>");
                sb.append(content);
                sb.append("</strike></html>");
            } else {
                setForeground(Color.black);
                sb.append(content);
            }
            setText(sb.toString());                
        }
        catch (Exception eIgnore) {}
        return this;
    }
    
}
