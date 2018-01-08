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
import java.util.Iterator;

/**
 *
 * @author damian
 */
public class ValueSet 
{
    private String name = null;
    private String dbObject = null;
    private String valueField = null;
    private ArrayList<ValueSetItem> items = new ArrayList<>();

    ValueSet(String n, String d, String f) {
        name = n;
        dbObject = d;
        valueField = f;
    }
    
    void add(String v, String a, String d) {
        items.add(new ValueSetItem(v,a,d));
    }
    public String getName() { return name; }
    String getDbObjectName() { return dbObject; }
    String getValueFieldName() { return valueField; }
    
    public boolean contains(String n) { 
        if (n == null)
            return false;
        for (ValueSetItem v : items) {
            if (v.getValue().contentEquals(n)) {
                return true;
            }                
        }
        return false;
    }
    
    public Iterator<String> iterator() {
        ArrayList<String> a = new ArrayList<>();
        for (ValueSetItem v : items) {
            if (!v.isDeprecated()) {
                a.add(v.getValue());
            }
        }
        return a.iterator();
    }
}
