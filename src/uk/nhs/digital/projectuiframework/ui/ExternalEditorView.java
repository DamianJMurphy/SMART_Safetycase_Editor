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
package uk.nhs.digital.projectuiframework.ui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import uk.nhs.digital.projectuiframework.ui.resources.ResourceUtils;

/**
 *
 * @author murff
 */
public class ExternalEditorView extends javax.swing.JFrame {

    private JTabbedPane redockTabbedPane = null;
    private Component editorComponent = null;
    private String title = null;
    private ImageIcon icon = null;
    private boolean defaultIcon = false;
    /**
     * Creates new form ExternalEditorView
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ExternalEditorView(java.awt.Component p, String t, final JTabbedPane pane) {
        init(p, t, pane);
        setDefaultIcon();
    }

    private void setDefaultIcon() 
    {
        try {
            String s = System.getProperty("uk.nhs.digial.projectuiframework.appicon");
            if (s != null) {
                icon = ResourceUtils.getImageIcon(s);
                defaultIcon = true;
                this.setIconImage(icon.getImage());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ExternalEditorView(java.awt.Component p, String t, final JTabbedPane pane, ImageIcon editorIcon) {
        init(p, t, pane);
        icon = editorIcon;
        if (icon != null)
            this.setIconImage(icon.getImage());
        else
            setDefaultIcon();
    }    
    
    private void init(java.awt.Component p, String t, final JTabbedPane pane) {
        initComponents();
        editorComponent = p;
        title = t;
        redockTabbedPane = pane;
        dockControlMenuBar.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        setTitle(title);
        setSize(600,500);
        setResizable(true);
        getContentPane().add(editorComponent);
        pack();
        setVisible(true);        
    }
    
    public JTabbedPane getRedockTabbedPane() { return redockTabbedPane; }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dockControlMenuBar = new javax.swing.JMenuBar();
        closeButton = new javax.swing.JMenu();
        dockButton = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/nhs/digital/projectuiframework/ui/resources/closepanelbutton16x16.png"))); // NOI18N
        closeButton.setToolTipText("Close");
        closeButton.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        closeButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        closeButton.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                closeButtonMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        dockControlMenuBar.add(closeButton);

        dockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/nhs/digital/projectuiframework/ui/resources/dockpanelbutton16x16.png"))); // NOI18N
        dockButton.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                dockButtonMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        dockControlMenuBar.add(dockButton);

        setJMenuBar(dockControlMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dockButtonMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_dockButtonMenuSelected
        redockTabbedPane.setSelectedComponent(redockTabbedPane.add(title, editorComponent));
        if ((defaultIcon) || (icon == null))
            redockTabbedPane.setTabComponentAt(redockTabbedPane.getSelectedIndex(), new UndockTabComponent(redockTabbedPane));                    
        else 
            redockTabbedPane.setTabComponentAt(redockTabbedPane.getSelectedIndex(), new UndockTabComponent(redockTabbedPane, icon));                    
        this.dispose(); 
    }//GEN-LAST:event_dockButtonMenuSelected

    private void closeButtonMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_closeButtonMenuSelected
        this.dispose();
    }//GEN-LAST:event_closeButtonMenuSelected

    /**
     * @param p
     * @param t
     * @param pane
     */
    public static void start(java.awt.Component p, String t, final JTabbedPane pane) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ExternalEditorView(p, t, pane).setVisible(true);
        });
    }

    public static void start(java.awt.Component p, String t, final JTabbedPane pane, ImageIcon editorIcon) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ExternalEditorView(p, t, pane, editorIcon).setVisible(true);
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu closeButton;
    private javax.swing.JMenu dockButton;
    private javax.swing.JMenuBar dockControlMenuBar;
    // End of variables declaration//GEN-END:variables
}
