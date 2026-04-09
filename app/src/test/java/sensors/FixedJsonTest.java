package sensors;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.business.sensor_types.*;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests every record from example_input.json.
 * Each test asserts the parsed type and, where applicable, the criticality.
 */
public class FixedJsonTest {

    private ObjectMapper mapper;
    private JSONParser processor;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        processor = new JSONParser();
    }

    // ==================== Record 1 ====================

    @Test
    void testRecord_A1B2C3D4() throws Exception {
        String json = """
                {
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 65.5, "LNE": 1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(TrafficSensorRead.class, result);
        // Traffic criticality is always false
    }

    // ==================== Record 2 ====================

    @Test
    void testRecord_e5f6g7h8() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": 120.0,
                    "attributes": { "lane_id": "L-2" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(TrafficSensorRead.class, result);
        // Traffic criticality is always false
    }

    // ==================== Record 3 ====================

    @Test
    void testRecord_9A8B7C6D() throws Exception {
        String json = """
                {
                    "schemaVersion": "2.0",
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 45.0, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(TrafficSensorRead.class, result);
        // Traffic criticality is always false
    }

    // ==================== Record 4 ====================

    @Test
    void testRecord_B1B1B1B1() throws Exception {
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 95.0, "H": 40.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, sensor);
        // 95°F = 35°C, within critical range [-15, 50]
        assertTrue(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    // ==================== Record 5 ====================

    @Test
    void testRecord_c2c2c2c2() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 32.5,
                    "attributes": { "HUMIDITY": 85.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, sensor);
        // 32.5°C, within critical range [-15, 50]
        assertTrue(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    // ==================== Record 6 ====================

    @Test
    void testRecord_D3D3D3D3_invalidHumidity() throws Exception {
        String json = """
                {
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 15.0, "humidity": 120.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        // humidity 120.0 > 100, fails validation
        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== Record 7 ====================

    @Test
    void testRecord_E4E4E4E4_speedTooHigh() throws Exception {
        String json = """
                {
                    "id": "E4E4E4E4",
                    "timestamp": "2026-03-04T10:30:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 350.0, "LNE": 2 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        // speed 350.0 > 300, fails validation
        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== Record 8 ====================

    @Test
    void testRecord_f5f5f5f5() throws Exception {
        String json = """
                {
                    "id": "f5f5f5f5",
                    "timestamp": "2026-03-04T10:35:00Z",
                    "version": 1.5,
                    "kind": "report",
                    "category": "Pothole",
                    "attributes": { "severity": "High" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(ReportSensorRead.class, result);
        // Reports are always critical
        assertTrue(new BusinessQualityValidations().isCriticalReport());
    }

    // ==================== Record 9 ====================

    @Test
    void testRecord_G6G6G6G6() throws Exception {
        String json = """
                {
                    "id": "G6G6G6G6",
                    "timestamp": "2026-03-04T10:40:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "REPORT",
                    "data": { "category": "TRAFFIC_LIGHT", "status": "BROKEN" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(ReportSensorRead.class, result);
        // Reports are always critical
        assertTrue(new BusinessQualityValidations().isCriticalReport());
    }

    // ==================== Record 10 ====================

    @Test
    void testRecord_h7h7h7h7_nullHumidity() throws Exception {
        String json = """
                {
                    "id": "h7h7h7h7",
                    "timestamp": "2026-03-04T10:45:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 10.0, "H": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        // humidity is null, fails validation
        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== Record 11 ====================

    @Test
    void testRecord_I8I8I8I8_nullVelocity() throws Exception {
        String json = """
                {
                    "id": "I8I8I8I8",
                    "timestamp": "2026-03-04T10:50:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": null,
                    "attributes": { "lane_id": "L-1" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        // velocity is null, fails validation
        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== Record 12 ====================

    @Test
    void testRecord_J9J9J9J9() throws Exception {
        String json = """
                {
                    "id": "J9J9J9J9",
                    "timestamp": "2026-03-04T10:55:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "REPORT",
                    "PAYLOAD": { "CAT": "pothole", "DESC": "Big hole in the middle of the street" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(ReportSensorRead.class, result);
        // Reports are always critical
        assertTrue(new BusinessQualityValidations().isCriticalReport());
    }

    // ==================== Record 13 ====================

    @Test
    void testRecord_k10k10k10() throws Exception {
        String json = """
                {
                    "id": "k10k10k10",
                    "timestamp": "2026-03-04T11:00:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 45.0,
                    "attributes": { "HUMIDITY": 10.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, sensor);
        // 45.0°C, within critical range [-15, 50]
        assertTrue(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    // ==================== Record 14 ====================

    @Test
    void testRecord_L11L11L11() throws Exception {
        String json = """
                {
                    "id": "L11L11L11",
                    "timestamp": "2026-03-04T11:05:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 115.0, "lane": 4 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(TrafficSensorRead.class, result);
        // Traffic criticality is always false
    }

    // ==================== Record 15 ====================

    @Test
    void testRecord_m12m12m12_missingSeverity() throws Exception {
        String json = """
                {
                    "id": "m12m12m12",
                    "timestamp": "2026-03-04T11:10:00Z",
                    "version": 1.5,
                    "kind": "report",
                    "category": "STREET_CLEAN",
                    "attributes": { "area": "North" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        // missing severity in attributes, fails validation
        assertInstanceOf(UnknownRead.class, result);
    }
}
