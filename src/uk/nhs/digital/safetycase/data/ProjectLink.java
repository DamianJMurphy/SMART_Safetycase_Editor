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
    
    private Persistable source = null;
    private Persistable remote = null;
    private int directDirection = NOTSET;
    private String remotePath = null;
    private String directComment = null;
    private String id = null;
    private boolean checked = false;
    private boolean pathContainsDeletedNode = false;
    
    ProjectLink(Persistable s, Persistable r, int d) {
        source = s;
        remote = r;
        directDirection = d;
        StringBuilder sb = new StringBuilder(s.getDatabaseObjectName());
        sb.append(s.getId());
        sb.append(r.getDatabaseObjectName());
        sb.append(r.getId());
        id = sb.toString();
    }
    
    public String getId() { return id; }
    public String getLocalDisplayName()  { return source.getDisplayName(); }
    public String getLocalTitle() { return source.getTitle(); }
    public String getRemoteDisplayName() { return remote.getDisplayName(); }
    public String getRemoteTitle() { return remote.getTitle(); }
    public String getDirectComment() { return (directComment == null) ? "" : directComment; }
    public String getRemotePath() { return (remotePath == null) ? "" : remotePath; }
    public boolean isChecked() { return checked; }
    public boolean remoteIsDeleted() { return remote.isDeleted(); }
    public boolean pathHasDeleted() { return pathContainsDeletedNode; }
    
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        try {
            ProjectLink p = (ProjectLink)o;
            return id.contentEquals(p.getId());
        }
        catch (Exception e) { return false; }
    }

    Persistable getRemote() { return remote; }    
    void setChecked() { checked = true; }
    void setDirectComment(String s) { directComment = s; }

    void addRemotePath(Persistable t, Relationship r, int d) {
        String src = null;
        String tgt = null;
        Persistable x = null;
        try {
            x = MetaFactory.getInstance().getFactory(r.getTargetType()).get(r.getTarget());
            tgt = x.getTitle();
            if (x.isDeleted())
                pathContainsDeletedNode = true;
        }
        catch (Exception e1) {
            tgt = "Could not resolve link target";
        }
        try {
            x = MetaFactory.getInstance().getFactory(r.getSourceType()).get(r.getSource());
            src = x.getTitle();
            if (x.isDeleted())
                pathContainsDeletedNode = true;
        }
        catch (Exception e2) {
            src = "Failed to link source";
        }
        
        StringBuilder sb = null;
        if (remotePath == null) {
            sb = new StringBuilder();         
        } else {
            sb = new StringBuilder(remotePath);
            sb.append(";");
        }
        if (d == TO) {
            sb.append(src);
            sb.append("/");
            sb.append(tgt);
        } else {
            sb.append(tgt);
            sb.append("/");
            sb.append(src);
        }
        if ((r.getComment() != null) && (r.getComment().trim().length() != 0)) {
            sb.append("(");
            sb.append(r.getComment());
            sb.append(")");
        }
        remotePath = sb.toString();
    }
    
}
