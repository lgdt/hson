package com.cellwize.hson.eventbroker.api;

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
    public ArrayList<String> measObjLdn; // details of the network element to which the measurements refer
    public Map<String, Double> measurements; // counterName, counterValue
    // In Ericsson PM files, counter value may contain multiple values.
    // In such cases we are storing the with counter name = counterName[i]

    public MeasResults() {
        measurements = new TreeMap<>();
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
        measurements.put(qName, value);
    }

    public MeasResults clone() {
        MeasResults clone = new MeasResults();
        clone.fileFormat = this.fileFormat;
        clone.measInfoId = this.measInfoId;
        clone.measObjLdn = this.measObjLdn;
        clone.start = this.start;
        clone.end = this.end;
        clone.granDuration = this.granDuration;
        clone.repDuration = this.repDuration;
        return clone;
    }

    public void clear() {
        this.fileFormat = null;
        this.measInfoId = null;
        this.start = null;
        this.end = null;
        this.granDuration = 0;
        this.repDuration = 0;
        measObjLdn.clear();
        measurements.clear();
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
                ", measurements=" + measurements +
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

    public Map<String, Double> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Map<String, Double> measurements) {
        this.measurements = measurements;
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

    public void addMeasurement(String counterName, Double counterVal) {
        getMeasurements().put(counterName, counterVal);
    }

    public int getGranDuration() {
        return granDuration;
    }
}
