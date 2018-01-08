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
package uk.nhs.digital.safetycase.importer;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 *
 * @author damian
 */
public class Importer {
    
    private File inputFile = null;
    private HashMap<String, uk.nhs.digital.safetycase.data.System> systems = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.SystemFunction> systemfunctions = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.Process> processes = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.ProcessStep> processsteps = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.Hazard> hazards = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.Control> controls = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.Cause> causes = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.Effect> effects = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.Location> locations = new HashMap<>();
    private HashMap<String, uk.nhs.digital.safetycase.data.Role> roles = new HashMap<>();
    
    public Importer(String f) 
            throws Exception
    {
        String problem = null;
        if ((f == null) || (f.trim().length() == 0))
            problem = "No file";
        inputFile = new File(f);
        if (!inputFile.exists())
            problem = "File " + f + " does not exist";
        if (!inputFile.isFile())
            problem = "File " + f + " must be a normal file";
        if (!inputFile.canRead())
            problem = "File " + f + " : no read permission";
        if (problem != null)
            throw new Exception(problem);
    }
    
    public void process()
            throws Exception
    {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = db.parse(inputFile);
        
    }
    
    private void doCareProcess(Document d)
            throws Exception
    {
        
    }
}

