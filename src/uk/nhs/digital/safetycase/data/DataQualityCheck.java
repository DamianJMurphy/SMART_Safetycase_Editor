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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author damian
 */
public class DataQualityCheck 
{
    static final String READ = "select * from DataQualityCheck";

    private int checkId = -1;
    private String name = null;
    private String description = null;
    private String primaryObjectType = null;
    private String addedDate = null;
    private String deprecatedDate = null;
    private String implementedBy = null;
    private String implementationArgument = null;
    
    private boolean changed = false;
    
    public DataQualityCheck() {
        changed = true;
    }
    
    DataQualityCheck(int i) { checkId = i; }
    void setId(int i) { checkId = i; }
    public int getId() { return checkId; }
    
    public boolean needsSaving() { return changed; }
    void clearChanged() { changed = false; }
    
    public boolean isDeprecated() { return (deprecatedDate != null); }
    public void deprecate() 
    { 
        deprecatedDate = Database.DATEONLY.format(new Date()); 
        changed = true;
    }
    public void undeprecate() 
    { 
        deprecatedDate = null; 
        changed = true;
    }
    String getDeprecatedDate() { return deprecatedDate; }
    public String getAddedDate() { return addedDate; }
    void setAddedOn(String s) { addedDate = s; }
    void setDeprecatedDate(String s) { deprecatedDate = s; }
    public String getName() { return name; }
    public void setName(String s) 
    { 
        name = s; 
        changed = true;
    }
    public String getDescription() { return description; }
    public void setDescription(String s) 
    { 
        description = s; 
        changed = true;
    }
    public String getPrimaryObjectType() { return primaryObjectType; }
    public void setPrimaryObjectType(String s) 
    { 
        primaryObjectType = s; 
        changed = true;
    }

    public String getImplementedBy() { return implementedBy; }
    public void setImplementedBy(String s)
    {
        implementedBy = s;
        changed = true;
    }
    public String getImplementationArgument() { return implementationArgument; }
    public void setImplementationArgument(String s)
    {
        implementationArgument = s;
        changed = true;
    }
}
