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
package uk.nhs.digital.safetycase.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import uk.nhs.digital.projectuiframework.smart.SmartProject;
import uk.nhs.digital.safetycase.data.Persistable;

/**
 *
 * @author damian
 */
public class LinkExplorer extends javax.swing.JDialog {

    /**
     * Creates new form LinkExplorer
     */
    public LinkExplorer(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        projectTree.setModel(SmartProject.getProject().getTreeModel());
        projectTree.setPreferredSize(null);
        setTitle("Link explorer");
        projectTree.setCellRenderer(SmartProject.getProject().getProjectTreeCellRenderer());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectTree = new javax.swing.JTree();
        linksTabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        projectTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                projectTreeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(projectTree);

        jSplitPane1.setLeftComponent(jScrollPane1);
        jSplitPane1.setRightComponent(linksTabbedPane);

        getContentPane().add(jSplitPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void projectTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_projectTreeMouseClicked

        // Ignore if the user has not selected a Persistable. Otherwise make an ObjectLinkReporter
        // passing the Persistable to it, and have that ObjectLinkReporter appear in the tabbed
        // control with the tab title being the name of the Persistable
        
        TreePath tp = projectTree.getPathForLocation(evt.getX(), evt.getY());    
        if (tp == null)
           return;
        try {
            Object o = tp.getLastPathComponent();
            DefaultMutableTreeNode d = (DefaultMutableTreeNode)o;
            Object u = d.getUserObject();
            if (u instanceof uk.nhs.digital.safetycase.data.Persistable) {
                Persistable p = (Persistable)u;
                ObjectLinkReporter olr = new ObjectLinkReporter(p);
                linksTabbedPane.add(p.getTitle(), olr);
            }
        }
        catch (Exception e) {}
    }//GEN-LAST:event_projectTreeMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LinkExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LinkExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LinkExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LinkExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LinkExplorer dialog = new LinkExplorer(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane linksTabbedPane;
    private javax.swing.JTree projectTree;
    // End of variables declaration//GEN-END:variables
}
