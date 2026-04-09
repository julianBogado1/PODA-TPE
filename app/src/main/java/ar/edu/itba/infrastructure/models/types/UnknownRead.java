package ar.edu.itba.infrastructure.models.types;

import java.util.Map;

public record UnknownRead(
        Map<String, Object> rawValues
) implements SensorRead {
}
