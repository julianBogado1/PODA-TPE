package sensors.v1_5;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V1_5ReportSensorTest {

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
    void testHasCorrectAttributes() throws Exception {
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

    @Test
    void testMissingSeverityReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "f5f5f5f5",
                    "timestamp": "2026-03-04T10:35:00Z",
                    "version": 1.5,
                    "kind": "report",
                    "category": "Pothole",
                    "attributes": {}
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== MISSING/NULL REQUIRED FIELDS ====================

    @Test
    void testMissingCategoryReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "f5f5f5f5",
                    "timestamp": "2026-03-04T10:35:00Z",
                    "version": 1.5,
                    "kind": "report",
                    "attributes": { "severity": "High" }
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
                    "id": "f5f5f5f5",
                    "timestamp": "2026-03-04T10:35:00Z",
                    "version": 1.5,
                    "kind": "report",
                    "category": null,
                    "attributes": { "severity": "High" }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullSeverityReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "f5f5f5f5",
                    "timestamp": "2026-03-04T10:35:00Z",
                    "version": 1.5,
                    "kind": "report",
                    "category": "Pothole",
                    "attributes": { "severity": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== FIELD MAPPING ====================

    @Test
    void testSeverityIsMappedFromAttributes() throws Exception {
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

        assertEquals("High", sensor.severity());
    }

    @Test
    void testDescriptionIsNull() throws Exception {
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

        assertNull(sensor.description());
    }

    @Test
    void testStatusIsNull() throws Exception {
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

        assertNull(sensor.status());
    }

    // ==================== CRITICALITY ====================

    @Test
    void testReportIsAlwaysCritical() throws Exception {
        assertTrue(new BusinessQualityValidations().isCriticalReport());
    }
}
