package ar.edu.itba.json;

import ar.edu.itba.models.DistributionReport;
import ar.edu.itba.sensors.types.*;
import ar.edu.itba.sensors.measures.Celsius;
import ar.edu.itba.sensors.measures.Farenheit;
import ar.edu.itba.sensors.types.WeatherSensorRead;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class JSONProcessor {

    private static final JSONProcessor instance = new JSONProcessor();
    private DistributionReport distributionReport;



    private JSONProcessor(){}

    public static JSONProcessor makeProcessor(){
        return instance;
    }

    public SensorRead parse(JsonNode node) {
        if (node == null || node.isNull()) {
            return new UnknownRead(Map.of());
        }
        //assuming all 1.0 versioned have the same key for version
        // version willa lways be 1.5 and so on
        if (node.has("SCHEMA_VER")) {
            return parseV1(node);
        } else if (node.has("version")) {
            return parseV1_5(node);
        } else if (node.has("schemaVersion")) {
            return parseV2(node);
        }

        return toUnknown(node);
    }

    //TODO los argumentos tienen que hacer tolower!!!!!!!!!!!!!!!!

    private SensorRead parseV1(JsonNode node) {
        //checks de validez
        //id, timestamp, type, payload, schema_type not null
        //sitipo es wth payload: t y h not null
        //si tipo es trf payload:spd, lne not null
        //si tipo es report payload: cat, desc not null
        String id = node.get("id").asText();
        String timestamp = node.get("timestamp").asText();
        String schemaVersion = node.get("SCHEMA_VER").asText();
        String type = node.get("TYPE").asText();
        JsonNode payload = node.get("PAYLOAD");

        //TODO add a check that any missing field must be flagged as unknown
        return switch (type) {
            case "TRF" -> new TrafficSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    payload.get("SPD").asDouble(),
                    payload.get("LNE").asText()
            );
            case "WTH" -> new WeatherSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    new Farenheit(payload.get("T").asDouble()),
                    payload.get("H").asDouble()
            );
            case "REPORT" -> new ReportSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    payload.has("CAT") && !payload.get("CAT").isNull() ? payload.get("CAT").asText() : null,
                    payload.has("DESC") && !payload.get("DESC").isNull() ? payload.get("DESC").asText() : null,
                    null,
                    null
            );
            default -> toUnknown(node);
        };
    }

    private SensorRead parseV1_5(JsonNode node) {
        //checks de validez
        //id, timestamp, kind, atributes, version not null
        //sitipo es wth temp_c not null attributs: humidity not null
        //si tipo es trf valocity y attr:lane_id
        //si tipo es report category not null attri:severity
        String id = node.get("id").asText();
        String timestamp = node.get("timestamp").asText();
        String schemaVersion = String.valueOf(node.get("version").asDouble());
        String kind = node.get("kind").asText();
        Double temperature = node.get("temp_c").asDouble();
        JsonNode attributes = node.has("attributes") ? node.get("attributes") : null;

        return switch (kind) {
            case "traffic" -> new TrafficSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    node.has("velocity") && !node.get("velocity").isNull() ? node.get("velocity").asDouble() : null,
                    attributes.get("lane_id").asText()
            );
            case "weather" -> new WeatherSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    new Celsius(temperature),
                    attributes != null && attributes.has("HUMIDITY") && !attributes.get("HUMIDITY").isNull() ? attributes.get("HUMIDITY").asDouble() : null
            );
            case "report" -> new ReportSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    node.has("category") && !node.get("category").isNull() ? node.get("category").asText() : null,
                    null,
                    attributes != null && attributes.has("severity") ? attributes.get("severity").asText() : null,
                    null
            );
            default -> toUnknown(node);
        };
    }

    private SensorRead parseV2(JsonNode node) {
        //checks de validez
        //id, timestamp, eventtype, data not null
        //sitipo es wth temp y h not null
        //si tipo es trf speed y lane not null
        //si tipo es report ?????? TODO investigar si tipo report necesita algo particular
        String id = node.get("id").asText();
        String timestamp = node.get("timestamp").asText();
        String schemaVersion = node.get("schemaVersion").asText();
        String eventType = node.get("eventType").asText();
        JsonNode data = node.get("data");

        return switch (eventType) {
            case "TRAFFIC" -> new TrafficSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    data.get("speedKmh").asDouble(),
                    data.get("lane").asText()
            );
            case "WEATHER" -> new WeatherSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    new Celsius(data.get("temperature").asDouble()),
                    data.get("humidity").asDouble()
            );
            case "REPORT" -> new ReportSensorRead(
                    id,
                    timestamp,
                    schemaVersion,
                    data.get("category").asText(),
                    null,
                    null,
                    data.get("status").asText()
            );
            default -> toUnknown(node);
        };
    }

    private UnknownRead toUnknown(JsonNode node) {
        Map<String, Object> raw = new HashMap<>();
        node.fields().forEachRemaining(entry -> raw.put(entry.getKey(), entry.getValue().asText()));
        return new UnknownRead(raw);
    }
}
