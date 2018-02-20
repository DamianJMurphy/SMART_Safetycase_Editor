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

import java.util.ArrayList;
import uk.nhs.digital.safetycase.data.Persistable;

/**
 *
 * @author damian
 */
public class DiagramEditorElement {

    int cellId = -1;
    
    public ArrayList<String> connections = new ArrayList<>();
    public String type = null;
    public String name = null;
    public Persistable object = null;
    public boolean updateDone = false;
    
    DiagramEditorElement(String s, String n, String c) {
        cellId = Integer.parseInt(c);
        name = n;
        if (s.contains("bowtie")) {
            // Bowtie types are the actual persistable classes
            int lastslash = s.lastIndexOf("/");
            char f = s.charAt(lastslash + 1);
            Character uf = Character.toUpperCase(f);
            type = uf + s.substring(lastslash + 2, s.indexOf("."));
        } else if (s.contains("processeditor")) {
            // Process editor types are the "type" values from a ProcessStep
            int lastslash = s.lastIndexOf("/");
            char f = s.charAt(lastslash + 1);
            Character uf = Character.toUpperCase(f);
            type = uf + s.substring(lastslash + 2, s.indexOf("."));
        }else if (s.contains("systemeditor") && (!s.contains("SystemFunction"))) {
            // system types are the actual persistable classes
            int lastslash = s.lastIndexOf("/");
            char f = s.charAt(lastslash + 1);
            Character uf = Character.toUpperCase(f);
            type = uf + s.substring(lastslash + 2, s.indexOf("."));
        } else if (s.contains("SystemFunction")) {
            // systemfunction types are the actual persistable classes
            int lastslash = s.lastIndexOf("/");
            char f = s.charAt(lastslash+1);
            Character uf = Character.toUpperCase(f);
            type = uf + s.substring(lastslash + 2, s.indexOf("."));
        }
    }
    
    public DiagramEditorElement(Persistable p) {
        name = p.getAttributeValue("Name");
        type = p.getDatabaseObjectName();
        cellId = p.getAttribute("GraphCellId").getIntValue();
        object = p;
    }
}
