package ar.edu.itba.business.sensor_types;

public record TrafficSensorRead(
        String id,
        String timestamp,
        String schemaVersion,
        Double speed,
        String laneId
) implements SensorRead {
}
