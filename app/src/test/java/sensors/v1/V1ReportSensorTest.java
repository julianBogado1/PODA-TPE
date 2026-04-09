package sensors.v1;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V1ReportSensorTest {

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
    void testHasCorrectAttributes() throws Exception {
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

    // ==================== MISSING/NULL PAYLOAD FIELDS ====================

    @Test
    void testMissingCategoryReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "J9J9J9J9",
                    "timestamp": "2026-03-04T10:55:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "REPORT",
                    "PAYLOAD": { "DESC": "Big hole in the middle of the street" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullCategoryReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "J9J9J9J9",
                    "timestamp": "2026-03-04T10:55:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "REPORT",
                    "PAYLOAD": { "CAT": null, "DESC": "Big hole in the middle of the street" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingDescriptionReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "J9J9J9J9",
                    "timestamp": "2026-03-04T10:55:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "REPORT",
                    "PAYLOAD": { "CAT": "pothole" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullDescriptionReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "J9J9J9J9",
                    "timestamp": "2026-03-04T10:55:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "REPORT",
                    "PAYLOAD": { "CAT": "pothole", "DESC": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== FIELD MAPPING ====================

    @Test
    void testDescriptionIsMappedFromDESC() throws Exception {
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

        assertEquals("Big hole in the middle of the street", sensor.description());
    }

    @Test
    void testSeverityIsNull() throws Exception {
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

        assertNull(sensor.severity());
    }

    @Test
    void testStatusIsNull() throws Exception {
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

        assertNull(sensor.status());
    }

    // ==================== CRITICALITY ====================

    @Test
    void testReportIsAlwaysCritical() throws Exception {
        assertTrue(new BusinessQualityValidations().isCriticalReport());
    }
}
