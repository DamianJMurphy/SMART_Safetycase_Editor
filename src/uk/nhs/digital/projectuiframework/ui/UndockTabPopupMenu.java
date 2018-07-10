/*
 * 
 *   Copyright 2018 NHS Digital
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
package uk.nhs.digital.projectuiframework.ui;

import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import uk.nhs.digital.projectuiframework.DataNotificationSubscriber;
import uk.nhs.digital.projectuiframework.smart.SmartProject;

/**
 *
 * @author damian
 */
public class UndockTabPopupMenu 
        extends JPopupMenu
{
    private JTabbedPane pane = null;
    private UndockTabComponent component = null;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UndockTabPopupMenu(JTabbedPane tp, UndockTabComponent c) {
        pane = tp;
        component = c;
        
        JMenuItem closeAllMenuItem = new JMenuItem("Close all tabs");
        JMenuItem closeOtherMenuItem = new JMenuItem("Close other tabs");
        
        closeAllMenuItem.addActionListener(listener -> closeAll());
        closeOtherMenuItem.addActionListener(listener -> closeOther());
        
        add(closeAllMenuItem);
        add(closeOtherMenuItem);        
    }
    
    private void closeAll() {
        int tabs = pane.getTabCount();
        for (int i = 0; i < tabs; i++) {
            Component c = pane.getComponentAt(i);
            if (c instanceof DataNotificationSubscriber) {
                DataNotificationSubscriber d = (DataNotificationSubscriber) c;
                if (d.isModified()) {
                    int r = JOptionPane.showConfirmDialog(c, "Save first ?", "Unsaved changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (r == JOptionPane.YES_OPTION) {
                        try {
                            d.notification(SmartProject.SAVE, d);
                        } catch (SaveRejectedException ex) {
                            return;
                        }
                    }
                }
                d.unsubscribe();
            }
        }    
        for (int i = tabs - 1; i >= 0; --i) {
            pane.remove(i);
        }
    }
    
    private void closeOther() {
        int tabs = pane.getTabCount();
        int current = pane.indexOfTabComponent(component);
        for (int i = 0; i < tabs; i++) {
            Component c = pane.getComponentAt(i);
            if (i == current)
                continue;
            
            if (c instanceof DataNotificationSubscriber) {
                DataNotificationSubscriber d = (DataNotificationSubscriber) c;
                if (d.isModified()) {
                    int r = JOptionPane.showConfirmDialog(c, "Save first ?", "Unsaved changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (r == JOptionPane.YES_OPTION) {
                        try {
                            d.notification(SmartProject.SAVE, d);
                        } catch (SaveRejectedException ex) {
                            return;
                        }
                    }
                }
                d.unsubscribe();
            }
        }    
        for (int i = tabs - 1; i >= 0; --i) {
            if (i != current)
                pane.remove(i);
        }
    }
}
