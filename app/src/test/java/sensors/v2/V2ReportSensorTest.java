package sensors.v2;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V2ReportSensorTest {

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
    void testHasCorrectAttributes() throws Exception {
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

    // ==================== MISSING/NULL DATA FIELDS ====================

    @Test
    void testMissingCategoryReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "G6G6G6G6",
                    "timestamp": "2026-03-04T10:40:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "REPORT",
                    "data": { "status": "BROKEN" }
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
                    "id": "G6G6G6G6",
                    "timestamp": "2026-03-04T10:40:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "REPORT",
                    "data": { "category": null, "status": "BROKEN" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingStatusReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "G6G6G6G6",
                    "timestamp": "2026-03-04T10:40:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "REPORT",
                    "data": { "category": "TRAFFIC_LIGHT" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullStatusReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "G6G6G6G6",
                    "timestamp": "2026-03-04T10:40:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "REPORT",
                    "data": { "category": "TRAFFIC_LIGHT", "status": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== FIELD MAPPING ====================

    @Test
    void testStatusIsMappedFromData() throws Exception {
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

        assertEquals("BROKEN", sensor.status());
    }

    @Test
    void testDescriptionIsNull() throws Exception {
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

        assertNull(sensor.description());
    }

    @Test
    void testSeverityIsNull() throws Exception {
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

        assertNull(sensor.severity());
    }

    // ==================== CRITICALITY ====================

    @Test
    void testReportIsAlwaysCritical() throws Exception {
        assertTrue(new BusinessQualityValidations().isCriticalReport());
    }
}
