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
package uk.nhs.digital.safetycase.ui.systemeditor;

import com.mxgraph.model.mxCell;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JPanel;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.CustomGraphComponent;
import uk.nhs.digital.projectuiframework.ui.ExternalEditorView;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.data.SystemFunction;

/**
 *
 * @author damian
 */
public class SystemFunctionDetailsAction 
        implements Action
{
    private boolean enabled = true;
    private ArrayList<PropertyChangeListener> listeners = null;
    private HashMap<String,Object> values = null;
    private mxCell selected = null;

   public SystemFunctionDetailsAction(Object o) {
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
    public void actionPerformed(ActionEvent e) 
    {
        try {
            Object o = e.getSource();
            CustomGraphComponent sgec = (CustomGraphComponent)o;

            int rootSystem = sgec.getObjectId();
            SystemGraphEditor sge = (SystemGraphEditor)sgec.getParent().getParent();
            Persistable p = getSelectedObject(rootSystem, selected.getId());
            if (p != null) {
                // Show the relevant details form: TEST UP TO HERE NEXT
                System.out.println("Got " + p.getDisplayName() + ":" + p.getTitle());
                JPanel pnl = SmartProject.getProject().getExistingEditor(p, sge);
                if (pnl != null) {
                    SmartProject.getProject().getProjectWindow().selectPanel(pnl);
                    return;
                }
                
                if (p.getDatabaseObjectName().contentEquals("System")) {
                    SystemEditorDetails sde = new SystemEditorDetails((uk.nhs.digital.safetycase.data.System)p);
                    ExternalEditorView systemView = new ExternalEditorView(sde, "System:" + p.getAttributeValue("Name"), SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());
                } else {
                    SystemFunctionEditor sfe = new SystemFunctionEditor();
                    sfe.setPersistableObject((SystemFunction)p);
                    ExternalEditorView systemView = new ExternalEditorView(sfe, "Function:" + p.getAttributeValue("Name"), SmartProject.getProject().getProjectWindow().getMainWindowTabbedPane());
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Persistable getSelectedObject(int rs, String cid) 
            throws Exception
    {
        int cellId = Integer.parseInt(cid);

        // when this is first called it gets the root system id, and the GraphCellId of the
        // selected object from the editor. Get the root System. Return it if that is what 
        // the user has selected. Otherwise, start searching the relationships until we find
        // a target with that cell id. Return null if we don't find one.
        
        uk.nhs.digital.safetycase.data.System root = (uk.nhs.digital.safetycase.data.System)MetaFactory.getInstance().getFactory("System").get(rs);
        if (root.getAttribute("GraphCellId").getIntValue() == cellId)
            return root;
                
         return search(root, cellId);
    }
    
    private Persistable search(Persistable p, int c)
            throws Exception
    {
        ArrayList<Relationship> rels = p.getRelationships("System");
        if (rels == null)
            rels = new ArrayList<>();
        ArrayList<Relationship> rf = p.getRelationships("SystemFunction");
        if (rf != null)
            rels.addAll(rf);
        
        if (rels.isEmpty())
            return null;
        
        for (Relationship r : rels) {
            if (!r.isDeleted()) {
                Persistable pr = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                if (!pr.isDeleted() && (pr.getAttribute("GraphCellId").getIntValue() == c)) {
                    return pr;
                }
                pr = search(pr, c);
                if (pr != null)
                    return pr;
            }
        }
        return null;
    }
}
