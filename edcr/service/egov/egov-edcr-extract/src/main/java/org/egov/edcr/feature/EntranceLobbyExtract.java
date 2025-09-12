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

import java.math.BigDecimal;
import java.util.*;

public class EntranceLobbyExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(EntranceLobbyExtract.class);

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail pl) {
        Map<String, Integer> lobbyOccupancyFeature = pl.getSubFeatureColorCodesMaster().get("entranceLobby");
        Set<String> lobbyOccupancyTypes = new HashSet<>();
        lobbyOccupancyTypes.addAll(lobbyOccupancyFeature.keySet());
        for (Block block : pl.getBlocks()) {
            if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty())
                for (Floor floor : block.getBuilding().getFloors()) {

                    Map<Integer, List<BigDecimal>> lobbyHeightMap = new HashMap<>();
                    String entranceLobbyLayerName = String.format(layerNames.getLayerName("BLK_" + block.getNumber() + "FLR_" + floor.getNumber() + "_ENTRANCE_LOBBY"), block.getNumber(), floor.getNumber(), "+\\d");
                    List<String> lobbyLayers = Util.getLayerNamesLike(pl.getDoc(), entranceLobbyLayerName);

                    if (!lobbyLayers.isEmpty()) {
                        for (String entranceLobbyLayer : lobbyLayers) {
                            for (String type : lobbyOccupancyTypes) {
                                Integer colorCode = lobbyOccupancyFeature.get(type);
                                List<BigDecimal> lobbyheights = Util.getListOfDimensionByColourCode(pl, entranceLobbyLayer, colorCode);
                                if (!lobbyheights.isEmpty())
                                    lobbyHeightMap.put(colorCode, lobbyheights);
                            }

                            List<DXFLWPolyline> lobbyPolyLines = Util.getPolyLinesByLayer(pl.getDoc(), entranceLobbyLayer);

                            if (!lobbyHeightMap.isEmpty() || !lobbyPolyLines.isEmpty()) {

                                boolean isClosed = lobbyPolyLines.stream().allMatch(dxflwPolyline -> dxflwPolyline.isClosed());

                                EntranceLobby lobby = new EntranceLobby();
                                String[] lobbyNo = entranceLobbyLayer.split("_");
                                if (lobbyNo != null && lobbyNo.length == 5) {
                                    lobby.setNumber(lobbyNo[4]);
                                }
                                lobby.setClosed(isClosed);

                                List<RoomHeight> lobbyHeights = new ArrayList<>();
                                if (!lobbyPolyLines.isEmpty()) {
                                    List<Measurement> lobbies = new ArrayList<Measurement>();
                                    lobbyPolyLines.stream().forEach(lp -> {
                                        Measurement m = new MeasurementDetail(lp, true);
                                        if (!lobbyHeightMap.isEmpty() && lobbyHeightMap.containsKey(m.getColorCode())) {
                                            for (BigDecimal value : lobbyHeightMap.get(m.getColorCode())) {
                                                RoomHeight roomHeight = new RoomHeight();
                                                roomHeight.setColorCode(m.getColorCode());
                                                roomHeight.setHeight(value);
                                                lobbyHeights.add(roomHeight);
                                            }
                                            lobby.setHeights(lobbyHeights);
                                        }
                                        lobbies.add(m);
                                    });
                                    lobby.setLobbies(lobbies);
                                }
                                floor.addEntranceLobby(lobby);
                            }
                        }
                    }
                }
        }
        return pl;
    }
}
