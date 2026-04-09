package ar.edu.itba.infrastructure.models.types;

public record ReportSensorRead(
        String id,
        String timestamp,
        String schemaVersion,
        String category,
        String description,
        String severity,
        String status
) implements SensorRead {
}
