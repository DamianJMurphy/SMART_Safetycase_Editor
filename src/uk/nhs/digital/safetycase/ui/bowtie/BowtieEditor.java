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
package uk.nhs.digital.safetycase.ui.bowtie;

import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import java.awt.Color;
import java.awt.Component;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.safetycase.data.Hazard;
import uk.nhs.digital.safetycase.data.MetaFactory;
import uk.nhs.digital.safetycase.data.Persistable;
import uk.nhs.digital.safetycase.data.PersistableFactory;
import uk.nhs.digital.safetycase.data.ProcessStep;
import uk.nhs.digital.safetycase.data.Relationship;
import uk.nhs.digital.safetycase.ui.BowtieElement;
import uk.nhs.digital.safetycase.ui.BrokenConnectionException;
import uk.nhs.digital.safetycase.ui.PersistableEditor;

/**
 *
 * @author murff
 */
public class BowtieEditor 
        extends javax.swing.JSplitPane
        implements PersistableEditor

{
    private HazardListForm table = null;
    private BowtieGraphEditor editor = null;
    private EditorComponent editorComponent = null;
    private int newObjectProjectId = -1;
    private ProcessStep processStep = null;
    
    
    public BowtieEditor() {
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);

        mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

        table = new HazardListForm(this);
        editor = new BowtieGraphEditor();
        this.setTopComponent(table);
        this.setBottomComponent(editor);        
    }
    
    public ProcessStep getProcessStep() { return processStep; }
    
    @Override
    public void setPersistableObject(Persistable p) {
        
        if (p == null)
            return;
        try {
            processStep = (ProcessStep)p;
            editor.setProcessStep(processStep);
            PersistableFactory<Hazard> pfh = MetaFactory.getInstance().getFactory("Hazard");
            Persistable process = MetaFactory.getInstance().getFactory("Process").get(processStep.getAttribute("ProcessID").getIntValue());
            int projectid = process.getAttribute("ProjectID").getIntValue();
            ArrayList<Relationship> relatedHazards = processStep.getRelationships("Hazard");
            if ((relatedHazards != null) && (!relatedHazards.isEmpty())) {
                for (Relationship r : relatedHazards) {
                    Hazard h = pfh.get(r.getTarget());
                    table.addHazard(h);
                }
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    void setSelectedHazard(int h) {
        try {
            PersistableFactory<Hazard> pfh = MetaFactory.getInstance().getFactory("Hazard");
            Hazard hazard = pfh.get(h);
            String xml =  hazard.getAttributeValue("GraphXml");
            if ((xml == null) || (xml.trim().length() == 0))
                return;
            
            editor.setHazardId(hazard.getId(),xml);
            
            // Make a collection of BowtieElement instances from the Hazard, and store it so we
            // can see what needs saving when the user asks.
            
            HashMap<String,BowtieElement> bowtieElements = new HashMap<>();
            bowtieElements.put(hazard.getAttributeValue("GraphCellId"), new BowtieElement(hazard));
            HashMap<String, ArrayList<Relationship>> hrels = hazard.getRelationshipsForLoad();
            if (hrels == null)
                return;
            
            for (ArrayList<Relationship> a : hrels.values()) {
                for (Relationship r : a) {
                    if ((r.getComment() != null) && (r.getComment().contains("Bowtie diagram"))) {
                        Persistable p = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
                        bowtieElements.put(p.getAttributeValue("GraphCellId"), new BowtieElement(p));
                    }
                }
            }
            // Get the graph xml from the hazard, and use the same process that the "save" function does to
            // tie everything together
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Element d = db.parse(is).getDocumentElement();
            NodeList nl = d.getElementsByTagName("mxCell");
            for (int i = 0; i < nl.getLength(); i++) {
                Element cell = (Element)nl.item(i);
                if (cell.hasAttribute("edge")) {
                    String s = cell.getAttribute("source");
                    String t = cell.getAttribute("target");
                    BowtieElement bt = bowtieElements.get(s);
                    bt.fromCell = Integer.parseInt(s);
                    bt.toCell = Integer.parseInt(t);
                }
            }
            
            editor.setExistingBowtie(bowtieElements);
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    EditorComponent getEditorComponent() { return editorComponent; }
    
    int getNewObjectProjectId() { return newObjectProjectId; }
    
    @Override
    public void setEditorComponent(EditorComponent ed) {
        editorComponent = ed;
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
