package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Corridor extends Measurement{
    private List<BigDecimal> corridorWidths;

    private List<BigDecimal> corridorHeights;

    private List<BigDecimal> corridorLengths;

    private List<BigDecimal> corridorArea;

    public List<BigDecimal> getCorridorWidths() {
        return corridorWidths;
    }

    public void setCorridorWidths(List<BigDecimal> corridorWidths) {
        this.corridorWidths = corridorWidths;
    }

    public List<BigDecimal> getCorridorHeights() {
        return corridorHeights;
    }

    public void setCorridorHeights(List<BigDecimal> corridorHeights) {
        this.corridorHeights = corridorHeights;
    }

    public List<BigDecimal> getCorridorLengths() {
        return corridorLengths;
    }

    public void setCorridorLengths(List<BigDecimal> corridorLengths) {
        this.corridorLengths = corridorLengths;
    }

    public List<BigDecimal> getCorridorArea() {
        return corridorArea;
    }

    public void setCorridorArea(List<BigDecimal> corridorArea) {
        this.corridorArea = corridorArea;
    }
}
