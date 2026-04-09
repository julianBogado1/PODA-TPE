package ar.edu.itba.io;

import ar.edu.itba.business.models.SensorMetrics;
import ar.edu.itba.infrastructure.types.SensorRead;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface Parser {
    SensorMetrics parse(DataSource<JsonNode> source);
}
