-- ==============================================
-- MAIN TABLES
-- ==============================================

CREATE TABLE IF NOT EXISTS ug_land_info(
	id character varying(64),
	land_uid character varying(64),
	land_unique_reg_no character varying(64),
	tenant_id character varying(256) NOT NULL,
	status character varying(64),
	ownership_category character varying(64),
	source character varying(64),
	channel character varying(64),
	old_dag_no character varying(64),
	new_dag_no character varying(64),
	old_patta_no character varying(64),
	new_patta_no character varying(64),
	total_plot_area double precision,
	
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
	
	CONSTRAINT pk_ug_land_info PRIMARY KEY (id),
	
	additional_details JSONB
);

CREATE TABLE IF NOT EXISTS ug_land_address(
	id character varying(64),
	land_info_id character varying(64),
	tenant_id character varying(256) NOT NULL,
	house_no character varying(64),
	address_line_1 character varying(200),
	address_line_2 character varying(200),
	landmark character varying(64),
	locality character varying(64),
	district character varying(64),
	region character varying(64),
	state character varying(64),
	country character varying(64),
	pincode character varying(64),

	created_by character varying(64),
	last_modified_by character varying(64),
	created_time bigint,
	last_modified_time bigint,

	CONSTRAINT pk_ug_land_address PRIMARY KEY (id),
	CONSTRAINT fk_ug_land_address_land FOREIGN KEY (land_info_id) REFERENCES ug_land_info (id),

	additional_details JSONB
);


CREATE TABLE IF NOT EXISTS ug_land_geolocation(
	id character varying(64),
	latitude double precision,
	longitude double precision,
	address_id character varying(64),
	
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,

	CONSTRAINT pk_ug_land_geolocation PRIMARY KEY (id),
	CONSTRAINT fk_ug_land_geolocation FOREIGN KEY (address_id) REFERENCES ug_land_address (id),
	
	additional_details JSONB
);

CREATE TABLE IF NOT EXISTS ug_land_owner_info(
	id character varying(64),
	user_uuid character varying(64),
	tenant_id character varying(64),
	name character varying(100),
	mobile_number character varying(20),
	alt_mobile_number character varying(20),
	dob timestamp,
	email_id character varying(128),
	gender character varying(1),
	relationship_type character varying(30),
	guardian_name character varying(100),
	mother_name character varying(100),
	pan_number character varying(20),
	aadhaar_number character varying(20),
	is_primary_owner boolean,
	ownership_percentage double precision,
	owner_type character varying(64),
	institution_id character varying(64),
	land_info_id character varying(64),
	status boolean DEFAULT TRUE,
	created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
	
	CONSTRAINT pk_ug_land_owner_info PRIMARY KEY (id),
	CONSTRAINT uk_ug_land_owner_info_composite UNIQUE (id, land_info_id),
	CONSTRAINT fk_ug_land_owner_info FOREIGN KEY (land_info_id) REFERENCES ug_land_info (id),
	
	additional_details JSONB
);

CREATE TABLE IF NOT EXISTS ug_land_owner_address(
	id character varying(64),
	owner_info_id character varying(64),
	tenant_id character varying(256) NOT NULL,
	house_no character varying(64),
	address_line_1 character varying(200),
	address_line_2 character varying(200),
	landmark character varying(64),
	locality character varying(64),
	district character varying(64),
	region character varying(64),
	state character varying(64),
	country character varying(64),
	pincode character varying(64),
	address_type character varying(64),

	created_by character varying(64),
	last_modified_by character varying(64),
	created_time bigint,
	last_modified_time bigint,

	CONSTRAINT pk_ug_land_owner_address PRIMARY KEY (id),
	CONSTRAINT fk_ug_land_owner_address FOREIGN KEY (owner_info_id) REFERENCES ug_land_owner_info (id),

	additional_details JSONB
);

CREATE TABLE IF NOT EXISTS ug_land_institution(
	id character varying(64),
	tenant_id character varying(256),
	type character varying(64),
	designation character varying(256),
	name_of_authorized_person character varying(256),
	land_info_id character varying(64),

    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
	CONSTRAINT pk_ug_land_institution PRIMARY KEY (id),
	CONSTRAINT fk_ug_land_institution FOREIGN KEY (land_info_id) REFERENCES ug_land_info (id),
	
	additional_details JSONB
);

CREATE TABLE IF NOT EXISTS ug_land_document(
	id character varying(64),
	document_type character varying(256),
	file_store_id character varying(256),
	document_uid character varying(256),
	land_info_id character varying(64),
	
	created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,

	CONSTRAINT pk_ug_land_document PRIMARY KEY (id),
	CONSTRAINT uk_ug_land_document_composite UNIQUE (id, land_info_id),
	CONSTRAINT fk_ug_land_document FOREIGN KEY (land_info_id) REFERENCES ug_land_info (id),
	
	additional_details JSONB
);

CREATE TABLE IF NOT EXISTS ug_land_unit(
	id character varying(64),
	tenant_id character varying(256),
	floor_no character varying(64),	
	unit_type character varying(256),
	usage_category character varying(64),
	occupancy_type character varying(64),
	occupancy_date bigint,
	land_info_id character varying(64),
	
	created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
		
	CONSTRAINT pk_ug_land_unit PRIMARY KEY (id),
	CONSTRAINT uk_ug_land_unit_composite UNIQUE (id, land_info_id, tenant_id),
	CONSTRAINT fk_ug_land_unit FOREIGN KEY (land_info_id) REFERENCES ug_land_info (id),
	
	additional_details JSONB
);

-- ==============================================
-- AUDIT TABLES
-- ==============================================

-- Drop existing audit tables to recreate with correct structure
DROP TABLE IF EXISTS ug_land_audit_details;
DROP TABLE IF EXISTS ug_land_owner_audit_details;
DROP TABLE IF EXISTS ug_land_institution_audit_details;
DROP TABLE IF EXISTS ug_land_unit_audit_details;
DROP TABLE IF EXISTS ug_land_address_audit_details;
DROP TABLE IF EXISTS ug_land_geolocation_audit_details;
DROP TABLE IF EXISTS ug_land_document_audit_details;

-- Audit table for ug_land_info
CREATE TABLE IF NOT EXISTS ug_land_audit_details(
    id character varying(64),
    land_uid character varying(64),
    land_unique_reg_no character varying(64),
    tenant_id character varying(256) NOT NULL,
    status character varying(64),
    ownership_category character varying(64),
    source character varying(64),
    channel character varying(64),
    old_dag_no character varying(64),
    new_dag_no character varying(64),
    old_patta_no character varying(64),
    new_patta_no character varying(64),
    total_plot_area double precision,
    
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
    additional_details JSONB
);

-- Audit table for ug_land_owner_info
CREATE TABLE IF NOT EXISTS ug_land_owner_audit_details(
   id character varying(64),
   	user_uuid character varying(64),
   	tenant_id character varying(64),
   	name character varying(100),
   	mobile_number character varying(20),
   	alt_mobile_number character varying(20),
   	dob timestamp,
   	email_id character varying(128),
   	gender character varying(1),
   	relationship_type character varying(30),
   	guardian_name character varying(100),
   	mother_name character varying(100),
   	pan_number character varying(20),
   	aadhaar_number character varying(20),
   	is_primary_owner boolean,
   	ownership_percentage double precision,
   	owner_type character varying(64),
   	institution_id character varying(64),
   	land_info_id character varying(64),
   	status boolean DEFAULT TRUE,
   	created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
    additional_details JSONB
);

-- Audit table for ug_land_institution
CREATE TABLE IF NOT EXISTS ug_land_institution_audit_details(
    id character varying(64),
    tenant_id character varying(256),
    type character varying(64),
    designation character varying(256),
    name_of_authorized_person character varying(256),
    land_info_id character varying(64),
    
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
    additional_details JSONB
);

-- Audit table for ug_land_unit
CREATE TABLE IF NOT EXISTS ug_land_unit_audit_details(
    id character varying(64),
    tenant_id character varying(256),
    floor_no character varying(64),    
    unit_type character varying(256),
    usage_category character varying(64),
    occupancy_type character varying(64),
    occupancy_date bigint,
    land_info_id character varying(64),
    
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
    additional_details JSONB
);

-- Audit table for ug_land_address
CREATE TABLE IF NOT EXISTS ug_land_address_audit_details(
    id character varying(64),
    land_info_id character varying(64),
    tenant_id character varying(256) NOT NULL,
    house_no character varying(64),
    address_line_1 character varying(200),
    address_line_2 character varying(200),
    landmark character varying(64),
    locality character varying(64),
    district character varying(64),
    region character varying(64),
    state character varying(64),
    country character varying(64),
    pincode character varying(64),

    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
    additional_details JSONB
);

-- Audit table for ug_land_geolocation
CREATE TABLE IF NOT EXISTS ug_land_geolocation_audit_details(
    id character varying(64),
    latitude double precision,
    longitude double precision,
    address_id character varying(64),
    
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
    additional_details JSONB
);

-- Audit table for ug_land_document
CREATE TABLE IF NOT EXISTS ug_land_document_audit_details(
    id character varying(64),
    document_type character varying(256),
    file_store_id character varying(256),
    document_uid character varying(256),
    land_info_id character varying(64),
    
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,
    
    additional_details JSONB
);

-- ==============================================
-- INDEXES
-- ==============================================

CREATE INDEX  IF NOT EXISTS land_index ON ug_land_info
(
    id,
    tenant_id,
    land_uid
);

CREATE INDEX  IF NOT EXISTS land_owner_index ON ug_land_owner_info
(
    id,
    land_info_id
);

CREATE INDEX  IF NOT EXISTS  land_address_index ON ug_land_address
(
    id,
    land_info_id,
    tenant_id
);

CREATE INDEX  IF NOT EXISTS land_unit_index ON ug_land_unit
(
    id,
    land_info_id,
    tenant_id
);

CREATE INDEX  IF NOT EXISTS land_institution_index ON ug_land_institution
(
    id,
    land_info_id,
    tenant_id
);

CREATE INDEX  IF NOT EXISTS land_document_index ON ug_land_document
(
    id,
    land_info_id
);

CREATE INDEX  IF NOT EXISTS land_geolocation_index ON ug_land_geolocation
(
    id,
    address_id
);
