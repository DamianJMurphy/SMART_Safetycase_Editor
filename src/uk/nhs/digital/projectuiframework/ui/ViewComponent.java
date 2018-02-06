/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private Project project = null;
    
    public static final int UNDEFINED = 0;
    public static final int CLOSE = 1;
    public static final int DOCKED = 2;
    public static final int UNDOCKED = 3;
    
    public ViewComponent(Component c, String t, Project p) {
        viewerPanel = c;
        title = t;
        project = p;
    }
    
    
}
