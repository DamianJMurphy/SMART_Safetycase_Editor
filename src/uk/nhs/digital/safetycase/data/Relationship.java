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
public class Relationship {
    
    private int relationshipId = -1;
    private int sourceId = -1;
    private int targetId = -1;
    private String sourceType = null;
    private String targetType = null;
    private boolean deleted = false;
    private String comment = null;
    private String managementClass = null;
    
    public Relationship(int s, int t, String type) {
        sourceId = s;
        targetId = t;
        targetType = type;
    }
    
    // Implementation detail: Relationship is immutable other than to change
    // the comment, or to mark it deleted
    //
    public void setComment(String s) { comment = s; }
    public String getComment() { return comment; }
    
    public void setManagementClass(String s) { managementClass = s; }
    public String getManagementClass() { return managementClass; }
    
    void setSourceType(String s) { sourceType = s; }
    String getSourceType() { return sourceType; }
    
    public boolean isDeleted() { return deleted; }
    
    void setDeleted() { deleted = true; }
    
    Relationship(int r, int s, int t, String type) {
        relationshipId = r;
        sourceId = s;
        targetId = t;
        targetType = type;
    }
    
    void setId(int r) { relationshipId = r; }
    public int getId() { return relationshipId; }
    public int getTarget() { return targetId; }
    public int getSource() { return sourceId; }
    public String getTargetType() { return targetType; }
    
}
