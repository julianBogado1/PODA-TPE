package ar.edu.itba.sensors.types;

import ar.edu.itba.sensors.measures.TemperatureMeasurement;

public record WeatherSensorRead(
        String id,
        String timestamp,
        String schemaVersion,
        TemperatureMeasurement temperature,
        Double humidity
) implements SensorRead {
}
