package ar.edu.itba.sensors.types;

public sealed interface SensorRead permits
        ReportSensorRead,
        TrafficSensorRead, WeatherSensorRead,
        UnknownRead{
}
