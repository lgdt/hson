package com.cellwize.hson.parsers.nokiaxml;

import com.cellwize.hson.eventbroker.api.AppConfig;
import com.cellwize.hson.eventbroker.api.KafkaMeasEventPublisher;
import com.cellwize.hson.eventbroker.api.MeasResults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;

public class KafkaMeasEventPublisherTest {

    @Test
    public void testPublisher() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        KafkaMeasEventPublisher kafkaMeasEventPublisher = context.getBean("kafkaMeasEventPublisher", KafkaMeasEventPublisher.class);
        MeasResults results = createResult();
        kafkaMeasEventPublisher.publishEvent(results);
    }

    private MeasResults createResult() {
        MeasResults results = new MeasResults("OMES", "Cell_Resource", new Date(), new Date(), 60);
        results.measObjLdn.add("PLMN-PLMN/RNC-2013/WBTS-14339/WCEL-44463");
        results.measObjLdn.add("PLMN-PLMN/MCC-262/MNC-3");
        results.measurements.put("M1000C0", 65.0);
        return results;
    }
}
