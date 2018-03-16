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
package uk.nhs.digital.safetycase.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import uk.nhs.digital.projectuiframework.smart.SmartProject;

/**
 *
 * @author damian
 */
public class MetaFactory {

    private static Exception bootException = null;
    
    private static final MetaFactory instance = new MetaFactory();
    
    
    private final HashMap<String,PersistableFactory> factories = new HashMap<>();
    private final HashMap<String,ValueSet> valueSets = new HashMap<>();
    private Database database = null;
    private uk.nhs.digital.projectuiframework.Project uiproject = null;
    private Library library = null;
    
    private MetaFactory() {
        try {
            database = new Database();
            PersistableFactory<Role> pfRole = new PersistableFactory<>(database, "Role");
            factories.put("Role", pfRole);
            PersistableFactory<System> pfSystem = new PersistableFactory<>(database, "System");
            factories.put("System", pfSystem);
            PersistableFactory<SystemFunction> pfSystemFunction = new PersistableFactory<>(database, "SystemFunction");
            factories.put("SystemFunction", pfSystemFunction);
            PersistableFactory<Location> pfLocation = new PersistableFactory<>(database, "Location");
            factories.put("Location", pfLocation);
            PersistableFactory<Project> pfProject = new PersistableFactory<>(database, "Project");
            factories.put("Project", pfProject);
            PersistableFactory<Process> pfProcess = new PersistableFactory<>(database, "Process");
            factories.put("Process", pfProcess);
            PersistableFactory<ProcessStep> pfProcessStep = new PersistableFactory<>(database, "ProcessStep");
            factories.put("ProcessStep", pfProcessStep);
            PersistableFactory<IssuesLog> pfIssuesLog = new PersistableFactory<>(database, "IssuesLog");
            factories.put("IssuesLog", pfIssuesLog);
            PersistableFactory<Effect> pfEffect = new PersistableFactory<>(database, "Effect");
            factories.put("Effect", pfEffect);
            PersistableFactory<Cause> pfCause = new PersistableFactory<>(database, "Cause");
            factories.put("Cause", pfCause);
            PersistableFactory<Control> pfControl = new PersistableFactory<>(database, "Control");
            factories.put("Control", pfControl);
            PersistableFactory<Hazard> pfHazard = new PersistableFactory<>(database, "Hazard");
            factories.put("Hazard", pfHazard);
            PersistableFactory<Report> pfReport = new PersistableFactory<>(database, "Report");
            factories.put("Report", pfReport);
            loadValueSets();   
            library = database.loadLibraries();
        }
        catch (Exception e) {
            bootException = e;
        }
    }
    
    public Library getLibrary() { return library; }
    
    private void loadValueSets()
            throws Exception
    {
        ValueSet v = new ValueSet("EffectType", "EffectType",	"Type");
        database.loadValueSet(v);
        valueSets.put(v.getName(), v);
        v = new ValueSet("HazardStatus", "HazardStatus", "Status");
        database.loadValueSet(v);
        valueSets.put(v.getName(), v);
        v = new ValueSet("ControlType","ControlType", "Type");
        database.loadValueSet(v);
        valueSets.put(v.getName(), v);
        v = new ValueSet("ControlState", "ControlState", "State");
        database.loadValueSet(v);
        valueSets.put(v.getName(), v);
        v = new ValueSet("ProcessStepType", "ProcessStepType", "Type");
        database.loadValueSet(v);
        valueSets.put(v.getName(), v);
        v = new ValueSet("DataQualityIssueResolutionType", "DataQualityIssueResolutionType", "ResolutionType");        
        database.loadValueSet(v);
        valueSets.put(v.getName(), v);
    }
    
    public String getDuplicateCheckMessage(String type, String display, String name, int pid, Persistable object) {
        if (type == null)
            return null;
        if (name == null)
            return null;
        PersistableFactory pf = factories.get(type);
        if (pf == null)
            return null;
        
        Collection<Persistable> c = null;
        if (pid != -1) {
            c = pf.getEntries(pid);
        } else {
            c = pf.getEntries();
        }
        for (Persistable p : c) {
            if (p == object)
                continue;
            try {
                if (p.getAttributeValue("Name").contentEquals(name)) {
                    StringBuilder sb = new StringBuilder();
                    if (p.isDeleted()) {
                         sb.append("A deleted instance of ");
                    } else {
                        sb.append("An instance of ");
                    }
                    sb.append(display);
                    sb.append(" already exists in this project with name '");
                    sb.append(name);
                    sb.append("' - either use a different name, or delete/purge the existing one.");
                    return sb.toString();
                }
            }
            catch (Exception e) {}
        }
        return null;
    }
    
    public static final MetaFactory getInstance()
            throws Exception
    {
        if (bootException != null)
            throw new Exception("MetaFactory instantiation failed", bootException);
        return instance;
    }

    public ValueSet getValueSet(String v) { return valueSets.get(v); }
    
    public Database getDatabase() { return database; }
    public void setUIProject(uk.nhs.digital.projectuiframework.Project p) { uiproject = p; }
    public uk.nhs.digital.projectuiframework.Project getUIProject() { return uiproject; }
    
    public ArrayList<Persistable> getChildren(String type, String attribute, int id)
    {
        ArrayList<Persistable> results = null;
        PersistableFactory pf = factories.get(type);
        if (pf == null)
            return null;
        Collection c = pf.getEntries();
        for (Object o : c) {
            Persistable p = (Persistable)o;
            try {
                Attribute a = p.getAttribute(attribute);
                if (a == null)
                    return null;
                if (a.getType() == Attribute.INTEGER) {
                    if (Integer.parseInt(a.toString()) == id) {
                        if (results == null)
                            results = new ArrayList<>();
                        results.add(p);
                    }
                }
            }
            catch (NumberFormatException e) {
                SmartProject.getProject().log("Error getting children: " + attribute + " from " + type + " for object " + id, e);
                // Requested parent id attribute doesn't exist or not an id (integer)
                // type - we'll find this out the first time we try, so just bug out.
                return null;
            }
        }
        return results;
    }
    
    public Set<String> getFactories() { return factories.keySet(); }
    
    public PersistableFactory getFactory(String type)
            throws Exception
    {
        if (!factories.containsKey(type))
            throw new Exception("No such type");
        return factories.get(type);
    }
    
    public void initialise()
            throws Exception
    {
        if (database == null)
            throw new Exception("Database initialisation failed");
        database.loadAllowedRelationships();
        for (PersistableFactory p : factories.values()) {
            p.loadAll();
        }        
    }
    
    public void initialise(int projectid)
            throws Exception
    {
        if (database == null)
            throw new Exception("Database initialisation failed");
        database.loadAllowedRelationships();
        for (PersistableFactory p : factories.values()) {
            p.loadAll(projectid);
        }
    }
    
    public void shutdown()
            throws Exception
    {
        database.shutdown(false);
        factories.clear();
    }
}
