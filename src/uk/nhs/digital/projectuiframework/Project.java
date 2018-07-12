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
 *
 * @author damian
 */
public interface Project {

    public static final int ADD = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    
    public static final int SAVE = 10;
    
    EditorComponent getEditorComponent(TreePath t);
    ViewComponent getViewComponent(TreePath t);
    String checkNewFromPopupMenu(TreePath t);
    boolean checkShowPopup(TreePath t);

    void setProjectWindow(ProjectWindow pw);
    ProjectWindow getProjectWindow();
    ProjectTreeCellRenderer getProjectTreeCellRenderer();

    String getApplicationIdentifier();
    String getName();
    void setName(String n);
    
    ImageIcon getIcon(String s);
    ImageIcon getIcon(Object o);
    ImageIcon getHelpAboutIcon();
    
    DefaultMutableTreeNode getProjectRoot();
    DefaultMutableTreeNode getTreeNode(Object o) throws Exception;
    DefaultTreeModel getTreeModel();

    void initialise() throws Exception;
    void reInitialiseProjectView() throws Exception;

    void load(File f) throws Exception;
    void load() throws Exception;
    String getFileName();
    void save(File f) throws Exception;
    void save() throws Exception;
    
    void saveUserProperties();
    
    boolean hasChanged();
    
    int getProjectID(DefaultMutableTreeNode n);
    int getCurrentProjectID();
    void setCurrentProjectID(int id);
    
    void editorEvent(int e, Object o);
    JPanel getExistingEditor(Object o, Object c);
    void addNotificationSubscriber(DataNotificationSubscriber n);
    void removeNotificationSubscriber(DataNotificationSubscriber n);
    void saveAll();
    
    void shutdown();
    
    boolean doDuplicateObjectCheck(String type);
    
    void log(String message, Throwable thrown);
    void log(Level level, String message, Throwable thrown);
}
