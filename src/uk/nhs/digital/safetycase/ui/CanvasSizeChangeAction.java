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

import com.mxgraph.swing.mxGraphComponent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Action;

/**
 *
 * @author damian
 */
public abstract class CanvasSizeChangeAction 
        implements Action
{
    private boolean enabled = true;
    private ArrayList<PropertyChangeListener> listeners = null;
    private HashMap<String,Object> values = null;

    
    protected void grow(mxGraphComponent c) {
        PageFormat format = c.getPageFormat();
        Paper p = format.getPaper();
        p.setSize(p.getWidth() * 2, p.getHeight() * 2);
        format.setPaper(p);
        c.setPageFormat(format);
        c.refresh();
    }
    
    protected void shrink(mxGraphComponent c) { 
        PageFormat format = c.getPageFormat();
        Paper p = format.getPaper();
        p.setSize(p.getWidth() / 2, p.getHeight() / 2);
        format.setPaper(p);
        c.setPageFormat(format);
        c.refresh();
    }

    @Override
    public Object getValue(String key) {
        if (values == null)
            return null;
        return values.get(key);
    }    
    
    @Override
    public void putValue(String key, Object value) {
        if (values == null)
            values = new HashMap<>();
        values.put(key, value);
    }

    @Override
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null)
            listeners = new ArrayList<>();
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null)
            return;
        listeners.remove(listener);
    }
    
    
}
