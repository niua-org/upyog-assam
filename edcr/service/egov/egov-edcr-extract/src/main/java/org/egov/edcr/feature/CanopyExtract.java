package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Canopy;
import org.egov.common.entity.edcr.Projections;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CanopyExtract extends FeatureExtract{
    private static final Logger LOG = LogManager.getLogger(CanopyExtract.class);
    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail extract(PlanDetail pl) {
        if (LOG.isDebugEnabled())
            LOG.debug("Starting of Canopy Extract......");

        for(Block bl: pl.getBlocks()){
            String canopyDetail = String.format(layerNames.getLayerName("LAYER_NAME_CANOPY_DETAIL"));
            List<DXFLWPolyline> canopypolyline = Util.getPolyLinesByLayer(pl.getDoc(), canopyDetail);
            List<Canopy> projectionMeasurements = canopypolyline.stream()
                    .map(pline -> {
                        Canopy canopy = new Canopy();
                        MeasurementDetail measurement = new MeasurementDetail(pline, true);
                        canopy.setArea(measurement.getArea());
                        canopy.setWidth(measurement.getWidth());
                        canopy.setHeight(measurement.getHeight());
                        return canopy;
                    }).collect(Collectors.toList());

            bl.getBuilding().setCanopy(projectionMeasurements);
        }

        if (LOG.isDebugEnabled())
            LOG.debug("End of Canopy Extract......");
        return pl;
    }

    @Override
    public PlanDetail validate(PlanDetail pl) {
        return pl;
    }
}
