
/*
 * UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.service.MDMSCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;

@Service
public class RoofSlope extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(RoofSlope.class);

    @Autowired
    private MDMSCacheManager cache;

    @Override
    public Plan validate(Plan pl) {
        LOG.info("Validating plan for RoofSlope feature...");
        return pl; // No pre-validation logic yet
    }

    @Override
    public Plan process(Plan pl) {
        LOG.info("Starting RoofSlope processing for plan");

        ScrutinyDetail scrutinyDetail = createScrutinyDetail();
        Map<String, String> details = initializeResultDetails();

        BigDecimal permissibleSlope = getPermissibleSlopeValue(pl);
        LOG.info("Fetched permissible roof slope from MDMS: {}", permissibleSlope);

        for (Block block : pl.getBlocks()) {
            LOG.info("Processing roof slope for Block {}", block.getNumber());
            processBlockForRoofSlope(pl, block, permissibleSlope, scrutinyDetail, new HashMap<>(details));
        }

        LOG.info("Completed RoofSlope processing for plan");
        return pl;
    }

    private ScrutinyDetail createScrutinyDetail() {
        LOG.info("Creating scrutiny detail for RoofSlope feature...");
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey(COMMON_ROOF_SLOPE);
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, VERIFIED);
        scrutinyDetail.addColumnHeading(4, ACTION);
        scrutinyDetail.addColumnHeading(5, STATUS);
        return scrutinyDetail;
    }

    private Map<String, String> initializeResultDetails() {
        LOG.info("Initializing result details for RoofSlope feature...");
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_50); 
        return details;
    }

    private BigDecimal getPermissibleSlopeValue(Plan pl) {
        LOG.info("Fetching permissible slope value from MDMS for plan");
        List<Object> rules = cache.getFeatureRules(pl, FeatureEnum.ROOF_SLOPE.toString(), false);
        Optional<MdmsFeatureRule> matchedRule = rules.stream()
                .map(obj -> (MdmsFeatureRule) obj)
                .findFirst();

        return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
    }

    private void processBlockForRoofSlope(Plan pl, Block block, BigDecimal permissibleSlope,
                                          ScrutinyDetail scrutinyDetail, Map<String, String> details) {
        if (block.getRoofSlopes() != null && !block.getRoofSlopes().isEmpty()) {
            BigDecimal maxSlope = block.getRoofSlopes().stream().reduce(BigDecimal::max).get();
            LOG.info("Block {} - Maximum provided roof slope: {}", block.getNumber(), maxSlope);

            details.put(DESCRIPTION, ROOFSLOPE_DESCRIPTION);
            details.put(VERIFIED, ROOFSLOPE_VERIFIED + permissibleSlope + DEGREE);

            if (maxSlope.compareTo(permissibleSlope) <= 0) {
                details.put(ACTION, ROOFSLOPE_ACTION_WITHIN + maxSlope + DEGREE + " is within limits");
                details.put(STATUS, Result.Accepted.getResultVal());
                LOG.info("Block {} - Roof slope {}° is within permissible limit ({}°).",
                        block.getNumber(), maxSlope, permissibleSlope);
            } else {
                details.put(ACTION, ROOFSLOPE_ACTION_EXCEEDS + maxSlope + DEGREE);
                details.put(STATUS, Result.Verify.getResultVal());
                LOG.warn("Block {} - Roof slope {}° exceeds permissible limit ({}°).",
                        block.getNumber(), maxSlope, permissibleSlope);
            }

            scrutinyDetail.getDetail().add(details);
            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        } else {
            LOG.info("No roof slope defined for Block {}", block.getNumber());
        }
    }

    @Override
    public Map<String, Date> getAmendments() {
        LOG.info("Fetching amendments for RoofSlope feature...");
        return new LinkedHashMap<>();
    }
}
