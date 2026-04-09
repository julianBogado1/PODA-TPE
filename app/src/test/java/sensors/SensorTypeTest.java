package sensors;

import ar.edu.itba.infrastructure.types.*;
import ar.edu.itba.io.JSONParser;
import ar.edu.itba.infrastructure.measures.Celsius;
import ar.edu.itba.infrastructure.measures.Farenheit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SensorTypeTest {

    private ObjectMapper mapper;
    private JSONParser processor;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        processor = new JSONParser();
    }

    // ==================== V1.0 TRAFFIC ====================

    @Test
    void testV1TrafficParsesCorrectType() throws Exception {
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
    }

    @Test
    void testV1TrafficHasCorrectAttributes() throws Exception {
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
        TrafficSensorRead sensor = (TrafficSensorRead) processor.parseSensorRead(node);

        assertEquals("A1B2C3D4", sensor.id());
        assertEquals("2026-03-04T10:00:00Z", sensor.timestamp());
        assertEquals("1.0", sensor.schemaVersion());
        assertEquals(65.5, sensor.speed());
        assertEquals("1", sensor.laneId());
    }

    // ==================== V1.5 TRAFFIC ====================

    @Test
    void testV1_5TrafficParsesCorrectType() throws Exception {
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
    }

    @Test
    void testV1_5TrafficHasCorrectAttributes() throws Exception {
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
        TrafficSensorRead sensor = (TrafficSensorRead) processor.parseSensorRead(node);

        assertEquals("e5f6g7h8", sensor.id());
        assertEquals("2026-03-04T10:05:00Z", sensor.timestamp());
        assertEquals("1.5", sensor.schemaVersion());
        assertEquals(120.0, sensor.speed());
        assertEquals("L-2", sensor.laneId());
    }

    // ==================== V2.0 TRAFFIC ====================

    @Test
    void testV2TrafficParsesCorrectType() throws Exception {
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
    }

    @Test
    void testV2TrafficHasCorrectAttributes() throws Exception {
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
        TrafficSensorRead sensor = (TrafficSensorRead) processor.parseSensorRead(node);

        assertEquals("9A8B7C6D", sensor.id());
        assertEquals("2026-03-04T10:10:00Z", sensor.timestamp());
        assertEquals("2.0", sensor.schemaVersion());
        assertEquals(45.0, sensor.speed());
        assertEquals("3", sensor.laneId());
    }

    // ==================== V1.0 WEATHER ====================

    @Test
    void testV1WeatherParsesCorrectType() throws Exception {
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
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testV1WeatherHasCorrectAttributes() throws Exception {
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

        assertEquals("B1B1B1B1", sensor.id());
        assertEquals("2026-03-04T10:15:00Z", sensor.timestamp());
        assertEquals("1.0", sensor.schemaVersion());
        assertInstanceOf(Farenheit.class, sensor.temperature());
        assertEquals(95.0, sensor.temperature().toFahrenheit());
        assertEquals(40.0, sensor.humidity());
    }

    // ==================== V1.5 WEATHER ====================

    @Test
    void testV1_5WeatherParsesCorrectType() throws Exception {
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
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testV1_5WeatherHasCorrectAttributes() throws Exception {
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

        assertEquals("c2c2c2c2", sensor.id());
        assertEquals("2026-03-04T10:20:00Z", sensor.timestamp());
        assertEquals("1.5", sensor.schemaVersion());
        assertInstanceOf(Celsius.class, sensor.temperature());
        assertEquals(32.5, sensor.temperature().toCelsius());
        assertEquals(85.0, sensor.humidity());
    }

    // ==================== V2.0 WEATHER ====================

    @Test
    void testV2WeatherWithInvalidHumidityReturnsUnknown() throws Exception {
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

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== V1.0 REPORT ====================

    @Test
    void testV1ReportParsesCorrectType() throws Exception {
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
    }

    @Test
    void testV1ReportHasCorrectAttributes() throws Exception {
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
        ReportSensorRead sensor = (ReportSensorRead) processor.parseSensorRead(node);

        assertEquals("J9J9J9J9", sensor.id());
        assertEquals("2026-03-04T10:55:00Z", sensor.timestamp());
        assertEquals("1.0", sensor.schemaVersion());
        assertEquals("pothole", sensor.category());
        assertEquals("Big hole in the middle of the street", sensor.description());
    }

    // ==================== V1.5 REPORT ====================

    @Test
    void testV1_5ReportParsesCorrectType() throws Exception {
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
    }

    @Test
    void testV1_5ReportHasCorrectAttributes() throws Exception {
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
        ReportSensorRead sensor = (ReportSensorRead) processor.parseSensorRead(node);

        assertEquals("f5f5f5f5", sensor.id());
        assertEquals("2026-03-04T10:35:00Z", sensor.timestamp());
        assertEquals("1.5", sensor.schemaVersion());
        assertEquals("Pothole", sensor.category());
        assertEquals("High", sensor.severity());
    }

    // ==================== V2.0 REPORT ====================

    @Test
    void testV2ReportParsesCorrectType() throws Exception {
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
    }

    @Test
    void testV2ReportHasCorrectAttributes() throws Exception {
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
        ReportSensorRead sensor = (ReportSensorRead) processor.parseSensorRead(node);

        assertEquals("G6G6G6G6", sensor.id());
        assertEquals("2026-03-04T10:40:00Z", sensor.timestamp());
        assertEquals("2.0", sensor.schemaVersion());
        assertEquals("TRAFFIC_LIGHT", sensor.category());
        assertEquals("BROKEN", sensor.status());
    }

    // ==================== EDGE CASES ====================

    @Test
    void testNullNodeReturnsUnknown() {
        SensorRead result = processor.parseSensorRead(null);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testUnknownTypeReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "XXXX",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "UNKNOWN_TYPE",
                    "PAYLOAD": {}
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testV1WeatherTemperatureIsFahrenheit() throws Exception {
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

        assertInstanceOf(Farenheit.class, sensor.temperature());
    }

    @Test
    void testV1_5WeatherTemperatureIsCelsius() throws Exception {
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

        assertInstanceOf(Celsius.class, sensor.temperature());
    }

    @Test
    void testV2WeatherTemperatureIsCelsius() throws Exception {
        String json = """
                {
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 15.0, "humidity": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertInstanceOf(Celsius.class, sensor.temperature());
    }

    @Test
    void testV1TrafficWithNullSpeedReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "E4E4E4E4",
                    "timestamp": "2026-03-04T10:30:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": null, "LNE": 2 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testV1_5TrafficWithNullVelocityReturnsUnknown() throws Exception {
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

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testV1WeatherWithNullHumidityReturnsUnknown() throws Exception {
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

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testV1_5ReportMissingSeverityReturnsUnknown() throws Exception {
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

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testVersionIsNormalizedToString() throws Exception {
        // v1.5 sends version as a number (1.5), should be normalized to "1.5"
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
        TrafficSensorRead sensor = (TrafficSensorRead) processor.parseSensorRead(node);

        assertEquals("1.5", sensor.schemaVersion());
    }
}
