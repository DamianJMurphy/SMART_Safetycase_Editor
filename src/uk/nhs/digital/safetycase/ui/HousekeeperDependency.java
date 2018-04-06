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

import java.awt.Color;
import uk.nhs.digital.safetycase.data.Persistable;

/**
 *
 * @author damian
 */
public class HousekeeperDependency 
{
    public static final int TO = -1;
    public static final int FROM  = 1;
    private static final int UNSET = 0;
    
    
    private String displayText = null;
    private boolean isRelationship = false;
    private Persistable linkedObject = null;
    private int toOrFrom = UNSET;
    private Color colour = null;
    
    private boolean dependencyIsDeleted = false;
    
    public HousekeeperDependency(String d, boolean r, Persistable l, int t) {
        displayText = d;
        isRelationship = r;
        linkedObject = l;
        toOrFrom = t;
        
        if ((l != null) && l.isDeleted()) {
            colour = Color.GREEN;
        } else {
            if (l != null) {
                colour = Color.RED;
                if (t == FROM) {
                    colour = Color.BLUE;
                }
            }
        }            
    }
    
    public Color getColour() { return (colour != null) ? colour : Color.BLACK; }
    public void setColour(Color c) { colour = c; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isRelationship) {
            sb.append("Linked ");
            switch (toOrFrom) {
                case TO:
                    sb.append(" to ");
                    break;
                case FROM:
                    sb.append(" from ");
                    break;
                default:
                    sb.append(" (automatic) ");
                    break;
            }
            sb.append(linkedObject.getDisplayName());
            sb.append(":");
            sb.append(linkedObject.getAttributeValue("Name"));
            if (displayText != null) {
                sb.append(" (");
                sb.append(displayText);
                sb.append(")");
            }
        } else {
            sb.append(displayText);
            sb.append(" (");
            sb.append(linkedObject.getDisplayName());
            sb.append(":");
            sb.append(linkedObject.getAttributeValue("Name"));
            sb.append(") ");            
        }
        return sb.toString();
    }
    
}
