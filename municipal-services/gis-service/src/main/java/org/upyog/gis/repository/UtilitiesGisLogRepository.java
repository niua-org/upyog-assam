package org.upyog.gis.repository;

import org.upyog.gis.entity.UtilitiesGisLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UtilitiesGisLog entity
 */
@Repository
public interface UtilitiesGisLogRepository extends JpaRepository<UtilitiesGisLog, Long> {

}
