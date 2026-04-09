package ar.edu.itba.io.models;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.business.models.SensorMetrics;
import ar.edu.itba.infrastructure.models.SensorProcessState;
import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.infrastructure.models.measures.Celsius;
import ar.edu.itba.infrastructure.models.measures.Farenheit;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class JSONParser implements Parser{
    private static final BusinessQualityValidations qa = new BusinessQualityValidations();



    public SensorMetrics parse(DataSource<JsonNode> source) {
        SensorProcessState state = SensorProcessState.initial(
                0.0, 0L, 0L, 0L, 0L, 0L, 0L
        );

        for (JsonNode node : source) {
            SensorRead read = parseSensorRead(node);

            state = switch (read) {
                case TrafficSensorRead t -> {
                    var s = state.withTrafficSensor(t.speed());
                    if (isCritical(t)) s = s.withCriticalEvent();
                    yield s;
                }
                case WeatherSensorRead w -> {
                    var s = state.withWeatherSensor();
                    if (isCritical(w)) s = s.withCriticalEvent();
                    yield s;
                }
                case ReportSensorRead r  -> {
                    var s = state.withReportSensor();
                    if (isCritical(r)) s = s.withCriticalEvent();
                    yield s;
                }
                case UnknownRead _ -> state;
            };

            state = countVersion(state, read.schemaVersion());
        }
        return state.toMetrics();
    }

    public SensorRead parseSensorRead(JsonNode node) {
        SensorRead read = switch (node) {
            case null -> new UnknownRead(Map.of());
            case JsonNode n when n.isNull() -> new UnknownRead(Map.of());
            case JsonNode n when n.has("SCHEMA_VER") -> parseV1(n);
            case JsonNode n when n.has("version") -> parseV1_5(n);
            case JsonNode n when n.has("schemaVersion") -> parseV2(n);
            default -> toUnknown(node);
        };
        return validate(read, node);
    }

    private SensorRead validate(SensorRead read, JsonNode node) {
        return switch (read) {
            case TrafficSensorRead t when !qa.isSpeedValid(t.speed()) -> toUnknown(node);
            case WeatherSensorRead w when !qa.isTemperatureValid(w.temperature().toCelsius())
                                       || !qa.isHumidityValid(w.humidity()) -> toUnknown(node);
            default -> read;
        };
    }

    private boolean hasField(JsonNode node, String field) {
        return node != null && node.has(field) && !node.get(field).isNull();
    }

    private SensorProcessState countVersion(SensorProcessState state, String version) {
        return switch (version) {
            case null -> state;
            case String v when v.startsWith("1.0") -> state.withV1Sensor();
            case String v when v.startsWith("1.5") -> state.withV1_5Sensor();
            case String v when v.startsWith("2") -> state.withV2Sensor();
            default -> state;
        };
    }

    private boolean isCritical(TrafficSensorRead read){
        return qa.isCriticalSpeed(read.speed());
    }
    private boolean isCritical(WeatherSensorRead read){
        return qa.isCriticalTemp(read.temperature().toCelsius());
    }
    public boolean isCritical(ReportSensorRead read){
        return qa.isCriticalReport();
    }

    private SensorRead parseV1(JsonNode node) {
        //id, timestamp, type, payload, schema_ver not null
        //si tipo es trf payload: spd, lne not null
        //si tipo es wth payload: t, h not null
        //si tipo es report payload: cat, desc not null
        if (!hasField(node, "id") || !hasField(node, "timestamp") ||
            !hasField(node, "SCHEMA_VER") || !hasField(node, "TYPE") || !hasField(node, "PAYLOAD")) {
            return toUnknown(node);
        }

        String id = node.get("id").asText();
        String timestamp = node.get("timestamp").asText();
        String schemaVersion = node.get("SCHEMA_VER").asText();
        String type = node.get("TYPE").asText();
        JsonNode payload = node.get("PAYLOAD");

        return switch (type) {
            case "TRF" -> {
                if (!hasField(payload, "SPD") || !hasField(payload, "LNE")) yield toUnknown(node);
                yield new TrafficSensorRead(id, timestamp, schemaVersion,
                        payload.get("SPD").asDouble(), payload.get("LNE").asText());
            }
            case "WTH" -> {
                if (!hasField(payload, "T") || !hasField(payload, "H")) yield toUnknown(node);
                yield new WeatherSensorRead(id, timestamp, schemaVersion,
                        new Farenheit(payload.get("T").asDouble()), payload.get("H").asDouble());
            }
            case "REPORT" -> {
                if (!hasField(payload, "CAT") || !hasField(payload, "DESC")) yield toUnknown(node);
                yield new ReportSensorRead(id, timestamp, schemaVersion,
                        payload.get("CAT").asText(), payload.get("DESC").asText(), null, null);
            }
            default -> toUnknown(node);
        };
    }

    private SensorRead parseV1_5(JsonNode node) {
        //id, timestamp, kind, attributes, version not null
        //si tipo es trf: velocity, attr:lane_id not null
        //si tipo es wth: temp_c not null, attributes: humidity not null
        //si tipo es report: category not null, attri:severity not null
        if (!hasField(node, "id") || !hasField(node, "timestamp") ||
            !hasField(node, "version") || !hasField(node, "kind") || !hasField(node, "attributes")) {
            return toUnknown(node);
        }

        String id = node.get("id").asText();
        String timestamp = node.get("timestamp").asText();
        String schemaVersion = String.valueOf(node.get("version").asDouble());
        String kind = node.get("kind").asText();
        JsonNode attributes = node.get("attributes");

        return switch (kind) {
            case "traffic" -> {
                if (!hasField(node, "velocity") || !hasField(attributes, "lane_id")) yield toUnknown(node);
                yield new TrafficSensorRead(id, timestamp, schemaVersion,
                        node.get("velocity").asDouble(), attributes.get("lane_id").asText());
            }
            case "weather" -> {
                if (!hasField(node, "temp_c") || !hasField(attributes, "HUMIDITY")) yield toUnknown(node);
                yield new WeatherSensorRead(id, timestamp, schemaVersion,
                        new Celsius(node.get("temp_c").asDouble()), attributes.get("HUMIDITY").asDouble());
            }
            case "report" -> {
                if (!hasField(node, "category") || !hasField(attributes, "severity")) yield toUnknown(node);
                yield new ReportSensorRead(id, timestamp, schemaVersion,
                        node.get("category").asText(), null, attributes.get("severity").asText(), null);
            }
            default -> toUnknown(node);
        };
    }

    private SensorRead parseV2(JsonNode node) {
        //id, timestamp, eventType, data not null
        //si tipo es trf: speedKmh, lane not null
        //si tipo es wth: temperature, humidity not null
        //si tipo es report: category, status not null
        if (!hasField(node, "id") || !hasField(node, "timestamp") ||
            !hasField(node, "schemaVersion") || !hasField(node, "eventType") || !hasField(node, "data")) {
            return toUnknown(node);
        }

        String id = node.get("id").asText();
        String timestamp = node.get("timestamp").asText();
        String schemaVersion = node.get("schemaVersion").asText();
        String eventType = node.get("eventType").asText();
        JsonNode data = node.get("data");

        return switch (eventType) {
            case "TRAFFIC" -> {
                if (!hasField(data, "speedKmh") || !hasField(data, "lane")) yield toUnknown(node);
                yield new TrafficSensorRead(id, timestamp, schemaVersion,
                        data.get("speedKmh").asDouble(), data.get("lane").asText());
            }
            case "WEATHER" -> {
                if (!hasField(data, "temperature") || !hasField(data, "humidity")) yield toUnknown(node);
                yield new WeatherSensorRead(id, timestamp, schemaVersion,
                        new Celsius(data.get("temperature").asDouble()), data.get("humidity").asDouble());
            }
            case "REPORT" -> {
                if (!hasField(data, "category") || !hasField(data, "status")) yield toUnknown(node);
                yield new ReportSensorRead(id, timestamp, schemaVersion,
                        data.get("category").asText(), null, null, data.get("status").asText());
            }
            default -> toUnknown(node);
        };
    }

    private UnknownRead toUnknown(JsonNode node) {
        Map<String, Object> raw = new HashMap<>();
        node.fields().forEachRemaining(entry -> raw.put(entry.getKey(), entry.getValue().asText()));
        return new UnknownRead(raw);
    }

}
