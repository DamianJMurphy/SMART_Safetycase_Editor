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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author damian
 */
public class SmartLogger 
        extends Logger
{

    private static final String LOGGER_NAME = "uk.nhs.digital.projectuiframework.smart.Logger";
    private static final int DEFAULT_LIMIT = 1048576;
    private static final int DEFAULT_FILES = 16;
    private static final String DEFAULT_FILENAME_PATTERN = "%h/SMART_%g.log";
    private static final String PATTERN_PROPERTY = "uk.nhs.digital.projectuiframework.smart.logging.filenamepattern";
    private static final String LIMIT_PROPERTY = "uk.nhs.digital.projectuiframework.smart.logging.filesizelimit";
    private static final String FILECOUNT_PROPERTY = "uk.nhs.digital.projectuiframework.smart.logging.filecount";
    
    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    public SmartLogger() {
        super(LOGGER_NAME, null);
        super.setLevel(Level.ALL);
        addHandler(makeHandler());        
        LogManager.getLogManager().addLogger(this);
        System.out.println("Logging to " + System.getProperty("user.home"));
    }
    
    private Handler makeHandler() {
        
        int count = DEFAULT_FILES;
        int limit = DEFAULT_LIMIT;
        String pattern = DEFAULT_FILENAME_PATTERN;
        
        try {
            String s = System.getProperty(PATTERN_PROPERTY);
            if (s != null)
                pattern = s;
            s = System.getProperty(LIMIT_PROPERTY);
            if (s != null)
                limit = Integer.parseInt(s);
            s = System.getProperty(FILECOUNT_PROPERTY);
            if (s != null)
                count = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {}
        @SuppressWarnings("UnusedAssignment")
        Handler f = null;
        try {
            f = new FileHandler(pattern, limit, count, true);          
        }
        catch (IOException | SecurityException e) {
            System.out.println("Failed to initialise SMART logging to file, using default global handler: " + e.toString());
            f = LogManager.getLogManager().getLogger("global").getHandlers()[0];
        }
        f.setFormatter(new SmartFormatter());
        return f;
    }
}
