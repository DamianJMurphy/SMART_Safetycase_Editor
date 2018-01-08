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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author damian
 */
public class DataQualityCheckRun 
{
    
    private ArrayList<DataQualityCheckIssue> issues = new ArrayList<>();
    private Date date = null;
    private int runId = -1;
    private int projectId = -1;
    private String runBy = null;
    private int supercededBy = -1;
    private int checkId = -1;
    
    private boolean changed = false;
    
    public DataQualityCheckRun() {
        changed = true;
    }
    
    DataQualityCheckRun(int id) {
        checkId = id;        
    }

    void clearChanged() { changed = false; }
    public boolean needsSaving() { return changed; }
    
    public void addIssue(DataQualityCheckIssue d) { issues.add(d); }
    public Iterator<DataQualityCheckIssue> listIssues() { return issues.iterator(); }
    
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
        changed = true;
    }

    public void setDate() {
        date = new Date();
        changed = true;
    }
    
    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }

    /**
     * @param runId the runId to set
     */
    void setRunId(int runId) {
        this.runId = runId;
    }

    /**
     * @return the projectId
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
        changed = true;
    }

    /**
     * @return the runBy
     */
    public String getRunBy() {
        return runBy;
    }

    /**
     * @param runBy the runBy to set
     */
    public void setRunBy(String runBy) {
        this.runBy = runBy;
        changed = true;
    }

    /**
     * @return the supercededBy
     */
    public int getSupercededBy() {
        return supercededBy;
    }

    /**
     * @param supercededBy the supercededBy to set
     */
    public void setSupercededBy(int supercededBy) {
        this.supercededBy = supercededBy;
        changed = true;
    }

    /**
     * @return the checkId
     */
    public int getCheckId() {
        return checkId;
    }

    /**
     * @param checkId the checkId to set
     */
    public void setCheckId(int checkId) {
        this.checkId = checkId;
        changed = true;
    }
    
}
