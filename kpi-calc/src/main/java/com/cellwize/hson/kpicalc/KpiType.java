package com.cellwize.hson.kpicalc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public enum KpiType {
    KPI_DL_CODE_R99_UTIL("((M1000C72/M1000C73)-100*(5*M1000C248+6*M1000C249+7*M1000C250+8*M1000C251+9*M1000C252+10*M1000C253+11*M1000C254+12*M1000C255+13*M1000C256+14*M1000C257+15*M1000C258)/((M1000C248+M1000C249+M1000C250+M1000C251+M1000C252+M1000C253+M1000C254+M1000C255+M1000C256+M1000C257+M1000C258)*16))",
            Arrays.asList("M1000C72", "M1000C73", "M1000C248", "M1000C249",
                    "M1000C250", "M1000C251", "M1000C252", "M1000C253",
                    "M1000C254", "M1000C255", "M1000C256", "M1000C257",
                    "M1000C258"));

    private final String kpiFormula;

    private final List<String> counterNames;

    KpiType(String kpiFormula, List<String> counterNames) {
        this.kpiFormula = kpiFormula;
        this.counterNames = counterNames;
    }

    @JsonIgnore
    public List<String> getCounterNames() {
        return counterNames;
    }

    @JsonIgnore
    public String getFormulaForEvaluation() {
        String result = kpiFormula;
        for (String counterName : counterNames) {
            result = result.replace(counterName, "#" + counterName);
        }

        return result;
    }

    @JsonCreator
    public static KpiType fromString(String string) {
        return KpiType.valueOf(string);
    }

}
