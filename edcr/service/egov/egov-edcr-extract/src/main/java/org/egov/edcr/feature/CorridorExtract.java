package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Corridor;
import org.egov.common.entity.edcr.Floor;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CorridorExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(CorridorExtract.class);

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail extract(PlanDetail planDetail) {
        List<Block> blocks = planDetail.getBlocks();
        Map<String, Integer> corridorColors = planDetail.getSubFeatureColorCodesMaster().get("Corridor");

        for (Block block : blocks) {
            if (block.getBuilding() != null) {
                for (Floor floor : block.getBuilding().getFloors()) {
                    String LAYER_CORRIDOR = "BLK_" + block.getNumber() + "_FLR_" + floor.getNumber() + "_CORRIDOR";
                    List<BigDecimal> corridorHeights = new ArrayList<>();
                    List<BigDecimal> corridorWidths = new ArrayList<>();
                    List<BigDecimal> corridorLengths = new ArrayList<>();

                    List<DXFDimension> corridorDimension = Util.getDimensionsByLayer(planDetail.getDoc(),
                            layerNames.getLayerName(LAYER_CORRIDOR));

                    // Extract area from polylines
                    List<DXFLWPolyline> corridorPolylines = Util.getPolyLinesByLayer(planDetail.getDoc(), LAYER_CORRIDOR);
                    BigDecimal corridorArea = BigDecimal.ZERO;

                    if (!corridorPolylines.isEmpty()) {
                        for (DXFLWPolyline polyline : corridorPolylines) {
                            BigDecimal area = Util.getPolyLineArea(polyline);
                            if (area != null && area.compareTo(BigDecimal.ZERO) > 0) {
                                corridorArea = corridorArea.add(area);
                            }
                        }
                    }

                    if (!corridorDimension.isEmpty()) {
                        for (DXFDimension dim : corridorDimension) {
                            corridorColors.entrySet().forEach(sub -> {
                                if (sub.getKey().equalsIgnoreCase("CorridorHeight")
                                        && sub.getValue().equals(Integer.valueOf(dim.getColor()))) {
                                    corridorHeights.addAll(buildDimension(planDetail, dim, sub,
                                            layerNames.getLayerName(LAYER_CORRIDOR)));
                                } else if (sub.getKey().equalsIgnoreCase("CorridorWidth")
                                        && sub.getValue().equals(Integer.valueOf(dim.getColor()))) {
                                    corridorWidths.addAll(buildDimension(planDetail, dim, sub,
                                            layerNames.getLayerName(LAYER_CORRIDOR)));
                                } else if (sub.getKey().equalsIgnoreCase("CorridorLength")
                                        && sub.getValue().equals(Integer.valueOf(dim.getColor()))) {
                                    corridorLengths.addAll(buildDimension(planDetail, dim, sub,
                                            layerNames.getLayerName(LAYER_CORRIDOR)));
                                }
                            });
                        }
                    }

                    if (!corridorWidths.isEmpty() || !corridorHeights.isEmpty()
                            || !corridorLengths.isEmpty() || corridorArea.compareTo(BigDecimal.ZERO) > 0) {
                        Corridor corridor = new Corridor();
                        corridor.setCorridorWidths(corridorWidths);
                        corridor.setCorridorHeights(corridorHeights);
                        corridor.setCorridorLengths(corridorLengths);
                        corridor.setArea(corridorArea);
                        floor.setCorridor(corridor);
                    }
                }
            }
        }
        return planDetail;
    }

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    private List<BigDecimal> buildDimension(PlanDetail pl, DXFDimension dim, Map.Entry<String, Integer> sub,
                                            String layerName) {
        List<BigDecimal> values = new ArrayList<>();
        LOG.info("**** Corridor -" + sub.getKey() + "- Dimension---->>>" + values);
        Util.extractDimensionValue(pl, values, dim, layerName);
        return values.isEmpty() ? Arrays.asList(BigDecimal.ZERO) : values;
    }
}