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

import com.mxgraph.model.mxCell;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.System;
import uk.nhs.digital.safetycase.data.SystemFunction;
import uk.nhs.digital.safetycase.ui.systemeditor.SystemGraphEditor;

/**
 *
 * @author damian
 */
public class SystemFunctionLinkAction 
        implements Action
{

    private boolean enabled = true;
    private ArrayList<PropertyChangeListener> listeners = null;
    private HashMap<String,Object> values = null;
    private mxCell selected = null;
    
    public SystemFunctionLinkAction(Object o) {
        selected = (mxCell)o;
    }
    
    public SystemFunctionLinkAction() {
        
    }
    
    @Override
    public Object getValue(String key) {
        if (values == null)
            return null;
        return values.get(key);
    }

    @Override
    public void putValue(String key, Object value) {
        if (values == null)
            values = new HashMap<>();
        values.put(key, value);
    }

    @Override
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null)
            listeners = new ArrayList<>();
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null)
            return;
        listeners.remove(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        Object o = e.getSource();
        SystemGraphEditor.CustomGraphComponent c = (SystemGraphEditor.CustomGraphComponent)o;
        int systemid = c.getSystemId();
        if (systemid != -1) {
            try {
                if (selected == null) {
                    PersistableFactory<uk.nhs.digital.safetycase.data.System> pf = MetaFactory.getInstance().getFactory("System");
                    uk.nhs.digital.safetycase.data.System system = pf.get(systemid);
                    JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(c), true);
                    linkEditor.add(new LinkEditor(system).setParent(linkEditor));
                    linkEditor.pack();
                    linkEditor.setVisible(true);
                } else {
                    // See if a system or a function is selected, and load the appropriate thing
                    // - use selected.getStyle() and see what the icon is
                    String stype = selected.getStyle();
                    if ((stype == null) || (stype.trim().length() == 0)) {
                        return;
                    }
                    ArrayList<Persistable> candidates = null;
                    if (stype.contains("system")) {
                        
                        // Need to see if this is the root system, or a subsystem. Check to see if the System instance referenced
                        // by systemid has the same GraphCellId as the selected one. If it isn't, we need to go through the
                        // related systems and find the one with the selected cell id.
                         
                    } else if (stype.contains("function")) {
                        
                    }
                    // TODO: This has been hacked just to get it to compile, and will need replacing with system and function
                    // equivalents once we know what the selected type is.
                    ArrayList<Persistable> steps = MetaFactory.getInstance().getChildren("ProcessStep", "ProcessID", systemid);
                    if (steps != null) {
                        for (Persistable p : steps) {
                            if (p.getAttributeValue("GraphCellId").contentEquals(selected.getId())) {
                                JDialog linkEditor = new JDialog(JOptionPane.getFrameForComponent(c), true);
                                linkEditor.add(new LinkEditor((System)p).setParent(linkEditor));
                                linkEditor.pack();
                                linkEditor.setVisible(true);
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                SmartProject.getProject().log("Failed to launch link editor", ex);

            }
        }
    }
    
}
