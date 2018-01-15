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
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author damian
 */
public abstract class Persistable {
   
    protected int identifier = -1;
    protected String dbObjectName = null;
    protected String editorType = null;
    protected HashMap<String,Attribute> writableAttributes = new HashMap<>();
    protected HashMap<String,Attribute> readOnlyAttributes = new HashMap<>();
    protected ArrayList<String> readOnlyAttributeList = null;
    protected ArrayList<String> writableAttributeList = null;
    protected HashMap<String, ArrayList<Relationship>> relationships = new HashMap<>();
    protected static final String[] TRACKINGFIELDS = {"CreatedDate", "LastUpdatedDate", "DeletedDate"};
    protected boolean changed = false;
    protected boolean referenceData = false;
    protected boolean deleted = false;
    protected String orderby = null;
    
    public int getId() { return identifier; }
    void setId(int i) { identifier = i; }
    void setDeleted() { deleted = true; }
    void clearChange() { changed = false; }
    String getOrderBy() { return orderby; }
    
    public boolean isReferenceData() { return referenceData; }
    public boolean isDeleted() { return deleted; }
    public String[] getTrackingFields() { return TRACKINGFIELDS; }
    public abstract String[] getFields();
    
    public abstract String getTitle();
    
//    abstract Persistable save() throws Exception;
//    abstract Persistable load(int i) throws Exception;
    
    public String getDatabaseObjectName() { return dbObjectName; }
    public String getEditorType() 
    { 
        return (editorType == null) ? dbObjectName : editorType; 
    }
    
    public HashMap<String, ArrayList<Relationship>> getRelationshipsForLoad() { return relationships; }
    
    @Override
    public String toString() { return getTitle(); }
    
    public void addRelationship(Relationship r) {
        String t = r.getTargetType();
        r.setSourceType(dbObjectName);
        if (relationships.containsKey(t)) {
            relationships.get(t).add(r);
        } else {
            ArrayList<Relationship> a = new ArrayList<>();
            a.add(r);
            relationships.put(t, a);
        }
        changed = true;
    }

    public ArrayList deleteAutomaticRelationships() {
        ArrayList<Relationship> deleted = new ArrayList<>();
        for (ArrayList<Relationship> a : relationships.values()) {
            for (Relationship r : a) {
                if (r.getManagementClass() != null) {
                    r.setDeleted();
                    changed = true;
                    deleted.add(r);
                }
            }
        }
        return deleted;
    }
    
    public void purgeAutomaticRelationships(ArrayList<Relationship> deleted) 
    {
        for (Relationship r : deleted) {
            relationships.get(r.getTargetType()).remove(r);
        }        
    }
    
    public void deleteRelationship(Relationship r) {
        String t = r.getTargetType();
        if (!relationships.containsKey(t))
            return;
        ArrayList<Relationship> rels = relationships.get(t);
        for (Relationship  x : rels) {
            if (r.getTargetType().contentEquals(x.getTargetType()) && (r.getTarget() == x.getTarget())) {
                x.setDeleted();
                break;
            }
        }
        changed = true;
    }
    
    public ArrayList<Relationship> getRelationships(String target) {
        return relationships.get(target);
    }

    public ArrayList<Relationship> getRelationshipsForClass(String m) {
        ArrayList<Relationship> rels = new ArrayList<>();
        for (ArrayList<Relationship> a : relationships.values()) {
            for (Relationship r : a) {
                if (r.getManagementClass().contentEquals(m))
                    rels.add(r);
            }
        }
        return rels;
    }

    
    public ArrayList<String> getReadOnlyAttributeNames() { 
        if (readOnlyAttributeList == null) {
            readOnlyAttributeList = new ArrayList<>();
            readOnlyAttributeList.addAll(Arrays.asList(TRACKINGFIELDS));
        }
        return readOnlyAttributeList;     
    }
    
    public ArrayList<String> getWritableAttributeNames() 
    {
        if (writableAttributeList == null) {
            writableAttributeList = new ArrayList<>();
            for (String s : writableAttributes.keySet()) {
                writableAttributeList.add(s);
            }
        }
        return writableAttributeList; 
    }
        
    public Attribute getAttribute(String n) 
    { 
        if (writableAttributes.containsKey(n))
            return writableAttributes.get(n);
        return readOnlyAttributes.get(n); 
    }

    public String getAttributeValue(String n) 
    { 
        if (writableAttributes.containsKey(n))
            return writableAttributes.get(n).toString();
        if (!readOnlyAttributes.containsKey(n))
            return null;
        return readOnlyAttributes.get(n).toString(); 
    }
    
    
    void setReadOnlyAttribute(String n, String v)
            throws IllegalArgumentException
    {
        if (!readOnlyAttributes.containsKey(n))
            throw new IllegalArgumentException("Key " + n + " not an attribute of " + dbObjectName);
        readOnlyAttributes.put(n, new Attribute(v));
    }
    
    public void setAttribute(String n, Attribute a)
            throws IllegalArgumentException
    {
        if (!writableAttributes.containsKey(n))
            throw new IllegalArgumentException("Key " + n + " not an attribute of " + dbObjectName);
        writableAttributes.put(n, a);
        changed = true;
    }
    
    public void setAttribute(String n, String v)
            throws IllegalArgumentException
    {
        if (!writableAttributes.containsKey(n))
            throw new IllegalArgumentException("Key " + n + " not an attribute of " + dbObjectName);
        writableAttributes.put(n, new Attribute(v));
        changed = true;
    }

    public void setAttribute(String n, int v)
            throws IllegalArgumentException
    {
        if (!writableAttributes.containsKey(n))
            throw new IllegalArgumentException("Key " + n + " not an attribute of " + dbObjectName);
        writableAttributes.put(n, new Attribute(v));
        changed = true;
    }
    
    public boolean needsSaving() { return changed; }
}
