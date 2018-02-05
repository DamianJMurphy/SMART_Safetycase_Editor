
insert into project (CreatedDate, LastUpdatedDate, DeletedDate, Name, Description, Owner, Customer)
values (CURRENT_DATE, null, null, 'Development project 1', 'Project record to support application development', 'developer', 'developer');

insert into hazardstatus (Status, AddedDate, DeprecatedDate) values ('Work in progress', CURRENT_DATE, null);
insert into hazardstatus (Status, AddedDate, DeprecatedDate) values ('Controlled', CURRENT_DATE, null);
insert into hazardstatus (Status, AddedDate, DeprecatedDate) values ('Mitigated', CURRENT_DATE, null);
insert into hazardstatus (Status, AddedDate, DeprecatedDate) values ('Controlled and mitigated', CURRENT_DATE, null);
insert into hazardstatus (Status, AddedDate, DeprecatedDate) values ('Accepted', CURRENT_DATE, null);
insert into hazardstatus (Status, AddedDate, DeprecatedDate) values ('New', CURRENT_DATE, null);

insert into effecttype (Type, AddedDate, DeprecatedDate) values ('New', CURRENT_DATE, null);
insert into controltype (Type, AddedDate, DeprecatedDate) values ('New', CURRENT_DATE, null);
insert into controlstate (State, AddedDate, DeprecatedDate) values ('New', CURRENT_DATE, null);


insert into effecttype (Type, AddedDate, DeprecatedDate) values ('Test effect', CURRENT_DATE, null);

insert into controltype (Type, AddedDate, DeprecatedDate) values ('Test control', CURRENT_DATE, null);

insert into controlstate (State, AddedDate, DeprecatedDate) values ('Active', CURRENT_DATE, null);


insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('ProcessStep', 'Hazard', CURRENT_DATE, null, 'exposes', 'ProcessStep exposes Hazard');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Hazard', 'Location', CURRENT_DATE, null, 'at', 'Hazard at Location');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Hazard', 'Role', CURRENT_DATE, null, 'due to', 'Hazard due to Role');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Hazard', 'System', CURRENT_DATE, null, 'in', 'Hazard in System');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Hazard', 'SystemFunction', CURRENT_DATE, null, 'in', 'Hazard in SystemFunction');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Cause', 'Hazard', CURRENT_DATE, null, 'causes', 'Causes Hazard');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Cause', 'Cause', CURRENT_DATE, null, 'causes', 'Causes cause');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Effect', 'Hazard', CURRENT_DATE, null, 'of', 'Effect of Hazard');
insert into RelationshipSemantics (SourceObjectType, TargetObjectType, AddedDate, DeprecatedDate, Summary, Description) values ('Effect', 'Effect', CURRENT_DATE, null, 'of', 'Effect of Effect');

insert into ProcessStepType (Type, AddedDate, DeprecatedDate) values ('Start', CURRENT_DATE, null);
insert into ProcessStepType (Type, AddedDate, DeprecatedDate) values ('Activity', CURRENT_DATE, null);
insert into ProcessStepType (Type, AddedDate, DeprecatedDate) values ('Decision', CURRENT_DATE, null);
insert into ProcessStepType (Type, AddedDate, DeprecatedDate) values ('Stop', CURRENT_DATE, null);

insert into Location (Name, ParentLocationID, ProjectID, CreatedDate, LastUpdatedDate, DeletedDate, Description, Mnemonic)
	values ('Test Location 1', -1, 0, CURRENT_DATE, null, null, 'Test location for development', 'TST');
	
insert into Role (Name, ProjectID, Category, CreatedDate, LastUpdatedDate, DeletedDate, Description)
	values ('Test role 1', 0,'developer', CURRENT_DATE, null, null, 'Test role for development');
	
insert into system (Name, ParentSystemID, ProjectID, CreatedDate, LastUpdatedDate, DeletedDate, Description, Version, Mnemonic)
	values ('Test system 1', -1, 0, CURRENT_DATE, null, null, 'Test system for development', '0.00a', 'JOHNNY');

insert into system (Name, ParentSystemID, ProjectID, CreatedDate, LastUpdatedDate, DeletedDate, Description, Version, Mnemonic, GraphXml, GraphCellId)
	values ('Test subsystem 1a', 0, 0, CURRENT_DATE, null, null, 'Test subsystem for development', '0.00b', 'JOHNNY', null, -1);
	
insert into systemfunction (Name, SystemID, ParentSystemFunctionID, CreatedDate, LastUpdatedDate, DeletedDate, Description, GraphCellId)
	values ('Test system function 1', -1, CURRENT_DATE, null, null, 'Test system function for development', -1);


insert into dataqualityissueresolutiontype (ResolutionType, AddedDate, DeprecatedDate) values ('Development', CURRENT_DATE, null);	
insert into dataqualityissueresolutiontype (ResolutionType, AddedDate, DeprecatedDate) values ('Not resolved', CURRENT_DATE, null);	
insert into dataqualityissueresolutiontype (ResolutionType, AddedDate, DeprecatedDate) values ('Fixed', CURRENT_DATE, null);	
insert into dataqualityissueresolutiontype (ResolutionType, AddedDate, DeprecatedDate) values ('Work in progress', CURRENT_DATE, null);	
insert into dataqualityissueresolutiontype (ResolutionType, AddedDate, DeprecatedDate) values ('External risk', CURRENT_DATE, null);
insert into dataqualityissueresolutiontype (ResolutionType, AddedDate, DeprecatedDate) values ('Acceptable risk', CURRENT_DATE, null);
insert into dataqualityissueresolutiontype (ResolutionType, AddedDate, DeprecatedDate) values ('Pending other system developments', CURRENT_DATE, null);	

insert into Process (Name, ProjectID, CreatedDate, LastUpdatedDate, DeletedDate, Description, Version, Source, CreatedBy, LastEditedBy, GraphXml)
			values ('Dev Process 1', 0, CURRENT_DATE, null, null, 'Test and development process', '0.00a', 'Thin air', 'damian', 'damian', null)
			
			
      

insert into LibraryType (ObjectType) values ('Location')
insert into LibraryType (ObjectType) values ('Role')
insert into LibraryType (ObjectType) values ('Hazard')
insert into LibraryType (ObjectType) values ('Control')
insert into LibraryType (ObjectType) values ('Cause')
insert into LibraryType (ObjectType) values ('Effect')
insert into LibraryType (ObjectType) values ('Condition')
insert into LibraryType (ObjectType) values ('System')
