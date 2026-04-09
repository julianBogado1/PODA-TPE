package ar.edu.itba.business.models;

public record SensorMetrics(
    Double avgSpeed,
    Long totalEvents,
    Long criticalEventCount,
    Long v1EventCount,
    Long v1_5EventCount,
    Long v2EventCount
    ){

    public Double getV1Proportion(){
        return v1EventCount.doubleValue()/totalEvents.doubleValue();
    }
    public Double getV1_5Proportion(){
        return v1_5EventCount.doubleValue()/totalEvents.doubleValue();
    }
    public Double getV2Proportion(){
        return v2EventCount.doubleValue()/totalEvents.doubleValue();
    }


}
