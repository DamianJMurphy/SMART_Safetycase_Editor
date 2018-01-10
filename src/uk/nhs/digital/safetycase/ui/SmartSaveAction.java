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
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.safetycase.data.Attribute;
import uk.nhs.digital.safetycase.data.Cause;
import uk.nhs.digital.safetycase.data.Control;
import uk.nhs.digital.safetycase.data.Effect;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.Process;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.bowtie.BowtieGraphEditor;
import uk.nhs.digital.safetycase.ui.processeditor.ProcessGraphEditor;
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
        try {
            BasicGraphEditor editor = getEditor(e);
            String geclass = editor.getClass().toString();
            if (geclass.contains("Process")) {
                ProcessSaveHandler psh = new ProcessSaveHandler();
                psh.handle(editor);
            } else if (geclass.contains("Bowtie")) {
                BowtieSaveHandler bsh = new BowtieSaveHandler();
                bsh.handle(editor);
            } else {
                // Can add more here if we do system/function or project overview editors
                return;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   
}
