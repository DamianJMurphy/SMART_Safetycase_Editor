/*
 * 
 *   Copyright 2018  NHS Digital
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
package uk.nhs.digital.projectuiframework.ui;

import java.awt.Component;
import uk.nhs.digital.projectuiframework.Project;

/**
 *
 * @author damian
 */
public class ViewComponent {
    private Component viewerPanel = null;
    private String title = null;
    private String className = null;
    private Project project = null;
    
    public static final int UNDEFINED = 0;
    public static final int CLOSE = 1;
    public static final int DOCKED = 2;
    public static final int UNDOCKED = 3;
    
    public ViewComponent(String t, String c) {
        title = t;
        className = c;
    }
    public String getClassName() { return className; }
    public void setComponent(Component c) { viewerPanel = c; }
    public void setProject(Project p) { project = p; }
    public Project getProject() { return project; }
    public Component getComponent() { return viewerPanel; }
    public String getTitle() { return title; }
    
    @Override
    public String toString() { return getTitle(); }    
}
