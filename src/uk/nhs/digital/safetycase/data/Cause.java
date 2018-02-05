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

/**
 *
 * @author damian
 */
public class Cause 
        extends Persistable
{
    private static final String[] FIELDS = {"ProjectID", "Description", "Name", "GraphCellId", "GroupingType"};
    
    public Cause() 
    {
        dbObjectName = "Cause";
        referenceData = false;
        for (String s: FIELDS) {
            if (s.endsWith("ID") || s.endsWith("Id"))
                writableAttributes.put(s, new Attribute(-1));
            else
                writableAttributes.put(s, new Attribute(""));
        }
        for (String s : TRACKINGFIELDS) {
            readOnlyAttributes.put(s, Database.empty);
        }
        changed = true;
    }        

    @Override
    public String getTitle() {
        String t = writableAttributes.get("Name").toString();
        if ((t == null) || (t.trim().length() == 0))
            return "Not set";
        return t;
    }
    
    @Override
    public String[] getFields() {
        return FIELDS;
    }
    
}
