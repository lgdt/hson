package com.cellwize.hson.kpicalc;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KpiValTest {

    @Test
    public void shouldBeValidKpiVal() {
        KpiVal kpiVal = new KpiVal(KpiType.KPI_DL_CODE_R99_UTIL);
        assertFalse(kpiVal.isValid());

        for (String name : KpiType.KPI_DL_CODE_R99_UTIL.getCounterNames()) {
            assertFalse(kpiVal.isValid());

            kpiVal.setCounterValue(name, 1.0f);
        }

        assertTrue(kpiVal.isValid());
    }

}