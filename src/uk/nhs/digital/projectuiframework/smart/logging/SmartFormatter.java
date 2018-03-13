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
package uk.nhs.digital.projectuiframework.smart.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author damian
 */
public class SmartFormatter 
        extends Formatter
{

    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
    private static final String LINEBREAK = System.getProperty("line.separator");
    
    @Override
    public String format(LogRecord record) {
        
        StringBuilder sb = new StringBuilder("LOG ");
        sb.append(record.getLevel().toString());
        sb.append(" ");
        sb.append(DATEFORMAT.format(new Date()));
        sb.append(" ");
        sb.append(record.getMessage());
        sb.append(LINEBREAK);
        
        Throwable t = record.getThrown();
        if (t != null) {
            if (t.getCause() != null) {
                sb.append("Caused by: ");
                sb.append(t.getCause().toString());
                sb.append(LINEBREAK);
            }
            StackTraceElement[] st = t.getStackTrace();
            sb.append("Stack trace:");
            sb.append(LINEBREAK);
            for (StackTraceElement s : st) {
                sb.append(s.toString());
                sb.append(LINEBREAK);
            }        
        }
       
        return sb.toString();
    }
    
}
