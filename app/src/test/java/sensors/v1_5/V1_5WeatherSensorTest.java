package sensors.v1_5;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.infrastructure.models.measures.Celsius;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V1_5WeatherSensorTest {

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
    void testHasCorrectAttributes() throws Exception {
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

    @Test
    void testTemperatureIsCelsius() throws Exception {
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

    // ==================== MISSING/NULL FIELDS → UNKNOWN ====================

    @Test
    void testMissingTempCReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "attributes": { "HUMIDITY": 85.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullTempCReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": null,
                    "attributes": { "HUMIDITY": 85.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingHumidityReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 32.5,
                    "attributes": {}
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullHumidityReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 32.5,
                    "attributes": { "HUMIDITY": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== TEMPERATURE BOUNDARY TESTS ====================

    @Test
    void testTemperatureAtMinBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": -50.0,
                    "attributes": { "HUMIDITY": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testTemperatureAtMaxBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 1000.0,
                    "attributes": { "HUMIDITY": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testTemperatureBelowMinReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": -50.1,
                    "attributes": { "HUMIDITY": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testTemperatureAboveMaxReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 1000.1,
                    "attributes": { "HUMIDITY": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== HUMIDITY BOUNDARY TESTS ====================

    @Test
    void testHumidityAtMinBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 20.0,
                    "attributes": { "HUMIDITY": 0.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testHumidityAtMaxBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 20.0,
                    "attributes": { "HUMIDITY": 100.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testHumidityBelowMinReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 20.0,
                    "attributes": { "HUMIDITY": -0.1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testHumidityAboveMaxReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 20.0,
                    "attributes": { "HUMIDITY": 100.1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== CRITICALITY TESTS ====================

    @Test
    void testCriticalTemperature() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 25.0,
                    "attributes": { "HUMIDITY": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertTrue(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    @Test
    void testNonCriticalTemperatureAboveRange() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": 60.0,
                    "attributes": { "HUMIDITY": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertFalse(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    @Test
    void testNonCriticalTemperatureBelowRange() throws Exception {
        String json = """
                {
                    "id": "c2c2c2c2",
                    "timestamp": "2026-03-04T10:20:00Z",
                    "version": 1.5,
                    "kind": "weather",
                    "temp_c": -20.0,
                    "attributes": { "HUMIDITY": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertFalse(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }
}
