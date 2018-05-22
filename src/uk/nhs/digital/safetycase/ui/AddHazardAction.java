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
import javax.swing.JOptionPane;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.CustomGraphComponent;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.ProcessStep;

/**
 *
 * @author murff
 */
public class AddHazardAction 
       implements Action
{

    private boolean enabled = true;
    private ArrayList<PropertyChangeListener> listeners = null;
    private HashMap<String,Object> values = null;
    private mxCell selected = null;

    public AddHazardAction(Object o) {
        selected = (mxCell)o;

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
            
        boolean didsomething = false;
        Object o = e.getSource();
        CustomGraphComponent c = (CustomGraphComponent)o;
        int processid = c.getObjectId();
        if (processid != -1) {
            try {
                if (selected != null) {
                    ArrayList<Persistable> steps = MetaFactory.getInstance().getChildren("ProcessStep", "ProcessID", processid);
                    if (steps != null) {
                        for (Persistable p : steps) {
                            if (p.getAttributeValue("GraphCellId").contentEquals(selected.getId())) {
                                didsomething = true;
                                HazardEditor he = new HazardEditor().setParent((ProcessStep)p);
                                ExternalEditorView editorView = new ExternalEditorView(he.getComponent(), p.getAttributeValue("Name") + ":Hazards", SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());
                                break;

                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                SmartProject.getProject().log("Failed to add a new hazard", ex);
            }
        }
        if (!didsomething) {
            JOptionPane.showMessageDialog(c, "Save process diagram before adding a Hazard", "Process not saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
}
