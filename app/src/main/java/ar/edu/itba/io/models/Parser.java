package ar.edu.itba.io.models;

import ar.edu.itba.business.models.SensorMetrics;
import com.fasterxml.jackson.databind.JsonNode;

public interface Parser {
    SensorMetrics parse(DataSource<JsonNode> source);
}
