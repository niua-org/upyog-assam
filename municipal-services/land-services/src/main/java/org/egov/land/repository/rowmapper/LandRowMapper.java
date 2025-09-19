package org.egov.land.repository.rowmapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.land.web.models.Address;
import org.egov.land.web.models.AuditDetails;
import org.egov.land.web.models.Boundary;
import org.egov.land.web.models.Channel;
import org.egov.land.web.models.Document;
import org.egov.land.web.models.GeoLocation;
import org.egov.land.web.models.Institution;
import org.egov.land.web.models.LandInfo;
import org.egov.land.web.models.OwnerInfoV2;
import org.egov.land.web.models.Unit;
import org.egov.land.web.models.Relationship;
import org.egov.land.web.models.Source;
import org.egov.land.web.models.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class LandRowMapper implements ResultSetExtractor<List<LandInfo>> {

	@Override
	public List<LandInfo> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, LandInfo> landMap = new LinkedHashMap<>();

		while (rs.next()) {
			String id = rs.getString("land_id");
			if (id == null)
				continue;

			LandInfo currentLandInfo = landMap.get(id);
			String tenantId = rs.getString("landInfo_tenantId");

			if (currentLandInfo == null) {

				// created / lastModified times (safely handle SQL NULLs)
				Long createdTime = rs.getLong("landInfo_createdTime");
				if (rs.wasNull())
					createdTime = null;
				Long lastModifiedTime = rs.getLong("landInfo_lastModifiedTime");
				if (rs.wasNull())
					lastModifiedTime = null;

				// additional details JSON
				String additionalDetailsStr = rs.getString("additional_details");
				Object additionalDetails = null;
				if (additionalDetailsStr != null && !additionalDetailsStr.equals("{}") && !additionalDetailsStr.equals("null")) {
					additionalDetails = new Gson().fromJson(additionalDetailsStr, Object.class);
				}

				AuditDetails auditdetails = AuditDetails.builder()
						.createdBy(rs.getString("landInfo_createdBy"))
						.createdTime(createdTime)
						.lastModifiedBy(rs.getString("landInfo_lastModifiedBy"))
						.lastModifiedTime(lastModifiedTime)
						.build();

				// geolocation
				Double latitude = (Double) rs.getObject("latitude");
				Double longitude = (Double) rs.getObject("longitude");

				Boundary locality = Boundary.builder().code(rs.getString("locality")).build();

				GeoLocation geoLocation = GeoLocation.builder()
						.id(rs.getString("landInfo_geo_loc"))
						.latitude(latitude)
						.longitude(longitude)
						.build();

				// address (match fields used in your model)
				Address address = Address.builder()
						.houseNo(rs.getString("house_no"))
						.district(rs.getString("district"))
						.region(rs.getString("region"))
						.state(rs.getString("state"))
						.country(rs.getString("country"))
						.id(rs.getString("landInfo_ad_id"))
						.landmark(rs.getString("landmark"))
						.geoLocation(geoLocation)
						.pincode(rs.getString("pincode"))
						.tenantId(tenantId)
						.locality(locality)
						.build();

				// build LandInfo
				currentLandInfo = LandInfo.builder()
						.id(id)
						.landUId(rs.getString("land_uid"))
						.landUniqueRegNo(rs.getString("land_regno"))
						.tenantId(tenantId)
						.status(rs.getString("status") != null ? Status.fromValue(rs.getString("status")) : null)
						.address(address)
						.ownershipCategory(rs.getString("ownership_category"))
						.source(rs.getString("source") != null ? Source.fromValue(rs.getString("source")) : null)
						.channel(rs.getString("channel") != null ? Channel.fromValue(rs.getString("channel")) : null)
						.auditDetails(auditdetails)
						.additionalDetails(additionalDetails)
						.build();

				landMap.put(id, currentLandInfo);
			}

			addChildrenToProperty(rs, currentLandInfo);
		}

		return new ArrayList<>(landMap.values());
	}

	private void addChildrenToProperty(ResultSet rs, LandInfo landInfo) throws SQLException {

		String tenantId = landInfo.getTenantId();

		AuditDetails auditdetails = AuditDetails.builder()
				.createdBy(rs.getString("landInfo_createdBy"))
				.createdTime(rs.getLong("landInfo_createdTime"))
				.lastModifiedBy(rs.getString("landInfo_lastModifiedBy"))
				.lastModifiedTime(rs.getLong("landInfo_lastModifiedTime"))
				.build();

		// Unit
		String unitId = rs.getString("landInfo_un_id");
		if (unitId != null) {
			Long occupancyDate = null;
			long occ = rs.getLong("occupancy_date");
			if (!rs.wasNull())
				occupancyDate = occ;

			Unit unit = Unit.builder()
					.id(unitId)
					.floorNo(rs.getString("floor_no"))
					.unitType(rs.getString("unit_type"))
					.usageCategory(rs.getString("usage_category"))
					.occupancyType(rs.getString("occupancy_type") != null ? rs.getString("occupancy_type") : null)
					.occupancyDate(occupancyDate)
					.auditDetails(auditdetails)
					.tenantId(tenantId)
					.build();
			landInfo.addUnitItem(unit);
		}

		// Owner
		String ownerId = rs.getString("landInfoowner_id");
		if (ownerId != null) {
			Boolean isPrimaryOwner = (Boolean) rs.getObject("is_primary_owner");
			Boolean status = (Boolean) rs.getObject("ownerstatus");
			BigDecimal ownerShipPercentage = rs.getBigDecimal("ownership_percentage");

			OwnerInfoV2 owner = OwnerInfoV2.builder()
					.tenantId(tenantId)
					.ownerId(ownerId)
					.uuid(rs.getString("landInfoowner_uuid"))
					.isPrimaryOwner(isPrimaryOwner)
					.ownerShipPercentage(ownerShipPercentage)
					.institutionId(rs.getString("owner_institution_id"))
					.auditDetails(auditdetails)
					.status(status)
					// relationship column is not present in new schema; keep null
					.relationship(null)
					.build();
			landInfo.addOwnerItem(owner);
		}

		// Institution (one per landInfo in your query; last seen will overwrite, as before)
		if (rs.getString("land_inst_id") != null) {
			Institution institution = Institution.builder()
					.id(rs.getString("land_inst_id"))
					.type(rs.getString("land_inst_type"))
					.tenantId(tenantId)
					.designation(rs.getString("land_inst_designation"))
					.nameOfAuthorizedPerson(rs.getString("land_inst_name_of_authorized_person"))
					.build();
			landInfo.setInstitution(institution);
		}

		// Document
		String documentId = rs.getString("landInfo_doc_id");
		if (documentId != null) {
			Document document = Document.builder()
					.documentType(rs.getString("landInfo_doc_documenttype"))
					.fileStoreId(rs.getString("landInfo_doc_filestore"))
					.id(documentId)
					.documentUid(rs.getString("landInfo_doc_uid"))
					.auditDetails(auditdetails)
					.build();
			landInfo.addDocumentItem(document);
		}
	}
}
