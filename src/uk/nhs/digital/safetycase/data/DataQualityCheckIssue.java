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

import java.util.Date;

/**
 *
 * @author damian
 */
public class DataQualityCheckIssue 
{

    private int issueId = -1;
    private int runId = -1;
    private String resolutionType = "Not resolved";
    private String issue = null;
    private String issueLocation = null;
    private String resolution = null;
    private Date resolvedOn = null;
    private String resolvedBy = null;
    
    private boolean changed = false;
    
    public DataQualityCheckIssue(DataQualityCheckRun d) {
        runId = d.getRunId();
    }
    
    void clearChanged() { changed = false; }
    public boolean needsSaving() { return changed; }
    
    void setIssueId(int i) { issueId = i; }
    
    /**
     * @return the issueId
     */
    public int getIssueId() {
        return issueId;
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
    public void setRunId(int runId) {
        this.runId = runId;
        changed = true;
    }

    /**
     * @return the resolutionType
     */
    public String getResolutionType() {
        return resolutionType;
    }

    /**
     * @param resolutionType the resolutionType to set
     */
    public void setResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
        changed = true;
    }

    /**
     * @return the issue
     */
    public String getIssue() {
        return issue;
    }

    /**
     * @param issue the issue to set
     */
    public void setIssue(String issue) {
        this.issue = issue;
        changed = true;
    }

    /**
     * @return the name of the resolver
     */
    public String getResolvedBy() {
        return resolvedBy;
    }

    /**
     * @param r the name of the resolver to set
     */
    public void setResolvedBy(String r) {
        this.resolvedBy = r;
        changed = true;
    }
    
    /**
     * @return the issueLocation
     */
    public String getIssueLocation() {
        return issueLocation;
    }

    /**
     * @param issueLocation the issueLocation to set
     */
    public void setIssueLocation(String issueLocation) {
        this.issueLocation = issueLocation;
        changed = true;
    }

    /**
     * @return the resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * @param resolution the resolution to set
     */
    public void setResolution(String resolution) {
        this.resolution = resolution;
        changed = true;
    }

    /**
     * @return the resolvedOn
     */
    public Date getResolvedOn() {
        return resolvedOn;
    }

    /**
     * @param resolvedOn the resolvedOn to set
     */
    public void setResolvedOn(Date resolvedOn) {
        this.resolvedOn = resolvedOn;
        changed = true;
    }

}
