package ar.edu.itba.business.sensor_types;

import ar.edu.itba.infrastructure.models.measures.TemperatureMeasurement;

public record WeatherSensorRead(
        String id,
        String timestamp,
        String schemaVersion,
        TemperatureMeasurement temperature,
        Double humidity
) implements SensorRead {
}
