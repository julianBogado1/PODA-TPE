package sensors.v1;

import ar.edu.itba.business.sensor_types.SensorRead;
import ar.edu.itba.business.sensor_types.TrafficSensorRead;
import ar.edu.itba.business.sensor_types.UnknownRead;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V1TrafficSensorTest {

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
    void testHasCorrectAttributes() throws Exception {
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

    @Test
    void testNullSpeedReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": null, "LNE": 1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== MISSING/NULL REQUIRED FIELDS ====================

    @Test
    void testMissingIdReturnsUnknown() throws Exception {
        String json = """
                {
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 65.5, "LNE": 1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingTimestampReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "A1B2C3D4",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 65.5, "LNE": 1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingTypeReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "PAYLOAD": { "SPD": 65.5, "LNE": 1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingPayloadReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF"
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullPayloadReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": null
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
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 65.5 }
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
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 65.5, "LNE": null }
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
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 0.0, "LNE": 1 }
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
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 300.0, "LNE": 1 }
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
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": -0.1, "LNE": 1 }
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
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 300.1, "LNE": 1 }
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
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "TRF",
                    "PAYLOAD": { "SPD": 65.5, "LNE": 2 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        TrafficSensorRead sensor = (TrafficSensorRead) processor.parseSensorRead(node);

        assertEquals("2", sensor.laneId());
    }

    @Test
    void testUnknownTypeValueReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "A1B2C3D4",
                    "timestamp": "2026-03-04T10:00:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "UNKNOWN_TYPE",
                    "PAYLOAD": { "SPD": 65.5, "LNE": 1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullNodeReturnsUnknown() throws Exception {
        SensorRead result = processor.parseSensorRead(null);

        assertInstanceOf(UnknownRead.class, result);
    }
}
