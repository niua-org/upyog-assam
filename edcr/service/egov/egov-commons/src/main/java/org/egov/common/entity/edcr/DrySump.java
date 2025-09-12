package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.List;

public class DrySump extends Measurement {
    private static final long serialVersionUID = 40L;

    private Integer number;
    private BigDecimal radius;
    private List<BigDecimal> tankHeight;
    private BigDecimal tankCapacity;

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public BigDecimal getRadius() { return radius; }
    public void setRadius(BigDecimal radius) { this.radius = radius; }

    public List<BigDecimal> getTankHeight() { return tankHeight; }
    public void setTankHeight(List<BigDecimal> tankHeight) { this.tankHeight = tankHeight; }

    public BigDecimal getTankCapacity() { return tankCapacity; }
    public void setTankCapacity(BigDecimal tankCapacity) { this.tankCapacity = tankCapacity; }
}
