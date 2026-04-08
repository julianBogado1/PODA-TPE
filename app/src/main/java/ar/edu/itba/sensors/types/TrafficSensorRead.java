package ar.edu.itba.sensors.types;

public record TrafficSensorRead(
        String id,
        String timestamp,
        String schemaVersion,
        Double speed,
        String laneId
) implements SensorRead {
}
