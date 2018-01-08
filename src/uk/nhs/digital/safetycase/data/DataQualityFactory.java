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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author damian
 */
public class DataQualityFactory {

    
    
    private final HashMap<Integer,DataQualityCheck> checks = new HashMap<>();
    private final HashMap<Integer, DataQualityCheckRun> runs = new HashMap<>();
    private Database database = null;
   
    public DataQualityFactory(Database d)
            throws Exception
    {
        database = d;
        loadChecks();
    }

    public void addCheck(DataQualityCheck d) 
            throws Exception
    {
        if (d.needsSaving()) {
            if (d.getId() == -1)
                createCheck(d);
            else
                updateCheck(d);
        }
        checks.put(d.getId(), d);
    }
    
    public void addCheckRun(DataQualityCheckRun d) 
            throws Exception
    {
        if (d.needsSaving()) {
            if (d.getRunId() == -1)
                createRun(d);
            else
                updateRun(d);
        }
        runs.put(d.getRunId(), d);        
    }
    
    public DataQualityCheck getCheck(int i) { return checks.get(i); }
    public Collection<DataQualityCheck> listChecks() { return checks.values(); }
    public Collection<DataQualityCheckRun> listCheckRuns() { return runs.values(); }
    
    private void loadChecks()
            throws Exception 
    {
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (!s.execute(DataQualityCheck.READ)) {
                throw new Exception("No data quality checks found");
            }
            ResultSet r = s.getResultSet();
            while (r.next()) {
                DataQualityCheck check = new DataQualityCheck();
                check.setId(r.getInt("DataQualityCheckId"));
                check.setName(r.getString("Name"));
                check.setDescription(r.getString("Description"));
                check.setPrimaryObjectType(r.getString("PrimaryObjectType"));
                check.setImplementedBy(r.getString("ImplementedBy"));
                check.setImplementationArgument(r.getString("ImplementationArgument"));
                check.setAddedOn(r.getString("AddedDate"));
                check.setDeprecatedDate(r.getString("DeprecatedDate"));
                check.clearChanged();
                checks.put(check.getId(), check);
            }
        }
    }

    public void loadCheckRuns(int checkId, int projectid)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("select * from DataQualityCheckRun where DataQualityCheckID = ");
        sb.append(checkId);
        if (projectid != -1) {
            sb.append(" and ProjectId = ");
            sb.append(projectid);
        }
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (!s.execute(sb.toString())) {
                throw new Exception("No data quality checks found");
            }
            ResultSet r = s.getResultSet();
            while (r.next()) {
                DataQualityCheckRun d = new DataQualityCheckRun(checkId);
                d.setRunId(r.getInt("DataQualityCheckRunID"));
                d.setProjectId(r.getInt("ProjectID"));
                d.setDate(r.getDate("Date"));
                d.setRunBy(r.getString("RunBy"));
                d.setSupercededBy(r.getInt("SupercededBy"));
                d.clearChanged();
                loadCheckRunIssues(c, d);
                runs.put(d.getRunId(), d);
            }
        }
    }
    
    private void loadCheckRunIssues(Connection c, DataQualityCheckRun run)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("select * from DataQualityCheckIssue where DataQualityCheckRunID = ");
        sb.append(run.getRunId());
        try (Statement s = c.createStatement()) {
            if (!s.execute(sb.toString())) {
                throw new Exception("No data quality issues found");
            }
            ResultSet r = s.getResultSet();
            while (r.next()) {
                DataQualityCheckIssue d = new DataQualityCheckIssue(run);
                d.setIssue(r.getString("Issue"));
                d.setIssueId(r.getInt("DataQualityCheckIssueID"));
                d.setIssueLocation(r.getString("IssueLocation"));
                d.setResolution(r.getString("Resolution"));
                d.setResolutionType(r.getString("ResolutionType"));
                d.setResolvedOn(r.getDate("ResolvedOn"));
                d.clearChanged();
                run.addIssue(d);
            }
        }
    }
    
    public void save()
            throws Exception
    {
        for (DataQualityCheck d : checks.values()) {
            if (d.needsSaving()) {
                if (d.getId() == -1)
                    createCheck(d);
                else
                    updateCheck(d);
            }
        }
        for (DataQualityCheckRun d : runs.values()) {
            if (d.needsSaving()) {
                if (d.getRunId() == -1)
                    createRun(d);
                else
                    updateRun(d);
            }            
            Iterator<DataQualityCheckIssue> issues = d.listIssues();
            while (issues.hasNext()) {
                DataQualityCheckIssue checkIssue = issues.next();
                if (checkIssue.getIssueId() == -1)
                    createIssue(checkIssue);
                else 
                    updateIssue(checkIssue);
            }
                
        }
    }
    
    private void createCheck(DataQualityCheck d)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("insert into DataQualityCheck (Name, Description, PrimaryObjectType, AddedDate, DeprecatedDate, ImplementedBy, ImplementationArgument) values (");
        sb.append(Database.prepareString(d.getName()));
        sb.append(", ");
        sb.append(Database.prepareString(d.getDescription()));
        sb.append(", ");
        sb.append(Database.prepareString(d.getPrimaryObjectType()));
        sb.append(", CURRENT_DATE, null, ");
        sb.append(Database.prepareString(d.getImplementedBy()));
        sb.append(", ");
        sb.append(Database.prepareString(d.getImplementationArgument()));
        sb.append(")");
        
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (s.executeUpdate(sb.toString(), Statement.RETURN_GENERATED_KEYS) == 1) {
                ResultSet generatedKeys = s.getGeneratedKeys();
                if (generatedKeys.next())
                    d.setId(generatedKeys.getInt(1));
                else
                    throw new Exception("Failed to retrieve new check id");
            }
            c.commit();            
        }      
        d.clearChanged();
    }
    
    private void updateCheck(DataQualityCheck d)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update DataQualityCheck set ");
        sb.append("Name = ");
        sb.append(Database.prepareString(d.getName()));
        sb.append(", Description = ");
        sb.append(Database.prepareString(d.getDescription()));
        sb.append(", PrimaryObjectType = ");
        sb.append(Database.prepareString(d.getPrimaryObjectType()));
        sb.append(", DeprecatedDate = ");
        sb.append(Database.prepareString(d.getDeprecatedDate()));
        sb.append(", ImplementedBy = ");
        sb.append(Database.prepareString(d.getImplementedBy()));
        sb.append(", ImplementationArgument = ");
        sb.append(Database.prepareString(d.getImplementationArgument()));
        sb.append(" where DataQualityCheckID = ");
        sb.append(d.getId());
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update object - not found");
            }
            c.commit();
            d.clearChanged();
        }
    }
    
    private void createRun(DataQualityCheckRun d)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("insert into DataQualityCheckRun (Date, DataQualityCheckID, ProjectId, RunBy, SupercededBy) values (");
        sb.append(Database.prepareDate(d.getDate()));
        sb.append(", ");
        sb.append(d.getCheckId());
        sb.append(", ");
        sb.append(d.getProjectId());
         sb.append(", ");
        sb.append(Database.prepareString(d.getRunBy()));
        sb.append(", ");
        sb.append(d.getSupercededBy());
        sb.append(")");
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (s.executeUpdate(sb.toString(), Statement.RETURN_GENERATED_KEYS) == 1) {
                ResultSet generatedKeys = s.getGeneratedKeys();
                if (generatedKeys.next())
                    d.setRunId(generatedKeys.getInt(1));
                else
                    throw new Exception("Failed to retrieve new check id");
            }
            c.commit();            
        }      
        d.clearChanged();
    }

    private void updateRun(DataQualityCheckRun d)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update DataQualityCheckRun set ");
        sb.append("Date = ");
        sb.append(Database.prepareDate(d.getDate()));
        sb.append(", RunBy = ");
        sb.append(Database.prepareString(d.getRunBy()));
        sb.append(", DataQualityCheckID = ");
        sb.append(d.getCheckId());
        sb.append(", ProjectID = ");
        sb.append(d.getProjectId());
        sb.append(", SupercededBy = ");
        sb.append(d.getSupercededBy());        
        sb.append(" where DataQualityCheckRunID = ");
        sb.append(d.getRunId());
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update object - not found");
            }
            c.commit();
            d.clearChanged();
        }        
    }
    
    private void createIssue(DataQualityCheckIssue d)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("insert into DataQualityCheckIssue (Issue, IssueLocation, Resolution, ResolvedOn, ResolvedBy, DataqualityCheckRunID, ResolutionType) values (");
        sb.append(Database.prepareString(d.getIssue()));
        sb.append(", ");
        sb.append(Database.prepareString(d.getIssueLocation()));
        sb.append(", ");
        sb.append(Database.prepareString(d.getResolution()));
        sb.append(", ");
        sb.append(Database.prepareDate(d.getResolvedOn()));
        sb.append(", ");
        sb.append(Database.prepareString(d.getResolvedBy()));
        sb.append(", ");
        sb.append(d.getRunId());
        sb.append(", ");
        sb.append(Database.prepareString(d.getResolutionType()));
        sb.append(")");
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (s.executeUpdate(sb.toString(), Statement.RETURN_GENERATED_KEYS) == 1) {
                ResultSet generatedKeys = s.getGeneratedKeys();
                if (generatedKeys.next())
                    d.setRunId(generatedKeys.getInt(1));
                else
                    throw new Exception("Failed to retrieve new issue id");
            }
            c.commit();            
        }      
        d.clearChanged();
    }

    private void updateIssue(DataQualityCheckIssue d)
            throws Exception
    {
        StringBuilder sb = new StringBuilder("update DataQualityCheckIssue set ");
        sb.append("Issue = ");
        sb.append(Database.prepareString(d.getIssue()));
        sb.append(", IssueLocation = ");
        sb.append(Database.prepareString(d.getIssueLocation()));
        sb.append(", Resolution = ");
        sb.append(Database.prepareString(d.getResolution()));
        sb.append(", ResolvedOn = ");
        sb.append(Database.prepareDate(d.getResolvedOn()));
        sb.append(", ResolvedBy = ");
        sb.append(Database.prepareString(d.getResolvedBy()));
        sb.append(", DataQualityCheckRunID = ");
        sb.append(d.getRunId());
        sb.append(", ResolutionType = ");
        sb.append(Database.prepareString(d.getResolutionType()));
        sb.append(" where DataQualityCheckIssueID = ");
        
        sb.append(d.getRunId());
        Connection c = database.getConnection();
        try (Statement s = c.createStatement()) {
            if (s.executeUpdate(sb.toString()) != 1) {
                throw new Exception("Cannot update object - not found");
            }
            c.commit();
            d.clearChanged();
        }        
        
    }
}
