# GIS Microservice

A Spring Boot microservice that provides GIS functionality for processing KML files and querying WFS (Web Feature Service) endpoints to extract district and zone information.

## Overview

This microservice accepts KML file uploads, extracts polygon geometries, queries a WFS service for intersecting features, and returns district/zone information along with the raw WFS response. The service includes robust error handling with retry logic and fallback mechanisms for unreliable WFS endpoints.

## Features

- **KML Processing**: Parses KML files and extracts polygon coordinates using JTS geometry library
- **WFS Integration**: Queries Web Feature Service endpoints for spatial intersections with CQL filters
- **Filestore Integration**: Stores uploaded KML files in the core filestore service
- **Audit Logging**: Comprehensive logging of all operations to PostgreSQL with JSONB details
- **Retry Logic**: Automatic retry with axis swapping for WFS calls that return empty results
- **Fallback Mechanism**: Returns structured fallback data when WFS service is unavailable
- **Multi-tenant Support**: Optional tenant ID support for multi-tenant deployments

## API Endpoints

### POST `/gis-service/process-polygon`

Process a polygon file and return district/zone information.

**Request:**
- **Content-Type**: `multipart/form-data`
- **Parameters**:
  - `file` (required): KML file to process
  - `tenantId` (optional): Tenant identifier
  - `applicationNo` (optional): Application number for tracking
  - `rtpiId` (optional): RTPI identifier

**Response:**
```json
{
  "district": "New Jersey",
  "zone": "NJ",
  "wfsResponse": {
    "type": "FeatureCollection",
    "totalFeatures": 1,
    "numberMatched": 1,
    "numberReturned": 1,
    "features": [
      {
        "type": "Feature",
        "id": "states.1",
        "properties": {
          "STATE_NAME": "New Jersey",
          "STATE_ABBR": "NJ",
          "STATE_FIPS": "34",
          "SUB_REGION": "Mid Atl",
          "LAND_KM": 19047.34,
          "WATER_KM": 3544.243,
          "PERSONS": 7730188
        },
        "geometry": {
          "type": "MultiPolygon",
          "coordinatesIncluded": false,
          "note": "Geometry coordinates excluded for brevity"
        }
      }
    ]
  },
  "fileStoreId": "actual-filestore-id-from-service"
}
```

**Example cURL:**
```bash
curl -F "file=@polygon.kml" \
     -F "tenantId=tenant1" \
     -F "applicationNo=APP123" \
     -F "rtpiId=RTPI456" \
     http://localhost:8081/gis-service/process-polygon
```

## Configuration

### Application Properties

```properties
# GIS Service Configuration
gis.wms-url=https://gistcpservices.assam.gov.in/api/v1/geoservices/wms/assamtcpo/Assam_Zonning58?request=GetCapabilities
gis.wfs-url=https://ahocevar.com/geoserver/wfs
gis.wfs-type-name=topp:states
gis.wfs-geometry-column=the_geom
gis.wfs-district-attribute=STATE_NAME
gis.wfs-zone-attribute=STATE_ABBR

# Filestore Configuration
gis.filestore-url=http://localhost:8083/filestore/v1/files

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/master_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.user=postgres
spring.flyway.password=root
spring.flyway.url=jdbc:postgresql://localhost:5432/master_db

# Server Configuration
server.port=8081

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Configuration
logging.level.org.upyog.gis=INFO
```

### Configuration Properties

- **gis.wms-url**: WMS GetCapabilities URL for reference (read-only)
- **gis.wfs-url**: WFS endpoint URL for feature queries
- **gis.wfs-type-name**: Feature type name for WFS queries (e.g., `topp:states`)
- **gis.wfs-geometry-column**: Geometry column name in WFS layer (default: `the_geom`)
- **gis.wfs-district-attribute**: Attribute name for district information (e.g., `STATE_NAME`)
- **gis.wfs-zone-attribute**: Attribute name for zone information (e.g., `STATE_ABBR`)
- **gis.filestore-url**: Complete URL to filestore service endpoint

## Database Schema

The service uses a PostgreSQL database with the following audit table:

### gis_log

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| file_store_id | VARCHAR(512) | Reference to stored file |
| tenant_id | VARCHAR(128) | Tenant identifier |
| application_no | VARCHAR(128) | Application number for tracking |
| rtpi_id | VARCHAR(128) | RTPI identifier |
| status | VARCHAR(50) | Processing status (PENDING/SUCCESS/FAILURE) |
| output | TEXT | JSON response output |
| audit_created_by | VARCHAR(128) | User who initiated the request |
| audit_created_time | TIMESTAMPTZ | Request timestamp |
| details | JSONB | Additional processing details |

## Architecture

### Components

1. **GisController**: REST endpoint for polygon processing
2. **GisService**: Business logic for KML processing and WFS integration
3. **FilestoreClient**: Interface for file storage operations
4. **WfsClient**: Interface for WFS operations with retry logic
5. **KmlParser**: Utility for parsing KML files and extracting geometries
6. **GisLog**: JPA entity for audit logging

### WFS Client Implementation

- **HttpWfsClient**: HTTP-based client using WebClient with:
  - CQL filter support for spatial queries
  - Automatic retry with axis swapping for empty results
  - Fallback mechanism for service unavailability
  - Proper URI encoding to prevent double encoding issues

### Filestore Client Implementation

- **RestFilestoreClient**: REST client for production use, integrates with egov-filestore service
  - Flexible JSON response parsing for different filestore response formats
  - Comprehensive error handling and logging

## Processing Flow

1. **File Upload**: KML file is uploaded via multipart form data
2. **Validation**: File type (.kml/.xml), size (max 10MB), and content validation
3. **Filestore Upload**: File is stored in the core filestore service
4. **Audit Logging**: Initial log entry created with PENDING status
5. **KML Parsing**: Polygon coordinates extracted from KML using JTS
6. **WFS Query**: Spatial intersection query performed against WFS with CQL filter
7. **Retry Logic**: If empty results, retry with swapped coordinate axes
8. **Fallback**: If WFS fails, return structured fallback response
9. **Response Processing**: District/zone information extracted from WFS response
10. **Response Filtering**: Large geometry coordinates excluded from response for brevity
11. **Audit Update**: Log entry updated with SUCCESS/FAILURE status
12. **Response**: JSON response returned to client

## Error Handling

- **File Validation**: Invalid file types or sizes return 400 Bad Request
- **WFS Failures**: Automatic retry with axis swapping, then fallback to mock data
- **Timeout Handling**: WebClient configured with 10-second timeout
- **Processing Errors**: All errors are logged and return appropriate HTTP status codes
- **Audit Trail**: All operations are logged regardless of success/failure

## WFS Query Details

The service constructs CQL (Common Query Language) filters for spatial queries:

```sql
INTERSECTS(the_geom, POLYGON((-75.2 39.6, -74.6 39.6, -74.6 40.1, -75.2 40.1, -75.2 39.6)))
```

**Query Parameters:**
- `service=WFS`
- `version=2.0.0`
- `request=GetFeature`
- `typeName=topp:states`
- `outputFormat=application/json`
- `srsName=EPSG:4326`
- `cql_filter=INTERSECTS(...)`

## Dependencies

- **Spring Boot 3.2.0**: Main framework
- **Spring WebFlux**: Reactive HTTP client for WFS calls
- **Spring Data JPA**: Database operations
- **JTS 1.19.0**: Geometry operations and WKT handling
- **PostgreSQL**: Database with JSONB support
- **Flyway**: Database migrations
- **Lombok**: Code generation
- **Jackson**: JSON processing
- **Hypersistence Utils**: Enhanced Hibernate support

## Development

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Access to filestore service

### Running the Application

1. Configure database connection in `application.properties`
2. Run Flyway migrations: `mvn flyway:migrate`
3. Start the application: `mvn spring-boot:run`
4. Test with sample KML file: `curl -F "file=@test-polygon.kml" http://localhost:8081/gis-service/process-polygon`

### Sample KML File

```xml
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
    <name>Test Polygon</name>
    <Placemark>
      <name>Test polygon for WFS intersect</name>
      <Polygon>
        <outerBoundaryIs>
          <LinearRing>
            <coordinates>
              -75.2,39.6 -74.6,39.6 -74.6,40.1 -75.2,40.1 -75.2,39.6
            </coordinates>
          </LinearRing>
        </outerBoundaryIs>
      </Polygon>
    </Placemark>
  </Document>
</kml>
```