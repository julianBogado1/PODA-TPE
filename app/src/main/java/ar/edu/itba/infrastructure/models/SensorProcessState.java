package ar.edu.itba.infrastructure.models;

import ar.edu.itba.business.models.SensorMetrics;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public record SensorProcessState(
        Double cumSpeed,
        Long totalSpeedReads,
        Long totalEvents,
        Long criticalEventCount,
        Long v1EventCount,
        Long v1_5EventCount,
        Long v2EventCount
) {
    public static SensorProcessState initial(
            Double cumSpeed, Long totalSpeedReads, Long totalEvents, Long criticalEventCount,
            Long v1EventCount,  Long v1_5EventCount, Long v2EventCount
    ) {
        return new SensorProcessState(cumSpeed, totalSpeedReads, totalEvents, criticalEventCount, v1EventCount, v1_5EventCount, v2EventCount);
    }

    public SensorProcessState withTrafficSensor(Double newSpeed){
    return new SensorProcessState(cumSpeed+newSpeed, totalSpeedReads+1, totalEvents+1, criticalEventCount,
            v1EventCount, v1_5EventCount, v2EventCount);
    }
    public SensorProcessState withWeatherSensor(){
        return new SensorProcessState(cumSpeed, totalSpeedReads, totalEvents+1, criticalEventCount,
                v1EventCount, v1_5EventCount, v2EventCount);
    }
    public SensorProcessState withReportSensor(){
        //business assumes report events critical
        return new SensorProcessState(cumSpeed, totalSpeedReads, totalEvents+1, criticalEventCount,
                v1EventCount, v1_5EventCount, v2EventCount);
    }

    public SensorProcessState withV1Sensor(){
        return new SensorProcessState(cumSpeed, totalSpeedReads, totalEvents, criticalEventCount,
                v1EventCount+1, v1_5EventCount, v2EventCount);
    }
    public SensorProcessState withV1_5Sensor(){
        return new SensorProcessState(cumSpeed, totalSpeedReads, totalEvents, criticalEventCount,
                v1EventCount, v1_5EventCount+1, v2EventCount);
    }
    public SensorProcessState withV2Sensor(){
        return new SensorProcessState(cumSpeed, totalSpeedReads, totalEvents, criticalEventCount,
                v1EventCount, v1_5EventCount, v2EventCount+1);
    }
    public SensorProcessState withCriticalEvent(){
        return new SensorProcessState(cumSpeed, totalSpeedReads, totalEvents, criticalEventCount+1,
                v1EventCount, v1_5EventCount, v2EventCount);
    }

    public SensorMetrics toMetrics(){
        return new SensorMetrics(
                cumSpeed/totalSpeedReads,
                totalEvents,
                criticalEventCount,
                v1EventCount,
                v1_5EventCount,
                v2EventCount
        );
    }

}
