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

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author murff
 */
public class LibraryObject {
    
    private HashMap<String,LibraryAttribute> attributes = new HashMap<>();
    private int id = -1;
    private String type = null;
    private String name = null;
    private String description = null;
    private String source = null;
    private String comment = null;
    private String addedDate = null;
    
    public LibraryObject(String t) {
        type = t;
    }
    
    LibraryObject(int i, String t) {
        type = t;
        id = i;
    }
    void setId(int i) { id = i; }
    public int getId() { return id; }
    
    public String getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSource() { return source; }
    public String getComment() { return comment; }
    public String getAddedDate() { return addedDate; }
    
    public void setName(String n) { name = n; }
    public void setDescription(String n) { description = n; }
    public void setSource(String n) { source = n; }
    public void setComment(String n) { comment = n; }
    public void setAddedDate(String n) { addedDate = n; }
    
    
    public void addAttribute(String n, LibraryAttribute v) {
        attributes.put(n, v);
    }
    
    public Attribute getAttribute(String n) { return attributes.get(n); }
    public Collection<LibraryAttribute> getAttributes() { return attributes.values(); }
    
    public void save()
            throws Exception
    {
        MetaFactory.getInstance().getDatabase().save(this);
    }
} 
