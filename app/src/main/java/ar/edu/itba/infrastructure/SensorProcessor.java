package ar.edu.itba.infrastructure;
import ar.edu.itba.business.models.SensorMetrics;
import ar.edu.itba.infrastructure.types.*;
import ar.edu.itba.io.DataSource;
import ar.edu.itba.io.JSONParser;
import ar.edu.itba.io.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SensorProcessor {

    private final Parser parser;
    private final DataSource source;

    public SensorProcessor(Parser parser,  DataSource source) {
        this.parser = parser;
        this.source = source;
    }

    public SensorMetrics processSensorData(){
        return parser.parse(source);
    }

}
