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
package uk.nhs.digital.safetycase.data;

/**
 *
 * @author damian
 */
public class Attribute {
    
    public static final int EMPTY = 0;
    public static final int INTEGER = 1;
    public static final int STRING = 2;
    
    protected boolean empty = true;
    protected boolean isDate = false;
    
    protected String stringValue = null;
    protected Integer intValue = null;
    
    public Attribute() {}
    public Attribute(String s) { 
        empty = false;
        stringValue = s; 
    }
    public Attribute(Integer i) { 
        empty = false;
        intValue = i; 
    }
    
    public boolean getIsDate() { return isDate; }
    public void setIsDate(boolean b) { isDate = b; }
    
    @Override
    public String toString() {
        if (empty) 
            return "";
        if (stringValue != null)
            return stringValue;
        if (intValue != null)
            return intValue.toString();
        return "";
    }
    
    public int getIntValue() { 
        if (empty || (intValue == null))
            return -1;
        return intValue.intValue();
    }
    
    public int getType() {
        if (empty)
            return EMPTY;
        return (stringValue == null) ? INTEGER : STRING;
    }
}
