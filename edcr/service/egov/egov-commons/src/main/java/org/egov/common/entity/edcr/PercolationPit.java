package org.egov.common.entity.edcr;

import java.math.BigDecimal;

public class PercolationPit extends Measurement {
    private static final long serialVersionUID = 49L;

    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;

    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }

    public BigDecimal getWidth() { return width; }
    public void setWidth(BigDecimal width) { this.width = width; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }
}
