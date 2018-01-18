package com.cellwize.hson.kpicalc;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelBasedKpiCalculator {

    public Float calcKpi(KpiVal kpiVal) {
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext simpleContext = new StandardEvaluationContext();

        if (kpiVal.isValid()) {
            for (String counterName : kpiVal.getKpiType().getCounterNames()) {
                simpleContext.setVariable(counterName, kpiVal.getCounterValue(counterName));
            }

            return (Float) parser.parseExpression(kpiVal.getKpiType().getFormulaForEvaluation()).getValue(simpleContext);
        } else {
            return null;
        }
    }
}
