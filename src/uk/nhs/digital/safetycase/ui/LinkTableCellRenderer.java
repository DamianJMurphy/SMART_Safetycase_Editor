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
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.Relationship;

/**
 *
 * @author damian
 */
public class LinkTableCellRenderer 
        extends DefaultTableCellRenderer
{
    private static final int TYPE = 0;
    private static final int NAME = 1;
    private static final int COMMENT = 2;
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(!(value instanceof uk.nhs.digital.safetycase.data.Relationship))
            return this;
        try {
            Relationship r = (Relationship)value;
            Persistable target = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
            StringBuilder sb = new StringBuilder();
            String content = null;
            switch (column) {
                case TYPE:
//                    content = r.getTargetType();
                    content = target.getDisplayName();
                    break;
                case NAME:
                    content = target.getAttributeValue("Name");
                    break;
                case COMMENT:
                    content = r.getComment();
                    if (content == null)
                        content = "";
                    break;
                default:
                    break;
            }
            if (target.isDeleted()) {
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
