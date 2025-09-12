-- MAIN TABLES
SELECT * FROM ug_land_info LIMIT 10;
SELECT * FROM ug_land_address LIMIT 10;
SELECT * FROM ug_land_geolocation LIMIT 10;
SELECT * FROM ug_land_owner_info LIMIT 10;
SELECT * FROM ug_land_owner_address LIMIT 10;
SELECT * FROM ug_land_institution LIMIT 10;
SELECT * FROM ug_land_document LIMIT 10;
SELECT * FROM ug_land_unit LIMIT 10;

-- AUDIT TABLES
SELECT * FROM ug_land_audit_details LIMIT 10;
SELECT * FROM ug_land_owner_audit_details LIMIT 10;
SELECT * FROM ug_land_institution_audit_details LIMIT 10;
SELECT * FROM ug_land_unit_audit_details LIMIT 10;
SELECT * FROM ug_land_address_audit_details LIMIT 10;
SELECT * FROM ug_land_geolocation_audit_details LIMIT 10;
SELECT * FROM ug_land_document_audit_details LIMIT 10;



-- First drop children referencing ug_land_info
DROP TABLE IF EXISTS ug_land_unit CASCADE;
DROP TABLE IF EXISTS ug_land_document CASCADE;
DROP TABLE IF EXISTS ug_land_institution CASCADE;
DROP TABLE IF EXISTS ug_land_owner_info CASCADE;
DROP TABLE IF EXISTS ug_land_address CASCADE;
DROP TABLE IF EXISTS ug_land_geolocation CASCADE;
DROP TABLE IF EXISTS ug_land_owner_address CASCADE;

-- Then drop parent
DROP TABLE IF EXISTS ug_land_info CASCADE;

-- Audit tables (no FKs defined → order doesn’t matter)
DROP TABLE IF EXISTS ug_land_audit_details CASCADE;
DROP TABLE IF EXISTS ug_land_owner_audit_details CASCADE;
DROP TABLE IF EXISTS ug_land_institution_audit_details CASCADE;
DROP TABLE IF EXISTS ug_land_unit_audit_details CASCADE;
DROP TABLE IF EXISTS ug_land_address_audit_details CASCADE;
DROP TABLE IF EXISTS ug_land_geolocation_audit_details CASCADE;
DROP TABLE IF EXISTS ug_land_document_audit_details CASCADE;