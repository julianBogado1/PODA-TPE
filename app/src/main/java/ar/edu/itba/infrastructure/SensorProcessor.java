package ar.edu.itba.infrastructure;
import ar.edu.itba.business.models.SensorMetrics;
import ar.edu.itba.io.models.DataSource;
import ar.edu.itba.io.models.Parser;

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
