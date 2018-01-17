package com.cellwize.hson.parsers.nokiaxml;

import com.cellwize.hson.eventbroker.api.MeasResults;
import com.cellwize.hson.parsers.ParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NokiaPmXmlParserTest {

    private int measResultsCnt;
    private int measCount = 0;

    @Test
    public void parserNokiaLTETest() throws URISyntaxException, IOException, ParserException {
        String TEST_NOKIA_LTE_OMES_FILE = "C:/git/hson/parsers/nokiaxml/src/test/resources/cson_WCEL_20170213070801_2009694.xml.gz";
        int NOKIA_LTE_CNT = 55;
        int NOKIA_COUNTER_CNT = 734;

        URI testURI = new URI(TEST_NOKIA_LTE_OMES_FILE);
        InputStream fileInputStream = new GZIPInputStream(new FileInputStream(TEST_NOKIA_LTE_OMES_FILE));

        NokiaPMXmlParser parser =  new NokiaPMXmlParser();

        parser.setResultHandler(o -> {
            MeasResults measResults = (MeasResults) o;
            measResultsCnt = measResults.getMeasurements().size();
            measCount++;
        });
        parser.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        parser.parse(testURI, fileInputStream);
        assertEquals(NOKIA_LTE_CNT,measCount);
        assertEquals(NOKIA_COUNTER_CNT, measResultsCnt);
    }
}
