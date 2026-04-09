package ar.edu.itba.infrastructure.types;

public sealed interface SensorRead permits
        ReportSensorRead,
        TrafficSensorRead, WeatherSensorRead,
        UnknownRead{
    default String schemaVersion() {
        return null;
    }
}
