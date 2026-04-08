package ar.edu.itba.sensors.types;

import java.util.Map;

public record UnknownRead(
        Map<String, Object> rawValues
) implements SensorRead {
}
