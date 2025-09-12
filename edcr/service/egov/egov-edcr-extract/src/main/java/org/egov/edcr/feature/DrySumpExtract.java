package org.egov.edcr.feature;


import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.DrySump;
import org.egov.common.entity.edcr.Measurement;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFCircle;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

    public class DrySumpExtract extends FeatureExtract {
        private static final Logger LOG = LogManager.getLogger(DrySumpExtract.class);

        @Autowired
        private LayerNames layerNames;

        @Override
        public PlanDetail extract(PlanDetail pl) {
            if (LOG.isInfoEnabled())
                LOG.info("Starting Dry Sump Extract...");

            // --- Extract polylines ---
            List<DXFLWPolyline> drySumpPolylines = Util.getPolyLinesByLayer(pl.getDoc(),
                    layerNames.getLayerName("LAYER_NAME_DRY_SUMP"));
            if (drySumpPolylines != null && !drySumpPolylines.isEmpty()) {
                for (DXFLWPolyline pline : drySumpPolylines) {
                    Measurement measurement = new MeasurementDetail(pline, true);
                    DrySump drySump = new DrySump();
                    drySump.setArea(measurement.getArea());
                    drySump.setColorCode(measurement.getColorCode());
                    drySump.setHeight(measurement.getHeight());
                    drySump.setWidth(measurement.getWidth());
                    drySump.setLength(measurement.getLength());
                    drySump.setInvalidReason(measurement.getInvalidReason());
                    drySump.setPresentInDxf(true);
                    pl.getUtility().addDrySump(drySump);
                }
            }

            // --- Extract circles ---
            List<DXFCircle> drySumpCircles = Util.getPolyCircleByLayer(pl.getDoc(),
                    layerNames.getLayerName("LAYER_NAME_DRY_SUMP"));
            if (drySumpCircles != null && !drySumpCircles.isEmpty()) {
                for (DXFCircle circle : drySumpCircles) {
                    DrySump drySump = new DrySump();
                    drySump.setColorCode(circle.getColor());
                    drySump.setRadius(BigDecimal.valueOf(circle.getRadius()));
                    drySump.setPresentInDxf(true);
                    pl.getUtility().addDrySump(drySump);
                }
            }

            // --- Extract capacity from MTEXT ---
            if (pl.getDoc().containsDXFLayer(layerNames.getLayerName("LAYER_NAME_DRY_SUMP"))) {
                String sumpCapacity = Util.getMtextByLayerName(pl.getDoc(),
                        layerNames.getLayerName("LAYER_NAME_DRY_SUMP"),
                        layerNames.getLayerName("LAYER_NAME_DRY_SUMP_CAPACITY_L"));
                if (sumpCapacity != null && !sumpCapacity.isEmpty()) {
                    try {
                        if (sumpCapacity.contains(";")) {
                            String[] textSplit = sumpCapacity.split(";");
                            sumpCapacity = textSplit[textSplit.length - 1];
                        }
                        sumpCapacity = sumpCapacity.replaceAll("[^\\d.]", "");
                        if (!sumpCapacity.isEmpty()) {
                            pl.getUtility().setDrySumpCapacity(BigDecimal.valueOf(Double.parseDouble(sumpCapacity)));
                        }
                    } catch (NumberFormatException e) {
                        pl.addError(layerNames.getLayerName("LAYER_NAME_DRY_SUMP"),
                                "Dry Sump capacity value contains non-numeric characters.");
                    }
                }
            }

            // --- Handle numbered layers like DRYSUMP_1, DRYSUMP_2 ---
            String drySumpLayerPattern = layerNames.getLayerName("LAYER_NAME_DRY_SUMP") + "_+\\d";
            List<String> drySumpLayers = Util.getLayerNamesLike(pl.getDoc(), drySumpLayerPattern);

            if (drySumpLayers != null && !drySumpLayers.isEmpty()) {
                for (String sumpLayer : drySumpLayers) {
                    String[] split = sumpLayer.split("_");
                    List<DXFLWPolyline> sumpPolys = Util.getPolyLinesByLayer(pl.getDoc(), sumpLayer);
                    List<BigDecimal> dimensions = Util.getListOfDimensionValueByLayer(pl, sumpLayer);

                    String sumpCapacity = Util.getMtextByLayerName(pl.getDoc(), sumpLayer,
                            layerNames.getLayerName("LAYER_NAME_DRY_SUMP_CAPACITY_L"));
                    if (sumpCapacity != null && !sumpCapacity.isEmpty()) {
                        try {
                            if (sumpCapacity.contains(";")) {
                                String[] textSplit = sumpCapacity.split(";");
                                sumpCapacity = textSplit[textSplit.length - 1];
                            }
                            sumpCapacity = sumpCapacity.replaceAll("[^\\d.]", "");
                        } catch (NumberFormatException e) {
                            pl.addError(layerNames.getLayerName("LAYER_NAME_DRY_SUMP"),
                                    "Dry Sump capacity value contains non-numeric characters.");
                        }
                    }

                    if (sumpPolys != null && !sumpPolys.isEmpty()) {
                        for (DXFLWPolyline pline : sumpPolys) {
                            Measurement m = new MeasurementDetail(pline, true);
                            DrySump drySump = new DrySump();
                            drySump.setNumber(Integer.valueOf(split[1]));
                            drySump.setArea(m.getArea());
                            drySump.setColorCode(m.getColorCode());
                            drySump.setHeight(m.getHeight());
                            drySump.setWidth(m.getWidth());
                            drySump.setLength(m.getLength());
                            drySump.setInvalidReason(m.getInvalidReason());
                            drySump.setPresentInDxf(true);
                            drySump.setTankHeight(dimensions);
                            if (sumpCapacity != null && !sumpCapacity.isEmpty()) {
                                drySump.setTankCapacity(BigDecimal.valueOf(Double.parseDouble(sumpCapacity)));
                            }
                            pl.getUtility().addDrySump(drySump);
                        }
                    }

                    List<DXFCircle> sumpCircles = Util.getPolyCircleByLayer(pl.getDoc(), sumpLayer);
                    if (sumpCircles != null && !sumpCircles.isEmpty()) {
                        for (DXFCircle circle : sumpCircles) {
                            DrySump drySump = new DrySump();
                            drySump.setNumber(Integer.valueOf(split[1]));
                            drySump.setColorCode(circle.getColor());
                            drySump.setRadius(BigDecimal.valueOf(circle.getRadius()));
                            drySump.setPresentInDxf(true);
                            drySump.setTankHeight(dimensions);
                            if (sumpCapacity != null && !sumpCapacity.isEmpty()) {
                                drySump.setTankCapacity(BigDecimal.valueOf(Double.parseDouble(sumpCapacity)));
                            }
                            pl.getUtility().addDrySump(drySump);
                        }
                    }
                }
            }

            if (LOG.isInfoEnabled())
                LOG.info("End of Dry Sump Extract.");
            return pl;
        }

        @Override
        public PlanDetail validate(PlanDetail pl) {
            return pl;
        }
    }