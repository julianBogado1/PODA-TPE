import ar.edu.itba.business.models.SensorMetrics;
import ar.edu.itba.infrastructure.SensorProcessor;
import ar.edu.itba.io.models.JSONDataSource;
import ar.edu.itba.io.models.JSONParser;
import ar.edu.itba.io.models.StringPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

void main() {
    String filePath = "app/src/main/resources/example_input.json";
    ObjectMapper mapper = new ObjectMapper();

    try{
        var dataSource = new JSONDataSource(mapper.readTree(new File(filePath)));
        var jsonParser = new JSONParser();
        var processor = new SensorProcessor(jsonParser, dataSource);
        SensorMetrics metrics = processor.processSensorData();
        var printer = new StringPrinter(metrics);
        IO.println(printer.print(metrics));
    }catch (Exception e) {
        e.printStackTrace();
    }

}
