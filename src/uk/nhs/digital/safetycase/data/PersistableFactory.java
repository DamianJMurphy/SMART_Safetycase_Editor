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
/**
 *
 * @author damian
 * @param <T>
 */
public class PersistableFactory <T extends Persistable> {
    
    private static final String PACKAGE = "uk.nhs.digital.safetycase.data.";
    
    private final HashMap<Integer,T> instances = new HashMap<>();
    private Database database = null;
    private Persistable dbObjectTemplate = null;
    private String typeName = null;
    
    public PersistableFactory() {}
    
    public void initialise(Database d, String t)
            throws Exception
    {
        database = d;
        typeName = t;
        dbObjectTemplate = ((Persistable)Class.forName(PACKAGE + typeName).newInstance());        
    }
    
    public PersistableFactory(Database d, String t) 
            throws Exception
    {
        initialise(d, t);
    }

    public String getDbObjectType() { return dbObjectTemplate.getDatabaseObjectName(); }
    
    public Collection<T> getEntries() { return instances.values(); }
    
    public Collection<T> getEntries(int projectid)
    {
        Collection<T> c = instances.values();
        ArrayList<T>o = new ArrayList<>();
        int id = -1;
        for (T e : c) {
            try {
                id = Integer.parseInt(e.getAttributeValue("ProjectID"));
                if (id == projectid)
                    o.add(e);
            }
            catch (Exception x) {
                return null;
            }
        }
        return o;
    }
    
    @SuppressWarnings("unchecked")    
    public int loadAll()
            throws Exception
    {
        ArrayList<Persistable> list = database.loadAll(dbObjectTemplate);
        for (Persistable p : list) {
            instances.put(p.getId(), (T)p);
        }
        return list.size();        
    }
    
    @SuppressWarnings("unchecked")    
    public int loadAll(int projectId)
            throws Exception
    {
        if (!dbObjectTemplate.isReferenceData()) {
            return loadAll();
        }
        ArrayList<Persistable> list = database.loadAll(dbObjectTemplate, projectId);
        for (Persistable p : list) {
            instances.put(p.getId(), (T)p);
        }
        return list.size();
    }
    
    @SuppressWarnings("unchecked")
    public T put(T thing)
            throws Exception
    {
        if (!thing.needsSaving())
            return thing;
        T savedThing = (T)database.save(thing);
        savedThing.clearChange();
        instances.put(savedThing.getId(), savedThing);
        return savedThing;
    }
    
    public T refresh(int id)
            throws Exception
    {
        if (instances.containsKey(id))
            instances.remove(id);
        return loadFromDatabase(id);
    }
    
    public T get(int id)
            throws Exception
    {
        if (instances.containsKey(id))
            return instances.get(id);
        
        return loadFromDatabase(id);
    }
    
    public void delete(T thing)
            throws Exception
    {
        // Mark deleted if this has an id, otherwise just drop it.
        //
        if (thing.getId() == -1) 
            return;        
        database.delete(thing);
    }
    
    @SuppressWarnings("unchecked")
    private T loadFromDatabase(int id)
            throws Exception
    {
        T thing = (T) database.load(dbObjectTemplate, id);
        instances.put(id, thing);
        return thing;
    }
}
