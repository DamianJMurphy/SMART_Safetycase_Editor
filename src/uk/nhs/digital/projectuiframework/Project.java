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
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import uk.nhs.digital.projectuiframework.smart.ProjectTreeCellRenderer;
import uk.nhs.digital.projectuiframework.ui.EditorComponent;
import uk.nhs.digital.projectuiframework.ui.ProjectWindow;
import uk.nhs.digital.projectuiframework.ui.ViewComponent;

/**
 * Interface implemented by application projects (I suspect we should have called this something else
 * as there is a "Project" in the data, too).
 * @author damian
 */
public interface Project {

    /**
     * Notification type to signal an object has been added
     */
    public static final int ADD = 1;
    /**
     * Notification type to signal an object has been updated
     */
    public static final int UPDATE = 2;
    /**
     * Notification type to signal an object has been deleted
     */
    public static final int DELETE = 3;

    /**
     * Notification type to signal an object has been saved
     */    
    public static final int SAVE = 10;
    
    /**
     * Used internally by the "project tree" view to resolve an editor from a selected TreePath
     * @param t User-selected path
     * @return Editor, or null if none found.
     */
    EditorComponent getEditorComponent(TreePath t);
    
    /**
     * Used internally by the "project tree" view to resolve a custom view from a selected TreePath
     * @param t User-selected path
     * @return Viewer, or null if none found.
     */    
    ViewComponent getViewComponent(TreePath t);
    
    /**
     * Should the user selecting the object which terminates the selected tree path, be offered
     * a "New..." option in the right-click pop-up menu.
     * @param t
     * @return 
     */
    String checkNewFromPopupMenu(TreePath t);
    
    /**
     * When the user right-clicks the project tree, should the user be shown a pop-up menu
     * for the selected path.
     * @param t
     * @return 
     */
    boolean checkShowPopup(TreePath t);

    /**
     * Binds the top-level project window to the project.
     * @param pw 
     */
    void setProjectWindow(ProjectWindow pw);
    
    /**
     * Get a reference to the top-level project window.
     * @return 
     */
    ProjectWindow getProjectWindow();
    
    /**
     * The project tree view supports using its own renderer for things like icons, and to
     * handle font-size changes for visual impairment or small displays with high pixel resolution.
     * If one has been set, this returns it.
     * @return 
     */
    ProjectTreeCellRenderer getProjectTreeCellRenderer();

    /**
     * Returns a string for the main window title bar
     * @return 
     */
    String getApplicationIdentifier();
    
    /**
     * Gets a short name for the project type.
     * @return 
     */
    String getName();
    
    /**
     * Sets a short name for the project type.
     * @param n 
     */
    void setName(String n);
    
    /**
     * Gets an icon for the tree view, based on a name.
     * @param s
     * @return 
     */
    ImageIcon getIcon(String s);
    
    /**
     * Gets an icon for the tree view, based on a project object.
     * @param o
     * @return 
     */
    ImageIcon getIcon(Object o);
    
    /**
     * Gets an icon for use in a "Help/About" popup.
     * @return 
     */
    ImageIcon getHelpAboutIcon();
    
    /**
     * Gets the root of the prpoject tree view.
     * @return 
     */
    DefaultMutableTreeNode getProjectRoot();
    
    /**
     * Makes a tree node for the given object. This provides the implementation with flexibility regarding
     * how the application displays its project structure.
     * @param o The project object
     * @return The tree node and any children the implementation considers needed, or null if null was passed in
     * @throws Exception 
     */
    DefaultMutableTreeNode getTreeNode(Object o) throws Exception;
    
    /**
     * Makes a DefaultTreeModel for the project objects.
     * @return 
     */
    DefaultTreeModel getTreeModel();

    /**
     * Called at application start-up to do any initialisation needed.
     * @throws Exception 
     */
    void initialise() throws Exception;
    
    /**
     * Run-time re-initialisation.
     * @throws Exception 
     */
    void reInitialiseProjectView() throws Exception;

    /**
     * For file-based projects
     * @param f
     * @throws Exception 
     */
    void load(File f) throws Exception;

    /**
     * For file-based projects
     * @param f
     * @throws Exception 
     */
    void load() throws Exception;

    /**
     * For file-based projects
     * @param f
     * @throws Exception 
     */
    String getFileName();

    /**
     * For file-based projects
     * @param f
     * @throws Exception 
     */
    void save(File f) throws Exception;

    /**
     * For file-based projects
     * @param f
     * @throws Exception 
     */
    void save() throws Exception;
    
    /**
     * Allows for a call to save user-selectable properties - for example to a default
     * location to support no-argument invocation.
     */
    void saveUserProperties();
    
    /**
     * Has the project data changed.
     * @return 
     */
    boolean hasChanged();
    
    /**
     * This assumes that project data is arranged with a "project object" that contains a related
     * set of information, and that the tree view may show multiple "project objects" and their children.
     * This allows for the identification of which "project object" is the parent for a selected node
     * in the tree.
     * @param n
     * @return 
     */
    int getProjectID(DefaultMutableTreeNode n);
    
    /**
     * Support for the notion of a "current project".
     * @return 
     */
    int getCurrentProjectID();
    /**
     * Support for the notion of a "current project".
     * @return 
     */
    void setCurrentProjectID(int id);

    /**
     * Part of the notification and control system. Used by editors to notify the application
     * of an event on a particular project data object.
     * @param e Event type. ADD, UPDATE, DELETE or SAVE
     * @param o The object.
     */
    void editorEvent(int e, Object o);
    
    /**
     * If there is an editor already for this object, return it.
     * @param o The object we're asking about
     * @param c The caller
     * @return Existing editor panel if there is one.
     */
    JPanel getExistingEditor(Object o, Object c);
    
    /**
     * Used by a DataNotificationSubscriber to register itself with the controller.
     * @param n 
     */
    void addNotificationSubscriber(DataNotificationSubscriber n);
    
    /**
     * Usedto de-register a DataNotificationSubscriber.
     * @param n 
     */    
    void removeNotificationSubscriber(DataNotificationSubscriber n);
    
    /**
     * Request intended to signal all notification subscribers to save their data.
     */
    void saveAll();
    
    /**
     * Instruct the project implementation to do any shutdown operations: closing connections and so on.
     */
    void shutdown();
    
    /**
     * Does the project data type allow duplicates ?
     * @param type
     * @return 
     */
    boolean doDuplicateObjectCheck(String type);
    
    void log(String message, Throwable thrown);
    void log(Level level, String message, Throwable thrown);
}
