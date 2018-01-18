package com.cellwize.hson.results;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by max on 20/11/2014.
 */
public class MeasResults {

    public String fileFormat;
    public String measInfoId; // Nokia & Huawei
    public Date start;
    public Date end;
    public int granDuration;
    public int repDuration;
    public ArrayList<String> measObjLdn;
    public String counterName;
    public Double counterValue;
    // In Ericsson PM files, counter value may contain multiple values.
    // In such cases we are storing the with counter name = counterName[i]

    public MeasResults() {
        measObjLdn = new ArrayList<>();
    }

    public MeasResults(String fileFormat, String measInfoId, Date startTime, Date endTime, int interval) {
        this();
        this.fileFormat = fileFormat;
        this.measInfoId = measInfoId;
        this.start = startTime;
        this.end = endTime;
        this.granDuration = interval; // in seconds
        this.repDuration = 0;
    }

    public MeasResults(MeasResults measResults, String qName, double value) {
        this(measResults.fileFormat, measResults.measInfoId, measResults.start, measResults.end, measResults.granDuration);
        this.repDuration = measResults.repDuration;
        measObjLdn.addAll(measResults.measObjLdn);
        counterName = qName;
        counterValue = value;
    }

    @Override
    public String toString() {
        return "MeasResults{" +
                "measInfoId='" + measInfoId + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", granDuration=" + granDuration +
                ", repDuration=" + repDuration +
                ", measObjLdn=" + measObjLdn +
                ", counterName=" + counterName +
                ", counterValue=" + counterValue +
                '}';
    }

    public String getMeasInfoId() {
        return measInfoId;
    }

    public void setMeasInfoId(String measInfoId) {
        this.measInfoId = measInfoId;
    }

    public ArrayList<String> getMeasObjLdn() {
        return measObjLdn;
    }

    public void setMeasObjLdn(ArrayList<String> measObjLdn) {
        this.measObjLdn = measObjLdn;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounter(String counterName, Double counterValue) {
        this.counterName = counterName;
        this.counterValue = counterValue;
    }

    public Double getCounterValue() {
        return counterValue;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getGranDuration() {
        return granDuration;
    }
}
