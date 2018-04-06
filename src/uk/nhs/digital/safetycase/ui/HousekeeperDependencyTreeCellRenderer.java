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

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author damian
 */
public class HousekeeperDependencyTreeCellRenderer
        extends DefaultTreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof javax.swing.tree.DefaultMutableTreeNode) {
            DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)value;
            if (dmtn.getUserObject() != null) {
                Object o = dmtn.getUserObject();
                if (o instanceof uk.nhs.digital.safetycase.ui.HousekeeperDependency) {
                    HousekeeperDependency dep = (HousekeeperDependency)o;
                    setForeground(dep.getColour());
                }
            }
        }
        return this;
    }
}
