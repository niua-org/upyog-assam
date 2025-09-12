package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CareTakerRoom {
    private List<Measurement> careTakerRooms = new ArrayList<>();

    protected List<BigDecimal> cabinHeights = new ArrayList<>();

    public List<Measurement> getCareTakerRooms() {
        return careTakerRooms;
    }

    public void setCareTakerRooms(List<Measurement> careTakerRooms) {
        this.careTakerRooms = careTakerRooms;
    }

    public List<BigDecimal> getCabinHeights() {
        return cabinHeights;
    }

    public void setCabinHeights(List<BigDecimal> cabinHeights) {
        this.cabinHeights = cabinHeights;
    }
}
