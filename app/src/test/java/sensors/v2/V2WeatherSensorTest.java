package sensors.v2;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.infrastructure.models.measures.Celsius;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V2WeatherSensorTest {

    private ObjectMapper mapper;
    private JSONParser processor;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        processor = new JSONParser();
    }

    // ==================== MIGRATED FROM SensorTypeTest ====================

    @Test
    void testInvalidHumidityReturnsUnknown() throws Exception {
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

    @Test
    void testTemperatureIsCelsius() throws Exception {
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

    // ==================== PARSE TYPE AND ATTRIBUTES ====================

    @Test
    void testParsesCorrectType() throws Exception {
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
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testHasCorrectAttributes() throws Exception {
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

        assertEquals("D3D3D3D3", sensor.id());
        assertEquals("2026-03-04T10:25:00Z", sensor.timestamp());
        assertEquals("2.0", sensor.schemaVersion());
        assertInstanceOf(Celsius.class, sensor.temperature());
        assertEquals(15.0, sensor.temperature().toCelsius());
        assertEquals(50.0, sensor.humidity());
    }

    // ==================== MISSING / NULL FIELDS ====================

    @Test
    void testMissingTemperatureReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "humidity": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testNullTemperatureReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": null, "humidity": 50.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 15.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 15.0, "humidity": null }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== TEMPERATURE BOUNDARIES ====================

    @Test
    void testTemperatureAtMinBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": -50.0, "humidity": 50.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 1000.0, "humidity": 50.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": -50.1, "humidity": 50.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 1000.1, "humidity": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== HUMIDITY BOUNDARIES ====================

    @Test
    void testHumidityAtMinBoundaryIsValid() throws Exception {
        String json = """
                {
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 20.0, "humidity": 0.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 20.0, "humidity": 100.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 20.0, "humidity": -0.1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== CRITICALITY ====================

    @Test
    void testCriticalTemperature() throws Exception {
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

        assertTrue(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    @Test
    void testNonCriticalTemperatureAboveRange() throws Exception {
        String json = """
                {
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": 60.0, "humidity": 50.0 }
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
                    "id": "D3D3D3D3",
                    "timestamp": "2026-03-04T10:25:00Z",
                    "schemaVersion": "2.0",
                    "eventType": "WEATHER",
                    "data": { "temperature": -20.0, "humidity": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertFalse(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }
}
