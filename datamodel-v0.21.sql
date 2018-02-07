
CREATE TABLE PUBLIC.LibraryType (
                ObjectType VARCHAR(32) NOT NULL,
                CONSTRAINT Library_pk PRIMARY KEY (ObjectType)
);

CREATE TABLE PUBLIC.Library (
                LibraryID IDENTITY NOT NULL,
                ObjectType VARCHAR(32) NOT NULL,
                Name VARCHAR(256) NOT NULL,
                Description VARCHAR(1024),
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                Source VARCHAR(1024) NOT NULL,
                Comment VARCHAR(1024),
                CONSTRAINT LibraryID PRIMARY KEY (LibraryID)
);

CREATE TABLE PUBLIC.LibraryAttribute (
                LibraryAttributeID IDENTITY NOT NULL,
                AttributeName VARCHAR(256) NOT NULL,
                LibraryID INTEGER NOT NULL,
                StringValue VARCHAR(65536) NOT NULL,
                IntegerValue INTEGER NOT NULL,
                CONSTRAINT LibraryAttribute_pk PRIMARY KEY (LibraryAttributeID)
);

CREATE TABLE PUBLIC.ProcessStepType (
                Type VARCHAR(64) NOT NULL,
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                CONSTRAINT ProcessStepType_pk PRIMARY KEY (Type)
);

CREATE TABLE PUBLIC.EffectType (
                Type VARCHAR(256) NOT NULL,
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                CONSTRAINT EffectType_pk PRIMARY KEY (Type)
);

CREATE TABLE PUBLIC.DataQualityIssueResolutionType (
                ResolutionType VARCHAR(64) NOT NULL,
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                CONSTRAINT DataQualityIssueResolutionType_pk PRIMARY KEY (ResolutionType)
);

CREATE TABLE PUBLIC.DataQualityCheck (
                DataQualityCheckID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                Description VARCHAR(1024),
                PrimaryObjectType VARCHAR(256) NOT NULL,
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                ImplementedBy VARCHAR(256),
                ImplementationArgument VARCHAR(256),
                CONSTRAINT DataQualityCheck_pk PRIMARY KEY (DataQualityCheckID)
);

CREATE TABLE PUBLIC.RelationshipSemantics (
                RelationshipSemanticsID IDENTITY NOT NULL,
                SourceObjectType VARCHAR(256) NOT NULL,
                TargetObjectType VARCHAR(256) NOT NULL,
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                Summary VARCHAR(256) NOT NULL,
                Description VARCHAR(1024),
                CONSTRAINT RelationshipSemantics_pk PRIMARY KEY (RelationshipSemanticsID)
);

CREATE TABLE PUBLIC.HazardStatus (
                Status VARCHAR(64) NOT NULL,
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                CONSTRAINT HazardStatus_pk PRIMARY KEY (Status)
);

CREATE TABLE PUBLIC.ControlState (
                State VARCHAR(64) NOT NULL,
                AddedDate DATE,
                DeprecatedDate DATE,
                CONSTRAINT ControlState_pk PRIMARY KEY (State)
);

CREATE TABLE PUBLIC.ControlType (
                Type VARCHAR(64) NOT NULL,
                AddedDate DATE NOT NULL,
                DeprecatedDate DATE,
                CONSTRAINT ControlType_pk PRIMARY KEY (Type)
);

CREATE TABLE PUBLIC.Project (
                ProjectID IDENTITY NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Name VARCHAR(256) NOT NULL,
                Description VARCHAR(1024) NOT NULL,
                Owner VARCHAR(256) NOT NULL,
                Customer VARCHAR(256),
                CONSTRAINT Project_pk PRIMARY KEY (ProjectID)
);

CREATE TABLE PUBLIC.IssuesLog (
                IssuesLogID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Description VARCHAR(1024) NOT NULL,
                Resolution VARCHAR(1024),
                ProjectID INTEGER NOT NULL,
                GroupingType VARCHAR(256) NOT NULL,
                ResolutionType VARCHAR(256),
                ResolvedDate DATE,
                CONSTRAINT Issues_Log_pk PRIMARY KEY (IssuesLogID)
);

CREATE TABLE PUBLIC.IssuesLogRelationship (
                IssuesLogRelationshipID IDENTITY NOT NULL,
                IssuesLogID INTEGER NOT NULL,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                ManagementClass VARCHAR(64),
                CONSTRAINT IssuesLogRelationship_pk PRIMARY KEY (IssuesLogRelationshipID)
);

CREATE TABLE PUBLIC.SystemFunction (
                SystemFunctionID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                ParentSystemFunctionID INTEGER,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Description VARCHAR(1024),
                GraphCellId INTEGER DEFAULT -1 NOT NULL,
                ProjectID INTEGER NOT NULL,
                CONSTRAINT SystemFunction_pk PRIMARY KEY (SystemFunctionID)
);

CREATE TABLE PUBLIC.SystemFunctionRelationship (
                SystemFunctionRelationshipID IDENTITY NOT NULL,
                RelatedObjectID INTEGER NOT NULL,
                SystemFunctionID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                ManagementClass VARCHAR(64),
                CONSTRAINT SystemFunctionRelationship_pk PRIMARY KEY (SystemFunctionRelationshipID)
);

CREATE TABLE PUBLIC.Report (
                ReportID IDENTITY NOT NULL,
                ProjectID INTEGER NOT NULL,
                PreparedBy VARCHAR(256),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Title VARCHAR(1024) NOT NULL,
                CCMdetails VARCHAR(65536),
                Introduction VARCHAR(65536),
                Text VARCHAR(1048576),
                CRMdetails VARCHAR(65536),
                SummarySafetySystemDetails VARCHAR(65536),
                QAADdetails VARCHAR(65536),
                CONSTRAINT Report_pk PRIMARY KEY (ReportID)
);

CREATE TABLE PUBLIC.Role (
                RoleID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                Category VARCHAR(64) NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Description VARCHAR(1024),
                ProjectID INTEGER NOT NULL,
                CONSTRAINT Role_pk PRIMARY KEY (RoleID)
);

CREATE TABLE PUBLIC.Location (
                LocationID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                ParentLocationID INTEGER,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Description VARCHAR(1024),
                Mnemonic VARCHAR(16),
                ProjectID INTEGER NOT NULL,
                CONSTRAINT Location_pk PRIMARY KEY (LocationID)
);

CREATE TABLE PUBLIC.System (
                SystemID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                ParentSystemID INTEGER,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Description VARCHAR(1024),
                Version VARCHAR(64),
                Mnemonic VARCHAR(16),
                ProjectID INTEGER NOT NULL,
                GraphXml VARCHAR(65536),
                GraphCellId INTEGER DEFAULT -1 NOT NULL,
                CONSTRAINT System_pk PRIMARY KEY (SystemID)
);

CREATE TABLE PUBLIC.SystemRelationship (
                SystemRelationshipID IDENTITY NOT NULL,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                SystemID INTEGER NOT NULL,
                ManagementClass VARCHAR(64),
                CONSTRAINT SystemRelationship_pk PRIMARY KEY (SystemRelationshipID)
);

CREATE TABLE PUBLIC.DataQualityCheckRun (
                DataQualityCheckRunID IDENTITY NOT NULL,
                Date DATE NOT NULL,
                DataQualityCheckID INTEGER NOT NULL,
                ProjectID INTEGER NOT NULL,
                RunBy VARCHAR(256) NOT NULL,
                SupercededBy INTEGER DEFAULT -1 NOT NULL,
                CONSTRAINT DataQualityCheckRun_pk PRIMARY KEY (DataQualityCheckRunID)
);

CREATE TABLE PUBLIC.DataQualityCheckIssue (
                DataQualityCheckIssueID IDENTITY NOT NULL,
                Issue VARCHAR(1024) NOT NULL,
                IssueLocation VARCHAR(1024),
                Resolution VARCHAR(1024),
                ResolvedOn DATE,
                ResolvedBy VARCHAR(256),
                DataQualityCheckRunID INTEGER NOT NULL,
                ResolutionType VARCHAR(64) NOT NULL,
                CONSTRAINT DataQualityCheckIssue_pk PRIMARY KEY (DataQualityCheckIssueID)
);

CREATE TABLE PUBLIC.Process (
                ProcessID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                ProjectID INTEGER NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Description VARCHAR(1024),
                Version VARCHAR(64),
                Source VARCHAR(256),
                CreatedBy VARCHAR(256) NOT NULL,
                LastEditedBy VARCHAR(256),
                GraphXml VARCHAR(65536),
                CONSTRAINT Process_pk PRIMARY KEY (ProcessID)
);

CREATE TABLE PUBLIC.ProcessRelationship (
                ProcessRelationshipID IDENTITY NOT NULL,
                ProcessID INTEGER NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                ManagementClass VARCHAR(64),
                CONSTRAINT ProcessRelationship_pk PRIMARY KEY (ProcessRelationshipID)
);

CREATE TABLE PUBLIC.ProcessStep (
                ProcessStepID IDENTITY NOT NULL,
                ProcessID INTEGER NOT NULL,
                Name VARCHAR(256) NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Description VARCHAR(1024),
                Type VARCHAR(64) NOT NULL,
                GraphCellId INTEGER NOT NULL,
                CONSTRAINT ProcessStep_pk PRIMARY KEY (ProcessStepID)
);

CREATE TABLE PUBLIC.ProcessStepRelationship (
                ProcessStepRelationshipID IDENTITY NOT NULL,
                ProcessStepID INTEGER NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                ManagementClass VARCHAR(64),
                CONSTRAINT ProcessStepRelationship_pk PRIMARY KEY (ProcessStepRelationshipID)
);

CREATE TABLE PUBLIC.Effect (
                EffectID IDENTITY NOT NULL,
                ProjectID INTEGER NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Name VARCHAR(256) NOT NULL,
                Description VARCHAR(1024),
                Type VARCHAR(256) NOT NULL,
                GraphCellId INTEGER DEFAULT -1 NOT NULL,
                GroupingType VARCHAR(256) NOT NULL,
                CONSTRAINT Effect_pk PRIMARY KEY (EffectID)
);

CREATE TABLE PUBLIC.EffectRelationship (
                EffectRelationshipID IDENTITY NOT NULL,
                EffectID INTEGER NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                ManagementClass VARCHAR(64),
                CONSTRAINT EffectRelationship_pk PRIMARY KEY (EffectRelationshipID)
);

CREATE TABLE PUBLIC.Cause (
                CauseID IDENTITY NOT NULL,
                Description VARCHAR(1024),
                ProjectID INTEGER NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                Name VARCHAR(256) NOT NULL,
                GraphCellId INTEGER DEFAULT -1 NOT NULL,
                GroupingType VARCHAR(256) NOT NULL,
                CONSTRAINT Cause_pk PRIMARY KEY (CauseID)
);

CREATE TABLE PUBLIC.CauseRelationship (
                CauseRelationshipID IDENTITY NOT NULL,
                CauseID INTEGER NOT NULL,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                ManagementClass VARCHAR(64),
                CONSTRAINT CauseRelationship_pk PRIMARY KEY (CauseRelationshipID)
);

CREATE TABLE PUBLIC.Control (
                ControlID IDENTITY NOT NULL,
                Name VARCHAR(256) NOT NULL,
                ClinicalJustification VARCHAR(1024),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                ProjectID INTEGER NOT NULL,
                Description VARCHAR(1024),
                Type VARCHAR(64) NOT NULL,
                State VARCHAR(64) NOT NULL,
                GraphCellId INTEGER DEFAULT -1 NOT NULL,
                GroupingType VARCHAR(256) NOT NULL,
                Evidence VARCHAR(1024),
                CONSTRAINT Control_pk PRIMARY KEY (ControlID)
);

CREATE TABLE PUBLIC.ControlRelationship (
                ControlRelationshipID IDENTITY NOT NULL,
                ControlID INTEGER NOT NULL,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                ManagementClass VARCHAR(64),
                CONSTRAINT ControlRelationship_pk PRIMARY KEY (ControlRelationshipID)
);

CREATE TABLE PUBLIC.Hazard (
                HazardID IDENTITY NOT NULL,
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                InitialSeverity INTEGER DEFAULT -1 NOT NULL,
                InitialLikelihood INTEGER DEFAULT -1 NOT NULL,
                ResidualSeverity INTEGER DEFAULT -1 NOT NULL,
                ResidualLikelihood INTEGER DEFAULT -1 NOT NULL,
                InitialRiskRating INTEGER DEFAULT -1 NOT NULL,
                ResidualRiskRating INTEGER DEFAULT -1 NOT NULL,
                Name VARCHAR(256),
                ClinicalJustification VARCHAR(1024),
                Description VARCHAR(1024),
                ProjectID INTEGER NOT NULL,
                Status VARCHAR(64) NOT NULL,
                GraphXml VARCHAR(65536) DEFAULT null,
                GraphCellId INTEGER DEFAULT -1 NOT NULL,
                GroupingType VARCHAR(256) NOT NULL,
                CONSTRAINT Hazard_pk PRIMARY KEY (HazardID)
);

CREATE TABLE PUBLIC.HazardRelationship (
                HazardRelationshipID IDENTITY NOT NULL,
                HazardID INTEGER NOT NULL,
                RelatedObjectID INTEGER NOT NULL,
                RelatedObjectType VARCHAR(256) NOT NULL,
                Comment VARCHAR(1024),
                CreatedDate DATE NOT NULL,
                LastUpdatedDate DATE,
                DeletedDate DATE,
                ManagementClass VARCHAR(64),
                CONSTRAINT HazardRelationship_pk PRIMARY KEY (HazardRelationshipID)
);

ALTER TABLE PUBLIC.Library ADD CONSTRAINT LibraryType_Library_fk
FOREIGN KEY (ObjectType)
REFERENCES PUBLIC.LibraryType (ObjectType)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.LibraryAttribute ADD CONSTRAINT Library_LibraryAttribute_fk
FOREIGN KEY (LibraryID)
REFERENCES PUBLIC.Library (LibraryID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ProcessStep ADD CONSTRAINT ProcessStepType_ProcessStep_fk
FOREIGN KEY (Type)
REFERENCES PUBLIC.ProcessStepType (Type)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Effect ADD CONSTRAINT EffectType_Effect_fk
FOREIGN KEY (Type)
REFERENCES PUBLIC.EffectType (Type)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.DataQualityCheckIssue ADD CONSTRAINT DataQualityIssueResolutionType_DataQualityCheckIssue_fk
FOREIGN KEY (ResolutionType)
REFERENCES PUBLIC.DataQualityIssueResolutionType (ResolutionType)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.DataQualityCheckRun ADD CONSTRAINT DataQualityCheck_DataQualityCheckRun_fk
FOREIGN KEY (DataQualityCheckID)
REFERENCES PUBLIC.DataQualityCheck (DataQualityCheckID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Hazard ADD CONSTRAINT HazardStatus_Hazard_fk
FOREIGN KEY (Status)
REFERENCES PUBLIC.HazardStatus (Status)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Control ADD CONSTRAINT ControlState_Control_fk
FOREIGN KEY (State)
REFERENCES PUBLIC.ControlState (State)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Control ADD CONSTRAINT ControlType_Control_fk
FOREIGN KEY (Type)
REFERENCES PUBLIC.ControlType (Type)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Hazard ADD CONSTRAINT Project_Hazard_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Control ADD CONSTRAINT Project_Control_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Cause ADD CONSTRAINT Project_Cause_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Process ADD CONSTRAINT Project_Process_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Effect ADD CONSTRAINT Project_Effect_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.DataQualityCheckRun ADD CONSTRAINT Project_DataQualityCheckRun_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.System ADD CONSTRAINT Project_System_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Location ADD CONSTRAINT Project_Location_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Role ADD CONSTRAINT Project_Role_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.Report ADD CONSTRAINT Project_Report_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.SystemFunction ADD CONSTRAINT Project_SystemFunction_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.IssuesLog ADD CONSTRAINT Project_IssuesLog_fk
FOREIGN KEY (ProjectID)
REFERENCES PUBLIC.Project (ProjectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.IssuesLogRelationship ADD CONSTRAINT IssuesLog_IssuesLogRelationship_fk
FOREIGN KEY (IssuesLogID)
REFERENCES PUBLIC.IssuesLog (IssuesLogID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.SystemFunctionRelationship ADD CONSTRAINT SystemFunction_SystemFunctionRelationship_fk
FOREIGN KEY (SystemFunctionID)
REFERENCES PUBLIC.SystemFunction (SystemFunctionID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.SystemRelationship ADD CONSTRAINT System_SystemRelationship_fk
FOREIGN KEY (SystemID)
REFERENCES PUBLIC.System (SystemID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.DataQualityCheckIssue ADD CONSTRAINT DataQualityCheckRun_DataQualityCheckIssue_fk
FOREIGN KEY (DataQualityCheckRunID)
REFERENCES PUBLIC.DataQualityCheckRun (DataQualityCheckRunID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ProcessStep ADD CONSTRAINT Process_ProcessStep_fk
FOREIGN KEY (ProcessID)
REFERENCES PUBLIC.Process (ProcessID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ProcessRelationship ADD CONSTRAINT Process_ProcessRelationship_fk
FOREIGN KEY (ProcessID)
REFERENCES PUBLIC.Process (ProcessID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ProcessStepRelationship ADD CONSTRAINT ProcessStep_ProcessStepRelationship_fk
FOREIGN KEY (ProcessStepID)
REFERENCES PUBLIC.ProcessStep (ProcessStepID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.EffectRelationship ADD CONSTRAINT Effect_EffectRelationship_fk
FOREIGN KEY (EffectID)
REFERENCES PUBLIC.Effect (EffectID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.CauseRelationship ADD CONSTRAINT Cause_CauseRelationship_fk
FOREIGN KEY (CauseID)
REFERENCES PUBLIC.Cause (CauseID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ControlRelationship ADD CONSTRAINT Control_ControlRelationship_fk
FOREIGN KEY (ControlID)
REFERENCES PUBLIC.Control (ControlID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.HazardRelationship ADD CONSTRAINT Hazard_HazardRelationship_fk
FOREIGN KEY (HazardID)
REFERENCES PUBLIC.Hazard (HazardID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;