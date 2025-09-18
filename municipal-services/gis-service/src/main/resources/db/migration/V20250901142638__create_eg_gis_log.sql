-- Create gis_log table for logging GIS processing operations
CREATE TABLE IF NOT EXISTS eg_gis_log (
    id BIGSERIAL PRIMARY KEY,
    application_no VARCHAR(128),
    rtpi_id VARCHAR(128),
    file_store_id VARCHAR(512) NOT NULL,
    tenant_id VARCHAR(128),
    status VARCHAR(50) NOT NULL,
    output TEXT,
    audit_created_by VARCHAR(128),
    audit_created_time TIMESTAMPTZ NOT NULL DEFAULT now(),
    details JSONB
);

-- Create index on file_store_id for efficient lookups
CREATE INDEX IF NOT EXISTS idx_gis_log_file_store_id ON eg_gis_log(file_store_id);

-- Create index on tenant_id for tenant-based queries
CREATE INDEX IF NOT EXISTS idx_gis_log_tenant_id ON eg_gis_log(tenant_id);

-- Create index on audit_created_time for time-based queries
CREATE INDEX IF NOT EXISTS idx_gis_log_created_time ON eg_gis_log(audit_created_time);
