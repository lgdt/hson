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
    public void parserNokiaTest() throws URISyntaxException, IOException, ParserException {
        String TEST_NOKIA_OMES_FILE = "C:/git/hson/parsers/nokiaxml/src/test/resources/cson_WCEL_20170213070801_2009694.xml.gz";
        int nokiaCellCount = 55;
        int nokiaCountersCount = 734;
        int eventCount = nokiaCellCount * nokiaCountersCount;

        URI testURI = new URI(TEST_NOKIA_OMES_FILE);
        InputStream fileInputStream = new GZIPInputStream(new FileInputStream(TEST_NOKIA_OMES_FILE));

        NokiaPMXmlParser parser =  new NokiaPMXmlParser();

        parser.setResultHandler(o -> {
            MeasResults measResults = (MeasResults) o;
            measResultsCnt = measResults.getMeasurements().size();
            measCount++;
        });
        parser.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        parser.parse(testURI, fileInputStream);
        assertEquals(eventCount,measCount);
    }
}
