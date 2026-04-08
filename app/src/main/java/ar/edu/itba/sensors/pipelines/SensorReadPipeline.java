package ar.edu.itba.sensors.pipelines;

import ar.edu.itba.sensors.types.SensorRead;

import java.util.List;

public class SensorReadPipeline {

    private List<SensorRead> sensorReads;

    public SensorReadPipeline(List<SensorRead> sensorReads){
        this.sensorReads = sensorReads;
    }

    public Integer getCorrectlyProcessedSensorReads() {
        return sensorReads.size();
    }

    public Double getAvgTrafficSpeed(){
        sensorReads.stream().filter(sensorRead -> )
    }
}
