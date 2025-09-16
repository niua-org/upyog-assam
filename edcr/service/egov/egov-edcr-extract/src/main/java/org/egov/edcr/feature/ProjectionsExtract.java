package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectionsExtract extends FeatureExtract{
    private static final Logger LOG = LogManager.getLogger(ProjectionsExtract.class);

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail pl) {
        if (LOG.isDebugEnabled())
            LOG.debug("Starting Projections Extract......");

        for(Block block: pl.getBlocks()){
            if(block.getBuilding()!=null && !block.getBuilding().getFloors().isEmpty()){
               for(Floor floor: block.getBuilding().getFloors()){
                   if(!floor.getRegularRooms().isEmpty()) {
                       for (Room room : floor.getRegularRooms()) {
                           for(Projections projection: room.getRoomProjections()) {
                               String projectionLayer = String.format(layerNames.getLayerName("BLK_" + block.getNumber() + "FLR_" + floor.getNumber() + "ROOM_" + room.getNumber() + "PROJECTION_" + projection.getNumber()));
                               List<DXFLWPolyline> polylines = Util.getPolyLinesByLayer(pl.getDoc(), projectionLayer);
                               List<Projections> projectionMeasurements = polylines.stream()
                                       .map(pline -> {
                                           Projections proj = new Projections();
                                           MeasurementDetail measurement = new MeasurementDetail(pline, true);
                                           proj.setArea(measurement.getArea());
                                           proj.setWidth(measurement.getWidth());
                                           proj.setHeight(measurement.getHeight());
                                           return proj;
                                       }).collect(Collectors.toList());

                               room.setRoomProjections(projectionMeasurements);
                           }
                       }
                   }
               }
            }
        }

        return pl;
    }
}
