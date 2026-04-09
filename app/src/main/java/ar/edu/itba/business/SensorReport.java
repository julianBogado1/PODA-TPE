package ar.edu.itba.business;

import ar.edu.itba.business.models.SensorMetrics;

public record SensorReport(SensorMetrics sensorMetrics){

    public String buildSensorReport(){
        return sensorMetrics.toString();
    }
}
