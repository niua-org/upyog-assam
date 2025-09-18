package org.egov.land.repository.querybuilder;

import java.util.List;

import org.egov.land.config.LandConfiguration;
import org.egov.land.web.models.LandSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class LandQueryBuilder {

	@Autowired
	private LandConfiguration config;

	private static final String INNER_JOIN_STRING = " INNER JOIN ";
	private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

	private static final String QUERY = "SELECT "
			+ "landInfo.*, landInfoaddress.*, landInfoowner.*, landInfounit.*, landInfogeolocation.*, landInstitution.*, landInfodoc.*, "
			+ "landInfo.id as land_id, landInfo.tenant_id as landInfo_tenantId, "
			+ "landInfo.last_modified_time as landInfo_lastModifiedTime, landInfo.created_by as landInfo_createdBy, landInfo.last_modified_by as landInfo_lastModifiedBy, "
			+ "landInfo.created_time as landInfo_createdTime, landInfo.additional_details, "
			+ "landInfoaddress.id as landInfo_ad_id, landInfogeolocation.id as landInfo_geo_loc, "
			+ "landInfoowner.id as landInfoowner_id, landInfoowner.uuid as landInfoowner_uuid, landInfoowner.status as ownerstatus, landInfoowner.is_primary_owner as is_primary_owner, landInfoowner.ownership_percentage as ownership_percentage, landInfoowner.institution_id as owner_institution_id, "
			+ "landInfo.land_unique_reg_no as land_regno, "
			+ "landInstitution.type as land_inst_type, landInstitution.id as land_inst_id, landInstitution.designation as land_inst_designation, landInstitution.name_of_authorized_person as land_inst_name_of_authorized_person, "
			+ "landInfounit.id as landInfo_un_id, "
			+ "landInfodoc.id as landInfo_doc_id, landInfodoc.document_type as landInfo_doc_documenttype, landInfodoc.file_store_id as landInfo_doc_filestore, landInfodoc.document_uid as landInfo_doc_uid "
			+ "FROM {schema}.ug_land_info landInfo" + INNER_JOIN_STRING
			+ "{schema}.ug_land_address landInfoaddress ON landInfoaddress.land_info_id = landInfo.id" + LEFT_OUTER_JOIN_STRING
			+ "{schema}.ug_land_institution landInstitution ON landInstitution.land_info_id = landInfo.id" + INNER_JOIN_STRING
			+ "{schema}.ug_land_owner_info landInfoowner ON landInfoowner.land_info_id = landInfo.id AND landInfoowner.status = true " + LEFT_OUTER_JOIN_STRING
			+ "{schema}.ug_land_unit landInfounit ON landInfounit.land_info_id = landInfo.id" + LEFT_OUTER_JOIN_STRING
			+ "{schema}.ug_land_document landInfodoc ON landInfodoc.land_info_id = landInfo.id" + LEFT_OUTER_JOIN_STRING
			+ "{schema}.ug_land_geolocation landInfogeolocation ON landInfogeolocation.address_id = landInfoaddress.id";


	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY landInfo_lastModifiedTime DESC) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";


	/**
	 * To give the Search query based on the requirements.
	 * 
	 * @param criteria
	 *            landInfo search criteria
	 * @param preparedStmtList
	 *            values to be replaced on the query
	 * @return Final Search Query
	 */
	public String getLandInfoSearchQuery(LandSearchCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(QUERY);

		// Tenant filter
		if (criteria.getTenantId() != null) {
			if (criteria.getTenantId().split("\\.").length == 1) {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" landInfo.tenant_id LIKE ? ");
				preparedStmtList.add('%' + criteria.getTenantId() + '%');
			} else {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" landInfo.tenant_id = ? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		// IDs filter
		List<String> ids = criteria.getIds();
		if (!CollectionUtils.isEmpty(ids)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" landInfo.id IN (").append(createQuery(ids)).append(") ");
			addToPreparedStatement(preparedStmtList, ids);
		}

		// User IDs (Owner UUIDs)
		if (criteria.getUserIds() != null && !criteria.getUserIds().isEmpty()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" landInfoowner.uuid IN (").append(createQuery(criteria.getUserIds())).append(") ");
			addToPreparedStatement(preparedStmtList, criteria.getUserIds());
		}

		// Land UID
		if (criteria.getLandUId() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" landInfo.land_uid = ? ");
			preparedStmtList.add(criteria.getLandUId());
		}

		// Locality filter
		if (criteria.getLocality() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" landInfoaddress.locality = ? ");
			preparedStmtList.add(criteria.getLocality());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}



	/**
	 * 
	 * @param query
	 *            prepared Query
	 * @param preparedStmtList
	 *            values to be replaced on the query
	 * @param criteria
	 *            landInfo search criteria
	 * @return the query by replacing the placeholders with preparedStmtList
	 */
	private String addPaginationWrapper(String query, List<Object> preparedStmtList, LandSearchCriteria criteria) {

		int limit = config.getDefaultLimit();
		int offset = config.getDefaultOffset();
		String finalQuery = paginationWrapper.replace("{}", query);

		if(criteria.getLimit() == null && criteria.getOffset() == null) {
        	limit = config.getMaxSearchLimit();
        } 
		
		if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit()) {
			limit = config.getMaxSearchLimit();
		}

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		if (limit == -1) {
			finalQuery = finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
		} else {
			preparedStmtList.add(offset);
			preparedStmtList.add(limit + offset);
		}

		return finalQuery;

	}

	private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});

	}

	private Object createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}
}
