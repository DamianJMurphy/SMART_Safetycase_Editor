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
package uk.nhs.digital.projectuiframework.smart;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import uk.nhs.digital.projectuiframework.Project;
/**
 * DefaultTreeCellRenderer subclass to set display text size, and node icon.
 * 
 * @author damian
 */
public class ProjectTreeCellRenderer 
        extends javax.swing.tree.DefaultTreeCellRenderer
{
    private Project project = null;
    
    public ProjectTreeCellRenderer(Project p) {
        super();
        project = p;
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, 
            boolean leaf, int row, boolean hasFocus) 
    {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setFont(SmartProject.getProject().getDisplayFont());
        ImageIcon icon = project.getIcon(value);
        if (icon != null) {
            setIcon(icon);
        }
        return this;
//        ImageIcon labelIcon = new ImageIcon(icon.getImage().getScaledInstance(c.getHeight(), c.getHeight(), Image.SCALE_DEFAULT));
//        JLabel cell = new JLabel(value.toString(), labelIcon, JLabel.LEADING);
//        return cell;
    }
}
