package org.upyog.gis.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;

/**
 * Entity for logging GIS processing operations
 */
@Entity
@Table(name = "eg_gis_log")
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

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "audit_created_by", length = 128)
    private String auditCreatedBy;

    @Column(name = "audit_created_time", nullable = false, columnDefinition = "TIMESTAMPTZ")
    @Builder.Default
    private OffsetDateTime auditCreatedTime = OffsetDateTime.now();

    @Type(JsonType.class)
    @Column(name = "details", columnDefinition = "jsonb")
    private JsonNode details;
}
