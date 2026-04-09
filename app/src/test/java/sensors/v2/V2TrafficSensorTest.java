package sensors.v2;

import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V2TrafficSensorTest {

    private ObjectMapper mapper;
    private JSONParser processor;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        processor = new JSONParser();
    }

    // ==================== MIGRATED FROM SensorTypeTest ====================

    @Test
    void testParsesCorrectType() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 45.0, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(TrafficSensorRead.class, result);
    }

    @Test
    void testHasCorrectAttributes() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
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

    // ==================== MISSING/NULL REQUIRED FIELDS ====================

    @Test
    void testMissingEventTypeReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "data": { "speedKmh": 45.0, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingDataReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC"
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullDataReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": null
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingSpeedKmhReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullSpeedKmhReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": null, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingLaneReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 45.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullLaneReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 45.0, "lane": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== EVENTTYPE VALUE EDGE CASES ====================

    @Test
    void testUnknownEventTypeReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "UNKNOWN",
                    "data": { "speedKmh": 45.0, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== SPEED BOUNDARY TESTS ====================

    @Test
    void testSpeedAtMinBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 0.0, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(TrafficSensorRead.class, result);
    }

    @Test
    void testSpeedAtMaxBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 300.0, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(TrafficSensorRead.class, result);
    }

    @Test
    void testSpeedBelowMinReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": -0.1, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testSpeedAboveMaxReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 300.1, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== OTHER EDGE CASES ====================

    @Test
    void testNumericLaneConvertsToString() throws Exception {
        String json = """
                {
                    "id": "9A8B7C6D",
                    "timestamp": "2026-03-04T10:10:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "TRAFFIC",
                    "data": { "speedKmh": 45.0, "lane": 3 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        TrafficSensorRead sensor = (TrafficSensorRead) processor.parseSensorRead(node);

        assertEquals("3", sensor.laneId());
    }
}
