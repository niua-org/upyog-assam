package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.LiquidWasteTreatementPlant;
import org.egov.common.entity.edcr.Measurement;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.entity.blackbox.PlotDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLWPolyline;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFText;
import org.kabeja.dxf.helpers.StyledTextParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WaterTreatmentPlantExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(WaterTreatmentPlantExtract.class);
    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail extract(PlanDetail pl) {
        if (LOG.isInfoEnabled())
            LOG.info("Starting of Water Treatment Plant Extract......");
        // Water treatement plant
        if (pl.getDoc().containsDXFLayer(layerNames.getLayerName("LAYER_NAME_INSITU_WASTE_TREATMENT_PLANT"))) {
            List<DXFLWPolyline> waterTreatementPlanPolyLines = Util.getPolyLinesByLayer(pl.getDoc(),
                    layerNames.getLayerName("LAYER_NAME_INSITU_WASTE_TREATMENT_PLANT"));
            if (waterTreatementPlanPolyLines != null && !waterTreatementPlanPolyLines.isEmpty())
                for (DXFLWPolyline pline : waterTreatementPlanPolyLines) {
                    Measurement measurement = new MeasurementDetail(pline, true);
                    LiquidWasteTreatementPlant liquidWaste = new LiquidWasteTreatementPlant();
                    liquidWaste.setArea(measurement.getArea());
                    liquidWaste.setColorCode(measurement.getColorCode());
                    liquidWaste.setHeight(measurement.getHeight());
                    liquidWaste.setWidth(measurement.getWidth());
                    liquidWaste.setLength(measurement.getLength());
                    liquidWaste.setInvalidReason(measurement.getInvalidReason());
                    liquidWaste.setPresentInDxf(true);
                    pl.getUtility().addLiquidWasteTreatementPlant(liquidWaste);
                }
        }
        extractInfo(pl);
        if (LOG.isInfoEnabled())
            LOG.info("End of Water Treatment Plant Extract......");
        return pl;
    }
    
    public Map<String, String> getFormatedProperties(DXFDocument doc) {

        DXFLayer waterlayer = doc.getDXFLayer(layerNames.getLayerName("LAYER_NAME_INSITU_WASTE_TREATMENT_PLANT"));
        List texts = waterlayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
        DXFText text = null;
        Map<String, String> waterProperties = new HashMap<>();

        if (texts != null && texts.size() > 0) {
            Iterator iterator = texts.iterator();
            while (iterator.hasNext()) {
                text = (DXFText) iterator.next();
                Iterator styledParagraphIterator = text.getTextDocument().getStyledParagraphIterator();
                while (styledParagraphIterator.hasNext()) {
                    StyledTextParagraph styledTextParagraph = (StyledTextParagraph) styledParagraphIterator.next();
                    String[] data = styledTextParagraph.getText().split("=");
                    LOG.info
(styledTextParagraph.getText());
                    if (data.length == 2)
                    	waterProperties.put(data[0].trim(), data[1].trim());
                }

            }
        }
        return waterProperties;
    }
    
    /**
     * Extracts additional water treatment plant information from the DXF document
     * and updates the {@link PlanDetail} with values like Settling Tank and Overhead Tank.
     * <p>
     * This method retrieves formatted properties from the DXF layer associated with
     * the water treatment plant. If valid values are found for specific keys 
     * (e.g., settling tank or overhead tank), they are stored in the 
     * {@link org.egov.edcr.entity.blackbox.Utility} object of the plan.
     * </p>
     *
     * @param pl the {@link PlanDetail} object containing the DXF document 
     *           and utility details to be updated
     */
    private void extractInfo(PlanDetail pl) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting extraction of additional water treatment plant info...");
        }

        Map<String, String> waterProperties = getFormatedProperties(pl.getDoc());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Extracted water treatment plant properties: {}", waterProperties);
        }

        String settlingTank = waterProperties.get(DxfFileConstants.SETTLING_TANK);
        if (StringUtils.isNotBlank(settlingTank)) {
            pl.getUtility().setSettlingTank(settlingTank);
            LOG.info("Settling Tank information extracted and set: {}", settlingTank);
        } else {
            LOG.warn("No valid Settling Tank information found in waterProperties.");
        }

        String overHeadTank = waterProperties.get(DxfFileConstants.OVERHEAD_TANK);
        if (StringUtils.isNotBlank(overHeadTank)) {
            pl.getUtility().setOverheadTank(overHeadTank);
            LOG.info("Overhead Tank information extracted and set: {}", overHeadTank);
        } else {
            LOG.warn("No valid Overhead Tank information found in waterProperties.");
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Completed extraction of additional water treatment plant info.");
        }
    }


    @Override
    public PlanDetail validate(PlanDetail pl) {
        return pl;
    }

}
