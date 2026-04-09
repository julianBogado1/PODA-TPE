package ar.edu.itba.business.sensor_types;

import java.util.Map;

public record UnknownRead(
        Map<String, Object> rawValues
) implements SensorRead {
}
