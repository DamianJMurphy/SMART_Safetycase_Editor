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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
/**
 *
 * @author damian
 */
public class Database {
    
    private Connection connection = null;
    public static final SimpleDateFormat DATEONLY = new SimpleDateFormat("yyyy-MM-dd");
    
    public static final Attribute empty = new Attribute();
    
    private final HashMap<String, ArrayList<RelationshipSemantics>> allowedRelationships = new HashMap<>();
    
    public static final String JAVADATABASEPREFIX = "uk.nhs.digital.safetycase.data.";
    public static final String CONNECTIONURLPROPERTY = "uk.nhs.digital.safetycase.database";
    
    public Database()
            throws Exception
    {
        initialise(java.lang.System.getProperties());
    }
    
    public Database(Properties p)
            throws Exception
    {        
        initialise(p);
    }
    
    private void initialise(Properties p)
            throws Exception
    {
        String url = p.getProperty(CONNECTIONURLPROPERTY);
        connection = DriverManager.getConnection(url, p);        
    }
    
    Connection getConnection()
        throws Exception
    { 
        return connection;
    }
    
    public void shutdown(boolean compact) 
            throws Exception
    {
        connection.commit();
        String sql = (compact) ? "SHUTDOWN COMPACT" : "SHUTDOWN";
        try (Statement s = connection.createStatement()) {
            s.execute(sql);
        }
    }
    
    ArrayList<String> getDistinctSet(String t, String f)
            throws Exception
    {
        ArrayList<String> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select distinct ");
        sql.append(f);
        sql.append(" from ");
        sql.append(t);
        sql.append(" order by ");
        sql.append(f);
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sql.toString())) {
                throw new Exception("Cannot read allowed relationship types " + sql.toString());
            }
            try (ResultSet r = s.getResultSet()) {
                while (r.next()) {
                   list.add(r.getString(f));
                }
            }
        }
        return list;
    }
        
    public ArrayList<RelationshipSemantics> getAllowedRelationships(String sourcetype) {
        return allowedRelationships.get(sourcetype);
    }
    
    public void loadAllowedRelationships()
            throws Exception
    {
        String sql = RelationshipSemantics.sqlActive;
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sql)) {
                throw new Exception("Cannot read allowed relationship types: " + sql);
            }
            try (ResultSet r = s.getResultSet()) {
                while (r.next()) {
                    int i = r.getInt("RelationshipSemanticsID");
                    RelationshipSemantics rs = new RelationshipSemantics(i);
                    rs.setSourceType(r.getString("SourceObjectType"));
                    rs.setTargetType(r.getString("TargetObjectType"));
                    rs.setSummary(r.getString("Summary"));
                    rs.setDescription(r.getString("Description"));
                    
                    if (allowedRelationships.containsKey(rs.getSourceType())) {
                        allowedRelationships.get(rs.getSourceType()).add(rs);
                    } else {
                        ArrayList<RelationshipSemantics> a = new ArrayList<>();
                        a.add(rs);
                        allowedRelationships.put(rs.getSourceType(), a);
                    }
                }
            }
        }
    }
    
    void save(LibraryObject lib)
            throws Exception
    {
        if (lib.getId() == -1)
            createLibraryObject(lib);
        else
            updateLibraryObject(lib);
    }
    
    void createLibraryObject(LibraryObject lib)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("insert into Library (ObjectType,Name,Description,AddedOn,DeprecatedDate,Source,Comment) values (");
        sb.append(prepareString(lib.getType()));
        sb.append(", ");
        sb.append(prepareString(lib.getName()));
        sb.append(", ");
        sb.append(prepareString(lib.getDescription()));
        sb.append(", ");
        sb.append("CURENT_DATE, null, ");
        sb.append(prepareString(lib.getSource()));
        sb.append(", ");
        sb.append(prepareString(lib.getComment()));
        sb.append(")");
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString(), Statement.RETURN_GENERATED_KEYS) == 1) {
                ResultSet generatedKeys = s.getGeneratedKeys();
                if (generatedKeys.next())
                    lib.setId(generatedKeys.getInt(1));
                else
                    throw new Exception("Failed to retrieve relationship id: " + sb.toString());
            }
            for (LibraryAttribute a : lib.getAttributes()) {
                createLibraryAttribute(lib.getId(), a);
            }
            connection.commit();
        }       
    }
    
    void updateLibraryObject(LibraryObject lib)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update Library set ObjectType = ");
        sb.append(prepareString(lib.getType()));
        sb.append(", Name = ");
        sb.append(prepareString(lib.getName()));
        sb.append(", Description = ");
        sb.append(prepareString(lib.getDescription()));
        sb.append(", Source = ");
        sb.append(prepareString(lib.getSource()));
        sb.append(", Comment = ");
        sb.append(prepareString(lib.getComment()));
        sb.append(" where LibraryID = ");
        sb.append(lib.getId());
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update LibraryObject - not found: " + sb.toString());
            }
            for (LibraryAttribute a : lib.getAttributes()) {
                updateLibraryAttribute(lib.getId(), a);
            }            
            connection.commit();
        }
        
    }
    
    void createLibraryAttribute(int lid, LibraryAttribute a)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("indsert into LibraryAttribute (AttributeName, LibraryID, StringValue, IntegerValue) values (");
        sb.append(prepareString(a.getName()));
        sb.append(", ");
        sb.append(lid);
        sb.append(", ");
        if (a.getType() == Attribute.STRING)
            sb.append(a.toString());
        else
            sb.append("null");
        sb.append(", ");
        sb.append(a.getIntValue());
        sb.append(")");
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString(), Statement.RETURN_GENERATED_KEYS) == 1) {
                ResultSet generatedKeys = s.getGeneratedKeys();
                if (generatedKeys.next())
                    a.setId(generatedKeys.getInt(1));
                else
                    throw new Exception("Failed to retrieve relationship id: " + sb.toString());
            }
        }
    }
    
    void updateLibraryAttribute(int lid, LibraryAttribute a)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update LibraryAttribute set ");
        sb.append("AttributeName = ");
        sb.append(prepareString(a.getName()));
        sb.append(", LibraryID = ");
        sb.append(lid);
        sb.append(", StringValue = ");
        if (a.getType() == Attribute.STRING)
            sb.append(a.toString());
        else
            sb.append("null");
        sb.append(", IntegerValue = ");
        sb.append(a.getIntValue());
        sb.append(" where LibraryAttributeID = ");
        sb.append(a.getID());
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update LibraryAttribute - not found: " + sb.toString());
            }
        }
    }
    
    
    Library loadLibraries() 
            throws Exception
    {
        Library l = null;
        try (Statement s = connection.createStatement()) {
            if (!s.execute("select * from Library where DeprecatedDate is null")) {
                throw new Exception("Cannot read library data");
            }
            try (ResultSet r = s.getResultSet()) {
                l = new Library();
                while (r.next()) {
                    String ot = r.getString("ObjectType");
                    int id = r.getInt("LibraryID");
                    LibraryObject lib = new LibraryObject(id, ot);
                    lib.setAddedDate(r.getString("AddedDate"));
                    lib.setComment(r.getString("Comment"));
                    lib.setDescription(r.getString("Description"));
                    lib.setName(r.getString("Name"));
                    lib.setSource(r.getString("Source"));
                    loadLibraryAttributes(lib);
                    l.putLibraryObject(lib);
                }
            }
        }
        return l;
    }
    
    void loadLibraryAttributes(LibraryObject l)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("select * from LibraryAttribute where LibraryID = ");
        sb.append(l.getId());
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sb.toString())) {
                throw new Exception("Cannot read library attribute data for LibraryID " + l.getId());
            }
            try (ResultSet r = s.getResultSet()) {
                while (r.next()) {
                    LibraryAttribute a = null;
                    int id = r.getInt("LibraryAttributeID");
                    String n = r.getString("AttributeName");
                    String v = r.getString("StringValue");
                    int j = r.getInt("IntegerValue");
                    if (n == null) {
                        a = new LibraryAttribute(id, n, j);
                    } else {
                        a = new LibraryAttribute(id, n, v);
                    }
                    l.addAttribute(a.getName(), a);
                }
            }
        }
    }
    
    void loadValueSet(ValueSet v)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("select * from ValueSetItem where SetName = ");
        sb.append(prepareString(v.getSetName()));
        sb.append(" order by OrderInSet");
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sb.toString())) {
                throw new Exception("Cannot read value set " + v.getSetName());
            }
            try (ResultSet r = s.getResultSet()) {
                while (r.next()) {
                    String val = r.getString("ItemName");
                    v.add(val);
                }
            }
        }
    }
    
    ArrayList<Relationship> loadFromRelationships(Persistable p, String fromType, boolean automatic, boolean manual)
            throws Exception
    {
        ArrayList<Relationship> results = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(fromType);
        sb.append("Relationship where ");
        sb.append("RelatedObjectType = ");
        sb.append(prepareString(p.getDatabaseObjectName()));
        sb.append(" and RelatedObjectID = ");
        sb.append(p.getId());
        sb.append(" and DeletedDate Is Null ");
        if (automatic) {
            if (!manual) {
                sb.append(" and ManagementClass Is Not Null ");
            }
        } else {
            if (manual) {
                sb.append(" and ManagementClass Is Null");
            } else {
                throw new Exception("Invalid request: neither automatic nor manual requests will return no results");
            }
        }
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sb.toString())) {
                throw new Exception("Cannot read allowed relationship types: " + sb.toString());
            }
            try (ResultSet r = s.getResultSet()) {
                while (r.next()) {
                    int rid = r.getInt(fromType + "RelationshipID");
                    int sid = r.getInt(fromType + "ID");
                    int tid = r.getInt("RelatedObjectID");
                    String rot = p.getDatabaseObjectName();
                    Relationship rs = new Relationship(rid, sid, tid, rot);
                    rs.setSourceType(fromType);
                    rs.setManagementClass(r.getString("ManagementClass"));
                    rs.setComment(r.getString("Comment"));
                    results.add(rs);
                }
            }
        }
                        
        return results;
    }
    
    void loadRelationships(Persistable p)
            throws Exception
    {
        String source = p.getDatabaseObjectName();
        HashMap<String, ArrayList<Relationship>> rels = p.getRelationshipsForLoad();
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(source);
        sb.append("Relationship where ");
        sb.append(source);
        sb.append("id = ");
        sb.append(p.getId());
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sb.toString())) {
                throw new Exception("Cannot read allowed relationship types: " + sb.toString());
            }
            try (ResultSet r = s.getResultSet()) {
                while (r.next()) {
                    int rid = r.getInt(source + "RelationshipID");
                    int sid = r.getInt(source + "ID");
                    int tid = r.getInt("RelatedObjectID");
                    String rot = r.getString("RelatedObjectType");
                    Relationship rs = new Relationship(rid, sid, tid, rot);
                    rs.setSourceType(p.getDatabaseObjectName());
                    rs.setManagementClass(r.getString("ManagementClass"));
                    if (r.getDate("DeletedDate") != null) {
                        rs.setDeleted();
                    } else {
                        rs.setComment(r.getString("Comment"));
                        if (rels.containsKey(rot)) {
                            rels.get(rot).add(rs);
                        } else {
                            ArrayList<Relationship> a = new ArrayList<>();
                            a.add(rs);
                            rels.put(rot, a);
                        }
                    }
                }
            }
        }
    }
    
    void delete(String source, int rid)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update ");
        sb.append(source);
        sb.append("Relationship set DeletedDate=CURRENT_DATE  where ");
        sb.append(source);
        sb.append("RelationshipID = ");
        sb.append(rid);
        java.lang.System.out.println(sb.toString());
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update object - not found");
            }
            connection.commit();
        }
    }
    
    Persistable load(Persistable p, int id)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(p.getDatabaseObjectName());
        sb.append(" where ");
        sb.append(p.getDatabaseObjectName());
        sb.append("ID = ");
        sb.append(id);
        if (p.getOrderBy() != null) {
            sb.append(" order by ");
            sb.append(p.getOrderBy());
        }
        return runSingleRowQuery(sb.toString(), p, Integer.toString(id), null);
    }
    
    private Persistable runSingleRowQuery(String sql, Persistable p, String id, String pid)
            throws Exception
    {
        Persistable newP = null;
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sql)) {
                throw new Exception("Cannot read " + p.getDatabaseObjectName() + " id " + id + " projectid " + pid + " : " + sql);
            }
            try (ResultSet r = s.getResultSet()) {
                if (r.next()) {
                    newP = loadResultSet(p, r);
                    if (!newP.isReferenceData())
                        loadRelationships(newP);
                }
            }            
        }
        return newP;
    }

    private ArrayList<Persistable> runMultiRowQuery(String sql, Persistable p, String id, String pid)
            throws Exception
    {
        Persistable newP = null;
        ArrayList<Persistable> a = null;
        try (Statement s = connection.createStatement()) {
            if (!s.execute(sql)) {
                throw new Exception("Cannot read " + p.getDatabaseObjectName() + " id " + id + " projectid " + pid + " : " + sql);
            }
            try (ResultSet r = s.getResultSet()) {
                a = new ArrayList<>();
                while (r.next()) {
                    newP = loadResultSet(p, r); 
                    a.add(newP);
                    if (!newP.isReferenceData())
                        loadRelationships(newP);
                }
            }            
        }
        return a;
    }    
    
    private Persistable loadResultSet(Persistable p, ResultSet r)
            throws Exception
    {
        Persistable newP = null;
        ResultSetMetaData rsmd = r.getMetaData();
        newP = (Persistable)Class.forName(JAVADATABASEPREFIX + p.getDatabaseObjectName()).newInstance();
        newP.setId(r.getInt(p.getDatabaseObjectName() + "ID"));
        if (r.getDate("DeletedDate") != null)
            newP.setDeleted();
        
        for (String a : p.getReadOnlyAttributeNames()) {
            if (r.getString(a) == null)
                newP.setReadOnlyAttribute(a, "");
            else 
                newP.setReadOnlyAttribute(a, r.getString(a));
        }
        for (String a : p.getWritableAttributeNames()) {
            if (r.getString(a) == null) {
                newP.setAttribute(a, Database.empty);
            } else {
                int c = r.findColumn(a);
                int t = rsmd.getColumnType(c);
                if (t == Types.INTEGER)
                    newP.setAttribute(a, r.getInt(a));
                else
                    newP.setAttribute(a, r.getString(a));
            }
        }
        newP.clearChange();
        return newP;
    }
    
    Persistable save(Persistable t)
            throws Exception
    {
        if (t == null)
            return t;
        if (t.getId() == -1)
            create(t);
        else
            update(t);
        if (!t.isReferenceData()) {
            HashMap<String, ArrayList<Relationship>> rels = t.getRelationshipsForLoad();
            for (String s : rels.keySet()) {
                for (Relationship r : rels.get(s)) {
                    if (r.isDeleted()) {
                        delete(r.getSourceType(), r.getId());
                    } else {
                        save(r);
                    }
                }
            }
        }
        return t;
    }
    
    public Relationship save(Relationship r)
            throws Exception
    {
        if (r == null)
            return r;
        if (r.getId() == -1)
            create(r);
        else
            update(r);
        return r;
    }
    
    private void create(Relationship r)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(r.getSourceType());
        sb.append("Relationship (");
        sb.append(r.getSourceType());
        sb.append("ID, RelatedObjectID, RelatedObjectType, Comment, ManagementClass, CreatedDate, LastUpdatedDate, DeletedDate) values (");
        sb.append(r.getSource());
        sb.append(", ");
        sb.append(r.getTarget());
        sb.append(", ");
        sb.append(Database.prepareString(r.getTargetType()));
        sb.append(", ");
        sb.append(Database.prepareString(r.getComment()));
        sb.append(", ");
        sb.append(Database.prepareString(r.getManagementClass()));
        sb.append(", CURRENT_DATE, null, null)");
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString(), Statement.RETURN_GENERATED_KEYS) == 1) {
                ResultSet generatedKeys = s.getGeneratedKeys();
                if (generatedKeys.next())
                    r.setId(generatedKeys.getInt(1));
                else {
                    java.lang.System.out.println(sb.toString());
                    r.setId(0);
//                    throw new Exception("Failed to retrieve relationship id");
                }
            }
            connection.commit();
        }
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
    }
    
    private void update(Relationship r)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update ");
        sb.append(r.getSourceType());
        sb.append("Relationship set ");
        sb.append("Comment = ");
        sb.append(Database.prepareString(r.getComment()));
        sb.append(", ManagementClass = ");
        sb.append(Database.prepareString(r.getManagementClass()));
        sb.append(", LastUpdatedDate = CURRENT_DATE where ");
        sb.append(r.getSourceType());
        sb.append("RelationshipID = ");
        sb.append(r.getId());
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update object - not found");
            }
            connection.commit();
        }
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
    }
    
    private void create(Persistable t)
            throws Exception
    {
        // Build an "insert" statement, then execute call IDENTITY(); and put the resulting value into
        // the identity member of t.
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(t.getDatabaseObjectName());
        sb.append(" (");
        for (String s : t.getWritableAttributeNames()) {
            sb.append(s);
            sb.append(",");
        }
        sb.append("CreatedDate, LastUpdatedDate, DeletedDate) values (");
        for (String s : t.getWritableAttributeNames()) {
            Attribute a = t.getAttribute(s);
            if (a.getType() == Attribute.STRING) {
                if (a.getIsDate())
                    sb.append(Database.prepareDate(a.toString()));
                else
                    sb.append(Database.prepareString(a.toString()));
            } else {
                sb.append(a.toString());
            }
            sb.append(", ");
        }
        sb.append("CURRENT_DATE, null, null)");
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString(), Statement.RETURN_GENERATED_KEYS) == 1) {
                ResultSet generatedKeys = s.getGeneratedKeys();
                if (generatedKeys.next())
                    t.setId(generatedKeys.getInt(1));
                else
                    throw new Exception("Failed to retrieve " +  t.getDatabaseObjectName() + " id");
            }
            connection.commit();            
        } 
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
    }
    
    private void update(Persistable t)
            throws Exception 
    {
        StringBuilder sb = new StringBuilder("update ");
        sb.append(t.getDatabaseObjectName());
        sb.append(" set ");
        for (String s : t.getWritableAttributeNames()) {
            sb.append(s);
            sb.append(" = ");
            Attribute a = t.getAttribute(s);
            if (a.getType() == Attribute.STRING) {
                if (a.getIsDate())
                    sb.append(Database.prepareDate(a.toString()));
                else
                    sb.append(Database.prepareString(a.toString()));
            } else {
                if (a.getIsDate() && a.empty) {
                    sb.append("null");
                } else {
                    if (a.empty)
                        sb.append(" null");
                    else 
                        sb.append(a.toString());
                }
            }
            sb.append(", ");
        }
        sb.append(" LastUpdatedDate = CURRENT_DATE where ");
        sb.append(t.getDatabaseObjectName());
        sb.append("ID = ");
        sb.append(t.getId());
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update object - not found");
            }
            connection.commit();
        }
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
    }
    
    Persistable undelete(Persistable t)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update ");
        sb.append(t.getDatabaseObjectName());
        sb.append(" set DeletedDate=null  where ");
        sb.append(t.getDatabaseObjectName());
        sb.append("ID = ");
        sb.append(t.getId());
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot mark object deleted - not found");
            }
            connection.commit();            
        }
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
        t.setUndeleted();
        return t;
    }
    
    void purgePersistable(String t)
            throws Exception
    {
//        purgeRelationships(t);
        StringBuilder sb = new StringBuilder("delete from ");
        sb.append(t);
        sb.append(" where DeletedDate is not null");
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            int r = s.executeUpdate(sb.toString());
            connection.commit();            
            java.lang.System.out.println("Deleted " + r + " " + t + " records");
        }
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
    }
    
    void purgeRelationships(String t) 
            throws Exception
    {
        StringBuilder sb = new StringBuilder("delete from ");
        sb.append(t);
        sb.append("Relationship where DeletedDate is not null");
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            int r = s.executeUpdate(sb.toString());
            connection.commit();            
            java.lang.System.out.println("Deleted " + r + " " + t + "Relationship records");
        }
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
    }
    
    Persistable delete(Persistable t)
        throws Exception
    {
        StringBuilder sb = new StringBuilder("update ");
        sb.append(t.getDatabaseObjectName());
        sb.append(" set DeletedDate=CURRENT_DATE  where ");
        sb.append(t.getDatabaseObjectName());
        sb.append("ID = ");
        sb.append(t.getId());
        java.lang.System.err.println(sb);
        try (Statement s = connection.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot mark object deleted - not found");
            }
            if (!t.isReferenceData()) {
                StringBuilder dr = new StringBuilder("update ");
                dr.append(t.getDatabaseObjectName());
                dr.append("Relationship ");
                dr.append(" set DeletedDate=CURRENT_DATE  where ");
                dr.append(t.getDatabaseObjectName());
                dr.append("ID = ");
                dr.append(t.getId());      
                Statement sr = connection.createStatement();
                s.executeUpdate(dr.toString());
            }
            connection.commit();            
        }
        catch(Exception e) {
            Exception edetail = new Exception(sb.toString(), e);
            throw edetail;
        }
        t.setDeleted();
        return t;
    }
    
    public ArrayList<Persistable> loadAll(Persistable p, int projectid)
            throws Exception
    {
        if (p.isReferenceData())
            return loadAll(p);
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(p.getDatabaseObjectName());
        sb.append(" where ");
        sb.append(p.getDatabaseObjectName());
        sb.append("ProjectID = ");
        sb.append(projectid);
        return runMultiRowQuery(sb.toString(), p, null, Integer.toString(projectid));    
    }
    
    public ArrayList<Persistable> loadAll(Persistable p)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(p.getDatabaseObjectName());
        return runMultiRowQuery(sb.toString(), p, null, null);    
    }
    
    private static final String escapeQuotes(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\'') {
                sb.append(c);
            } else {
                sb.append("''");
            }
        }
        return sb.toString();
    }
    
    public static final String prepareString(String s)
    {
        if (s == null)
            return "null";
        if (s.length() == 0)
            return "''";
        StringBuilder sb = new StringBuilder("'");
        
        // TODO: FIXME
        // This is an SQL injection attack waiting to happen, but for the sake of
        // getting the rest of the job done right now it can be fixed later.
        // MUST DO SO before any real network deployment.
        //
        sb.append(escapeQuotes(s));
        
        sb.append("'");
        return sb.toString();
    }
    
    public static final String prepareDate(String s) 
            throws Exception
    {
        if ((s == null) || (s.trim().length() == 0))
            return null;
        Date d = DATEONLY.parse(s);
        return prepareDate(d);
    }
    
    public static final String prepareDate(Date d) {
        if (d == null)
            return "null";
        String s = DATEONLY.format(d);
        return Database.prepareString(s);        
    }
}
