package ar.edu.itba.business.sensor_types;

public sealed interface SensorRead permits
        ReportSensorRead,
        TrafficSensorRead, WeatherSensorRead,
        UnknownRead{
    default String schemaVersion() {
        return null;
    }
}
