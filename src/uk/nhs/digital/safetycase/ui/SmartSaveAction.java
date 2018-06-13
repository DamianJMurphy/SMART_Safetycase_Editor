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

//import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import static com.mxgraph.examples.swing.editor.EditorActions.getEditor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.projectuiframework.ui.SaveRejectedException;
/**
 *
 * @author damian
 */
public class SmartSaveAction 
    extends AbstractAction
{    
    public SmartSaveAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        BasicGraphEditor editor = null;
        try {
            editor = getEditor(e);
            String geclass = editor.getClass().toString();
            if (geclass.contains("Process")) {
                ProcessSaveHandler psh = new ProcessSaveHandler();
                psh.handle(editor);
            } else if (geclass.contains("Bowtie")) {
                BowtieSaveHandler bsh = new BowtieSaveHandler();
                bsh.handle(editor);
            } else if (geclass.contains("SystemGraph")) {
                SystemSaveHandler ssh = new SystemSaveHandler();
                ssh.handle(editor);
            } else {
                // Can add more here if we do system/function or project overview editors
                return;
            }
        }
        catch (SaveRejectedException sre) {}
        catch (Exception ex) {
            JOptionPane.showMessageDialog(editor, "Failed to save", "Save failed, send logs to support", JOptionPane.ERROR_MESSAGE);
            SmartProject.getProject().log("Failed to save in SmartSaveAction", ex);
        }
    }
   
}
