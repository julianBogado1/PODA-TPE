package sensors.v1;

import ar.edu.itba.business.models.BusinessQualityValidations;
import ar.edu.itba.infrastructure.models.types.*;
import ar.edu.itba.infrastructure.models.measures.Farenheit;
import ar.edu.itba.io.models.JSONParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class V1WeatherSensorTest {

    private ObjectMapper mapper;
    private JSONParser processor;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        processor = new JSONParser();
    }

    // ==================== Migrated from SensorTypeTest ====================

    @Test
    void testParsesCorrectType() throws Exception {
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
    void testHasCorrectAttributes() throws Exception {
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

    @Test
    void testTemperatureIsFahrenheit() throws Exception {
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
    void testNullHumidityReturnsUnknown() throws Exception {
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

    // ==================== Missing/null fields -> UnknownRead ====================

    @Test
    void testMissingTemperatureReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "H": 40.0 }
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
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": null, "H": 40.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testMissingHumidityFieldReturnsUnknown() throws Exception {
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 95.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== Temperature boundaries (Fahrenheit values) ====================

    @Test
    void testTemperatureAtMinBoundaryIsValid() throws Exception {
        // -58.0°F = -50°C (valid minimum boundary)
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": -58.0, "H": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testTemperatureAtMaxBoundaryIsValid() throws Exception {
        // 1832.0°F = 1000°C (valid maximum boundary)
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 1832.0, "H": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testTemperatureBelowMinReturnsUnknown() throws Exception {
        // -59.0°F ≈ -50.56°C (just below valid minimum)
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": -59.0, "H": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testTemperatureAboveMaxReturnsUnknown() throws Exception {
        // 1833.0°F ≈ 1000.56°C (just above valid maximum)
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 1833.0, "H": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== Humidity boundaries ====================

    @Test
    void testHumidityAtMinBoundaryIsValid() throws Exception {
        // H=0.0 is valid minimum
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 95.0, "H": 0.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testHumidityAtMaxBoundaryIsValid() throws Exception {
        // H=100.0 is valid maximum
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 95.0, "H": 100.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(WeatherSensorRead.class, result);
    }

    @Test
    void testHumidityBelowMinReturnsUnknown() throws Exception {
        // H=-0.1 is just below valid minimum
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 95.0, "H": -0.1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    @Test
    void testHumidityAboveMaxReturnsUnknown() throws Exception {
        // H=100.1 is just above valid maximum
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 95.0, "H": 100.1 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        SensorRead result = processor.parseSensorRead(node);

        assertInstanceOf(UnknownRead.class, result);
    }

    // ==================== Criticality ====================

    @Test
    void testCriticalTemperature() throws Exception {
        // T=32.0°F = 0°C, within critical range [-15, 50]
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 32.0, "H": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertTrue(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    @Test
    void testNonCriticalTemperatureAboveRange() throws Exception {
        // T=248.0°F = 120°C, above critical maximum of 50
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": 248.0, "H": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertFalse(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }

    @Test
    void testNonCriticalTemperatureBelowRange() throws Exception {
        // T=-22.0°F = -30°C, below critical minimum of -15
        String json = """
                {
                    "id": "B1B1B1B1",
                    "timestamp": "2026-03-04T10:15:00Z",
                    "SCHEMA_VER": "1.0",
                    "TYPE": "WTH",
                    "PAYLOAD": { "T": -22.0, "H": 50.0 }
                }
                """;
        JsonNode node = mapper.readTree(json);
        WeatherSensorRead sensor = (WeatherSensorRead) processor.parseSensorRead(node);

        assertFalse(new BusinessQualityValidations().isCriticalTemp(sensor.temperature().toCelsius()));
    }
}
