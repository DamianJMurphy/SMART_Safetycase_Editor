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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;
import uk.nhs.digital.projectuiframework.ui.resources.ResourceUtils;

/**
 *
 * @author murff
 */
public class UndockTabComponent extends JPanel {
    
    private final JTabbedPane pane;
    
    private final static String UNDOCK_ICON = "/uk/nhs/digital/projectuiframework/ui/resources/undockpanelbutton16x16.png";
    private final static String CLOSE_ICON = "/uk/nhs/digital/projectuiframework/ui/resources/closepanelbutton16x16.png";
    
   @SuppressWarnings("OverridableMethodCallInConstructor")
    public UndockTabComponent(final JTabbedPane pane) 
    {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        setOpaque(false);
        
        //make JLabel read titles from JTabbedPane
        JLabel label = new JLabel() {
            @Override
            public String getText() {
                int i = pane.indexOfTabComponent(UndockTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };
        
        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        try {
        JButton undockbutton = new UndockTabComponent.UndockButton();
        add(undockbutton);

        JButton closebutton = new UndockTabComponent.CloseButton();
        add(closebutton);
        }
        catch (Exception e) {
            throw new Error("JARfile corrupted - internal resources missing or unreadable");
        }
            
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
    
    private class CloseButton extends JButton implements ActionListener {
        @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
        public CloseButton() 
                throws Exception
        {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            this.setIcon(ResourceUtils.getImageIcon(CLOSE_ICON));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(closeButtonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(UndockTabComponent.this);
            if (i != -1) {
                Component c = pane.getComponentAt(i);
                String t = pane.getTitleAt(i);
                pane.remove(i);
            }
        }        
    }

    private final static MouseListener closeButtonMouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
    private class UndockButton extends JButton implements ActionListener {
        @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
        public UndockButton()
                throws Exception
        {
            int size = 17;
            
            setPreferredSize(new Dimension(size, size));
            this.setIcon(ResourceUtils.getImageIcon(UNDOCK_ICON));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(undockButtonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        @Override
        @SuppressWarnings("ResultOfObjectAllocationIgnored")
        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(UndockTabComponent.this);
            if (i != -1) {
                ExternalEditorView.start(pane.getComponentAt(i), pane.getTitleAt(i), pane);
                try {
                    pane.remove(i);
                }
                catch (IndexOutOfBoundsException eiob) {}
            }
        }

    }

    private final static MouseListener undockButtonMouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
    
}