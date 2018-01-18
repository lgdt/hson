package com.cellwize.hson.parsers.nokiaxml;

import com.cellwize.hson.eventbroker.api.EventPublisher;
import com.cellwize.hson.parsers.Parser;
import com.cellwize.hson.parsers.ParserException;
import com.cellwize.hson.results.MeasResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yaelk on 2/5/2015.
 *
 * Parsing a Nokia XML file and creating NokiaMeasResults object for each PMMOResult element
 * parse method will be invoked by the AcquisitionTasklet and
 *
 */
@Component
public class NokiaPMXmlParser extends DefaultHandler implements Parser {

    private Logger logger = LoggerFactory.getLogger(NokiaPMXmlParser.class);

    // Hold the possible elements appearing the xml format
    // NE_LNBTS_1_0 - For LTE optimizations counters
    private enum State {OMeS,PMSetup,PMMOResult,PMTarget,MO,baseId,localMoid,DN,WCEL_Nokia_5_0,CID_Nokia_5_0,COUNTER, NE_LNBTS_1_0};
    private Stack<State> currentState;

    private String dateFormat;
    private String fileNamePattern; // Reading it from properties file - jobs.nokia.opt.filenamePattern
    private StringBuilder value = new StringBuilder(); // Holds the element's value - gets populated in characters method
    private MeasResults measResults; // Holds the data read from a PMMOResult section in the xml
    private EventPublisher eventPublisher;

    // Time and interval details which are being read from the PMSetup element in the XML file
    private Date startTime;
    private Date endTime;
    private int interval;

    // Performance data set, taken from a counter type name (M1008C4 >> 1008)
    private String dataSet;

    // Measurement Type attribute rom PMTarget or WCEL_Nokia_5.0 or CID_Nokia_5_0 or NE-LNBTS_1.0 element in xml
    private String measType;

    // Predefined values for Nokia XML elements
    private static final Map<String, State> elementNameStateMap;

    static {
        elementNameStateMap = new HashMap<>();
        for (State state : State.values()) {
            elementNameStateMap.put(state.name(), state);
        }
    }

    public NokiaPMXmlParser(String dateFormat) {
        this.dateFormat = dateFormat;
        currentState = new Stack<>();
    }

    /*******************************************************
     * Allows AcquisitionTasklet to get the results of the parsing
     * @param eventPublisher
     *******************************************************/
    public void setResultHandler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }

    /*******************************************************
     * The parser is being created on the AcqusitionTasklet
     * @param uri - Holds the file name of the zipped file
     *              (we need to use some data from the name)
     * @param inputStream - Holds the file's contents
     *******************************************************/
    public void parse(URI uri, InputStream inputStream) throws ParserException {
        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            // Sets the reader to ignore the external DTD completely
            xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            xmlReader.setContentHandler(this);
            xmlReader.parse(new InputSource(inputStream));
        } catch (SAXException e) {
            throw new ParserException("Invalid Xml file " +uri,e);
        } catch (IOException e) {
            throw new ParserException("Can't open XML file " + uri,e);
        }
        finally {
            try{ inputStream.close();}catch (Exception e){}
        }
    }

    /*******************************************************
     * Is being called when a new XML element is starting
     * Entering the new element to currentState stack
     * Measurements - <M1xxxxx> elements are entered into currentState as COUNTER
     *******************************************************/
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if(currentState.empty() || (currentState.peek() != State.WCEL_Nokia_5_0 && currentState.peek() != State.PMTarget && currentState.peek() != State.CID_Nokia_5_0 && currentState.peek() != State.NE_LNBTS_1_0 ))
        {
            State entered = elementNameStateMap.get(localName.replaceAll("\\W", "_")); // Replacing Special chars like . and -
            if (entered != null) {
                currentState.push(entered);
            }
        }
        // In case the last element in currentState is a a WCEL_Nokia or PMTarget or CID_Nokia_5_0 or NE_LNBTS_1_0 tag, we reached a counter element
        else
        {
            currentState.push(State.COUNTER);
        }
        if (currentState.isEmpty()) {
            return;
        }

        // In case it's a localMoid, we want to concatenate it to baseId before adding to MOs
        // For all other elements, we are resetting the value field
        switch (currentState.peek()) {
            case PMSetup:
                value.setLength(0);
                handleDates(attributes);
                break;
            case PMMOResult:
                dataSet=null;
                value.setLength(0);
                measResults = new MeasResults("OMES", dataSet, startTime, endTime, interval);
                break;
            case baseId:
                value.setLength(0);
                value.append(State.baseId + "=");
                break;
            case localMoid:
                /* Concatenating the localId tag and value to the baseId tag and value under one MO value.
                The elements are separated by a ;
                <baseId>NE-RNC-150</baseId>
                <localMoid>DN:NE-WBTS-5021/WCEL-32</localMoid>
                >> baseId=NE-RNC-150;localMoid=DN:NE-WBTS-5021/WCEL-32
                Since we are not resetting value before reading localMoid, it also includes a \n and extra spaces
                Removing \n and the extra spaces */
                value.replace(value.indexOf("\n"),value.length(),"");
                value.append(";" + State.localMoid + "=");
                break;
            case PMTarget:
            case WCEL_Nokia_5_0:
            case NE_LNBTS_1_0:
                value.setLength(0);
                setMeasType(attributes);
                measResults.setMeasInfoId(measType);
                break;
            default:
                value.setLength(0);
                break;
        }
    }

    /*******************************************************
     * Is being called when a new XML element is starting
     * Pulling the element out of the currentState stack
     *******************************************************/
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentState.isEmpty()) {
            return;
        }
        switch (currentState.pop())
        {
            case PMSetup:
                break;
            case PMMOResult:
//                publishMeas(measResults);
                break;
            case WCEL_Nokia_5_0:
            case CID_Nokia_5_0:
            case NE_LNBTS_1_0:
            case PMTarget:
//                measResults.setMeasInfoId(measType);
                break;
            case MO:
                // Adding a value to the measObjectDNs in measResults
                value.replace(value.indexOf("\n"),value.length(),"");
                measResults.measObjLdn.add(value.toString());
                break;
            case COUNTER:
                /* Checking if qName includes a "_" and if it does, getting only the text before it. For Nokia GSM counters. */
                if (qName.contains("_")) {
                    qName = qName.substring(0,qName.indexOf("_"));
                }
                MeasResults result = new MeasResults(measResults, qName, Double.parseDouble(value.toString()));
                publishMeas(result);
                break;
        }
    }

    private void publishMeas(MeasResults measResults) {
        if(measResults != null){
            if(measResults.getMeasInfoId() != null && measResults.getMeasObjLdn().size() > 0 && measResults.getCounterName() != null){
                eventPublisher.publishEvent(measResults);
            }
        }
    }

    /*******************************************************
     * Populating the value parameter
     *******************************************************/
    public void characters(char[] ch, int start, int length) throws SAXException {
        value.append(ch, start, length);
    }

    /*******************************************************
     * Reading the date & time info from the PMSetup attribute
     * Keeping the info in startTime, endTime and interval parameters
     * to use them in each measResults object
     *******************************************************/
    protected void handleDates(Attributes attributes) {
        String startTimeAtr = attributes.getValue("startTime");
        String intervalAtr = attributes.getValue("interval");
        if (startTimeAtr == null) {
            return;
        }

        try {
            startTime = new SimpleDateFormat(dateFormat).parse(startTimeAtr);
            interval = Integer.parseInt(intervalAtr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            cal.add(Calendar.MINUTE, interval);
            endTime = cal.getTime();
        } catch (java.text.ParseException e) {
            logger.error("Wrong start time (" + startTimeAtr
                    + ") or interval (" + intervalAtr + ")");
        }
    }

    protected void setMeasType(Attributes attributes) {
        measType = attributes.getValue("measurementType");
        if (measType == null) {
            logger.error("Empty Measurement type");
        }
    }
}
