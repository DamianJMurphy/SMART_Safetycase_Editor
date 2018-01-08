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
import java.util.Iterator;

/**
 *
 * @author murff
 */
public class Library {
    private static final String[] LIBRARIES = {"System", "Hazard", "Control", "Cause", "Effect", "Process", "ProcessStep"};
    private final HashMap<String,ArrayList<LibraryObject>> libraries = new HashMap<>();
    private final ArrayList<String> libnames = new ArrayList<>();
    
    Library() 
            throws Exception
    {
        libnames.addAll(Arrays.asList(LIBRARIES));
    }
    
    public void putLibraryObject(LibraryObject l) 
            throws Exception
    {
        if (!libraries.containsKey(l.getType())) {
            libraries.put(l.getType(), new ArrayList<>());
        }
        ArrayList<LibraryObject> a = libraries.get(l.getType());
        boolean found = false;
        for (LibraryObject lib : a) {
            if (lib.getName().contentEquals(l.getName())) {
                a.remove(lib);
                a.add(l);
                found = true;
            }
        }
        if (!found)
            a.add(l);
        MetaFactory.getInstance().getDatabase().save(l);
    }
    
    public Iterator<String> getLibraryNames() { return libnames.iterator(); }
    
    public Iterator<LibraryObject> getLibrary(String n) {
        if (!libraries.containsKey(n))
            return null;
        return libraries.get(n).iterator();
    }
    
    public LibraryObject getLibraryObject(String n, int i) {
        if (!libraries.containsKey(n))
            return null;
        ArrayList<LibraryObject> a = libraries.get(n);
        if ((i < 0) || (i >= a.size()))
            return null;
        return a.get(i);
    }
    
}
