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
package uk.nhs.digital.safetycase.ui.processeditor;

import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import javax.swing.JSplitPane;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.ui.PersistableEditor;
import uk.nhs.digital.safetycase.data.Process;

/**
 *
 * @author damian
 */
public class ProcessEditor 
        extends javax.swing.JSplitPane
        implements PersistableEditor
{
    
    private ProcessListForm table = null;
    private ProcessGraphEditor editor = null;
    private final String[] columns = {"ID", "Name", "Description", "Created"};
    private EditorComponent editorComponent = null;
    private int newObjectProjectId = -1;
    
    public ProcessEditor() {
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);

        mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

        table = new ProcessListForm(this);
	editor = new ProcessGraphEditor();
        this.setTopComponent(table);
        this.setBottomComponent(editor);
    }

    EditorComponent getEditorComponent() { return editorComponent; }
    
    int getNewObjectProjectId() { return newObjectProjectId; }
    
    @Override
    public void setEditorComponent(EditorComponent ed) {
        editorComponent = ed;
    }
    
    
    void addNewProcess(Process pr) {
        String[] row = new String[columns.length];
        row[0] = Integer.toString(pr.getId());
        row[1] = pr.getAttributeValue("Name");
        row[2] = pr.getAttributeValue("Description");
        row[3] = pr.getAttributeValue("CreatedDate");
        table.getTableModel().addRow(row);
        editorComponent.notifyEditorEvent(Project.ADD, pr);
    }   
    
    @Override
    public void setPersistableObject(Persistable p) {
        // This editor is used for all processes in the current project, so find the
        // processes, and load them into the table in the list form... but then 
        // highlight the one passed here unless it is null.
        if (p == null)
            return;
        try {
            Process process = (Process)p;
            PersistableFactory<Process> pfp = MetaFactory.getInstance().getFactory("Process");
            int projectid = Integer.parseInt(process.getAttributeValue("ProjectID"));
            Collection<Process> cp = pfp.getEntries(projectid);
            if ((cp == null) || (cp.isEmpty()))
                return;
            DefaultTableModel dtm = new DefaultTableModel(columns, 0);
            int i = 0;
            int selected = -1;
            for (Process pr : cp) {
                if (pr.isDeleted())
                    continue;
                String[] row = new String[columns.length];
                row[0] = Integer.toString(pr.getId());
                row[1] = pr.getAttributeValue("Name");
                row[2] = pr.getAttributeValue("Description");
                row[3] = pr.getAttributeValue("CreatedDate");
                dtm.addRow(row);
                if (pr.getId() == process.getId())
                    selected = i;
                i++;
            }
            table.setTableModel(dtm);
            if (selected != -1) {
                table.setSelected(selected);
                editor.setProcessId(process.getId(), process.getAttributeValue("GraphXml"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    void setSelectedProcess(int i) {
        try {
            PersistableFactory<Process> pfp = MetaFactory.getInstance().getFactory("Process");
            Process process = pfp.get(i);
            editor.setProcessId(process.getId(), process.getAttributeValue("GraphXml"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public void setNewObjectProjectId(int i) {
        newObjectProjectId = i;
    }
}
