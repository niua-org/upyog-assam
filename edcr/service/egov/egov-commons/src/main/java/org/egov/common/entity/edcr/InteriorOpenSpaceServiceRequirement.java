package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InteriorOpenSpaceServiceRequirement extends MdmsFeatureRule {

    @JsonProperty("minInteriorAreaValueOne")
    private BigDecimal minInteriorAreaValueOne;
    @JsonProperty("minInteriorAreaValueTwo")
    private BigDecimal minInteriorAreaValueTwo;
    @JsonProperty("minInteriorWidthValueOne")
    private BigDecimal minInteriorWidthValueOne;
    @JsonProperty("minInteriorWidthValueTwo")
    private BigDecimal minInteriorWidthValueTwo;
    @JsonProperty("minVentilationAreaValueOne")
    private BigDecimal minVentilationAreaValueOne;
    @JsonProperty("minVentilationAreaValueTwo")
    private BigDecimal minVentilationAreaValueTwo;
    @JsonProperty("minVentilationWidthValueOne")
    private BigDecimal minVentilationWidthValueOne;
    @JsonProperty("minVentilationWidthValueTwo")
    private BigDecimal minVentilationWidthValueTwo;

    @JsonProperty("buildingHeightThreshold")
    private BigDecimal buildingHeightThreshold;
    @JsonProperty("wcAreaAboveThreshold")
    private BigDecimal wcAreaAboveThreshold;
    @JsonProperty("wcAreaBelowThreshold")
    private BigDecimal wcAreaBelowThreshold;
    @JsonProperty("wcWidthAboveThreshold")
    private BigDecimal wcWidthAboveThreshold;
    @JsonProperty("wcWidthBelowThreshold")
    private BigDecimal wcWidthBelowThreshold;
    @JsonProperty("kitchenAreaAboveThreshold")
    private BigDecimal kitchenAreaAboveThreshold;
    @JsonProperty("kitchenAreaBelowThreshold")
    private BigDecimal kitchenAreaBelowThreshold;
    @JsonProperty("kitchenWidthAboveThreshold")
    private BigDecimal kitchenWidthAboveThreshold;
    @JsonProperty("kitchenWidthBelowThreshold")
    private BigDecimal kitchenWidthBelowThreshold;


    public BigDecimal getMinVentilationAreaValueOne() {
        return minVentilationAreaValueOne;
    }

    public void setMinVentilationAreaValueOne(BigDecimal minVentilationAreaValueOne) {
        this.minVentilationAreaValueOne = minVentilationAreaValueOne;
    }

    public BigDecimal getMinVentilationAreaValueTwo() {
        return minVentilationAreaValueTwo;
    }

    public void setMinVentilationAreaValueTwo(BigDecimal minVentilationAreaValueTwo) {
        this.minVentilationAreaValueTwo = minVentilationAreaValueTwo;
    }

    public BigDecimal getMinVentilationWidthValueOne() {
        return minVentilationWidthValueOne;
    }

    public void setMinVentilationWidthValueOne(BigDecimal minVentilationWidthValueOne) {
        this.minVentilationWidthValueOne = minVentilationWidthValueOne;
    }

    public BigDecimal getMinVentilationWidthValueTwo() {
        return minVentilationWidthValueTwo;
    }

    public void setMinVentilationWidthValueTwo(BigDecimal minVentilationWidthValueTwo) {
        this.minVentilationWidthValueTwo = minVentilationWidthValueTwo;
    }

    @Override
    public String toString() {
        return "InteriorOpenSpaceServiceRequirement [minInteriorAreaValueOne=" + minInteriorAreaValueOne
                + ", minInteriorAreaValueTwo=" + minInteriorAreaValueTwo + ", minInteriorWidthValueOne="
                + minInteriorWidthValueOne + ", minInteriorWidthValueTwo=" + minInteriorWidthValueTwo
                + ", minVentilationAreaValueOne=" + minVentilationAreaValueOne + ", minVentilationAreaValueTwo="
                + minVentilationAreaValueTwo + ", minVentilationWidthValueOne=" + minVentilationWidthValueOne
                + ", minVentilationWidthValueTwo=" + minVentilationWidthValueTwo + "]";
    }

    public BigDecimal getMinInteriorAreaValueOne() {
        return minInteriorAreaValueOne;
    }

    public void setMinInteriorAreaValueOne(BigDecimal minInteriorAreaValueOne) {
        this.minInteriorAreaValueOne = minInteriorAreaValueOne;
    }

    public BigDecimal getMinInteriorAreaValueTwo() {
        return minInteriorAreaValueTwo;
    }

    public void setMinInteriorAreaValueTwo(BigDecimal minInteriorAreaValueTwo) {
        this.minInteriorAreaValueTwo = minInteriorAreaValueTwo;
    }

    public BigDecimal getMinInteriorWidthValueOne() {
        return minInteriorWidthValueOne;
    }

    public void setMinInteriorWidthValueOne(BigDecimal minInteriorWidthValueOne) {
        this.minInteriorWidthValueOne = minInteriorWidthValueOne;
    }

    public BigDecimal getMinInteriorWidthValueTwo() {
        return minInteriorWidthValueTwo;
    }

    public void setMinInteriorWidthValueTwo(BigDecimal minInteriorWidthValueTwo) {
        this.minInteriorWidthValueTwo = minInteriorWidthValueTwo;
    }

    public BigDecimal getBuildingHeightThreshold() {
        return buildingHeightThreshold;
    }

    public void setBuildingHeightThreshold(BigDecimal buildingHeightThreshold) {
        this.buildingHeightThreshold = buildingHeightThreshold;
    }

    public BigDecimal getWcAreaAboveThreshold() {
        return wcAreaAboveThreshold;
    }

    public void setWcAreaAboveThreshold(BigDecimal wcAreaAboveThreshold) {
        this.wcAreaAboveThreshold = wcAreaAboveThreshold;
    }

    public BigDecimal getWcAreaBelowThreshold() {
        return wcAreaBelowThreshold;
    }

    public void setWcAreaBelowThreshold(BigDecimal wcAreaBelowThreshold) {
        this.wcAreaBelowThreshold = wcAreaBelowThreshold;
    }

    public BigDecimal getWcWidthAboveThreshold() {
        return wcWidthAboveThreshold;
    }

    public void setWcWidthAboveThreshold(BigDecimal wcWidthAboveThreshold) {
        this.wcWidthAboveThreshold = wcWidthAboveThreshold;
    }

    public BigDecimal getWcWidthBelowThreshold() {
        return wcWidthBelowThreshold;
    }

    public void setWcWidthBelowThreshold(BigDecimal wcWidthBelowThreshold) {
        this.wcWidthBelowThreshold = wcWidthBelowThreshold;
    }

    public BigDecimal getKitchenAreaAboveThreshold() {
        return kitchenAreaAboveThreshold;
    }

    public void setKitchenAreaAboveThreshold(BigDecimal kitchenAreaAboveThreshold) {
        this.kitchenAreaAboveThreshold = kitchenAreaAboveThreshold;
    }

    public BigDecimal getKitchenAreaBelowThreshold() {
        return kitchenAreaBelowThreshold;
    }

    public void setKitchenAreaBelowThreshold(BigDecimal kitchenAreaBelowThreshold) {
        this.kitchenAreaBelowThreshold = kitchenAreaBelowThreshold;
    }

    public BigDecimal getKitchenWidthAboveThreshold() {
        return kitchenWidthAboveThreshold;
    }

    public void setKitchenWidthAboveThreshold(BigDecimal kitchenWidthAboveThreshold) {
        this.kitchenWidthAboveThreshold = kitchenWidthAboveThreshold;
    }

    public BigDecimal getKitchenWidthBelowThreshold() {
        return kitchenWidthBelowThreshold;
    }

    public void setKitchenWidthBelowThreshold(BigDecimal kitchenWidthBelowThreshold) {
        this.kitchenWidthBelowThreshold = kitchenWidthBelowThreshold;
    }
}
