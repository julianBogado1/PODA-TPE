package ar.edu.itba.infrastructure.models.types;

public record TrafficSensorRead(
        String id,
        String timestamp,
        String schemaVersion,
        Double speed,
        String laneId
) implements SensorRead {
}
