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
package uk.nhs.digital.projectuiframework;
import java.io.File;
/**
 * Some static helper functions for instantiating Project implementations based on a property.
 * Set System property referenced by PROJECTCLASSPROPERTY to the class name of the implementation.
 * @author damian
 */
public abstract class ProjectHelper implements Project {
    
    public static final String PROJECTCLASSPROPERTY = "uk.nhs.digital.projectuiframework.projectclass";
    
    /**
     * Instantiate a project with System properties only.
     * @return
     * @throws Exception 
     */
    public static Project createProject() 
            throws Exception
    { 
        Project p = makeProject();
        p.initialise();
        return p; 
    }

    /**
     * Instantiate a project and load the given properties file.
     * @return
     * @throws Exception 
     */    
    public static Project createProject(File f) 
            throws Exception
    { 
        Project p = makeProject();
        p.initialise();
        p.load(f);
        return p; 
    }

    private static Project makeProject()
            throws Exception
    {
        String s = System.getProperty(PROJECTCLASSPROPERTY);
        if ((s == null) || (s.trim().length() == 0))
            throw new Exception("System property " + PROJECTCLASSPROPERTY + " not defined");
        
        return (Project)(Class.forName(s).newInstance());
    }
    
}
