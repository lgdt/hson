package com.cellwize.hson.kpicalc;

public class CellKpi {
    private String cellId;

    private Long timestamp;

    private KpiVal kpiVal;

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public KpiVal getKpiVal() {
        return kpiVal;
    }

    public void setKpiVal(KpiVal kpiVal) {
        this.kpiVal = kpiVal;
    }
}
