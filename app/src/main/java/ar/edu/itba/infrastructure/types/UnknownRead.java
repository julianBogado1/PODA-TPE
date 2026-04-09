package ar.edu.itba.infrastructure.types;

import java.util.Map;

public record UnknownRead(
        Map<String, Object> rawValues
) implements SensorRead {
}
