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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JSplitPane;
import javax.swing.table.DefaultTableModel;
import uk.nhs.digital.projectuiframework.Project;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.PersistableFilter;
import uk.nhs.digital.safetycase.ui.PersistableEditor;
import uk.nhs.digital.safetycase.data.Process;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.ui.DiagramEditorElement;

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

    // "Edit existing" functionality modelled on that for "Bowtie". Some differences:
    // 1. A bowtie has a central hazard. There is no "central" process step and there is already a foreign
    //      key relationship between the process and its steps.
    // 2. *At present* ProcessStep instances are created connected to the Process, but no relationships are
    //      made that connect them to one another - those connections exist only in the graph.
    // 3. There isn't any "obvious" way to distinguish between connection types in the way that there is with
    //      a bowtie where we have well-defined semantics for cause->control->hazard->mitigating control->effect
    //      relationships.
    // 4. But we do have an idea of flow, and "next step".
    //
    // Initial answer:
    // - don't try to make relationships between the ProcessStep instances. Keep it as it is with a collection
    //      of steps owned by a process
    // - treat these as with the Persistable instances in the bowtie, except that they're just a collection with
    //      no links between them
    //  - rationale is:
    //          a. "Decision" steps have open semantics. They can be "do a or b", "yes or no", "Liverpool or
    //              Newcastle" or any other choice. 
    //          b. Processes can rejoin. So rules for handling connection semantics may be complex and are in any
    //              case unexplored.
    //          c. Probably safer just now to identify a particular process.
    //          d. Relationships that are needed between steps can be added manually.
    //
    void setSelectedProcess(int i) {
        try {
            PersistableFactory<Process> pfp = MetaFactory.getInstance().getFactory("Process");
            Process process = pfp.get(i);
            editor.setProcessId(process.getId(), process.getAttributeValue("GraphXml"));
            
            PersistableFactory<ProcessStep> pfs = MetaFactory.getInstance().getFactory("ProcessStep");
            ArrayList<PersistableFilter> filter = new ArrayList<>();
            filter.add(new PersistableFilter("ProjectID", process.getAttributeValue("ProjectID")));
            filter.add(new PersistableFilter("ProcessID", process.getAttributeValue("ProcessID")));
            Collection<ProcessStep> steps = pfs.getEntries(filter);
            HashMap<String,DiagramEditorElement> existingSteps = new HashMap<>();
            for (ProcessStep ps : steps) {
                existingSteps.put(ps.getAttributeValue("GraphCellId"), new DiagramEditorElement(ps));
            }
            editor.setExistingSteps(existingSteps);
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
