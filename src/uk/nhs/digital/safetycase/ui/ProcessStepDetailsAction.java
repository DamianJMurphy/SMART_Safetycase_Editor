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
import javax.swing.JPanel;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.ui.processeditor.ProcessGraphEditor;
import uk.nhs.digital.safetycase.ui.processeditor.ProcessStepDetail;

/**
 *
 * @author murff
 */
public class ProcessStepDetailsAction 
       implements Action
{
    private boolean enabled = true;
    private ArrayList<PropertyChangeListener> listeners = null;
    private HashMap<String,Object> values = null;
    private mxCell selected = null;
    
    public ProcessStepDetailsAction(Object o) {
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
        Object o = e.getSource();
        uk.nhs.digital.safetycase.ui.processeditor.ProcessGraphEditor.CustomGraphComponent c = (uk.nhs.digital.safetycase.ui.processeditor.ProcessGraphEditor.CustomGraphComponent)o;
        ProcessGraphEditor container = (ProcessGraphEditor)c.getParent().getParent();
        int processid = c.getProcessId();
        if (processid != -1) {
            try {
                if (selected != null) {
                    ArrayList<Persistable> steps = MetaFactory.getInstance().getChildren("ProcessStep", "ProcessID", processid);
                    if (steps != null) {
                        for (Persistable p : steps) {
                            if (p.getAttributeValue("GraphCellId").contentEquals(selected.getId())) {
                                
                                JPanel pnl = SmartProject.getProject().getExistingEditor(p, container);
                                if (pnl != null) {
                                    SmartProject.getProject().getProjectWindow().selectPanel(pnl);
                                    return;
                                }
                                
                                ProcessStepDetail psd = new ProcessStepDetail((ProcessStep)p);
                                ExternalEditorView editorView = new ExternalEditorView(psd, "Step:" + p.getAttributeValue("Name"), SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());
//                                JDialog detailEditor = new JDialog(JOptionPane.getFrameForComponent(c), false);
//                                detailEditor.add(new ProcessStepDetail((ProcessStep)p).setParent(detailEditor));
//                                detailEditor.pack();
//                                detailEditor.setVisible(true);
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
    }
}
