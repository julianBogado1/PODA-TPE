package ar.edu.itba.infrastructure.types;

import ar.edu.itba.infrastructure.measures.TemperatureMeasurement;

public record WeatherSensorRead(
        String id,
        String timestamp,
        String schemaVersion,
        TemperatureMeasurement temperature,
        Double humidity
) implements SensorRead {
}
