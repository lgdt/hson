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

    private int measCount = 0;

    @Test
    public void parserNokiaTest() throws URISyntaxException, IOException, ParserException {
        String TEST_NOKIA_OMES_FILE = "C:/git/hson/parsers/nokiaxml/src/test/resources/cson_WCEL_20170213072357_1103782.xml.gz";
        int nokiaCellCount = 244;
        int nokiaCountersCount = 373;
        int eventCount = nokiaCellCount * nokiaCountersCount;

        URI testURI = new URI(TEST_NOKIA_OMES_FILE);
        InputStream fileInputStream = new GZIPInputStream(new FileInputStream(TEST_NOKIA_OMES_FILE));

        NokiaPMXmlParser parser =  new NokiaPMXmlParser();

        parser.setResultHandler(o -> {
            MeasResults measResults = (MeasResults) o;
            measResults.getMeasurements().size();
            measCount++;
            return null;
        });
        parser.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        parser.parse(testURI, fileInputStream);
        assertEquals(eventCount,measCount);
    }
}
