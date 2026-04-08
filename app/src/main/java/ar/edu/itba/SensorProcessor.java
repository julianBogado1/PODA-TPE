package ar.edu.itba;
import ar.edu.itba.json.JSONProcessor;
import ar.edu.itba.models.DistributionReport;
import ar.edu.itba.sensors.types.*;
import ar.edu.itba.sensors.types.SensorRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SensorProcessor {

    private static final Double MIN_SPEED = 0.0;
    private static final Double MAX_SPEED = 300.0;
    private static final Double MAX_TEMP = 1000.0;
    private static final Double MIN_TEMP = -50.0;


    private Integer criticalEventCount = 0;


    //TODO el provider de sensor deberia ser interface para no atarse a un solo tipo de lectura
    //el json processor puede recibir el path porque es especifico de ese processor

    //que el sensorProcessor reciba por parametro el tipo de processor que usa y que el main
    //se encargue
    private String jsonPath = "app/src/main/resources/example_input.json";

    Logger LOGGER = LoggerFactory.getLogger(SensorProcessor.class);

    private List<ReportSensorRead>      reports         = new ArrayList<>();
    private List<TrafficSensorRead>     trafficSensors  = new ArrayList<>();
    private List<WeatherSensorRead>     weatherSensors  = new ArrayList<>();
    private List<UnknownRead>           unknownSensors  = new ArrayList<>();

    public void processSensorData(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(jsonPath));
            var jsonProcessor = JSONProcessor.makeProcessor();
            root.forEach(jsonNode -> {
                var read = jsonProcessor.parse(jsonNode);
                switch (read) {
                    case ReportSensorRead r         -> reports.add(r);
                    case TrafficSensorRead t        -> trafficSensors.add(t);
                    case WeatherSensorRead w        -> weatherSensors.add(w);
                    case UnknownRead u              -> unknownSensors.add(u);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getCorrectlyProcessedCount(){
        LOGGER.info("Getting correctly processed count");

        return  reports.size() +
                trafficSensors.size() +
                weatherSensors.size();
    }

    public Double getAvgSpeed(){
        LOGGER.info("Getting average speed");
        return trafficSensors.stream()
                .filter(t -> valueBetween(t.speed(), MIN_SPEED, MAX_SPEED))
                .mapToDouble(TrafficSensorRead::speed)
                .average()
                .orElse(0.0);
    }

    public Long getCriticalEventCount() {
        LOGGER.info("Getting critical event count");
        return countCriticalTrafficReads() + countCriticalWeatherReads();
    }

    public Double getV1Proportion(){
        Integer totalCount = reports.size() + weatherSensors.size() + trafficSensors.size();

    }

    private boolean valueBetween(Double val, Double min, Double max){
        return min <= val &&  val <= max;
    }

    //business logic dictates speeds above 300kmh are a critical event
    private Long countCriticalTrafficReads(){
        return trafficSensors.stream().filter(trafficSensorRead ->
                trafficSensorRead.speed() > MAX_SPEED).count();
    }
    //business logic dictates temps above 1000C are a critical event
    private Long countCriticalWeatherReads(){
        return weatherSensors.stream().filter(weatherSensorRead ->
                weatherSensorRead.temperature().toCelsius() > MAX_TEMP).count();
    }

    //business logic dictates ????? is a critical event
    private Long countCriticalReportReads(){
        return 0L;
    }
}
