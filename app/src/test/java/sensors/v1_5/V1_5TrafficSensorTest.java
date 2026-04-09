package sensors.v1_5;

import ar.edu.itba.business.sensor_types.SensorRead;
import ar.edu.itba.business.sensor_types.TrafficSensorRead;
import ar.edu.itba.business.sensor_types.UnknownRead;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V1_5TrafficSensorTest {

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
    void testHasCorrectAttributes() throws Exception {
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

    @Test
    void testNullVelocityReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": null,
                    "attributes": { "lane_id": "L-2" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testVersionIsNormalizedToString() throws Exception {
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

    // ==================== MISSING/NULL REQUIRED FIELDS ====================

    @Test
    void testMissingKindReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "velocity": 120.0,
                    "attributes": { "lane_id": "L-2" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingVelocityFieldReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "attributes": { "lane_id": "L-2" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingAttributesReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": 120.0
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingLaneIdInAttributesReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": 120.0,
                    "attributes": {}
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullLaneIdReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": 120.0,
                    "attributes": { "lane_id": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== KIND VALUE EDGE CASES ====================

    @Test
    void testUnknownKindValueReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "unknown_kind",
                    "velocity": 120.0,
                    "attributes": { "lane_id": "L-2" }
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
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": 0.0,
                    "attributes": { "lane_id": "L-2" }
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
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": 300.0,
                    "attributes": { "lane_id": "L-2" }
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
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": -0.1,
                    "attributes": { "lane_id": "L-2" }
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
                    "id": "e5f6g7h8",
                    "timestamp": "2026-03-04T10:05:00Z",
                    "version": 1.5,
                    "kind": "traffic",
                    "velocity": 300.1,
                    "attributes": { "lane_id": "L-2" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }
}
