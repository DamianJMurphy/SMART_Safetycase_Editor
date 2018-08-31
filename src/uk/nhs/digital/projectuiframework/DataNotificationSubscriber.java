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
package uk.nhs.digital.projectuiframework;

import javax.swing.JPanel;
import uk.nhs.digital.projectuiframework.ui.SaveRejectedException;

/**
 * Implemented by "subscriber" editor UIs in the notification and editor control system.
 * The system is a list of subscribers maintained by an implementation of the Project
 * interface. Subscribers register via the Project.addNotificationSubscriber() method
 * and inform of changes via Project.editorEvent(). The notification system can then
 * inform subscribers by calling their notification() method, to do things like 
 * display updates.
 * 
 * @author damian
 */
public interface DataNotificationSubscriber {
    
    /**
     * Called by the notification system to inform of some sort of event. 
     * 
     * @param evtype One of Project.ADD, Project.UPDATE, Project.DELETE or Project.SAVE
     * @param o The object that was updated.
     * @return true if this object is to be removed from the notification list following a call to the method, false otherwise - should usually be false
     * @throws SaveRejectedException If a Project.SAVE notification resulted in a save being rejected due to some internal data quality or other check
     */
    boolean notification(int evtype, Object o) throws SaveRejectedException;
    
    /**
     * Get a reference to the editor for the given object, if there is one.
     * @param o The object being queried
     * @return The editor UI JPanel, or null.
     */
    JPanel getEditor(Object o);
    
    /**
     * Inform the Project that this object should be removed from the notifications list.
     * |For example, when it has been closed by the user.
     */
    void unsubscribe();
    
    /**
     * Check on whether anything in this editor has been modified.
     * @return 
     */
    boolean isModified();
    
}
