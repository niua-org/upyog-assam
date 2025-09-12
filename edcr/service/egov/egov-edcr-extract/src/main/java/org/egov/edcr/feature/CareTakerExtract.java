package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.CareTakerRoom;
import org.egov.common.entity.edcr.GuardRoom;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CareTakerExtract extends FeatureExtract {
    private static final Logger LOGGER = LogManager.getLogger(CareTakerExtract.class);

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail extract(PlanDetail pl) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Starting of CareTaker Extract......");

        pl.setCareTakerRoom(new CareTakerRoom());
        pl.getCareTakerRoom().setCareTakerRooms(new ArrayList<>());

        List<DXFLWPolyline> guardRoomPolylines = Util.getPolyLinesByLayer(pl.getDoc(),
                layerNames.getLayerName("LAYER_NAME_CARE_TAKER_ROOM"));
        for (DXFLWPolyline pline : guardRoomPolylines)
            pl.getCareTakerRoom().getCareTakerRooms().add(new MeasurementDetail(pline, true));

        pl.getCareTakerRoom().setCabinHeights(Util.getListOfDimensionValueByLayer(pl, layerNames.getLayerName("LAYER_NAME_CARE_TAKER_ROOM")));

        if (LOGGER.isInfoEnabled())
            LOGGER.info("End of CareTaker Extract......");
        return pl;
    }

    @Override
    public PlanDetail validate(PlanDetail pl) {
        return pl;
    }
}
