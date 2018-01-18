package com.cellwize.hson.kpicalc;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KpiVal {
    private KpiType kpiType;

    private List<Float> counterValues;

    private Float value;

    private boolean valid = false;

    public KpiVal() {
    }

    public KpiVal(KpiType kpiType) {
        this.kpiType = kpiType;
        this.counterValues = new ArrayList<>(kpiType.getCounterNames().size());
        kpiType.getCounterNames().forEach(name -> counterValues.add(null));
    }

    public KpiType getKpiType() {
        return kpiType;
    }

    public void setKpiType(KpiType kpiType) {
        this.kpiType = kpiType;
        this.counterValues = new ArrayList<>(kpiType.getCounterNames().size());
        kpiType.getCounterNames().forEach(name -> counterValues.add(null));
    }

    public void setCounterValue(String counterName, float counterValue) {
        if (kpiType.getCounterNames().contains(counterName)) {
            int idx = kpiType.getCounterNames().indexOf(counterName);

            this.counterValues.set(idx, counterValue);
        }
    }

    @Transient
    public Float getCounterValue(String counterName) {
        if (kpiType.getCounterNames().contains(counterName)) {
            int idx = kpiType.getCounterNames().indexOf(counterName);

            return this.counterValues.get(idx);
        }

        return null;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public void checkValid() {
        setValid(kpiType.getCounterNames().size() == counterValues.stream()
                .filter(Objects::nonNull)
                .count());
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public List<Float> getCounterValues() {
        return counterValues;
    }

    public void setCounterValues(List<Float> counterValues) {
        this.counterValues = counterValues;
    }
}
