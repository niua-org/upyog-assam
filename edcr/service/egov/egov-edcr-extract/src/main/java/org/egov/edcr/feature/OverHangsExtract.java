
package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.AccessoryBlock;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OverHangsExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(OverHangsExtract.class);
    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail planDetail) {
        for (Block block : planDetail.getBlocks()) {
            String protectedBalcony = String.format(layerNames.getLayerName("LAYER_NAME_PROJECTED_BALCONY"), block.getNumber());
            List<DXFLWPolyline> protectedBalconies = Util.getPolyLinesByLayer(planDetail.getDoc(), protectedBalcony);
            List<Measurement> protectedBalconyMeasurements = protectedBalconies.stream()
                    .map(flightPolyLine -> new MeasurementDetail(flightPolyLine, true)).collect(Collectors.toList());

            block.setProtectedBalconies(protectedBalconyMeasurements);

            if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
                for (Floor floor : block.getBuilding().getFloors()) {
                    String overhang = String.format(layerNames.getLayerName("LAYER_NAME_SHADE_OVERHANG"), block.getNumber(),
                            floor.getNumber());
                    List<DXFLWPolyline> overHangs = Util.getPolyLinesByLayer(planDetail.getDoc(), overhang);
                    List<Measurement> overHangMeasurements = overHangs.stream()
                            .map(flightPolyLine -> new MeasurementDetail(flightPolyLine, true))
                            .collect(Collectors.toList());
                    floor.setOverHangs(overHangMeasurements);
//                }
//
//                for (Floor floor : block.getBuilding().getFloors()) {
                    String floorProjectedBalcony = String.format(layerNames.getLayerName("LAYER_NAME_FLOOR_PROJECTED_BALCONY"), block.getNumber(), floor.getNumber());

                    //-------------- TO BE TESTED AND CHECK OUT WHICH CODE IS NEEDED ----------------
//                    Map<String, Integer> OverHangColors = planDetail.getSubFeatureColorCodesMaster().get("Balcony");
//                    List<BigDecimal> balconyLengths = new ArrayList<>();

//                    List<DXFDimension> balconyDimension = Util.getDimensionsByLayer(planDetail.getDoc(),
//                            layerNames.getLayerName(floorProjectedBalcony));
//                    for (DXFDimension dim : balconyDimension) {
//                        OverHangColors.entrySet().forEach(sub -> {
//                            if (sub.getKey().equalsIgnoreCase("BalconyLength")
//                                            && sub.getValue().equals(Integer.valueOf(dim.getColor()))) {
//                                balconyLengths.addAll(buildDimension(planDetail, dim, sub,
//                                                layerNames.getLayerName(floorProjectedBalcony)));
//                            }
//                        });
//                    }
                    if(!floorProjectedBalcony.isEmpty()) {
                        List<BigDecimal> balconyLength = Util.getListOfDimensionValueByLayer(planDetail, floorProjectedBalcony);
                        floor.setFloorProjectedBalconies(balconyLength);
                    }

                    // Balcony distance from plot boundary
                    String balconyDistLayerName = String.format(
                            layerNames.getLayerName("LAYER_NAME_BALCONY_DIST_TO_PLOT_BNDRY"), block.getNumber(), floor.getNumber());
                    if (!balconyDistLayerName.isEmpty()) {
                        List<BigDecimal> distanceToPlotList = Util.getListOfDimensionValueByLayer(planDetail, balconyDistLayerName);
                        floor.setBalconyDistanceFromPlotBoundary(distanceToPlotList);
                    }
                }
            }
        }

        return planDetail;
    }

//    private List<BigDecimal> buildDimension(PlanDetail pl, DXFDimension dim, Map.Entry<String, Integer> sub,
//                                            String layerName) {
//        List<BigDecimal> values = new ArrayList<>();
//        LOG.info("**** Corridor -" + sub.getKey() + "- Dimension---->>>" + values);
//        Util.extractDimensionValue(pl, values, dim, layerName);
//        return values.isEmpty() ? Arrays.asList(BigDecimal.ZERO) : values;
//    }
}
