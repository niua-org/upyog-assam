package org.upyog.gis.repository;

import org.upyog.gis.entity.GisLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for GisLog entity
 */
@Repository
public interface GisLogRepository extends JpaRepository<GisLog, Long> {
}
