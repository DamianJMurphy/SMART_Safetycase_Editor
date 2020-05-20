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
package uk.nhs.digital.safetycase.ui.bowtie;

import com.mxgraph.model.mxCell;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.CustomGraphComponent;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.HazardEditor;
import uk.nhs.digital.safetycase.ui.PersistableEditor;

/**
 *
 * @author damian
 */
public class BowtieObjectDetailsAction 
        implements Action
{
    private boolean enabled = true;
    private ArrayList<PropertyChangeListener> listeners = null;
    private HashMap<String,Object> values = null;
    private mxCell selected = null;

   public BowtieObjectDetailsAction(Object o) {
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
//        uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor.CustomGraphComponent c = (uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor.CustomGraphComponent)o;
        CustomGraphComponent c = (CustomGraphComponent)o;
        int hazardid = c.getObjectId();
        BowtieGraphEditor container = (BowtieGraphEditor)c.getParent().getParent();
        
        // this.selected is the cell, which tells us what type of thing has been clicked and its cell id.
        // So get the Hazard, and its relationships to that type, and find the one with that cell id. That
        // will give us the editor type we need to show, and the object it needs to contain
        
        try {
            Hazard h = (Hazard)MetaFactory.getInstance().getFactory("Hazard").get(hazardid);
            

            String cellid = selected.getId();
            String style = selected.getStyle();
            int lastslash = style.lastIndexOf("/");
            char f = style.charAt(lastslash + 1);
            Character uf = Character.toUpperCase(f);
            String type = uf + style.substring(lastslash + 2, style.indexOf("."));
            
            // Is it the Hazard itself ?
            if (type.contentEquals("Hazard")) {

                JPanel pnl = SmartProject.getProject().getExistingEditor(h, container);
                if (pnl != null) {
                    SmartProject.getProject().getProjectWindow().selectPanel(pnl);
                    return;
                }

//                JDialog detailEditor = new JDialog(JOptionPane.getFrameForComponent(c), true);
                HazardEditor he = new HazardEditor();
                he.setPersistableObject(h);
//                detailEditor.add(he);
//                detailEditor.pack();
//                detailEditor.setVisible(true);
                ExternalEditorView hazardView = new ExternalEditorView(he, "Hazard:" + h.getAttributeValue("Name"), SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());
            } else {            
                // No... find the object and display the editor

                for (Relationship r :h.getRelationships(type)) {
                    Persistable p = MetaFactory.getInstance().getFactory(type).get(r.getTarget());
                    String v = p.getAttributeValue("GraphCellId");
                    if ((v != null) && (cellid.contentEquals(v))) {
                        // Found it - we're editing p - now find what sort of editor we need
                        String s = p.getEditorType();
                        String eclass = java.lang.System.getProperty(uk.nhs.digital.projectuiframework.smart.SmartProject.EDITORCLASSROOT + s);
                        if (eclass == null)
                            return;
                        
                        JPanel pnl = SmartProject.getProject().getExistingEditor(p, container);
                        if (pnl != null) {
                            SmartProject.getProject().getProjectWindow().selectPanel(pnl);
                            return;
                        }
                        
//                        JDialog detailEditor = new JDialog(JOptionPane.getFrameForComponent(c), true);
                        PersistableEditor pe = (PersistableEditor)Class.forName(eclass).newInstance();
                        pe.setPersistableObject(p);
//                        detailEditor.add(pe.getComponent());
//                        detailEditor.pack();
//                        detailEditor.setVisible(true);
                        ExternalEditorView editorView = new ExternalEditorView(pe.getComponent(), p.getDisplayName() + ":" + p.getAttributeValue("Name"), SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());
                        return;
                    }
                }
            }
        }
        catch (Exception ex) {
                JOptionPane.showMessageDialog(container, "Cannot make details view. Send logs to support", "Warning", JOptionPane.INFORMATION_MESSAGE);
                SmartProject.getProject().log("Failed to open bowtie object details view", ex);
        }
    }
    
}
