package org.upyog.gis.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.time.Instant;


/**
 * Entity for logging GIS processing operations
 */
@Entity
@Table(name = "ug_gis_log")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GisLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_no", length = 128)
    private String applicationNo;

    @Column(name = "rtpi_id", length = 128)
    private String rtpiId;

    @Column(name = "file_store_id", nullable = false, length = 512)
    private String fileStoreId;

    @Column(name = "tenant_id", length = 128)
    private String tenantId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "response_status", length = 50)
    private String responseStatus;

    @Column(name = "response_json", columnDefinition = "TEXT")
    private String responseJson;

    @Column(name = "createdby", length = 128)
    private String createdby;

    @Column(name = "createdtime", nullable = false)
    @Builder.Default
    private Long createdtime = Instant.now().toEpochMilli();

    @Column(name = "lastmodifiedby", length = 128)
    private String lastmodifiedby;

    @Column(name = "lastmodifiedtime")
    private Long lastmodifiedtime;

    @Type(type = "jsonb")
    @Column(name = "details", columnDefinition = "jsonb")
    private JsonNode details;
}
