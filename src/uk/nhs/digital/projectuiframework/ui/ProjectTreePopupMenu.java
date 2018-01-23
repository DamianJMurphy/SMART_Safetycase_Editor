/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.projectuiframework.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

/**
 *
 * @author damian
 */
public class ProjectTreePopupMenu 
        extends JPopupMenu
        implements ActionListener
{
    private JMenuItem newMenuItem = null;
    private ProjectWindow projectWindow = null;
    private JTree projectTree = null;
    private int showX = -1;
    private int showY = -1;
    
    public ProjectTreePopupMenu(ProjectWindow p) {
        super();
        projectWindow = p;
        newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(this);
        newMenuItem.setActionCommand("new");
        add(newMenuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("new")) 
            projectWindow.newObjectRequested(projectTree.getPathForLocation(showX, showY));
    }
    
    @Override
    public void show(Component invoker, int x, int y) {
        if (invoker instanceof javax.swing.JTree) {
            showX = x;
            showY = y;
            projectTree = (JTree)invoker;
            super.show(invoker, x, y);
        }
    }
}
