package org.upyog.gis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for GIS service
 */
@Data
@Component
@ConfigurationProperties(prefix = "gis")
public class GisProperties {

    private String wmsUrl;
    private String wfsUrl;
    private String wfsTypeName = "topp:states";
    private String wfsGeometryColumn = "the_geom";
    private String wfsDistrictAttribute = "STATE_NAME";
    private String wfsZoneAttribute = "STATE_ABBR";
    private String filestoreUrl;
}
