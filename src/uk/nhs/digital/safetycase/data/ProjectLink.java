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
package uk.nhs.digital.safetycase.data;

/**
 *
 * @author damian
 */
@SuppressWarnings("EqualsAndHashcode")
public class ProjectLink {
   
    public static final int NOTSET = -1;
    public static final int TO = 0;
    public static final int FROM = 1;
    public static final int BOTH = 2;
    public static final int DIRECTONLY = 0;
    public static final int REMOTEONLY = 1;
    
    private Persistable source = null;
    private Persistable target = null;
    private int directDirection = NOTSET;
    private int order = NOTSET;
    private String remotePath = null;
    private String directComment = null;
    private String id = null;
    private boolean checked = false;
    
    ProjectLink(Persistable s, Persistable t, int d, int o) {
        source = s;
        target = t;
        directDirection = d;
        order = o;
        StringBuilder sb = new StringBuilder(s.getDatabaseObjectName());
        sb.append(s.getId());
        sb.append(t.getDatabaseObjectName());
        sb.append(t.getId());
        id = sb.toString();
    }
    
    public String getId() { return id; }
    public String getTargetDisplayName() { return target.getDisplayName(); }
    public String getTargetTitle() { return target.getTitle(); }
    public String getDirectComment() { return (directComment == null) ? "" : directComment; }
    public String getRemotePath() { return (remotePath == null) ? "" : remotePath; }
    public boolean isChecked() { return checked; }
    
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        try {
            ProjectLink p = (ProjectLink)o;
            return id.contentEquals(p.getId());
        }
        catch (Exception e) { return false; }
    }

    Persistable getTarget() { return target; }    
    void setChecked() { checked = true; }
    void setDirectComment(String s) { directComment = s; }

    void addRemotePath(Persistable t, String c, int d) {
        StringBuilder sb = null;
        if (remotePath == null) {
            if (d == TO) {
                sb = new StringBuilder("To ");
            } else {
                sb = new StringBuilder("From ");
            }
        } else {
            sb = new StringBuilder(remotePath);
            if (d == TO) {
                sb = new StringBuilder(" and to ");
            } else {
                sb = new StringBuilder(" and from ");
            }
        }
        sb.append(t.getDisplayName());
        sb.append(":");
        sb.append(t.getTitle());
        if (c != null) {
            sb.append(" (");
            sb.append(c);
            sb.append(")");
        }
        remotePath = sb.toString();
    }
    
}
